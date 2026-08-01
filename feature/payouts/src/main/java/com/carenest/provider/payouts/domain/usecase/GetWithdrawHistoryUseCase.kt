package com.carenest.provider.payouts.domain.usecase

import com.carenest.provider.payouts.domain.model.PayoutItem
import com.carenest.provider.payouts.domain.repository.PayoutsRepository
import javax.inject.Inject

class GetWithdrawHistoryUseCase @Inject constructor(
    private val repository: PayoutsRepository
) {
    suspend operator fun invoke(): Result<List<PayoutItem>> = repository.getWithdrawHistory()
}
