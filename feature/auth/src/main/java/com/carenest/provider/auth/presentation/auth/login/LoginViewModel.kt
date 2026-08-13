package com.carenest.provider.auth.presentation.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.auth.domain.model.GoogleLoginResult
import com.carenest.provider.auth.domain.usecase.DevLoginUseCase
import com.carenest.provider.auth.domain.usecase.GoogleLoginUseCase
import com.carenest.provider.auth.domain.usecase.ResolveAuthenticationDestinationUseCase
import com.carenest.provider.auth.domain.validation.PhoneValidator
import com.carenest.provider.auth.presentation.auth.AuthUiError
import com.carenest.provider.auth.presentation.auth.toAuthUiError
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val devLoginUseCase: DevLoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val resolveDestinationUseCase: ResolveAuthenticationDestinationUseCase,
) : ViewModel(),
    StateHolder<LoginState> by DefaultStateHolder(LoginState()),
    EffectPublisher<LoginEffect> by DefaultEffectPublisher() {

    fun onEvent(event: LoginIntent) {
        when (event) {
            is LoginIntent.PhoneNumberChanged -> {
                val phone = PhoneValidator.sanitize(
                    event.phone,
                    currentState.selectedCountry.phoneConfig,
                )
                updateState {
                    copy(
                        phoneNumber = phone,
                        phoneValidationError = phone.takeIf(String::isNotBlank)?.let {
                            PhoneValidator.validate(it, selectedCountry.phoneConfig)
                        },
                        errorMessage = null,
                    )
                }
            }

            is LoginIntent.OtpMethodChanged -> {
                updateState { copy(selectedOtpMethod = event.method) }
            }

            is LoginIntent.CountryCodeChanged -> {
                val phone = PhoneValidator.sanitize(
                    currentState.phoneNumber,
                    event.country.phoneConfig,
                )
                updateState {
                    copy(
                        phoneNumber = phone,
                        selectedCountry = event.country,
                        isCountryDropdownExpanded = false,
                        phoneValidationError = phone.takeIf(String::isNotBlank)?.let {
                            PhoneValidator.validate(it, event.country.phoneConfig)
                        },
                        errorMessage = null,
                    )
                }
            }

            LoginIntent.ToggleCountryDropdown -> {
                updateState { copy(isCountryDropdownExpanded = !isCountryDropdownExpanded) }
            }

            LoginIntent.ContinueWithPhoneClicked -> {
                updateState { copy(currentStep = LoginStep.PHONE_INPUT) }
            }

            is LoginIntent.GoogleSignInClicked -> googleSignIn(
                idToken = event.idToken,
                firstName = event.firstName,
                lastName = event.lastName,
                email = event.email,
                profileImageUrl = event.profileImageUrl,
            )
            is LoginIntent.GoogleSignInFailed -> {
                val error = event.errorMessage?.takeIf { it.isNotBlank() }?.let { AuthUiError.Custom(it) } ?: AuthUiError.VerificationFailed
                updateState { copy(isLoading = false, errorMessage = error) }
            }

            LoginIntent.RequestOtpClicked -> requestOtp()
            LoginIntent.BackClicked -> handleBack()
            LoginIntent.DismissErrorDialog -> updateState { copy(errorMessage = null) }
        }
    }

    private fun handleBack() {
        when (currentState.currentStep) {
            LoginStep.PHONE_INPUT -> updateState {
                copy(
                    currentStep = LoginStep.LANDING,
                    phoneNumber = "",
                    pendingToken = null,
                    googleEmail = null,
                    googleFirstName = null,
                    googleLastName = null,
                    googleProfileImageUrl = null,
                    phoneValidationError = null,
                    errorMessage = null,
                )
            }

            LoginStep.LANDING -> {
                // Not handled here, screen level back or exit app
            }
        }
    }

    private fun googleSignIn(
        idToken: String,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        profileImageUrl: String? = null,
    ) {
        if (currentState.isLoading) {
            println("GoogleAuthVM: Google Sign-In ignored because state is already loading")
            Log.w("GoogleAuthVM", "Google Sign-In ignored because state is already loading")
            return
        }

        println("GoogleAuthVM: Initiating Google Sign-In backend request with ID token length=${idToken.length}, firstName=$firstName, lastName=$lastName")
        Log.i("GoogleAuthVM", "Initiating Google Sign-In backend request with ID token (length=${idToken.length}), firstName=$firstName, lastName=$lastName")
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            val result = googleLoginUseCase(
                idToken = idToken,
                firstName = firstName,
                lastName = lastName,
                email = email,
                profileImageUrl = profileImageUrl,
            )

            result.fold(
                onSuccess = { googleResult ->
                    when (googleResult) {
                        is GoogleLoginResult.Authenticated -> {
                            println("GoogleAuthVM: Result AUTHENTICATED (Nurse ID: ${googleResult.nurse?.id})")
                            Log.i("GoogleAuthVM", "Google Sign-In result: AUTHENTICATED (Nurse ID: ${googleResult.nurse?.id})")
                            updateState { copy(isLoading = false) }
                            resolveDestinationUseCase(googleResult.nurse).fold(
                                onSuccess = { destination ->
                                    println("GoogleAuthVM: Navigating to destination: $destination")
                                    Log.i("GoogleAuthVM", "Navigating to destination: $destination")
                                    sendEffect(LoginEffect.NavigateToDestination(destination))
                                },
                                onFailure = { error ->
                                    println("GoogleAuthVM: Failed to resolve destination: ${error.message}")
                                    Log.e("GoogleAuthVM", "Failed to resolve destination: ${error.message}", error)
                                    updateState {
                                        copy(
                                            errorMessage = error.toAuthUiError(AuthUiError.ProfileLoadFailed),
                                        )
                                    }
                                },
                            )
                        }

                        is GoogleLoginResult.PhoneRequired -> {
                            println("GoogleAuthVM: Result PHONE_REQUIRED (Email: ${googleResult.email})")
                            Log.i("GoogleAuthVM", "Google Sign-In result: PHONE_REQUIRED (Email: ${googleResult.email})")
                            updateState {
                                copy(
                                    isLoading = false,
                                    currentStep = LoginStep.PHONE_INPUT,
                                    pendingToken = googleResult.pendingToken,
                                    googleEmail = googleResult.email,
                                    googleFirstName = googleResult.firstName,
                                    googleLastName = googleResult.lastName,
                                    googleProfileImageUrl = googleResult.profileImageUrl,
                                    errorMessage = null,
                                )
                            }
                        }
                    }
                },
                onFailure = { error ->
                    println("GoogleAuthVM: Google Sign-In backend call failed: ${error.message}")
                    Log.e("GoogleAuthVM", "Google Sign-In failed: ${error.message}", error)
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.toAuthUiError(AuthUiError.VerificationFailed),
                        )
                    }
                },
            )
        }
    }

    private fun requestOtp() {
        if (currentState.isLoading) return

        val validationError = PhoneValidator.validate(
            currentState.phoneNumber,
            currentState.selectedCountry.phoneConfig,
        )
        if (validationError != null) {
            updateState { copy(phoneValidationError = validationError, errorMessage = null) }
            return
        }

        viewModelScope.launch {
            val fullPhoneNumber = PhoneValidator.toInternationalNumber(
                currentState.phoneNumber,
                currentState.selectedCountry.phoneConfig,
            )
            updateState { copy(isLoading = true, errorMessage = null) }

            val result = devLoginUseCase(fullPhoneNumber)

            updateState { copy(isLoading = false) }

            result.fold(
                onSuccess = { otp ->
                    sendEffect(
                        LoginEffect.NavigateToOtp(
                            phone = fullPhoneNumber,
                            method = currentState.selectedOtpMethod,
                            otp = otp,
                            pendingToken = currentState.pendingToken,
                        )
                    )
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            errorMessage = error.toAuthUiError(AuthUiError.SendCodeFailed),
                        )
                    }
                },
            )
        }
    }
}
