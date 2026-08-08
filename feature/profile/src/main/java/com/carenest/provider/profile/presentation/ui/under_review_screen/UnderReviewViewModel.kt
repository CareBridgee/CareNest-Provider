package com.carenest.provider.profile.presentation.ui.under_review_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.profile.domain.model.VerificationStatus
import com.carenest.provider.profile.domain.usecase.GetNurseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnderReviewViewModel @Inject constructor(
    private val getNurse: GetNurseUseCase,
) : ViewModel(),
    StateHolder<UnderReviewState> by DefaultStateHolder(UnderReviewState()),
    EffectPublisher<UnderReviewEvent> by DefaultEffectPublisher() {

    fun load(nurseId: String) {
        if (nurseId.isBlank() || (currentState.nurseId == nurseId && currentState.isLoading)) return
        viewModelScope.launch {
            updateState { copy(nurseId = nurseId, isLoading = true, error = null) }
            getNurse(nurseId).fold(
                onSuccess = { nurse ->
                    updateState {
                        copy(
                            isLoading = false,
                            profileImageUrl = nurse.profileImageUrl,
                            underReviewState = when (nurse.verificationStatus) {
                                VerificationStatus.UNDER_REVIEW -> ReviewState.UnderReview
                                VerificationStatus.APPROVED -> ReviewState.Success
                                VerificationStatus.REJECTED -> ReviewState.Error
                            },
                            rejectionReason = nurse.rejectionReason.orEmpty(),
                            failedSteps = nurse.failedSteps,
                        )
                    }
                },
                onFailure = { error ->
                    updateState { copy(isLoading = false, error = error.message ?: "profile_status_load_failed") }
                },
            )
        }
    }

    fun onIntent(intent: UnderReviewIntent) {
        when (intent) {
            UnderReviewIntent.OnBackClick -> sendEffect(UnderReviewEvent.OnBackClick)
            UnderReviewIntent.OnGoToHomeClick -> sendEffect(UnderReviewEvent.OnGoToHomeClick)
            UnderReviewIntent.OnContactSupportClick -> sendEffect(UnderReviewEvent.OnContactSupportClick)
            UnderReviewIntent.OnBackToLoginClick -> sendEffect(UnderReviewEvent.OnBackToLoginClick)
            UnderReviewIntent.OnRetry -> load(currentState.nurseId)
        }
    }
}
