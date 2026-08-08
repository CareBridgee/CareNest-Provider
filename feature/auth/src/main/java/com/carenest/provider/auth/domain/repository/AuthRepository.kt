package com.carenest.provider.auth.domain.repository

import com.carenest.provider.core.util.Resource

interface AuthRepository {

    suspend fun login(phoneNumber: String): Result<Unit>

    suspend fun devLogin(phoneNumber: String): Resource<String>

    suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
    ): Result<AuthenticatedNurse?>

    suspend fun getCurrentUser(): Result<AuthenticatedUser>
}

data class AuthenticatedUser(
    val profileCompleted: Boolean,
)

data class AuthenticatedNurse(
    val id: String,
    val verificationStatus: NurseVerificationStatus,
)

enum class NurseVerificationStatus {
    UNDER_REVIEW,
    APPROVED,
    REJECTED,
}