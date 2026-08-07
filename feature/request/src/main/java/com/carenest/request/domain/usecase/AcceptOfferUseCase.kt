package com.carenest.request.domain.usecase

import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class AcceptOfferUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(offerId: String): Result<Unit> = runCatching {
        repository.acceptOffer(offerId)
        Unit
    }
}
