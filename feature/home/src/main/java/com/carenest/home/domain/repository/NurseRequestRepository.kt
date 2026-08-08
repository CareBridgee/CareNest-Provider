package com.carenest.home.domain.repository

import com.carenest.home.domain.model.EarningsSummary
import com.carenest.home.domain.model.NurseProfile
import com.carenest.home.domain.model.NurseRequest
import com.carenest.provider.core.network.socket.model.ReservationEvent
import kotlinx.coroutines.flow.Flow

interface NurseRequestsRepository {
    suspend fun fetchIncomingRequests(): List<NurseRequest>
    suspend fun createOffer(
        serviceRequestId: String,
        proposedPrice: Double,
        proposedDate: String,
        proposedTime: String,
        message: String? = null
    )
    fun listenReservationEvents(reservationId: String): Flow<ReservationEvent>
    suspend fun fetchEarningsSummary(): EarningsSummary
    suspend fun getNurseProfile(): NurseProfile
}