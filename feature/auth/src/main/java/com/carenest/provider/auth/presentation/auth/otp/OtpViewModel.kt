package com.carenest.provider.auth.presentation.auth.otp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.auth.domain.usecase.VerifyOtpUseCase
import com.carenest.provider.auth.domain.usecase.ResolveAuthenticationDestinationUseCase
import com.carenest.provider.auth.domain.repository.AuthenticatedNurse
import com.carenest.provider.auth.domain.util.AuthenticationDestination
import com.carenest.provider.core.datastore.AuthenticationSession
import com.carenest.provider.core.datastore.AuthenticationCredentials
import com.carenest.provider.core.datastore.AuthenticationSessionDestination
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val resolveDestination: ResolveAuthenticationDestinationUseCase,
    private val authenticationSessionStore: AuthenticationSessionStore,
) : ViewModel(),
    StateHolder<OtpState> by DefaultStateHolder(OtpState()),
    EffectPublisher<OtpEffect> by DefaultEffectPublisher() {

    private var verifiedNurse: AuthenticatedNurse? = null
    private var verifiedCredentials: AuthenticationCredentials? = null


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
            val result = verifyOtpUseCase(sanitizedPhone, currentState.otpCode)

            result.fold(
                onSuccess = { nurse ->
                    verifiedNurse = nurse
                    verifiedCredentials = authenticationSessionStore.state.first().credentials
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
        if (currentState.isLoading) return
        verifyOtp()
    }

    private suspend fun resolveAuthenticatedDestination(nurse: AuthenticatedNurse?) {
        val phoneNumber = currentState.phoneNumber.normalizedPhoneNumber()
        val savedSession = authenticationSessionStore.session.first()
        resolveDestination(nurse).fold(
            onSuccess = { serverDestination ->
                val destination = serverDestination.withSavedProgressFallback(
                    savedSession = savedSession,
                    phoneNumber = phoneNumber,
                )
                val expectedCredentials = verifiedCredentials
                if (expectedCredentials == null) {
                    authenticationSessionStore.clearSession()
                    updateState {
                        copy(
                            isLoading = false,
                            canRetryDestination = false,
                            errorMessage = "Unable to initialize the authenticated session",
                        )
                    }
                    return@fold
                }
                val completed = authenticationSessionStore.completeAuthentication(
                    expectedCredentials = expectedCredentials,
                    session = destination.toSavedSession(phoneNumber),
                )
                if (!completed) {
                    authenticationSessionStore.clearSession()
                    verifiedNurse = null
                    verifiedCredentials = null
                    updateState {
                        copy(
                            isLoading = false,
                            canRetryDestination = false,
                            errorMessage = "Unable to initialize the authenticated session",
                        )
                    }
                    return@fold
                }
                Log.d(
                    "AuthRouting",
                    "nurseStatus=${nurse?.verificationStatus}, " +
                        "serverDestination=${serverDestination::class.simpleName}, " +
                        "destination=${destination::class.simpleName}",
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
                authenticationSessionStore.clearSession()
                verifiedNurse = null
                verifiedCredentials = null
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

private fun AuthenticationDestination.toSavedSession(phoneNumber: String): AuthenticationSession = when (this) {
    AuthenticationDestination.CompleteProfile -> AuthenticationSession(
        destination = AuthenticationSessionDestination.COMPLETE_PROFILE,
        phoneNumber = phoneNumber,
    )
    is AuthenticationDestination.UnderReview -> AuthenticationSession(
        destination = AuthenticationSessionDestination.UNDER_REVIEW,
        nurseId = nurseId,
        phoneNumber = phoneNumber,
    )
    is AuthenticationDestination.Rejected -> AuthenticationSession(
        destination = AuthenticationSessionDestination.REJECTED,
        nurseId = nurseId,
        phoneNumber = phoneNumber,
    )
    is AuthenticationDestination.Approved -> AuthenticationSession(
        destination = AuthenticationSessionDestination.APPROVED,
        nurseId = nurseId,
        phoneNumber = phoneNumber,
    )
}

internal fun AuthenticationDestination.withSavedProgressFallback(
    savedSession: AuthenticationSession?,
    phoneNumber: String,
): AuthenticationDestination {
    if (this != AuthenticationDestination.CompleteProfile ||
        savedSession?.phoneNumber != phoneNumber
    ) {
        return this
    }

    val nurseId = savedSession.nurseId ?: return this
    return when (savedSession.destination) {
        AuthenticationSessionDestination.COMPLETE_PROFILE -> this
        AuthenticationSessionDestination.UNDER_REVIEW ->
            AuthenticationDestination.UnderReview(nurseId)
        AuthenticationSessionDestination.REJECTED ->
            AuthenticationDestination.Rejected(nurseId)
        AuthenticationSessionDestination.APPROVED ->
            AuthenticationDestination.Approved(nurseId)
    }
}

private fun String.normalizedPhoneNumber(): String =
    "+${replace(Regex("[^0-9]"), "")}"
