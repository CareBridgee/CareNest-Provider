package com.carenest.provider.auth.domain.repository

interface AuthRepository {
    suspend fun login(phoneNumber: String): Result<Unit>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<Unit>
}
