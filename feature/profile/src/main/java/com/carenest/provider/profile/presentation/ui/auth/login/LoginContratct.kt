package com.carenest.presentation.ui.auth.login

sealed interface LoginIntent {
    data class PhoneNumberChanged(val phone: String) : LoginIntent
    data class OtpMethodChanged(val method: OtpDeliveryMethod) : LoginIntent
    data class CountryCodeChanged(val country: Country) : LoginIntent
    data object ToggleCountryDropdown : LoginIntent
    data object ContinueWithPhoneClicked : LoginIntent
    data object RequestOtpClicked : LoginIntent
    data object BackClicked : LoginIntent
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
    val name: String,
    val code: String,
    val flag: String
)

val countries = listOf(
    Country("Egypt", "+20", "\uD83C\uDDEA\uD83C\uDDEC"),
    Country("Saudi Arabia", "+966", "\uD83C\uDDF8\uD83C\uDDE6"),
    Country("UAE", "+971", "\uD83C\uDDE6\uD83C\uDDEA")
)

data class LoginState(
    val currentStep: LoginStep = LoginStep.LANDING,
    val phoneNumber: String = "",
    val selectedCountry: Country = countries[0],
    val isCountryDropdownExpanded: Boolean = false,
    val selectedOtpMethod: OtpDeliveryMethod = OtpDeliveryMethod.SMS,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface LoginEffect {
    data class NavigateToOtp(val phone: String, val method: OtpDeliveryMethod) : LoginEffect
}
