package com.carenest.provider.auth.data.repository

import com.carenest.provider.auth.data.remote.AuthRemoteDataSource
import com.carenest.provider.auth.data.remote.dto.AuthResponseDto
import com.carenest.provider.auth.data.remote.dto.ErrorResponseDto
import com.carenest.provider.auth.domain.repository.AuthRepository
import com.carenest.provider.core.datastore.TokenManager
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthRemoteDataSource,
    private val tokenManager: TokenManager
) : AuthRepository {
    override suspend fun login(phoneNumber: String): Result<Unit> {
        return try {
            val response = dataSource.login(phoneNumber)
            handleGenericResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<Unit> {
        return try {
            val response = dataSource.verifyOtp(phoneNumber, otp)
            if (response.status.isSuccess()) {
                val authResponse = response.body<AuthResponseDto>()
                tokenManager.saveTokens(authResponse.accessToken, authResponse.refreshToken)
                Result.success(Unit)
            } else {
                handleErrorResponse(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun handleGenericResponse(response: HttpResponse): Result<Unit> {
        return if (response.status.isSuccess()) {
            Result.success(Unit)
        } else {
            handleErrorResponse(response)
        }
    }

    private suspend fun handleErrorResponse(response: HttpResponse): Result<Unit> {
        return try {
            val errorBody = response.body<ErrorResponseDto>()
            Result.failure(Exception(errorBody.message))
        } catch (e: Exception) {
            Result.failure(Exception("Unknown error: ${response.status}"))
        }
    }
}
