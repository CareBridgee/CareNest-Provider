package com.carenest.request.data.datasource

import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request
import com.carenest.provider.core.network.socket.model.ReservationEvent
import kotlinx.coroutines.flow.Flow

interface NurseRequestsDataSource {
    suspend fun getIncomingRequests(): List<Request>
    suspend fun getRequestContract(requestId: String): Offer
    suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String
    ): Boolean

    suspend fun completeRequest(serviceRequestId: String, visitCode: String): Boolean
    suspend fun createOffer(requestId: String, proposedPrice: Double, message: String? = null)
    suspend fun withdrawOffer(offerId: String)
    fun listenReservationEvents(reservationId: String): Flow<ReservationEvent>
}