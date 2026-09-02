package com.carenest.provider.auth.presentation.auth.otp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.auth.domain.usecase.VerifyOtpUseCase
import com.carenest.provider.auth.domain.usecase.DevLoginUseCase
import com.carenest.provider.auth.domain.usecase.ResolveAuthenticationDestinationUseCase
import com.carenest.provider.auth.domain.model.AuthenticatedNurse
import com.carenest.provider.auth.domain.util.AuthenticationDestination
import com.carenest.provider.auth.domain.validation.PhoneValidator
import com.carenest.provider.auth.presentation.auth.AuthUiError
import com.carenest.provider.auth.presentation.auth.toAuthUiError
import com.carenest.provider.core.datastore.AuthenticationSession
import com.carenest.provider.core.datastore.AuthenticationCredentials
import com.carenest.provider.core.datastore.AuthenticationSessionDestination
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val devLoginUseCase: DevLoginUseCase,
    private val resolveDestination: ResolveAuthenticationDestinationUseCase,
    private val authenticationSessionStore: AuthenticationSessionStore,
) : ViewModel(),
    StateHolder<OtpState> by DefaultStateHolder(OtpState()),
    EffectPublisher<OtpEffect> by DefaultEffectPublisher() {

    private var verifiedNurse: AuthenticatedNurse? = null
    private var verifiedCredentials: AuthenticationCredentials? = null
    private var countdownJob: Job? = null

    init {
        startCountdown()
    }

    fun onEvent(event: OtpIntent) {
        when (event) {
            is OtpIntent.PhoneNumberChanged -> updateState { copy(phoneNumber = event.phone) }
            is OtpIntent.PendingTokenChanged -> updateState { copy(pendingToken = event.pendingToken) }
            is OtpIntent.OtpCodeChanged -> updateState { copy(otpCode = event.otp, errorMessage = null) }
            OtpIntent.VerifyOtpClicked -> verifyOtp()
            OtpIntent.RetryDestinationResolution -> retryDestinationResolution()
            OtpIntent.BackClicked -> sendEffect(OtpEffect.NavigateBack)
            OtpIntent.ResendClicked -> resendOtp()
            OtpIntent.DismissErrorDialog -> updateState { copy(errorMessage = null) }
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        updateState {
            copy(
                remainingSeconds = RESEND_SECONDS,
                countdownGeneration = countdownGeneration + 1,
            )
        }
        countdownJob = viewModelScope.launch {
            for (seconds in (RESEND_SECONDS - 1) downTo 0) {
                delay(ONE_SECOND_MILLIS)
                updateState { copy(remainingSeconds = seconds) }
            }
        }
    }

    private fun resendOtp() {
        if (!currentState.canResend) return

        val phoneNumber = PhoneValidator.normalizeInternationalNumber(currentState.phoneNumber)
        if (phoneNumber == null) {
            updateState { copy(errorMessage = AuthUiError.InvalidPhone) }
            return
        }

        updateState { copy(isResending = true, errorMessage = null) }
        viewModelScope.launch {
            devLoginUseCase(phoneNumber).fold(
                onSuccess = { otp ->
                    updateState {
                        copy(
                            otpCode = otp,
                            isResending = false,
                            errorMessage = null,
                        )
                    }
                    startCountdown()
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            isResending = false,
                            remainingSeconds = 0,
                            errorMessage = error.toAuthUiError(AuthUiError.ResendCodeFailed),
                        )
                    }
                },
            )
        }
    }

    private fun verifyOtp() {
        if (currentState.isLoading) return

        if (currentState.otpCode.length != 6) {
            updateState { copy(errorMessage = AuthUiError.OtpIncomplete) }
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            val sanitizedPhone = PhoneValidator.normalizeInternationalNumber(currentState.phoneNumber)
            if (sanitizedPhone == null) {
                updateState { copy(isLoading = false, errorMessage = AuthUiError.InvalidPhone) }
                return@launch
            }
            val result = verifyOtpUseCase(sanitizedPhone, currentState.otpCode, currentState.pendingToken)

            result.fold(
                onSuccess = { nurse ->
                    verifiedNurse = nurse
                    val sessionState = kotlinx.coroutines.withTimeoutOrNull(2000) {
                        authenticationSessionStore.state.first { it.credentials?.isComplete == true }
                    } ?: authenticationSessionStore.state.first()
                    verifiedCredentials = sessionState.credentials
                    resolveAuthenticatedDestination(nurse)
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            canRetryDestination = false,
                            errorMessage = error.toAuthUiError(AuthUiError.VerificationFailed),
                        )
                    }
                }
            )
        }
    }

    private fun retryDestinationResolution() {
        if (currentState.isLoading) return
        // The OTP was already consumed by the first verify; re-resolving only
        // avoids burning a second code and keeps the freshly minted tokens.
        val nurse = verifiedNurse
        if (nurse != null && verifiedCredentials != null) {
            viewModelScope.launch {
                updateState { copy(isLoading = true) }
                resolveAuthenticatedDestination(nurse)
            }
        } else {
            verifyOtp()
        }
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
                    ?: authenticationSessionStore.state.first().credentials
                if (expectedCredentials == null) {
                    updateState {
                        copy(
                            isLoading = false,
                            canRetryDestination = false,
                            errorMessage = AuthUiError.ProfileLoadFailed,
                        )
                    }
                    return@fold
                }
                val completed = authenticationSessionStore.completeAuthentication(
                    expectedCredentials = expectedCredentials,
                    session = destination.toSavedSession(phoneNumber),
                )
                if (!completed) {
                    // The stored credentials moved underneath us (concurrent login);
                    // only retire the pair this flow owns, never the live session.
                    authenticationSessionStore.clearSessionIfCurrent(expectedCredentials)
                    verifiedNurse = null
                    verifiedCredentials = null
                    updateState {
                        copy(
                            isLoading = false,
                            canRetryDestination = false,
                            errorMessage = AuthUiError.ProfileLoadFailed,
                        )
                    }
                    return@fold
                }
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
                // Keep the freshly minted tokens: a failed destination lookup
                // (timeout, 5xx) says nothing about credential validity, and the
                // retry path re-resolves without consuming another OTP.
                updateState {
                    copy(
                        isLoading = false,
                        canRetryDestination = true,
                        errorMessage = error.toAuthUiError(AuthUiError.ProfileLoadFailed),
                    )
                }
            },
        )
    }

    private companion object {
        const val RESEND_SECONDS = 30
        const val ONE_SECOND_MILLIS = 1_000L
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
