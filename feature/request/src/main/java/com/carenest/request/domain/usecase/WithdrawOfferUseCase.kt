package com.carenest.request.domain.usecase

import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class WithdrawOfferUseCase @Inject constructor(
    private val repository: NurseRequestsRepository
) {
    suspend operator fun invoke(offerId: String) {
        repository.withdrawOffer(offerId)
    }
}