package com.carenest.request.domain.usecase

import com.carenest.request.domain.model.VisitCode
import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class GenerateVisitCodeUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(requestId: String): Result<VisitCode> =
        runCatching { repository.generateVisitCode(requestId) }
}
