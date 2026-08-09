package com.carenest.provider.core.network.socket.client

import android.util.Log
import com.carenest.provider.core.BuildConfig
import com.carenest.provider.core.datastore.TokenManager
import com.carenest.provider.core.network.socket.model.AcceptOfferRequest
import com.carenest.provider.core.network.socket.model.AvailabilityRequest
import com.carenest.provider.core.network.socket.model.CancelReservationRequest
import com.carenest.provider.core.network.socket.model.ChatMessageResponse
import com.carenest.provider.core.network.socket.model.CreateOfferRequest
import com.carenest.provider.core.network.socket.model.ListOffersRequest
import com.carenest.provider.core.network.socket.model.LocationUpdateRequest
import com.carenest.provider.core.network.socket.model.NearbyNurseServiceRequestResponse
import com.carenest.provider.core.network.socket.model.NotificationResponse
import com.carenest.provider.core.network.socket.model.NurseOfferResponse
import com.carenest.provider.core.network.socket.model.ReservationEvent
import com.carenest.provider.core.network.socket.model.SendChatMessageRequest
import com.carenest.provider.core.network.socket.model.SocketConnectionState
import com.carenest.provider.core.network.socket.model.SocketErrorPayload
import com.carenest.provider.core.network.socket.model.UpdateOfferRequest
import com.carenest.provider.core.network.socket.model.WithdrawOfferRequest
import com.carenest.provider.core.network.socket.stomp.StompClient
import com.carenest.provider.core.network.socket.stomp.StompCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class NurseSocketClientImpl @Inject constructor(
    private val stompClient: StompClient,
    private val tokenManager: TokenManager,
    private val json: Json
) : NurseSocketClient {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val connectionState: StateFlow<SocketConnectionState> = stompClient.connectionState

    private val _notifications = MutableSharedFlow<NotificationResponse>(extraBufferCapacity = 64)
    override val notifications: SharedFlow<NotificationResponse> = _notifications.asSharedFlow()

    private val _socketErrors = MutableSharedFlow<SocketErrorPayload>(extraBufferCapacity = 64)
    override val socketErrors: SharedFlow<SocketErrorPayload> = _socketErrors.asSharedFlow()

    private val _nearbyRequests = MutableSharedFlow<NearbyNurseServiceRequestResponse>(extraBufferCapacity = 64)
    override val nearbyRequests: SharedFlow<NearbyNurseServiceRequestResponse> = _nearbyRequests.asSharedFlow()

    private val _reservationEvents = MutableSharedFlow<ReservationEvent>(extraBufferCapacity = 64)
    override val reservationEvents: SharedFlow<ReservationEvent> = _reservationEvents.asSharedFlow()

    private val _chatMessages = MutableSharedFlow<ChatMessageResponse>(extraBufferCapacity = 64)
    override val chatMessages: SharedFlow<ChatMessageResponse> = _chatMessages.asSharedFlow()

    private val activeReservationSubscriptions = ConcurrentHashMap.newKeySet<String>()
    private val activeChatSubscriptions = ConcurrentHashMap.newKeySet<String>()

    private var connectionManagerJob: Job? = null
    private var heartbeatJob: Job? = null
    private var frameCollectorJob: Job? = null

    private var isExplicitlyDisconnected = false

    override fun connect() {
        Log.i("NurseSocketClient", "Connect requested (isExplicitlyDisconnected=$isExplicitlyDisconnected)")
        isExplicitlyDisconnected = false
        if (connectionManagerJob?.isActive == true) {
            Log.d("NurseSocketClient", "Connection manager already active")
            return
        }

        connectionManagerJob = scope.launch {
            manageConnectionLifecycle()
        }

        if (frameCollectorJob?.isActive != true) {
            Log.d("NurseSocketClient", "Starting frame collector")
            frameCollectorJob = scope.launch {
                collectIncomingFrames()
            }
        }
    }

    override fun disconnect() {
        Log.i("NurseSocketClient", "Disconnect requested")
        isExplicitlyDisconnected = true
        heartbeatJob?.cancel()
        heartbeatJob = null

        connectionManagerJob?.cancel()
        connectionManagerJob = null

        scope.launch {
            stompClient.disconnect()
        }
    }

    private suspend fun manageConnectionLifecycle() {
        var attempt = 0
        val wsUrl = resolveWebSocketUrl(BuildConfig.BASE_URL)

        while (!isExplicitlyDisconnected) {
            val token = tokenManager.accessToken.first()
            if (token.isNullOrBlank()) {
                Log.w("NurseSocketClient", "No access token available, waiting...")
                delay(2000)
                continue
            }

            Log.d("NurseSocketClient", "Attempting connection (attempt=$attempt)")
            val success = stompClient.connect(wsUrl, token)
            if (success) {
                Log.i("NurseSocketClient", "Successfully connected to STOMP")
                attempt = 0
                onConnected()
                // Wait until state changes from Connected
                stompClient.connectionState.first { it != SocketConnectionState.Connected }
                Log.w("NurseSocketClient", "Socket connection lost, restarting lifecycle")
                heartbeatJob?.cancel()
                heartbeatJob = null
            } else {
                attempt++
                val backoffMs = (1000L * (1 shl minOf(attempt, 5))).coerceAtMost(30000L)
                Log.e("NurseSocketClient", "Connection failed, retrying in ${backoffMs}ms")
                delay(backoffMs)
            }
        }
        Log.i("NurseSocketClient", "Connection manager lifecycle ended (explicitly disconnected)")
    }

    private fun String?.isNull_OrEmpty(): Boolean = this == null || this.trim().isEmpty()

    private var isAvailableState: Boolean = false
    private var lastKnownLat: Double? = null
    private var lastKnownLng: Double? = null

    private suspend fun onConnected() {
        Log.d("NurseSocketClient", "onConnected: Subscribing to default topics")
        // Subscribe to default nurse topics
        stompClient.subscribe(DEST_USER_NOTIFICATIONS)
        stompClient.subscribe(DEST_USER_ERRORS)
        stompClient.subscribe(DEST_USER_NEARBY_REQUEST)

        // Restore dynamic topic subscriptions
        activeReservationSubscriptions.forEach { reservationId ->
            Log.d("NurseSocketClient", "Restoring reservation subscription: $reservationId")
            stompClient.subscribe("$DEST_TOPIC_RESERVATION_PREFIX/$reservationId")
        }

        activeChatSubscriptions.forEach { reservationId ->
            Log.d("NurseSocketClient", "Restoring chat subscription: $reservationId")
            stompClient.subscribe("$DEST_TOPIC_CHAT_PREFIX/$reservationId")
        }

        if (isAvailableState) {
            Log.d("NurseSocketClient", "Restoring availability state: $isAvailableState")
            val payload = json.encodeToString(AvailabilityRequest(true, lastKnownLat, lastKnownLng))
            stompClient.send(DEST_APP_AVAILABILITY, payload)
        }

        // Start heartbeat ticker (~30s)
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            Log.d("NurseSocketClient", "Starting heartbeat ticker")
            while (stompClient.connectionState.value == SocketConnectionState.Connected) {
                delay(30_000)
                sendHeartbeat()
            }
        }
    }

    private suspend fun collectIncomingFrames() {
        stompClient.incomingFrames.collect { frame ->
            Log.d("NurseSocketClient", "Raw Frame Received: Command=${frame.command}, Headers=${frame.headers}")
            
            if (frame.command == StompCommand.MESSAGE) {
                val destination = frame.headers["destination"] ?: return@collect
                val body = frame.body?.trim('\u0000') ?: return@collect

                Log.i("NurseSocketClient", "Message from $destination: $body")

                try {
                    when {
                        destination.contains("/queue/notifications") -> {
                            val notification = json.decodeFromString<NotificationResponse>(body)
                            _notifications.emit(notification)
                        }
                        destination.contains("/queue/errors") -> {
                            val errorPayload = json.decodeFromString<SocketErrorPayload>(body)
                            Log.e("NurseSocketClient", "Server Error on $destination: ${errorPayload.code} - ${errorPayload.message}")
                            _socketErrors.emit(errorPayload)
                        }
                        destination.contains("/queue/nearby-request") -> {
                            Log.i("NurseSocketClient", "!!! MATCHED NEARBY REQUEST !!!")
                            val nearbyReq = json.decodeFromString<NearbyNurseServiceRequestResponse>(body)
                            _nearbyRequests.emit(nearbyReq)
                        }
                        destination.contains("reservation") -> {
                            val reservationEvent = json.decodeFromString<ReservationEvent>(body)
                            _reservationEvents.emit(reservationEvent)
                        }
                        destination.contains("/topic/chat/") -> {
                            val chatMessage = json.decodeFromString<ChatMessageResponse>(body)
                            _chatMessages.emit(chatMessage)
                        }
                        else -> {
                            Log.w("NurseSocketClient", "No handler for destination: $destination")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("NurseSocketClient", "Decoding failed for $destination", e)
                }
            } else if (frame.command == StompCommand.ERROR) {
                Log.e("NurseSocketClient", "Terminal STOMP ERROR: ${frame.headers["message"]}")
            }
        }
    }

    override suspend fun sendHeartbeat() {
        stompClient.send(DEST_APP_HEARTBEAT)
    }

    override suspend fun updateAvailability(available: Boolean, lat: Double?, lng: Double?) {
        isAvailableState = available
        lastKnownLat = lat
        lastKnownLng = lng

        if (stompClient.connectionState.value != SocketConnectionState.Connected) {
            kotlinx.coroutines.withTimeoutOrNull(5000.milliseconds) {
                stompClient.connectionState.first { it == SocketConnectionState.Connected }
            }
        }

        if (stompClient.connectionState.value == SocketConnectionState.Connected) {
            val payload = json.encodeToString(AvailabilityRequest(available, lat, lng))
            stompClient.send(DEST_APP_AVAILABILITY, payload)
        }
    }

    override suspend fun updateLocation(lat: Double, lng: Double) {
        val payload = json.encodeToString(LocationUpdateRequest(lat, lng))
        stompClient.send(DEST_APP_LOCATION, payload)
    }

    override suspend fun createOffer(
        serviceRequestId: String,
        proposedPrice: Double,
        proposedDate: String,
        proposedTime: String,
        message: String?
    ) {
        val req = CreateOfferRequest(serviceRequestId, proposedPrice, proposedDate, proposedTime, message)
        stompClient.send(DEST_APP_OFFER_CREATE, json.encodeToString(req))
    }

    override suspend fun updateOffer(
        offerId: String,
        proposedPrice: Double?,
        proposedDate: String?,
        proposedTime: String?,
        message: String?
    ) {
        val req = UpdateOfferRequest(offerId, proposedPrice, proposedDate, proposedTime, message)
        stompClient.send(DEST_APP_OFFER_UPDATE, json.encodeToString(req))
    }

    override suspend fun acceptOffer(offerId: String) {
        val req = AcceptOfferRequest(offerId)
        stompClient.send(DEST_APP_OFFER_ACCEPT, json.encodeToString(req))
    }

    override suspend fun withdrawOffer(offerId: String) {
        val req = WithdrawOfferRequest(offerId)
        stompClient.send(DEST_APP_OFFER_WITHDRAW, json.encodeToString(req))
    }

    override suspend fun cancelReservation(serviceRequestId: String) {
        val req = CancelReservationRequest(serviceRequestId)
        stompClient.send(DEST_APP_CANCEL, json.encodeToString(req))
    }

    override suspend fun requestOffersList(serviceRequestId: String) {
        val req = ListOffersRequest(serviceRequestId)
        stompClient.send(DEST_APP_OFFERS_LIST, json.encodeToString(req))
    }

    override suspend fun sendChatMessage(reservationId: String, content: String) {
        val req = SendChatMessageRequest(content)
        val dest = "$DEST_APP_CHAT_PREFIX/$reservationId/send"
        stompClient.send(dest, json.encodeToString(req))
    }

    override suspend fun subscribeToReservation(reservationId: String) {
        activeReservationSubscriptions.add(reservationId)
        if (connectionState.value == SocketConnectionState.Connected) {
            stompClient.subscribe("$DEST_TOPIC_RESERVATION_PREFIX/$reservationId")
        }
    }

    override suspend fun unsubscribeFromReservation(reservationId: String) {
        activeReservationSubscriptions.remove(reservationId)
        // Topic unsubscribing is tracked automatically by server/client
    }

    override suspend fun subscribeToChat(reservationId: String) {
        activeChatSubscriptions.add(reservationId)
        if (connectionState.value == SocketConnectionState.Connected) {
            stompClient.subscribe("$DEST_TOPIC_CHAT_PREFIX/$reservationId")
        }
    }

    override suspend fun unsubscribeFromChat(reservationId: String) {
        activeChatSubscriptions.remove(reservationId)
    }

    private fun resolveWebSocketUrl(baseUrl: String): String {
        val trimmed = baseUrl.trimEnd('/')
        val wsScheme = if (trimmed.startsWith("https")) "wss" else "ws"
        val pathStripped = trimmed.replace(Regex("^https?://"), "")
        return "$wsScheme://$pathStripped/ws"
    }

    companion object {
        private const val DEST_USER_NOTIFICATIONS = "/user/queue/notifications"
        private const val DEST_USER_ERRORS = "/user/queue/errors"
        private const val DEST_USER_NEARBY_REQUEST = "/user/queue/nearby-request"

        private const val DEST_TOPIC_RESERVATION_PREFIX = "/topic/reservation"
        private const val DEST_TOPIC_CHAT_PREFIX = "/topic/chat"

        private const val DEST_APP_HEARTBEAT = "/app/heartbeat"
        private const val DEST_APP_AVAILABILITY = "/app/reservation/availability"
        private const val DEST_APP_LOCATION = "/app/reservation/location"

        private const val DEST_APP_OFFER_CREATE = "/app/reservation/offer/create"
        private const val DEST_APP_OFFER_UPDATE = "/app/reservation/offer/update"
        private const val DEST_APP_OFFER_ACCEPT = "/app/reservation/offer/accept"
        private const val DEST_APP_OFFER_WITHDRAW = "/app/reservation/offer/withdraw"
        private const val DEST_APP_CANCEL = "/app/reservation/cancel"
        private const val DEST_APP_OFFERS_LIST = "/app/reservation/offers/list"
        private const val DEST_APP_CHAT_PREFIX = "/app/chat"
    }
}
