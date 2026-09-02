package com.carenest.provider.core.network.socket.client

import android.util.Log
import com.carenest.provider.core.BuildConfig
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.datastore.AuthenticationCredentials
import com.carenest.provider.core.network.AuthenticationRecoveryResult
import com.carenest.provider.core.network.AuthenticationRefreshCoordinator
import com.carenest.provider.core.network.requestTokenRefresh
import com.carenest.provider.core.network.socket.model.AcceptOfferRequest
import com.carenest.provider.core.network.socket.model.AvailabilityRequest
import com.carenest.provider.core.network.socket.model.CancelReservationRequest
import com.carenest.provider.core.network.socket.model.ChatMessageResponse
import com.carenest.provider.core.network.socket.model.CreateOfferRequest
import com.carenest.provider.core.network.socket.model.ListOffersRequest
import com.carenest.provider.core.network.socket.model.LocationUpdateRequest
import com.carenest.provider.core.network.socket.model.NearbyNurseServiceRequestResponse
import com.carenest.provider.core.network.socket.model.NotificationResponse
import com.carenest.provider.core.network.socket.model.ReservationEvent
import com.carenest.provider.core.network.socket.model.ReservationEventType
import com.carenest.provider.core.network.socket.model.SendChatMessageRequest
import com.carenest.provider.core.network.socket.model.SocketConnectionState
import com.carenest.provider.core.network.socket.model.SocketErrorPayload
import com.carenest.provider.core.network.socket.model.UpdateOfferRequest
import com.carenest.provider.core.network.socket.model.WithdrawOfferRequest
import com.carenest.provider.core.network.socket.stomp.StompClient
import com.carenest.provider.core.network.socket.stomp.StompCommand
import com.carenest.provider.core.network.socket.stomp.StompConnectResult
import com.carenest.provider.core.network.socket.stomp.indicatesSocketAuthenticationFailure
import io.ktor.client.HttpClient
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class NurseSocketClientImpl @Inject constructor(
    private val stompClient: StompClient,
    private val tokenManager: AuthenticationSessionStore,
    private val authenticationRefreshCoordinator: AuthenticationRefreshCoordinator,
    private val httpClient: HttpClient,
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
    private var sessionObserverJob: Job? = null

    private var isExplicitlyDisconnected = false
    @Volatile
    private var activeSocketCredentials: AuthenticationCredentials? = null

    override fun connect() {
        Log.i("NurseSocketClient", "Connect requested (isExplicitlyDisconnected=$isExplicitlyDisconnected)")
        isExplicitlyDisconnected = false
        if (connectionManagerJob?.isActive == true) {
            Log.d("NurseSocketClient", "Connection manager already active")
            return
        }

        startSessionObserver()

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

        sessionObserverJob?.cancel()
        sessionObserverJob = null
        activeSocketCredentials = null

        scope.launch {
            stompClient.disconnect()
        }
    }

    private suspend fun manageConnectionLifecycle() {
        var attempt = 0
        val wsUrl = resolveWebSocketUrl(BuildConfig.BASE_URL)

        while (!isExplicitlyDisconnected) {
            var credentials = tokenManager.state.first().credentials
            val token = credentials?.accessToken.orEmpty()
            if (token.isBlank()) {
                Log.w("NurseSocketClient", "No access token available, waiting...")
                delay(2000.milliseconds)
                continue
            }

            Log.d("NurseSocketClient", "Attempting connection (attempt=$attempt)")
            activeSocketCredentials = credentials
            val connectResult = stompClient.connect(wsUrl, token)
            val latestCredentials = tokenManager.state.first().credentials
            if (latestCredentials != credentials) {
                if (latestCredentials?.sessionId != credentials?.sessionId) {
                    clearSessionBoundSocketState()
                    activeSocketCredentials = null
                    stompClient.disconnect()
                    continue
                }
                // Same-account rotation while connecting: keep the established
                // socket and adopt the latest credentials instead of bouncing it.
                credentials = latestCredentials
                activeSocketCredentials = latestCredentials
            }

            when (connectResult) {
                StompConnectResult.CONNECTED -> {
                    Log.i("NurseSocketClient", "Successfully connected to STOMP")
                    val connectedAt = System.currentTimeMillis()
                    onConnected()
                    // Wait until state changes from Connected
                    val disconnectedState = stompClient.connectionState.first {
                        it != SocketConnectionState.Connected
                    }
                    val connectionDuration = System.currentTimeMillis() - connectedAt
                    Log.w("NurseSocketClient", "Socket connection lost after ${connectionDuration}ms, restarting lifecycle")

                    heartbeatJob?.cancel()
                    heartbeatJob = null
                    activeSocketCredentials = null

                    if (disconnectedState is SocketConnectionState.Error) {
                        _socketErrors.emit(
                            SocketErrorPayload(
                                code = "SOCKET_DISCONNECTED",
                                message = disconnectedState.message
                            )
                        )
                    }

                    if (connectionDuration < 5000L) {
                        attempt++
                        Log.w("NurseSocketClient", "Rapid disconnect detected (${connectionDuration}ms); purging pending dynamic topic subscriptions to prevent reconnect loops")
                        activeReservationSubscriptions.clear()
                        activeChatSubscriptions.clear()
                    } else {
                        attempt = 1
                    }

                    if (
                        disconnectedState is SocketConnectionState.Error &&
                        disconnectedState.message.indicatesSocketAuthenticationFailure()
                    ) {
                        val recovered = recoverSocketAuthentication(credentials)
                        if (!recovered) {
                            val backoffMs = connectionBackoff(attempt)
                            Log.w("NurseSocketClient", "Auth recovery failed; waiting ${backoffMs}ms before retry")
                            delay(backoffMs)
                        } else {
                            attempt = 0
                        }
                    } else {
                        val backoffMs = connectionBackoff(attempt)
                        Log.w("NurseSocketClient", "Reconnecting; backing off ${backoffMs}ms before attempt $attempt")
                        delay(backoffMs)
                    }
                }

                StompConnectResult.AUTHENTICATION_FAILED -> {
                    activeSocketCredentials = null
                    val recovered = recoverSocketAuthentication(credentials)
                    if (!recovered) {
                        attempt++
                        delay(connectionBackoff(attempt))
                    } else {
                        attempt = 0
                    }
                }

                StompConnectResult.FAILED -> {
                    activeSocketCredentials = null
                    attempt++
                    val backoffMs = connectionBackoff(attempt)
                    Log.e("NurseSocketClient", "Connection failed, retrying in ${backoffMs}ms")
                    delay(backoffMs)
                }
            }
        }
        Log.i("NurseSocketClient", "Connection manager lifecycle ended (explicitly disconnected)")
    }

    private fun startSessionObserver() {
        if (sessionObserverJob?.isActive == true) return

        sessionObserverJob = scope.launch {
            tokenManager.state
                .map { it.credentials }
                .distinctUntilChanged()
                .collect { currentCredentials ->
                    val socketCredentials = activeSocketCredentials ?: return@collect
                    if (currentCredentials == socketCredentials) return@collect

                    val accountChanged = currentCredentials?.sessionId != socketCredentials.sessionId
                    if (currentCredentials == null || accountChanged) {
                        clearSessionBoundSocketState()
                        Log.i("NurseSocketClient", "Account changed; reconnecting socket")
                        stompClient.disconnect()
                        return@collect
                    }

                    // Routine same-account token rotation: adopt the new credentials
                    // silently - a live STOMP connection survives access-token rotation.
                    activeSocketCredentials = currentCredentials
                }
        }
    }

    private suspend fun recoverSocketAuthentication(
        failedCredentials: AuthenticationCredentials?,
    ): Boolean {
        if (failedCredentials == null) return false

        return when (
            authenticationRefreshCoordinator.recover(failedCredentials) { refreshToken ->
                httpClient.requestTokenRefresh(refreshToken)
            }
        ) {
            AuthenticationRecoveryResult.RECOVERED,
            AuthenticationRecoveryResult.SESSION_CHANGED,
            -> true

            AuthenticationRecoveryResult.REJECTED,
            AuthenticationRecoveryResult.TEMPORARILY_UNAVAILABLE,
            -> false
        }
    }

    private fun clearSessionBoundSocketState() {
        activeReservationSubscriptions.clear()
        activeChatSubscriptions.clear()
        isAvailableState = false
        lastKnownLat = null
        lastKnownLng = null
    }

    private fun connectionBackoff(attempt: Int): Long =
        (1000L * (1 shl minOf(attempt, 5))).coerceAtMost(30_000L)

    private fun String?.isNull_OrEmpty(): Boolean = this == null || this.trim().isEmpty()

    private var isAvailableState: Boolean = false
    private var lastKnownLat: Double? = null
    private var lastKnownLng: Double? = null

    private suspend fun onConnected() {
        Log.d("NurseSocketClient", "onConnected: Subscribing to default topics")
        // Subscribe to default nurse topics
        runCatching { stompClient.subscribe(DEST_USER_NOTIFICATIONS) }
        runCatching { stompClient.subscribe(DEST_USER_ERRORS) }
        runCatching { stompClient.subscribe(DEST_USER_NEARBY_REQUEST) }

        // Restore dynamic topic subscriptions
        val reservationSubs = activeReservationSubscriptions.toList()
        reservationSubs.forEach { reservationId ->
            Log.d("NurseSocketClient", "Restoring reservation subscription: $reservationId")
            runCatching { stompClient.subscribe("$DEST_TOPIC_RESERVATION_PREFIX/$reservationId") }
        }

        val chatSubs = activeChatSubscriptions.toList()
        chatSubs.forEach { reservationId ->
            Log.d("NurseSocketClient", "Restoring chat subscription: $reservationId")
            runCatching { stompClient.subscribe("$DEST_TOPIC_CHAT_PREFIX/$reservationId") }
        }

        if (isAvailableState && lastKnownLat != null && lastKnownLng != null) {
            Log.d("NurseSocketClient", "Restoring availability state: $isAvailableState")
            val payload = json.encodeToString(AvailabilityRequest(true, lastKnownLat, lastKnownLng))
            runCatching { stompClient.send(DEST_APP_AVAILABILITY, payload) }
        } else if (isAvailableState) {
            Log.w("NurseSocketClient", "Skipping availability restore: coordinates missing (lat=$lastKnownLat, lng=$lastKnownLng)")
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

                            val resId = reservationEvent.effectiveReservationId
                            if (!resId.isNullOrEmpty()) {
                                when (reservationEvent.eventType) {
                                    ReservationEventType.OFFER_CREATED,
                                    ReservationEventType.OFFER_UPDATED,
                                    ReservationEventType.OFFER_COUNTERED,
                                    ReservationEventType.OFFER_ACCEPTED -> {
                                        if (activeReservationSubscriptions.add(resId)) {
                                            Log.d("NurseSocketClient", "Confirmed active offer event received; subscribing to topic: $resId")
                                            runCatching { stompClient.subscribe("$DEST_TOPIC_RESERVATION_PREFIX/$resId") }
                                        }
                                    }
                                    ReservationEventType.OFFER_WITHDRAWN,
                                    ReservationEventType.OFFER_REJECTED,
                                    ReservationEventType.REQUEST_CANCELLED,
                                    ReservationEventType.COMPLETED -> {
                                        activeReservationSubscriptions.remove(resId)
                                        activeChatSubscriptions.remove(resId)
                                        Log.i("NurseSocketClient", "Pruned completed/cancelled/rejected reservation topic: $resId")
                                    }
                                    else -> {}
                                }
                            }
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

    private suspend fun ensureConnected(): Boolean {
        if (stompClient.connectionState.value != SocketConnectionState.Connected) {
            kotlinx.coroutines.withTimeoutOrNull(5000.milliseconds) {
                stompClient.connectionState.first { it == SocketConnectionState.Connected }
            }
        }
        return stompClient.connectionState.value == SocketConnectionState.Connected
    }

    override suspend fun updateAvailability(available: Boolean, lat: Double?, lng: Double?) {
        if (available && (lat == null || lng == null)) {
            Log.w("NurseSocketClient", "Cannot set availability to true without location coordinates (lat=$lat, lng=$lng)")
            return
        }
        isAvailableState = available
        lastKnownLat = lat
        lastKnownLng = lng

        if (ensureConnected()) {
            val payload = json.encodeToString(AvailabilityRequest(available, lat, lng))
            stompClient.send(DEST_APP_AVAILABILITY, payload)
        }
    }

    override suspend fun updateLocation(lat: Double, lng: Double) {
        if (ensureConnected()) {
            val payload = json.encodeToString(LocationUpdateRequest(lat, lng))
            stompClient.send(DEST_APP_LOCATION, payload)
        }
    }

    override suspend fun createOffer(
        serviceRequestId: String,
        proposedPrice: Double,
        proposedDate: String,
        proposedTime: String,
        message: String?
    ) {
        if (ensureConnected()) {
            Log.d("NurseSocketClient", "Sending createOffer for $serviceRequestId, price=$proposedPrice, date=$proposedDate, time=$proposedTime")
            val req = CreateOfferRequest(serviceRequestId, proposedPrice, proposedDate, proposedTime, message)
            stompClient.send(DEST_APP_OFFER_CREATE, json.encodeToString(req))
        } else {
            Log.e("NurseSocketClient", "Failed to send createOffer for $serviceRequestId: Socket not connected")
        }
    }

    override suspend fun updateOffer(
        offerId: String,
        proposedPrice: Double?,
        proposedDate: String?,
        proposedTime: String?,
        message: String?
    ) {
        if (ensureConnected()) {
            val req = UpdateOfferRequest(offerId, proposedPrice, proposedDate, proposedTime, message)
            stompClient.send(DEST_APP_OFFER_UPDATE, json.encodeToString(req))
        }
    }

    override suspend fun acceptOffer(offerId: String) {
        if (ensureConnected()) {
            val req = AcceptOfferRequest(offerId)
            stompClient.send(DEST_APP_OFFER_ACCEPT, json.encodeToString(req))
        }
    }

    override suspend fun withdrawOffer(offerId: String) {
        if (ensureConnected()) {
            val req = WithdrawOfferRequest(offerId)
            stompClient.send(DEST_APP_OFFER_WITHDRAW, json.encodeToString(req))
        }
    }

    override suspend fun cancelReservation(serviceRequestId: String) {
        activeReservationSubscriptions.remove(serviceRequestId)
        activeChatSubscriptions.remove(serviceRequestId)
        if (ensureConnected()) {
            val req = CancelReservationRequest(serviceRequestId)
            stompClient.send(DEST_APP_CANCEL, json.encodeToString(req))
        }
    }

    override suspend fun requestOffersList(serviceRequestId: String) {
        if (ensureConnected()) {
            val req = ListOffersRequest(serviceRequestId)
            stompClient.send(DEST_APP_OFFERS_LIST, json.encodeToString(req))
        }
    }

    override suspend fun sendChatMessage(reservationId: String, content: String) {
        if (ensureConnected()) {
            val req = SendChatMessageRequest(content)
            val dest = "$DEST_APP_CHAT_PREFIX/$reservationId/send"
            stompClient.send(dest, json.encodeToString(req))
        }
    }

    override suspend fun subscribeToReservation(reservationId: String) {
        activeReservationSubscriptions.add(reservationId)
        if (connectionState.value == SocketConnectionState.Connected) {
            stompClient.subscribe("$DEST_TOPIC_RESERVATION_PREFIX/$reservationId")
        }
    }

    override suspend fun subscribeToReservationAfterOffer(reservationId: String) {
        Log.d("NurseSocketClient", "Subscribing to reservation topic after offer confirmed: $reservationId")
        subscribeToReservation(reservationId)
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
