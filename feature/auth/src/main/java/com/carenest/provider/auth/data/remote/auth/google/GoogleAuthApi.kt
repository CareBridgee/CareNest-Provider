package com.carenest.provider.auth.data.remote.auth.google

import io.ktor.client.statement.HttpResponse


interface GoogleAuthApi {
    suspend fun googleLogin(
        idToken: String,
    ): HttpResponse
    suspend fun verifyOtp(phoneNumber: String, otp: String, pendingToken: String? = null): HttpResponse
    suspend fun getCurrentUser(): HttpResponse
}
