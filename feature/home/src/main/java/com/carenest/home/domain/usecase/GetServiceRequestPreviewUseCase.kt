package com.carenest.home.domain.usecase

import com.carenest.home.domain.model.ServiceRequestPreview
import com.carenest.home.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class GetServiceRequestPreviewUseCase @Inject constructor(
    private val repository: NurseRequestsRepository
) {
    suspend operator fun invoke(serviceRequestId: String): ServiceRequestPreview =
        repository.getServiceRequestPreview(serviceRequestId)
}