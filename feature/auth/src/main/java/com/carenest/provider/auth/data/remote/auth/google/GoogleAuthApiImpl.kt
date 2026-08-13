package com.carenest.provider.auth.data.remote.auth.google

import com.carenest.provider.auth.data.remote.dto.GoogleLoginRequestDto
import com.carenest.provider.auth.data.remote.dto.VerifyOtpRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class GoogleAuthApiImpl @Inject constructor(
    private val httpClient: HttpClient,
) : GoogleAuthApi {
    override suspend fun googleLogin(
        idToken: String,
    ): HttpResponse {
        return httpClient.post("/api/v1/auth/nurse/google") {
            contentType(ContentType.Application.Json)
            setBody(
                GoogleLoginRequestDto(
                    idToken = idToken,
                )
            )
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

    override suspend fun getCurrentUser(): HttpResponse {
        TODO("Not supported")
    }
}
