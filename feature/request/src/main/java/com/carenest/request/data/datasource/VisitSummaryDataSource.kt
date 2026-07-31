package com.carenest.request.data.datasource

import com.carenest.request.domain.model.VisitSummary

interface VisitSummaryDataSource {
    suspend fun getVisitSummary(requestId: String): VisitSummary
    suspend fun submitRating(requestId: String, rating: Int, comment: String?)
}