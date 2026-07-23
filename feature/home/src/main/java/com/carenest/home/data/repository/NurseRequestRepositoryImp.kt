package com.carenest.home.data.repository

import com.carenest.home.data.datasource.NurseRequestsDataSource
import com.carenest.home.domain.model.EarningsSummary
import com.carenest.home.domain.model.NurseProfile
import com.carenest.home.domain.model.NurseRequest
import com.carenest.home.domain.repository.NurseRequestsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NurseRequestsRepositoryImpl @Inject constructor(
    private val dataSource: NurseRequestsDataSource,
) : NurseRequestsRepository {

    override suspend fun fetchIncomingRequests(): List<NurseRequest> =
        dataSource.getIncomingRequests()

    override suspend fun fetchEarningsSummary(): EarningsSummary =
        dataSource.getEarningsSummary()

    override suspend fun getNurseProfile(): NurseProfile =
        dataSource.getNurseProfile()

    override fun sendOfferToPatient(requestId: String): Pair<Boolean, Int> =
        dataSource.sendOfferToPatient(requestId)
}