package com.carenest.provider.auth.presentation.auth.otp

import com.carenest.provider.auth.domain.repository.AuthenticationDestination

sealed interface OtpIntent {
    data class OtpCodeChanged(val otp: String) : OtpIntent
    data class PhoneNumberChanged(val phone: String) : OtpIntent
    data object VerifyOtpClicked : OtpIntent
    data object RetryDestinationResolution : OtpIntent
    data object BackClicked : OtpIntent
    data object ResendClicked : OtpIntent
}

data class OtpState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val canRetryDestination: Boolean = false,
)

sealed interface OtpEffect {
    data class AuthenticationSucceeded(val destination: AuthenticationDestination) : OtpEffect
    data object NavigateBack : OtpEffect
}
