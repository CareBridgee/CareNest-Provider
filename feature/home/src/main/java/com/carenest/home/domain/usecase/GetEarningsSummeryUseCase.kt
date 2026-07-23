package com.carenest.home.domain.usecase


import com.carenest.home.domain.model.EarningsSummary
import com.carenest.home.domain.repository.NurseRequestsRepository
import javax.inject.Inject

class GetEarningsSummaryUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(): Result<EarningsSummary> =
        runCatching { repository.fetchEarningsSummary() }
}