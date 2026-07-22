package com.carenest.provider.profile.presentation.ui.under_review_screen

import androidx.lifecycle.ViewModel
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UnderReviewViewModel @Inject constructor() : ViewModel(),
    StateHolder<UnderReviewState> by DefaultStateHolder(UnderReviewState()),
    EffectPublisher<UnderReviewEvent> by DefaultEffectPublisher() {

    fun onIntent(intent: UnderReviewIntent) {
        when (intent) {
            UnderReviewIntent.OnBackClick -> {
                sendEffect(UnderReviewEvent.OnBackClick)
            }

            UnderReviewIntent.OnGoToHomeClick -> {
                sendEffect(UnderReviewEvent.OnGoToHomeClick)
            }

            UnderReviewIntent.OnContactSupportClick -> {
                sendEffect(UnderReviewEvent.OnContactSupportClick)
            }

            UnderReviewIntent.OnBackToLoginClick -> {
                sendEffect(UnderReviewEvent.OnBackToLoginClick)
            }
        }
    }

    fun setReviewState(reviewState: ReviewState) {
        updateState {
            copy(
                underReviewState = reviewState,
                error = null
            )
        }
    }

    fun showError(message: String) {
        updateState {
            copy(error = message)
        }
    }

    fun clearError() {
        updateState {
            copy(error = null)
        }
    }
}
