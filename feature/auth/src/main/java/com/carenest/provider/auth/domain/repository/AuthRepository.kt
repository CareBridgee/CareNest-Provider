package com.carenest.provider.auth.domain.repository

interface AuthRepository {

    suspend fun login(phoneNumber: String): Result<Unit>

    suspend fun devLogin(phoneNumber: String): Result<String>

    suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
    ): Result<AuthenticatedNurse?>

    suspend fun getCurrentUser(): Result<AuthenticatedUser>
}

data class AuthenticatedUser(
    val id: String? = null,
    val profileCompleted: Boolean,
    val nurse: AuthenticatedNurse? = null,
)

data class AuthenticatedNurse(
    val id: String,
    val verificationStatus: NurseVerificationStatus,
    val hasSubmittedApplication: Boolean = false,
)

enum class NurseVerificationStatus {
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
}
