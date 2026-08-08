package com.carenest.request.domain.usecase

import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class CancelRequestUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(requestId: String): Result<Boolean> =
        runCatching { repository.cancelRequest(requestId) }
}
