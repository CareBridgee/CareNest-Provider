package com.carenest.request.data.repository

import com.carenest.request.data.mapper.toVisitSummary
import com.carenest.request.data.remote.RequestRemoteDataSource
import com.carenest.request.domain.model.VisitSummary
import com.carenest.request.domain.repository.VisitSummaryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitSummaryRepositoryImpl @Inject constructor(
    private val remoteDataSource: RequestRemoteDataSource,
) : VisitSummaryRepository {
    override suspend fun getVisitSummary(requestId: String): Result<VisitSummary> = runCatching {
        val details = remoteDataSource.getServiceRequestDetails(requestId)
        val embeddedAcceptedOffer = details.offers.firstOrNull {
            it.status.equals("ACCEPTED", ignoreCase = true)
        } ?: details.offers.singleOrNull()
        details.toVisitSummary(requestId, embeddedAcceptedOffer)
    }

    override suspend fun submitRating(
        requestId: String,
        rating: Int,
        comment: String?
    ): Result<Unit> = Result.success(Unit) // Dummy/local until a provider-to-patient rating API exists.
}
