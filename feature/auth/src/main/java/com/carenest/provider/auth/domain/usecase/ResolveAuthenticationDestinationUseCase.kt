package com.carenest.provider.auth.domain.usecase

import com.carenest.provider.auth.domain.model.AuthenticatedNurse
import com.carenest.provider.auth.domain.model.NurseVerificationStatus
import com.carenest.provider.auth.domain.repository.AuthRepository
import com.carenest.provider.auth.domain.util.AuthenticationDestination
import javax.inject.Inject

class AuthenticationDestinationResolver @Inject constructor() {
    fun resolve(
        profileCompleted: Boolean,
        nurse: AuthenticatedNurse?,
    ): AuthenticationDestination {
        if (nurse == null || (!profileCompleted && !nurse.hasSubmittedApplication)) {
            return AuthenticationDestination.CompleteProfile
        }
        return when (nurse.verificationStatus) {
            NurseVerificationStatus.UNDER_REVIEW -> AuthenticationDestination.UnderReview(nurse.id)
            NurseVerificationStatus.APPROVED -> AuthenticationDestination.Approved(nurse.id)
            NurseVerificationStatus.REJECTED -> AuthenticationDestination.Rejected(nurse.id)
        }
    }
}

class ResolveAuthenticationDestinationUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val resolver: AuthenticationDestinationResolver,
) {
    suspend operator fun invoke(nurse: AuthenticatedNurse?): Result<AuthenticationDestination> =
        repository.getCurrentUser().map { user ->
            resolver.resolve(
                profileCompleted = user.profileCompleted,
                nurse = nurse ?: user.nurse,
            )
        }
}
