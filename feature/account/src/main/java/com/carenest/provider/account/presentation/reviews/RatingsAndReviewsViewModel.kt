package com.carenest.provider.account.presentation.reviews

import androidx.lifecycle.ViewModel
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RatingsAndReviewsViewModel @Inject constructor() : ViewModel(),
    StateHolder<RatingsAndReviewsUiState> by DefaultStateHolder(RatingsAndReviewsUiState()),
    EffectPublisher<RatingsAndReviewsEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: RatingsAndReviewsIntent) {
        when (intent) {
            RatingsAndReviewsIntent.BackClicked ->
                sendEffect(RatingsAndReviewsEffect.NavigateBack)
            is RatingsAndReviewsIntent.FilterSelected ->
                updateState { copy(selectedFilter = intent.filter) }
            RatingsAndReviewsIntent.LoadMoreClicked ->
                sendEffect(RatingsAndReviewsEffect.LoadMoreReviews)
        }
    }
}
