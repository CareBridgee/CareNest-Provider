package com.carenest.request.domain.usecase

import com.carenest.request.domain.repository.VisitSummaryRepository
import javax.inject.Inject

class SubmitVisitRatingUseCase @Inject constructor(
    private val repository: VisitSummaryRepository,
) {
    suspend operator fun invoke(requestId: String, rating: Int, comment: String? = null): Result<Unit> =
        repository.submitRating(requestId, rating, comment)
}