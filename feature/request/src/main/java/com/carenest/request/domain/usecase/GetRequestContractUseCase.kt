package com.carenest.request.domain.usecase

import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class GetRequestContractUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(requestId: String): Result<Offer> =
        runCatching { repository.fetchRequestContract(requestId) }
}
