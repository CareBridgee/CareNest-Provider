package com.carenest.provider.earnings.domain.usecase

import com.carenest.provider.earnings.domain.model.EarningsSummary
import com.carenest.provider.earnings.domain.model.ServiceEarningItem
import com.carenest.provider.earnings.domain.repository.EarningsRepository
import javax.inject.Inject

class GetServiceEarningsUseCase @Inject constructor(
    private val repository: EarningsRepository
) {
    suspend fun getSummary(): Result<EarningsSummary> = repository.getEarningsSummary()

    suspend fun getEarningsList(): Result<List<ServiceEarningItem>> = repository.getServiceEarnings()
}
