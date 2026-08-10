package com.carenest.home.domain.usecase

import com.carenest.home.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class SendOfferToPatientUseCase @Inject constructor(
    private val repository: NurseRequestsRepository
) {
    suspend operator fun invoke(
        requestId: String,
        proposedPrice: Double,
        proposedDate: String = "2026-08-15",
        proposedTime: String = "10:00",
        message: String? = null
    ) {
        repository.createOffer(requestId, proposedPrice, proposedDate, proposedTime, message)
    }
}