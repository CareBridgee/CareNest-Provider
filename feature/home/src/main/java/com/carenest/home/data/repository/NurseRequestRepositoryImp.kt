package com.carenest.home.data.repository

import com.carenest.home.data.datasource.NurseRequestsDataSource
import com.carenest.home.data.mapper.toDomain
import com.carenest.home.domain.model.EarningsSummary
import com.carenest.home.domain.model.NurseProfile
import com.carenest.home.domain.model.NurseRequest
import com.carenest.home.domain.model.ServiceRequestPreview
import com.carenest.home.domain.repository.NurseRequestsRepository
import com.carenest.provider.core.network.socket.model.ReservationEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NurseRequestsRepositoryImpl @Inject constructor(
    private val dataSource: NurseRequestsDataSource,
) : NurseRequestsRepository {

    override suspend fun fetchIncomingRequests(): List<NurseRequest> =
        dataSource.getIncomingRequests()

    override suspend fun createOffer(
        serviceRequestId: String,
        proposedPrice: Double,
        proposedDate: String,
        proposedTime: String,
        message: String?
    ) {
        dataSource.createOffer(serviceRequestId, proposedPrice, proposedDate, proposedTime, message)
    }

    override fun listenReservationEvents(reservationId: String): Flow<ReservationEvent> =
        dataSource.listenReservationEvents(reservationId)

    override suspend fun fetchEarningsSummary(): EarningsSummary =
        dataSource.getEarningsSummary()

    override suspend fun getNurseProfile(): NurseProfile =
        dataSource.getNurseProfile()

    override suspend fun getServiceRequestPreview(serviceRequestId: String): ServiceRequestPreview =
        dataSource.getServiceRequestPreview(serviceRequestId).toDomain()
}