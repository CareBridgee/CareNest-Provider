package com.carenest.home.domain.usecase


import com.carenest.home.domain.model.NurseRequest
import com.carenest.home.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class GetIncomingRequestsUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(): Result<List<NurseRequest>> =
        runCatching { repository.fetchIncomingRequests() }
}