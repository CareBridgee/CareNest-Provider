package com.carenest.provider.auth.presentation.auth.otp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.auth.domain.usecase.VerifyOtpUseCase
import com.carenest.provider.auth.domain.usecase.ResolveAuthenticationDestinationUseCase
import com.carenest.provider.auth.domain.repository.AuthenticatedNurse
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resolveDestination: ResolveAuthenticationDestinationUseCase,
) : ViewModel(),
    StateHolder<OtpState> by DefaultStateHolder(OtpState()),
    EffectPublisher<OtpEffect> by DefaultEffectPublisher() {

    private var verifiedNurse: AuthenticatedNurse? = null
    private var otpVerified: Boolean = false


    fun onEvent(event: OtpIntent) {
        when (event) {
            is OtpIntent.PhoneNumberChanged -> updateState {
                copy(
                    phoneNumber = event.phone,
                    otpCode = event.otp ?: otpCode
                )
            }
            is OtpIntent.OtpCodeChanged -> updateState { copy(otpCode = event.otp, errorMessage = null) }
            OtpIntent.VerifyOtpClicked -> verifyOtp()
            OtpIntent.RetryDestinationResolution -> retryDestinationResolution()
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

            result.fold(
                onSuccess = { nurse ->
                    verifiedNurse = nurse
                    otpVerified = true
                    resolveAuthenticatedDestination(nurse)
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            canRetryDestination = false,
                            errorMessage = error.message ?: "Verification failed",
                        )
                    }
                }
            )
        }
    }

    private fun retryDestinationResolution() {
        if (!otpVerified || currentState.isLoading) return
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            resolveAuthenticatedDestination(verifiedNurse)
        }
    }

    private suspend fun resolveAuthenticatedDestination(nurse: AuthenticatedNurse?) {
        resolveDestination(nurse).fold(
            onSuccess = { destination ->
                Log.d(
                    "AuthRouting",
                    "nurseStatus=${nurse?.verificationStatus}, destination=${destination::class.simpleName}",
                )
                updateState {
                    copy(
                        isLoading = false,
                        isSuccess = true,
                        canRetryDestination = false,
                    )
                }
                sendEffect(OtpEffect.AuthenticationSucceeded(destination))
            },
            onFailure = { error ->
                updateState {
                    copy(
                        isLoading = false,
                        canRetryDestination = true,
                        errorMessage = error.message ?: "Unable to determine account destination",
                    )
                }
            },
        )
    }
}
