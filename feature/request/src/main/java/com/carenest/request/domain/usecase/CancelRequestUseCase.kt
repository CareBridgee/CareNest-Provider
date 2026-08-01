package com.carenest.request.domain.usecase

import com.carenest.request.domain.model.CancellationReason
import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class CancelRequestUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(
        requestId: String,
        reason: CancellationReason,
        note: String,
    ): Result<Boolean> = runCatching { repository.cancelRequest(requestId, reason, note) }
}
