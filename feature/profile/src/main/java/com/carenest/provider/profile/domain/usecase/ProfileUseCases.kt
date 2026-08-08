package com.carenest.provider.profile.domain.usecase

import com.carenest.provider.profile.domain.model.NurseProfile
import com.carenest.provider.profile.domain.model.NurseUpdate
import com.carenest.provider.profile.domain.model.RegistrationSubmission
import com.carenest.provider.profile.domain.model.ServiceType
import com.carenest.provider.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class LoadServiceTypesUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): Result<List<ServiceType>> = repository.getServiceTypes()
}

class SubmitRegistrationUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(submission: RegistrationSubmission): Result<NurseProfile> {
        val profileImageUrl = repository.updateCurrentUser(submission.user)
            .getOrElse { return Result.failure(it) }
        val nurse = repository.registerNurse(submission.nurse).getOrElse { return Result.failure(it) }
        val services = repository.addServices(nurse.id, submission.serviceTypeIds)
            .getOrElse { return Result.failure(it) }
        if (services.failed.isNotEmpty()) {
            val details = services.failed.joinToString { "${it.serviceTypeId}: ${it.reason}" }
            return Result.failure(PartialServiceSubmissionException(details))
        }
        return repository.getNurse(nurse.id)
            .recover { nurse }
            .map { refreshed ->
                if (refreshed.profileImageUrl == null && profileImageUrl != null) {
                    refreshed.copy(profileImageUrl = profileImageUrl)
                } else refreshed
            }
    }
}

class GetNurseUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(nurseId: String): Result<NurseProfile> = repository.getNurse(nurseId)
}

class UpdateNurseUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(nurseId: String, request: NurseUpdate): Result<NurseProfile> =
        repository.updateNurse(nurseId, request)
}

class PartialServiceSubmissionException(details: String) :
    IllegalStateException("Some selected services could not be added: $details")
