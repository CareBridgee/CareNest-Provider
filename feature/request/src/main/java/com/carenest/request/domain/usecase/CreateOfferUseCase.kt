package com.carenest.request.domain.usecase

import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class CreateOfferUseCase @Inject constructor(
    private val repository: NurseRequestsRepository
) {
    suspend operator fun invoke(requestId: String, proposedPrice: Double, message: String? = null) {
        repository.createOffer(requestId, proposedPrice, message)
    }
}
