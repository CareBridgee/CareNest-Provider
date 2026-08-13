package com.carenest.provider.auth.data.remote.auth.phone

import io.ktor.client.statement.HttpResponse

interface DefaultAuthApi {
    suspend fun login(phoneNumber: String): HttpResponse
    suspend fun devLogin(phoneNumber: String): HttpResponse
    suspend fun verifyOtp(phoneNumber: String, otp: String, pendingToken: String? = null): HttpResponse
    suspend fun getCurrentUser(): HttpResponse
}