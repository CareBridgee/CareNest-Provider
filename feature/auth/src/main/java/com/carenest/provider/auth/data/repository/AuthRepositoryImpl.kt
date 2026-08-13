package com.carenest.provider.auth.data.repository

import com.carenest.provider.auth.data.remote.auth.AuthRemoteDataSource
import com.carenest.provider.auth.data.remote.dto.AuthResponseDto
import com.carenest.provider.auth.data.remote.dto.CurrentUserDto
import com.carenest.provider.auth.data.remote.dto.DevLoginResponseDto
import com.carenest.provider.auth.data.remote.dto.ErrorResponseDto
import com.carenest.provider.auth.domain.model.AuthException
import com.carenest.provider.auth.domain.model.AuthFailure
import com.carenest.provider.auth.domain.model.AuthenticatedNurse
import com.carenest.provider.auth.domain.model.AuthenticatedUser
import com.carenest.provider.auth.domain.model.GoogleLoginResult
import com.carenest.provider.auth.domain.model.NurseVerificationStatus
import com.carenest.provider.auth.domain.repository.AuthRepository
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import android.util.Log
import java.io.IOException
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

            handleGenericResponse(response, AuthOperation.REQUEST_OTP)
        } catch (e: Exception) {
            Result.failure(e.toAuthException(AuthOperation.REQUEST_OTP))
        }
    }

    override suspend fun devLogin(
        phoneNumber: String,
    ): Result<String> {
        return try {
            val response = dataSource.devLogin(phoneNumber)

            if (response.status.isSuccess()) {
                val devResponse = response.body<DevLoginResponseDto>()
                Result.success(devResponse.otp)
            } else {
                handleErrorResponse(response, AuthOperation.REQUEST_OTP)
            }
        } catch (e: Exception) {
            Result.failure(e.toAuthException(AuthOperation.REQUEST_OTP))
        }
    }

    override suspend fun googleLogin(
        idToken: String,
        firstName: String?,
        lastName: String?,
        email: String?,
        profileImageUrl: String?,
    ): Result<GoogleLoginResult> {
        return try {
            Log.i("AuthRepo", "Executing POST /api/v1/auth/nurse/google with Google ID token")
            val response = dataSource.googleLogin(
                idToken = idToken,
                firstName = firstName,
                lastName = lastName,
                email = email,
                profileImageUrl = profileImageUrl,
            )

            if (response.status.isSuccess()) {
                val authResponse = response.body<AuthResponseDto>()
                Log.i("AuthRepo", "Google login HTTP ${response.status.value} received. Payload status=${authResponse.status}")

                when (authResponse.status) {
                    "AUTHENTICATED" -> {
                        val accessToken = requireNotNull(authResponse.accessToken) {
                            "Google auth response did not contain an access token"
                        }
                        val refreshToken = requireNotNull(authResponse.refreshToken) {
                            "Google auth response did not contain a refresh token"
                        }

                        authenticationSessionStore.beginAuthentication(
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                        )

                        val nurse = authResponse.nurseUser?.nurse?.toDomain()
                            ?: authResponse.user?.nurse?.toDomain()
                        Log.i("AuthRepo", "Google login AUTHENTICATED successfully. Tokens saved.")
                        Result.success(GoogleLoginResult.Authenticated(nurse))
                    }
                    "PHONE_REQUIRED" -> {
                        val pendingToken = requireNotNull(authResponse.pendingToken) {
                            "Google auth response did not contain pendingToken for PHONE_REQUIRED"
                        }
                        Log.i("AuthRepo", "Google login PHONE_REQUIRED. Email=${authResponse.email}")
                        Result.success(
                            GoogleLoginResult.PhoneRequired(
                                pendingToken = pendingToken,
                                email = authResponse.email,
                                firstName = authResponse.firstName,
                                lastName = authResponse.lastName,
                                profileImageUrl = authResponse.profileImageUrl,
                            )
                        )
                    }
                    else -> {
                        Log.e("AuthRepo", "Unexpected status in Google login response: ${authResponse.status}")
                        handleErrorResponse(response, AuthOperation.GOOGLE_LOGIN)
                    }
                }
            } else {
                Log.e("AuthRepo", "Google login HTTP response failed with status code ${response.status.value}")
                handleErrorResponse(response, AuthOperation.GOOGLE_LOGIN)
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Google login exception: ${e.message}", e)
            authenticationSessionStore.clearSession()
            Result.failure(e.toAuthException(AuthOperation.GOOGLE_LOGIN))
        }
    }

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
        pendingToken: String?,
    ): Result<AuthenticatedNurse?> {
        return try {
            val response = dataSource.verifyOtp(
                phoneNumber = phoneNumber,
                otp = otp,
                pendingToken = pendingToken,
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

                val nurse = authResponse.nurseUser?.nurse?.toDomain()
                    ?: authResponse.user?.nurse?.toDomain()
                Result.success(nurse)
            } else {
                handleErrorResponse(response, AuthOperation.VERIFY_OTP)
            }
        } catch (e: Exception) {
            authenticationSessionStore.clearSession()
            Result.failure(e.toAuthException(AuthOperation.VERIFY_OTP))
        }
    }

    override suspend fun getCurrentUser(): Result<AuthenticatedUser> {
        return try {
            val response = dataSource.getCurrentUser()

            if (response.status.isSuccess()) {
                val user = response.body<CurrentUserDto>()
                val userNurse = user.nurse?.toDomain()
                val topLevelId = user.id ?: user.nurseId ?: user.profileId ?: user.userId

                Result.success(
                    AuthenticatedUser(
                        id = topLevelId ?: userNurse?.id,
                        profileCompleted = user.profileCompleted,
                        nurse = userNurse ?: if (!topLevelId.isNullOrBlank()) {
                            AuthenticatedNurse(
                                id = topLevelId,
                                verificationStatus = NurseVerificationStatus.APPROVED,
                                hasSubmittedApplication = true,
                            )
                        } else null,
                    ),
                )
            } else {
                handleErrorResponse(response, AuthOperation.AUTHENTICATED_REQUEST)
            }
        } catch (error: Exception) {
            Result.failure(error.toAuthException(AuthOperation.AUTHENTICATED_REQUEST))
        }
    }

    private suspend fun handleGenericResponse(
        response: HttpResponse,
        operation: AuthOperation,
    ): Result<Unit> {
        return if (response.status.isSuccess()) {
            Result.success(Unit)
        } else {
            handleErrorResponse(response, operation)
        }
    }

    private suspend fun <T> handleErrorResponse(
        response: HttpResponse,
        operation: AuthOperation,
    ): Result<T> {
        val statusCode = response.status.value

        val rawBody = runCatching { response.bodyAsText() }.getOrNull()
        Log.e("AuthRepo", "HTTP Error $statusCode response raw body: $rawBody")

        val errorBody = runCatching { response.body<ErrorResponseDto>() }.getOrNull()
        val parsedMessage = errorBody?.message
            ?: errorBody?.error
            ?: errorBody?.details
            ?: rawBody?.takeIf(String::isNotBlank)
            ?: "HTTP $statusCode (${response.status.description})"

        return Result.failure(
            authException(
                operation = operation,
                statusCode = statusCode,
                backendCode = errorBody?.code,
                message = parsedMessage,
            )
        )
    }
}

private enum class AuthOperation {
    REQUEST_OTP,
    VERIFY_OTP,
    GOOGLE_LOGIN,
    AUTHENTICATED_REQUEST,
}

private fun Throwable.toAuthException(operation: AuthOperation): AuthException {
    if (this is AuthException) return this

    return authException(
        operation = operation,
        message = message ?: "Authentication request failed",
        cause = this,
    )
}

private fun authException(
    operation: AuthOperation,
    message: String,
    statusCode: Int? = null,
    backendCode: String? = null,
    cause: Throwable? = null,
): AuthException {
    val searchableMessage = listOfNotNull(backendCode, message)
        .joinToString(" ")
        .lowercase()

    val failure = when {
        cause is IOException ||
            searchableMessage.contains("timeout") ||
            searchableMessage.contains("unable to resolve host") ||
            searchableMessage.contains("failed to connect") -> AuthFailure.Network
        searchableMessage.contains("too many") ||
            searchableMessage.contains("rate limit") -> AuthFailure.TooManyRequests
        operation == AuthOperation.VERIFY_OTP && searchableMessage.contains("expired") ->
            AuthFailure.ExpiredOtp
        operation == AuthOperation.VERIFY_OTP &&
            (searchableMessage.contains("invalid otp") ||
                searchableMessage.contains("incorrect code") ||
                searchableMessage.contains("invalid code")) -> AuthFailure.InvalidOtp
        statusCode == 408 -> AuthFailure.Network
        statusCode == 429 -> AuthFailure.TooManyRequests
        statusCode != null && statusCode >= 500 -> AuthFailure.Server
        operation == AuthOperation.VERIFY_OTP && statusCode in setOf(400, 401, 403, 404, 409, 422) ->
            AuthFailure.InvalidOtp
        operation == AuthOperation.REQUEST_OTP && statusCode in setOf(400, 404, 422) ->
            AuthFailure.InvalidPhone
        else -> AuthFailure.Unknown
    }

    val userFriendlyMessage = if (operation == AuthOperation.GOOGLE_LOGIN &&
        (searchableMessage.contains("check constraint") || searchableMessage.contains("not-null"))) {
        "Google Sign-In is temporarily unavailable because the server could not create the account. Please continue with phone or try again later."
    } else {
        message
    }

    return AuthException(
        failure = failure,
        message = userFriendlyMessage,
        statusCode = statusCode,
        backendCode = backendCode,
        cause = cause,
    )
}

private fun com.carenest.provider.auth.data.remote.dto.NurseAuthDto.toDomain() =
    AuthenticatedNurse(
        id = id ?: nurseId ?: profileId ?: userId ?: "",
        verificationStatus = runCatching {
            NurseVerificationStatus.valueOf((verificationStatus ?: "APPROVED").uppercase())
        }.getOrDefault(NurseVerificationStatus.APPROVED),
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
