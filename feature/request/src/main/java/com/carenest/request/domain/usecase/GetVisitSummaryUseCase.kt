package com.carenest.request.domain.usecase

import com.carenest.request.domain.model.VisitSummary
import com.carenest.request.domain.repository.VisitSummaryRepository
import javax.inject.Inject

class GetVisitSummaryUseCase @Inject constructor(
    private val repository: VisitSummaryRepository,
) {
    suspend operator fun invoke(requestId: String): Result<VisitSummary> =
        repository.getVisitSummary(requestId)
}