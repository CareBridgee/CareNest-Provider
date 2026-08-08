package com.carenest.provider.auth.data.repository

import android.util.Log
import com.carenest.provider.auth.data.remote.AuthRemoteDataSource
import com.carenest.provider.auth.data.remote.dto.AuthResponseDto
import com.carenest.provider.auth.data.remote.dto.ErrorResponseDto
import com.carenest.provider.auth.data.remote.dto.CurrentUserDto
import com.carenest.provider.auth.domain.repository.AuthRepository
import com.carenest.provider.auth.domain.repository.AuthenticatedNurse
import com.carenest.provider.auth.domain.repository.AuthenticatedUser
import com.carenest.provider.auth.domain.repository.NurseVerificationStatus
import com.carenest.provider.core.datastore.TokenManager
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
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

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<AuthenticatedNurse?> {
        return try {
            val response = dataSource.verifyOtp(phoneNumber, otp)
            if (response.status.isSuccess()) {
                val authResponse = response.body<AuthResponseDto>()
                val accessToken = requireNotNull(authResponse.accessToken) {
                    "Authentication response did not contain an access token"
                }
                val refreshToken = requireNotNull(authResponse.refreshToken) {
                    "Authentication response did not contain a refresh token"
                }
                tokenManager.saveTokens(accessToken, refreshToken)
                val nurse = authResponse.user?.nurse
                Result.success(
                    nurse?.let {
                        AuthenticatedNurse(
                            id = it.id,
                            verificationStatus = NurseVerificationStatus.valueOf(it.verificationStatus),
                        )
                    }
                )
            } else {
                handleErrorResponse(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<AuthenticatedUser> = try {
        val response = dataSource.getCurrentUser()
        if (response.status.isSuccess()) {
            val user = response.body<CurrentUserDto>()
            Log.d("AuthRouting", "profileCompleted=${user.profileCompleted}")
            Result.success(AuthenticatedUser(profileCompleted = user.profileCompleted))
        } else {
            handleErrorResponse(response)
        }
    } catch (error: Exception) {
        Result.failure(error)
    }

    private suspend fun handleGenericResponse(response: HttpResponse): Result<Unit> {
        return if (response.status.isSuccess()) {
            Result.success(Unit)
        } else {
            handleErrorResponse(response)
        }
    }

    private suspend fun <T> handleErrorResponse(response: HttpResponse): Result<T> {
        val statusCode = response.status.value
        return try {
            val errorBody = response.body<ErrorResponseDto>()
            val parsedMsg = errorBody.message ?: errorBody.error ?: errorBody.details
            if (!parsedMsg.isNullOrBlank()) {
                Result.failure(Exception(parsedMsg))
            } else {
                val rawBody = response.bodyAsText()
                if (rawBody.isNotBlank()) {
                    Result.failure(Exception("HTTP $statusCode: $rawBody"))
                } else {
                    Result.failure(Exception("HTTP $statusCode (${response.status.description})"))
                }
            }
        } catch (e: Exception) {
            val rawBody = runCatching { response.bodyAsText() }.getOrDefault("")
            if (rawBody.isNotBlank()) {
                Result.failure(Exception("HTTP $statusCode: $rawBody"))
            } else {
                Result.failure(Exception("HTTP $statusCode (${response.status.description})"))
            }
        }
    }
}
