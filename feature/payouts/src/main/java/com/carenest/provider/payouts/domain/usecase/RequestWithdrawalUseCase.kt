package com.carenest.provider.payouts.domain.usecase

import com.carenest.provider.payouts.domain.repository.PayoutsRepository
import javax.inject.Inject

class RequestWithdrawalUseCase @Inject constructor(
    private val repository: PayoutsRepository
) {
    suspend operator fun invoke(amount: String): Result<Unit> = repository.requestWithdrawal(amount)
}
