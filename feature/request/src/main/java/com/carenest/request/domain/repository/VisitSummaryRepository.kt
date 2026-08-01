package com.carenest.request.domain.repository

import com.carenest.request.domain.model.VisitSummary


interface VisitSummaryRepository {
    suspend fun getVisitSummary(requestId: String): Result<VisitSummary>
    suspend fun submitRating(requestId: String, rating: Int, comment: String?): Result<Unit>
}