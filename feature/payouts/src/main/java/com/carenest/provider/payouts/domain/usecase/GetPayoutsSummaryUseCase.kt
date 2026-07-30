package com.carenest.provider.payouts.domain.usecase

import com.carenest.provider.payouts.domain.model.PayoutItem
import com.carenest.provider.payouts.domain.model.PayoutSummary
import com.carenest.provider.payouts.domain.repository.PayoutsRepository
import javax.inject.Inject

class GetPayoutsSummaryUseCase @Inject constructor(
    private val repository: PayoutsRepository
) {
    suspend fun getSummary(): Result<PayoutSummary> = repository.getPayoutSummary()

    suspend fun getHistory(): Result<List<PayoutItem>> = repository.getWithdrawHistory()
}
