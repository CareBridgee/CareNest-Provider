package com.carenest.provider.payouts.domain.repository

import com.carenest.provider.payouts.domain.model.PayoutItem
import com.carenest.provider.payouts.domain.model.PayoutSummary

interface PayoutsRepository {
    suspend fun getPayoutSummary(): Result<PayoutSummary>
    suspend fun getWithdrawHistory(): Result<List<PayoutItem>>
    suspend fun requestWithdrawal(amount: String): Result<Unit>
}
