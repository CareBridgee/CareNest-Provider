package com.carenest.provider.earnings.domain.usecase

import com.carenest.provider.earnings.domain.model.EarningsSummary
import com.carenest.provider.earnings.domain.repository.EarningsRepository
import javax.inject.Inject

class GetEarningsSummaryUseCase @Inject constructor(
    private val repository: EarningsRepository
) {
    suspend operator fun invoke(): Result<EarningsSummary> = repository.getEarningsSummary()
}
