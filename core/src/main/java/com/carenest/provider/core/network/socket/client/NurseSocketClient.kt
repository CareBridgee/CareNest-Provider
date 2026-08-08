package com.carenest.provider.core.network.socket.client

import com.carenest.provider.core.network.socket.model.ChatMessageResponse
import com.carenest.provider.core.network.socket.model.NearbyNurseServiceRequestResponse
import com.carenest.provider.core.network.socket.model.NotificationResponse
import com.carenest.provider.core.network.socket.model.ReservationEvent
import com.carenest.provider.core.network.socket.model.SocketConnectionState
import com.carenest.provider.core.network.socket.model.SocketErrorPayload
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface NurseSocketClient {
    val connectionState: StateFlow<SocketConnectionState>

    val notifications: SharedFlow<NotificationResponse>
    val socketErrors: SharedFlow<SocketErrorPayload>
    val nearbyRequests: SharedFlow<NearbyNurseServiceRequestResponse>
    val reservationEvents: SharedFlow<ReservationEvent>
    val chatMessages: SharedFlow<ChatMessageResponse>

    fun connect()
    fun disconnect()

    suspend fun sendHeartbeat()
    suspend fun updateAvailability(available: Boolean, lat: Double? = null, lng: Double? = null)
    suspend fun updateLocation(lat: Double, lng: Double)

    suspend fun createOffer(
        serviceRequestId: String,
        proposedPrice: Double,
        proposedDate: String,
        proposedTime: String,
        message: String? = null
    )

    suspend fun updateOffer(
        offerId: String,
        proposedPrice: Double? = null,
        proposedDate: String? = null,
        proposedTime: String? = null,
        message: String? = null
    )

    suspend fun acceptOffer(offerId: String)
    suspend fun withdrawOffer(offerId: String)
    suspend fun cancelReservation(serviceRequestId: String)
    suspend fun requestOffersList(serviceRequestId: String)

    suspend fun sendChatMessage(reservationId: String, content: String)

    suspend fun subscribeToReservation(reservationId: String)
    suspend fun unsubscribeFromReservation(reservationId: String)

    suspend fun subscribeToChat(reservationId: String)
    suspend fun unsubscribeFromChat(reservationId: String)
}
