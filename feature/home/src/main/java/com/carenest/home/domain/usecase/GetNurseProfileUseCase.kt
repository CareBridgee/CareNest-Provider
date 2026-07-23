package com.carenest.home.domain.usecase


import com.carenest.home.domain.repository.NurseRequestsRepository
import com.carenest.home.domain.model.NurseProfile
import javax.inject.Inject

class GetNurseProfileUseCase @Inject constructor(
    private val repository: NurseRequestsRepository,
) {
    suspend operator fun invoke(): Result<NurseProfile> =
        runCatching { repository.getNurseProfile() }
}