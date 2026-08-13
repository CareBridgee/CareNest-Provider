package com.carenest.provider.auth.presentation.auth

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.carenest.provider.auth.domain.model.AuthException
import com.carenest.provider.auth.domain.model.AuthFailure
import com.carenest.provider.designsystem.R
import java.io.IOException

sealed interface AuthUiError {
    data class Resource(@get:StringRes val messageRes: Int) : AuthUiError
    data class Custom(val message: String) : AuthUiError

    companion object {
        val InvalidPhone = Resource(R.string.auth_error_invalid_phone)
        val OtpIncomplete = Resource(R.string.auth_error_otp_incomplete)
        val InvalidOtp = Resource(R.string.auth_error_invalid_otp)
        val ExpiredOtp = Resource(R.string.auth_error_expired_otp)
        val NetworkUnavailable = Resource(R.string.auth_error_network)
        val TooManyRequests = Resource(R.string.auth_error_too_many_requests)
        val ServiceUnavailable = Resource(R.string.auth_error_service_unavailable)
        val SendCodeFailed = Resource(R.string.auth_error_send_code)
        val ResendCodeFailed = Resource(R.string.auth_error_resend_code)
        val VerificationFailed = Resource(R.string.auth_error_verification)
        val ProfileLoadFailed = Resource(R.string.auth_error_profile_load)
    }
}

@Composable
fun AuthUiError?.localizedMessage(): String? = when (this) {
    null -> null
    is AuthUiError.Resource -> stringResource(messageRes)
    is AuthUiError.Custom -> message
}

fun Throwable.toAuthUiError(default: AuthUiError): AuthUiError = when (this) {
    is AuthException -> when (failure) {
        AuthFailure.Network -> AuthUiError.NetworkUnavailable
        AuthFailure.InvalidPhone -> AuthUiError.InvalidPhone
        AuthFailure.InvalidOtp -> AuthUiError.InvalidOtp
        AuthFailure.ExpiredOtp -> AuthUiError.ExpiredOtp
        AuthFailure.TooManyRequests -> AuthUiError.TooManyRequests
        AuthFailure.Server -> AuthUiError.ServiceUnavailable
        AuthFailure.Unknown -> {
            val msg = message
            if (!msg.isNullOrBlank() && msg != "technical backend message" && msg != "Authentication request failed") {
                AuthUiError.Custom(msg)
            } else {
                default
            }
        }
    }

    is IOException -> AuthUiError.NetworkUnavailable
    else -> this.message?.takeIf { it.isNotBlank() }?.let { AuthUiError.Custom(it) } ?: default
}
