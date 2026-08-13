package com.carenest.provider.auth.domain.repository

import com.carenest.provider.auth.domain.model.AuthenticatedNurse
import com.carenest.provider.auth.domain.model.AuthenticatedUser
import com.carenest.provider.auth.domain.model.GoogleLoginResult

interface AuthRepository {

    suspend fun login(phoneNumber: String): Result<Unit>

    suspend fun devLogin(phoneNumber: String): Result<String>

    suspend fun googleLogin(
        idToken: String,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        profileImageUrl: String? = null,
    ): Result<GoogleLoginResult>

    suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
        pendingToken: String? = null,
    ): Result<AuthenticatedNurse?>

    suspend fun getCurrentUser(): Result<AuthenticatedUser>
}