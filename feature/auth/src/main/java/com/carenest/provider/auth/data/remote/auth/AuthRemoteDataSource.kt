package com.carenest.provider.auth.data.remote.auth

import com.carenest.provider.auth.data.remote.auth.google.GoogleAuthApi
import io.ktor.client.statement.HttpResponse

interface AuthRemoteDataSource {
    suspend fun login(phoneNumber: String): HttpResponse
    suspend fun devLogin(phoneNumber: String): HttpResponse
    suspend fun googleLogin(
        idToken: String,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        profileImageUrl: String? = null,
    ): HttpResponse
    suspend fun verifyOtp(phoneNumber: String, otp: String, pendingToken: String? = null): HttpResponse
    suspend fun getCurrentUser(): HttpResponse
}

interface GoogleAuthDataSource: GoogleAuthApi