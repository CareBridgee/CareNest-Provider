package com.carenest.request.domain.usecase


import com.carenest.request.domain.model.NurseRequest
import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class GetIncomingRequestsUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(): Result<List<NurseRequest>> =
        runCatching { repository.fetchIncomingRequests() }
}