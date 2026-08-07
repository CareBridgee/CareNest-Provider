package com.carenest.request.presentation.ui.visit_summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.request.domain.usecase.SubmitVisitRatingUseCase
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.request.domain.usecase.GetVisitSummaryUseCase
import com.carenest.request.presentation.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitCompletedViewModel @Inject constructor(
    private val getVisitSummaryUseCase: GetVisitSummaryUseCase,
    private val submitVisitRatingUseCase: SubmitVisitRatingUseCase,
) : ViewModel(), EffectPublisher<VisitCompletedEffect> by DefaultEffectPublisher(),
    StateHolder<VisitCompletedState> by DefaultStateHolder(VisitCompletedState()) {

    private var requestId: String? = null

    fun handleIntent(intent: VisitCompletedIntent) {
        when (intent) {
            is VisitCompletedIntent.LoadVisitSummary -> loadVisitSummary(intent.requestId)
            is VisitCompletedIntent.OnStarSelected -> updateState { copy(selectedRating = intent.rating) }
            VisitCompletedIntent.OnSubmitRatingClicked -> submitRating()
            VisitCompletedIntent.OnDismissRatingDialogClicked -> updateState {
                copy(showRatingDialog = false)
            }
            VisitCompletedIntent.OnBackToHomeClicked -> sendEffect(VisitCompletedEffect.NavigateHome)
            VisitCompletedIntent.OnErrorDismissed -> updateState { copy(errorMessage = null) }
        }
    }

    private fun loadVisitSummary(requestId: String) {
        this.requestId = requestId
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            getVisitSummaryUseCase(requestId)
                .onSuccess { summary ->
                    updateState {
                        copy(isLoading = false, summary = summary, showRatingDialog = true)
                    }
                }
                .onFailure { throwable ->
                    updateState { copy(isLoading = false, errorMessage = throwable.message) }
                    sendEffect(VisitCompletedEffect.ShowError(throwable.toUiText()))
                }
        }
    }

    private fun submitRating() {
        val id = requestId ?: return
        val rating = currentState.selectedRating
        if (rating <= 0 || currentState.isSubmittingRating) return

        viewModelScope.launch {
            updateState { copy(isSubmittingRating = true) }

            submitVisitRatingUseCase(id, rating)
                .onSuccess {
                    updateState { copy(isSubmittingRating = false, showRatingDialog = false) }
                    sendEffect(VisitCompletedEffect.RatingSubmitted)
                }
                .onFailure { throwable ->
                    updateState { copy(isSubmittingRating = false) }
                    sendEffect(VisitCompletedEffect.ShowError(throwable.toUiText()))
                }
        }
    }
}
