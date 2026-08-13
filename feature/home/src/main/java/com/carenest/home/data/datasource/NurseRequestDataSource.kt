package com.carenest.home.data.datasource

import com.carenest.home.data.ServiceRequestPreviewDto
import com.carenest.home.domain.model.EarningsSummary
import com.carenest.home.domain.model.NurseProfile
import com.carenest.home.domain.model.NurseRequest
import com.carenest.provider.core.network.socket.model.ReservationEvent
import kotlinx.coroutines.flow.Flow

interface NurseRequestsDataSource {
    suspend fun getIncomingRequests(): List<NurseRequest>
    suspend fun createOffer(
        serviceRequestId: String,
        proposedPrice: Double,
        proposedDate: String,
        proposedTime: String,
        message: String? = null
    )
    fun listenReservationEvents(reservationId: String): Flow<ReservationEvent>
    suspend fun getEarningsSummary(): EarningsSummary
    suspend fun getNurseProfile(): NurseProfile
    suspend fun getServiceRequestPreview(serviceRequestId: String): ServiceRequestPreviewDto
}