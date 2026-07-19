package com.carenest.presentation.ui.auth.otp

sealed interface OtpIntent {
    data class OtpCodeChanged(val otp: String) : OtpIntent
    data class PhoneNumberChanged(val phone: String) : OtpIntent
    data object VerifyOtpClicked : OtpIntent
    data object BackClicked : OtpIntent
    data object ResendClicked : OtpIntent
}

data class OtpState(
    val phoneNumber: String = "",
    val otpCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

sealed interface OtpEffect {
    data object NavigateToHome : OtpEffect
    data object NavigateBack : OtpEffect
}
