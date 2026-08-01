package com.carenest.request.domain.usecase


import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class GetIncomingRequestsUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(): Result<List<Request>> =
        runCatching { repository.fetchIncomingRequests() }
}