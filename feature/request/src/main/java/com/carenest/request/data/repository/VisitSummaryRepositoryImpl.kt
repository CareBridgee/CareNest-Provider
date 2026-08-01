package com.carenest.request.data.repository

import com.carenest.request.data.datasource.VisitSummaryDataSource
import com.carenest.request.domain.model.VisitSummary
import com.carenest.request.domain.repository.VisitSummaryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitSummaryRepositoryImpl @Inject constructor(
    private val dataSource: VisitSummaryDataSource
) : VisitSummaryRepository {
    override suspend fun getVisitSummary(requestId: String): Result<VisitSummary> =
        runCatching { dataSource.getVisitSummary(requestId) }

    override suspend fun submitRating(
        requestId: String,
        rating: Int,
        comment: String?
    ): Result<Unit> = runCatching { dataSource.submitRating(requestId, rating, comment) }
}