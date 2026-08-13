package com.carenest.provider.auth.data.remote.auth.google

import com.carenest.provider.auth.data.remote.auth.GoogleAuthDataSource
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class GoogleAuthRemoteDataSourceImpl @Inject constructor(
    private val googleAuthApi: GoogleAuthApi,
) : GoogleAuthDataSource {

    override suspend fun googleLogin(
        idToken: String,
    ): HttpResponse {
        return googleAuthApi.googleLogin(idToken)
    }

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
        pendingToken: String?,
    ): HttpResponse {
        return googleAuthApi.verifyOtp(phoneNumber, otp, pendingToken)
    }

    override suspend fun getCurrentUser(): HttpResponse = googleAuthApi.getCurrentUser()

}
