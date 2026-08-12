package com.carenest.request.domain.usecase

import com.carenest.request.domain.model.PatientMedicalSummary
import com.carenest.request.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class GetPatientSummaryUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(requestId: String): Result<PatientMedicalSummary> =
        runCatching { repository.fetchPatientSummary(requestId) }
}
