package com.carenest.provider.auth.domain.repository

import com.carenest.provider.core.util.Resource

interface AuthRepository {
    suspend fun login(phoneNumber: String): Result<Unit>
    suspend fun devLogin(phoneNumber: String): Resource<String>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<Unit>
}
