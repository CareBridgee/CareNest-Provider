package com.carenest.provider.auth.presentation.auth.otp

import com.carenest.provider.auth.domain.util.AuthenticationDestination
import com.carenest.provider.auth.presentation.auth.AuthUiError


sealed interface OtpIntent {
    data class OtpCodeChanged(val otp: String) : OtpIntent
    data class PhoneNumberChanged(val phone: String) : OtpIntent
    data class PendingTokenChanged(val pendingToken: String?) : OtpIntent
    data object VerifyOtpClicked : OtpIntent
    data object RetryDestinationResolution : OtpIntent
    data object BackClicked : OtpIntent
    data object ResendClicked : OtpIntent
    data object DismissErrorDialog : OtpIntent
}

data class OtpState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val pendingToken: String? = null,
    val isLoading: Boolean = false,
    val remainingSeconds: Int = 30,
    val isResending: Boolean = false,
    val errorMessage: AuthUiError? = null,
    val isSuccess: Boolean = false,
    val canRetryDestination: Boolean = false,
    val countdownGeneration: Int = 0,
) {
    val canResend: Boolean get() = remainingSeconds == 0 && !isResending
}

sealed interface OtpEffect {
    data class AuthenticationSucceeded(val destination: AuthenticationDestination) : OtpEffect
    data object NavigateBack : OtpEffect
}
