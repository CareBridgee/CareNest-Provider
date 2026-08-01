package com.carenest.request.data.datasource

import com.carenest.request.domain.model.VisitSummary
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeVisitSummaryDataSource @Inject constructor() : VisitSummaryDataSource {
    override suspend fun getVisitSummary(requestId: String): VisitSummary {
        delay(800)
        return VisitSummary(
            requestId = requestId,
            professionalName = "Dr. Sarah Mitchell",
            serviceType = "Post-Op Wound Care",
            durationMinutes = 45,
            completedDate = "Nov 24, 2024",
            totalAmount = 95.0,
            isVerified = true
        )
    }

    override suspend fun submitRating(requestId: String, rating: Int, comment: String?) {
        delay(500)
    }
}