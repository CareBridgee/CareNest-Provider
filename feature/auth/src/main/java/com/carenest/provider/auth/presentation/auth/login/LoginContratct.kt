package com.carenest.provider.auth.presentation.auth.login

import com.carenest.provider.auth.domain.util.AuthenticationDestination
import com.carenest.provider.auth.domain.validation.PhoneNumberValidationError
import com.carenest.provider.auth.domain.validation.SupportedPhoneCountry
import com.carenest.provider.auth.presentation.auth.AuthUiError

sealed interface LoginIntent {
    data class PhoneNumberChanged(val phone: String) : LoginIntent
    data class OtpMethodChanged(val method: OtpDeliveryMethod) : LoginIntent
    data class CountryCodeChanged(val country: Country) : LoginIntent
    data object ToggleCountryDropdown : LoginIntent
    data object ContinueWithPhoneClicked : LoginIntent
    data class GoogleSignInClicked(
        val idToken: String,
        val firstName: String? = null,
        val lastName: String? = null,
        val email: String? = null,
        val profileImageUrl: String? = null,
    ) : LoginIntent
    data class GoogleSignInFailed(val errorMessage: String? = null) : LoginIntent
    data object RequestOtpClicked : LoginIntent
    data object BackClicked : LoginIntent
    data object DismissErrorDialog : LoginIntent
}

enum class LoginStep {
    LANDING,
    PHONE_INPUT
}

enum class OtpDeliveryMethod {
    SMS,
    WHATSAPP
}

data class Country(
    val flag: String,
    val phoneConfig: SupportedPhoneCountry,
) {
    val code: String get() = phoneConfig.dialCode
}

val countries = listOf(
    Country("\uD83C\uDDEA\uD83C\uDDEC", SupportedPhoneCountry.EGYPT),
    Country("\uD83C\uDDF8\uD83C\uDDE6", SupportedPhoneCountry.SAUDI_ARABIA),
    Country("\uD83C\uDDE6\uD83C\uDDEA", SupportedPhoneCountry.UAE),
)

data class LoginState(
    val currentStep: LoginStep = LoginStep.LANDING,
    val phoneNumber: String = "",
    val selectedCountry: Country = countries[0],
    val isCountryDropdownExpanded: Boolean = false,
    val selectedOtpMethod: OtpDeliveryMethod = OtpDeliveryMethod.SMS,
    val isLoading: Boolean = false,
    val pendingToken: String? = null,
    val googleEmail: String? = null,
    val googleFirstName: String? = null,
    val googleLastName: String? = null,
    val googleProfileImageUrl: String? = null,
    val phoneValidationError: PhoneNumberValidationError? = null,
    val errorMessage: AuthUiError? = null,
) {
    val isPhoneValid: Boolean
        get() = phoneNumber.isNotBlank() &&
            selectedCountry.phoneConfig.validate(phoneNumber) == null
}

sealed interface LoginEffect {
    data class NavigateToOtp(
        val phone: String,
        val method: OtpDeliveryMethod,
        val otp: String? = null,
        val pendingToken: String? = null,
    ) : LoginEffect
    data class NavigateToDestination(
        val destination: AuthenticationDestination,
    ) : LoginEffect
}
