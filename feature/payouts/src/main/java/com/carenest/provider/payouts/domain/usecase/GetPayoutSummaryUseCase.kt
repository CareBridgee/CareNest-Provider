package com.carenest.provider.payouts.domain.usecase

import com.carenest.provider.payouts.domain.model.PayoutSummary
import com.carenest.provider.payouts.domain.repository.PayoutsRepository
import javax.inject.Inject

class GetPayoutSummaryUseCase @Inject constructor(
    private val repository: PayoutsRepository
) {
    suspend operator fun invoke(): Result<PayoutSummary> = repository.getPayoutSummary()
}
