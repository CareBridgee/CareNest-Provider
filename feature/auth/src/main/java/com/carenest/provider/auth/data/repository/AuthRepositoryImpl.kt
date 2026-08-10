package com.carenest.provider.auth.data.repository

import com.carenest.provider.auth.data.remote.AuthRemoteDataSource
import com.carenest.provider.auth.data.remote.dto.AuthResponseDto
import com.carenest.provider.auth.data.remote.dto.CurrentUserDto
import com.carenest.provider.auth.data.remote.dto.DevLoginResponseDto
import com.carenest.provider.auth.data.remote.dto.ErrorResponseDto
import com.carenest.provider.auth.domain.repository.AuthRepository
import com.carenest.provider.auth.domain.repository.AuthenticatedNurse
import com.carenest.provider.auth.domain.repository.AuthenticatedUser
import com.carenest.provider.auth.domain.repository.NurseVerificationStatus
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.util.Resource
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthRemoteDataSource,
    private val authenticationSessionStore: AuthenticationSessionStore,
) : AuthRepository {

    override suspend fun login(
        phoneNumber: String,
    ): Result<Unit> {
        return try {
            val response = dataSource.login(phoneNumber)

            handleGenericResponse(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun devLogin(
        phoneNumber: String,
    ): Resource<String> {
        return try {
            val response = dataSource.devLogin(phoneNumber)

            if (response.status.isSuccess()) {
                val devResponse = response.body<DevLoginResponseDto>()

                Resource.Success(devResponse.otp)
            } else {
                val errorBody = response.body<ErrorResponseDto>()

                Resource.Error(
                    errorBody.message
                        ?: "Something went wrong",
                )
            }
        } catch (e: Exception) {
            Resource.Error(
                e.message
                    ?: "An unexpected error occurred",
            )
        }
    }

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
    ): Result<AuthenticatedNurse?> {
        return try {
            val response = dataSource.verifyOtp(
                phoneNumber = phoneNumber,
                otp = otp,
            )

            if (response.status.isSuccess()) {
                val authResponse = response.body<AuthResponseDto>()

                val accessToken = requireNotNull(authResponse.accessToken) {
                    "Authentication response did not contain an access token"
                }

                val refreshToken = requireNotNull(authResponse.refreshToken) {
                    "Authentication response did not contain a refresh token"
                }

                authenticationSessionStore.beginAuthentication(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                )

                Result.success(authResponse.user?.nurse?.toDomain())
            } else {
                handleErrorResponse(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<AuthenticatedUser> {
        return try {
            val response = dataSource.getCurrentUser()

            if (response.status.isSuccess()) {
                val user = response.body<CurrentUserDto>()

                Result.success(
                    AuthenticatedUser(
                        profileCompleted = user.profileCompleted,
                        nurse = user.nurse?.toDomain(),
                    ),
                )
            } else {
                handleErrorResponse(response)
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    private suspend fun handleGenericResponse(
        response: HttpResponse,
    ): Result<Unit> {
        return if (response.status.isSuccess()) {
            Result.success(Unit)
        } else {
            handleErrorResponse(response)
        }
    }

    private suspend fun <T> handleErrorResponse(
        response: HttpResponse,
    ): Result<T> {
        val statusCode = response.status.value

        return try {
            val errorBody = response.body<ErrorResponseDto>()

            val parsedMessage =
                errorBody.message
                    ?: errorBody.error
                    ?: errorBody.details

            if (!parsedMessage.isNullOrBlank()) {
                Result.failure(
                    Exception(parsedMessage),
                )
            } else {
                val rawBody = response.bodyAsText()

                if (rawBody.isNotBlank()) {
                    Result.failure(
                        Exception("HTTP $statusCode: $rawBody"),
                    )
                } else {
                    Result.failure(
                        Exception(
                            "HTTP $statusCode (${response.status.description})",
                        ),
                    )
                }
            }
        } catch (e: Exception) {
            val rawBody = runCatching {
                response.bodyAsText()
            }.getOrDefault("")

            if (rawBody.isNotBlank()) {
                Result.failure(
                    Exception("HTTP $statusCode: $rawBody"),
                )
            } else {
                Result.failure(
                    Exception(
                        "HTTP $statusCode (${response.status.description})",
                    ),
                )
            }
        }
    }
}

private fun com.carenest.provider.auth.data.remote.dto.NurseAuthDto.toDomain() =
    AuthenticatedNurse(
        id = id,
        verificationStatus = verificationStatus.toNurseVerificationStatus(),
        hasSubmittedApplication = listOf(
            nationalId,
            nationalIdFrontUrl,
            nationalIdBackUrl,
            licenseImageUrl,
            professionalCertificateUrl,
            specialization,
        ).any { !it.isNullOrBlank() } || yearsOfExperience != null,
    )

private fun String.toNurseVerificationStatus(): NurseVerificationStatus = when (uppercase()) {
    "VERIFIED", "APPROVED" -> NurseVerificationStatus.APPROVED
    "REJECTED" -> NurseVerificationStatus.REJECTED
    else -> NurseVerificationStatus.UNDER_REVIEW
}
