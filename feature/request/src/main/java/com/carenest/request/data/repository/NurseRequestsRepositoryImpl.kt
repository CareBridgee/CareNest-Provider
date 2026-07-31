package com.carenest.request.data.repository

import com.carenest.request.data.datasource.NurseRequestsDataSource
import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NurseRequestsRepositoryImpl @Inject constructor(
    private val dataSource: NurseRequestsDataSource
) : NurseRequestsRepository {

    override suspend fun fetchIncomingRequests(): List<Request> =
        dataSource.getIncomingRequests()

    override fun sendOfferToPatient(requestId: String): Pair<Boolean, Int> =
        dataSource.sendOfferToPatient(requestId)

    override suspend fun fetchRequestContract(requestId: String): Offer =
        dataSource.getRequestContract(requestId)

    override suspend fun cancelRequest(
        requestId: String,
        reason: CancellationReason,
        note: String
    ): Boolean = dataSource.cancelRequest(requestId, reason, note)
}