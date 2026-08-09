package com.carenest.request.domain.usecase

import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class CompleteServiceRequestUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(requestId: String, visitCode: String): Result<Unit> = runCatching {
        repository.completeRequest(requestId, visitCode)
        Unit
    }
}
