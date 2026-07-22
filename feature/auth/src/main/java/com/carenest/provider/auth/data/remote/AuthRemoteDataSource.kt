package com.carenest.provider.auth.data.remote

import com.carenest.provider.auth.data.remote.dto.AuthResponseDto
import com.carenest.provider.auth.data.remote.dto.LoginRequestDto
import com.carenest.provider.auth.data.remote.dto.VerifyOtpRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

interface AuthRemoteDataSource {
    suspend fun login(phoneNumber: String): HttpResponse
    suspend fun verifyOtp(phoneNumber: String, otp: String): HttpResponse
}

class KtorAuthRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient
) : AuthRemoteDataSource {
    override suspend fun login(phoneNumber: String): HttpResponse {
        return httpClient.post("/api/v1/auth/nurse/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(phoneNumber))
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): HttpResponse {
        return httpClient.post("/api/v1/auth/nurse/verify-otp") {
            contentType(ContentType.Application.Json)
            setBody(VerifyOtpRequestDto(phoneNumber, otp))
        }
    }
}
