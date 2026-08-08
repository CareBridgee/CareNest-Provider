package com.carenest.request.domain.usecase

import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class CompleteRequestUseCase @Inject constructor(
    private val repository: NurseRequestsRepository
) {
    suspend operator fun invoke(serviceRequestId: String, visitCode: String): Result<Boolean> {
        return try {
            val result = repository.completeRequest(serviceRequestId, visitCode)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
