package com.carenest.provider.auth.data.remote.auth.phone

import com.carenest.provider.auth.data.remote.auth.AuthRemoteDataSource
import com.carenest.provider.auth.data.remote.auth.google.GoogleAuthApi
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class DefaultAuthRemoteDataSource @Inject constructor(
    private val defaultAuthApi: DefaultAuthApi,
    private val googleAuthApi: GoogleAuthApi,
) : AuthRemoteDataSource {

    override suspend fun login(phoneNumber: String): HttpResponse =
        defaultAuthApi.login(phoneNumber)

    override suspend fun devLogin(phoneNumber: String): HttpResponse =
        defaultAuthApi.devLogin(phoneNumber)

    override suspend fun googleLogin(
        idToken: String,
        firstName: String?,
        lastName: String?,
        email: String?,
        profileImageUrl: String?,
    ): HttpResponse =
        googleAuthApi.googleLogin(idToken)

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
        pendingToken: String?,
    ): HttpResponse = defaultAuthApi.verifyOtp(phoneNumber, otp, pendingToken)

    override suspend fun getCurrentUser(): HttpResponse =
        defaultAuthApi.getCurrentUser()
}
