package com.carenest.request.domain.repository

import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.PatientMedicalSummary
import com.carenest.request.domain.model.Request
import com.carenest.provider.core.network.socket.model.ReservationEvent
import kotlinx.coroutines.flow.Flow

interface NurseRequestsRepository {
    suspend fun fetchIncomingRequests(): List<Request>
    suspend fun fetchRequestContract(requestId: String): Offer
    suspend fun fetchPatientSummary(requestId: String): PatientMedicalSummary
    suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String,
    ): Boolean
    suspend fun completeRequest(serviceRequestId: String, visitCode: String): Boolean
    suspend fun createOffer(requestId: String, proposedPrice: Double, message: String? = null)
    suspend fun withdrawOffer(offerId: String)
    fun listenReservationEvents(reservationId: String): Flow<ReservationEvent>
    suspend fun acceptOffer(offerId: String): Boolean
}
