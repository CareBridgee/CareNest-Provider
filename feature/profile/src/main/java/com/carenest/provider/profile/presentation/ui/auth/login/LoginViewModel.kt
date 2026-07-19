package com.carenest.presentation.ui.auth.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.auth.LoginWithPhoneUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithPhoneUseCase: LoginWithPhoneUseCase
) : ViewModel(),
    StateHolder<LoginState> by DefaultStateHolder(LoginState()),
    EffectPublisher<LoginEffect> by DefaultEffectPublisher() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    fun onEvent(event: LoginIntent) {
        when (event) {
            is LoginIntent.PhoneNumberChanged -> {
                updateState { copy(phoneNumber = event.phone, errorMessage = null) }
            }

            is LoginIntent.OtpMethodChanged -> {
                updateState { copy(selectedOtpMethod = event.method) }
            }

            is LoginIntent.CountryCodeChanged -> {
                updateState { copy(selectedCountry = event.country, isCountryDropdownExpanded = false) }
            }

            LoginIntent.ToggleCountryDropdown -> {
                updateState { copy(isCountryDropdownExpanded = !isCountryDropdownExpanded) }
            }

            LoginIntent.ContinueWithPhoneClicked -> {
                updateState { copy(currentStep = LoginStep.PHONE_INPUT) }
            }

            LoginIntent.RequestOtpClicked -> requestOtp()
            LoginIntent.BackClicked -> handleBack()
        }
    }

    private fun handleBack() {
        when (currentState.currentStep) {
            LoginStep.PHONE_INPUT -> updateState {
                copy(
                    currentStep = LoginStep.LANDING,
                    phoneNumber = ""
                )
            }

            LoginStep.LANDING -> {
                // Not handled here, screen level back or exit app
            }
        }
    }

    private fun requestOtp() {
        if (currentState.phoneNumber.isBlank()) {
            updateState { copy(errorMessage = "Phone number is required") }
            return
        }

        viewModelScope.launch {

            val rawPhoneNumber = "${currentState.selectedCountry.code}${currentState.phoneNumber}"
            val digitsOnly = rawPhoneNumber.replace(Regex("[^0-9]"), "")
            val fullPhoneNumber = "+$digitsOnly"
            Log.d(TAG, "requestOtp: $fullPhoneNumber")

            updateState { copy(isLoading = true, errorMessage = null) }

            val result = loginWithPhoneUseCase(fullPhoneNumber)

            updateState { copy(isLoading = false) }

            result.fold(
                onSuccess = {
                    Log.d(TAG, "requestOtp success")
                    sendEffect(
                        LoginEffect.NavigateToOtp(
                            fullPhoneNumber,
                            currentState.selectedOtpMethod
                        )
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "requestOtp failed: ${error.message}", error)
                    updateState {
                        copy(
                            errorMessage = error.message ?: "Something went wrong. Please try again."
                        )
                    }
                }
            )
        }
    }
}
