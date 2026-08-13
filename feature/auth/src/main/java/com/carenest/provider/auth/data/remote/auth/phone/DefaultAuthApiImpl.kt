package com.carenest.provider.auth.data.remote.auth.phone

import com.carenest.provider.auth.data.remote.dto.LoginRequestDto
import com.carenest.provider.auth.data.remote.dto.VerifyOtpRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class DefaultAuthApiImpl @Inject constructor(
    private val httpClient: HttpClient
) : DefaultAuthApi {
    override suspend fun login(phoneNumber: String): HttpResponse {
        return httpClient.post("/api/v1/auth/nurse/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(phoneNumber))
        }
    }

    override suspend fun devLogin(phoneNumber: String): HttpResponse {
        return httpClient.post("/api/v1/auth/dev/request-otp") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(phoneNumber))
        }
    }

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
        pendingToken: String?,
    ): HttpResponse {
        val endpoint = if (pendingToken != null) {
            "/api/v1/auth/verify-otp"
        } else {
            "/api/v1/auth/nurse/verify-otp"
        }
        return httpClient.post(endpoint) {
            contentType(ContentType.Application.Json)
            setBody(VerifyOtpRequestDto(phoneNumber, otp, pendingToken))
        }
    }

    override suspend fun getCurrentUser(): HttpResponse =
        httpClient.get("/api/v1/users/me")
}