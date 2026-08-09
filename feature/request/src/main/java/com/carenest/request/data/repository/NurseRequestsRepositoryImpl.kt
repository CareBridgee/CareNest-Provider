package com.carenest.request.data.repository

import com.carenest.provider.core.network.socket.model.ReservationEvent
import com.carenest.request.data.datasource.NurseRequestsDataSource
import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.repository.NurseRequestsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NurseRequestsRepositoryImpl @Inject constructor(
    private val dataSource: NurseRequestsDataSource
) : NurseRequestsRepository {

    override suspend fun fetchIncomingRequests(): List<Request> =
        dataSource.getIncomingRequests()


    override suspend fun fetchRequestContract(requestId: String): Offer =
        dataSource.getRequestContract(requestId)

    override suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String
    ): Boolean = dataSource.cancelRequest(requestId, reason, note)

    override suspend fun completeRequest(serviceRequestId: String, visitCode: String): Boolean =
        dataSource.completeRequest(serviceRequestId, visitCode)

    override suspend fun createOffer(requestId: String, proposedPrice: Double, message: String?) {
        dataSource.createOffer(requestId, proposedPrice, message)
    }

    override suspend fun withdrawOffer(offerId: String) {
        dataSource.withdrawOffer(offerId)
    }

    override fun listenReservationEvents(reservationId: String): Flow<ReservationEvent> =
        dataSource.listenReservationEvents(reservationId)
}