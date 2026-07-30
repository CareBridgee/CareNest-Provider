package com.carenest.provider.earnings.domain.repository

import com.carenest.provider.earnings.domain.model.EarningsSummary
import com.carenest.provider.earnings.domain.model.ServiceEarningItem

interface EarningsRepository {
    suspend fun getEarningsSummary(): Result<EarningsSummary>
    suspend fun getServiceEarnings(): Result<List<ServiceEarningItem>>
}
