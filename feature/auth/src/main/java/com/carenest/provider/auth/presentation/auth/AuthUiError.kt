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
    is AuthException -> {
        val customMsg = message?.takeIf {
            it.isNotBlank() &&
                !it.startsWith("HTTP ") &&
                it != "technical backend message" &&
                it != "Authentication request failed"
        }
        when (failure) {
            AuthFailure.Network -> AuthUiError.NetworkUnavailable
            AuthFailure.InvalidPhone -> customMsg?.let { AuthUiError.Custom(it) } ?: AuthUiError.InvalidPhone
            AuthFailure.InvalidOtp -> customMsg?.let { AuthUiError.Custom(it) } ?: AuthUiError.InvalidOtp
            AuthFailure.ExpiredOtp -> customMsg?.let { AuthUiError.Custom(it) } ?: AuthUiError.ExpiredOtp
            AuthFailure.TooManyRequests -> AuthUiError.TooManyRequests
            AuthFailure.Server -> customMsg?.let { AuthUiError.Custom(it) } ?: AuthUiError.ServiceUnavailable
            AuthFailure.Unknown -> customMsg?.let { AuthUiError.Custom(it) } ?: default
        }
    }

    is IOException -> AuthUiError.NetworkUnavailable
    else -> this.message?.takeIf { it.isNotBlank() && !it.startsWith("HTTP ") }?.let { AuthUiError.Custom(it) } ?: default
}
