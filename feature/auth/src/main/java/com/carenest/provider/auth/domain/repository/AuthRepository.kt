package com.carenest.provider.auth.domain.repository

interface AuthRepository {
    suspend fun login(phoneNumber: String): Result<Unit>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthenticatedNurse?>
    suspend fun getCurrentUser(): Result<AuthenticatedUser>
}

data class AuthenticatedUser(val profileCompleted: Boolean)

data class AuthenticatedNurse(
    val id: String,
    val verificationStatus: NurseVerificationStatus,
)

enum class NurseVerificationStatus { UNDER_REVIEW, APPROVED, REJECTED }

sealed interface AuthenticationDestination {
    data object CompleteProfile : AuthenticationDestination
    data class UnderReview(val nurseId: String) : AuthenticationDestination
    data class Approved(val nurseId: String) : AuthenticationDestination
    data class Rejected(val nurseId: String) : AuthenticationDestination
}
