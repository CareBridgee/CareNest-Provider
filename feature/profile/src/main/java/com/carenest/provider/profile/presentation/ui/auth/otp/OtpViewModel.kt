package com.carenest.presentation.ui.auth.otp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.auth.VerifyOtpUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel(),
    StateHolder<OtpState> by DefaultStateHolder(OtpState()),
    EffectPublisher<OtpEffect> by DefaultEffectPublisher() {


    fun onEvent(event: OtpIntent) {
        when (event) {
            is OtpIntent.PhoneNumberChanged -> updateState { copy(phoneNumber = event.phone) }
            is OtpIntent.OtpCodeChanged -> updateState { copy(otpCode = event.otp, errorMessage = null) }
            OtpIntent.VerifyOtpClicked -> verifyOtp()
            OtpIntent.BackClicked -> sendEffect(OtpEffect.NavigateBack)
            OtpIntent.ResendClicked -> { /* TODO: Resend OTP */ }
        }
    }

    private fun verifyOtp() {
        if (currentState.otpCode.length != 6) {
            updateState { copy(errorMessage = "Invalid OTP code") }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            val digitsOnly = currentState.phoneNumber.replace(Regex("[^0-9]"), "")
            val sanitizedPhone = "+$digitsOnly"
            Log.d("OtpViewModel", "Verifying OTP for phone: $sanitizedPhone")
            
            val result = verifyOtpUseCase(sanitizedPhone, currentState.otpCode)
            
            updateState { copy(isLoading = false) }

            result.fold(
                onSuccess = { authResult ->
                    // TODO: Save tokens to DataStore/EncryptedSharedPreferences
                    updateState { copy(isSuccess = true) }
                    sendEffect(OtpEffect.NavigateToHome)
                },
                onFailure = { error ->
                    updateState { copy(errorMessage = error.message ?: "Verification failed") }
                }
            )
        }
    }
}
