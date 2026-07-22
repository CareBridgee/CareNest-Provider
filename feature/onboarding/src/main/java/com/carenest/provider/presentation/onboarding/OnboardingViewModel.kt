package com.carenest.provider.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.feature.onboarding.domain.usecase.CompleteOnboardingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val completeOnboarding: CompleteOnboardingUseCase,
) : ViewModel(),
    StateHolder<OnboardingState> by DefaultStateHolder(OnboardingState()),
    EffectPublisher<OnboardingEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.PageChanged -> changePage(intent.pageIndex)
            OnboardingIntent.NextClicked -> moveToNextPage()
            OnboardingIntent.BackToStartClicked -> moveToStart()
            OnboardingIntent.SkipClicked,
            OnboardingIntent.FinalActionClicked,
            -> complete()
        }
    }

    private fun changePage(pageIndex: Int) {
        if (currentState.isCompleting) return

        val validPage = pageIndex.coerceIn(0, currentState.totalPageCount - 1)
        updateState { copy(currentPageIndex = validPage) }
    }

    private fun moveToNextPage() {
        if (currentState.isCompleting) return

        if (currentState.isLastPage) {
            complete()
            return
        }

        val nextPage = currentState.currentPageIndex + 1
        sendEffect(OnboardingEffect.MoveToPage(nextPage))
    }

    private fun moveToStart() {
        if (currentState.isCompleting || currentState.currentPageIndex == 0) return

        updateState { copy(currentPageIndex = 0) }
        sendEffect(OnboardingEffect.MoveToPage(0))
    }

    private fun complete() {
        if (currentState.isCompleting) return

        updateState { copy(isCompleting = true) }

        viewModelScope.launch {
            runCatching { completeOnboarding() }
                .onSuccess {
                    sendEffect(OnboardingEffect.NavigateToAuthentication)
                }
                .onFailure {
                    updateState { copy(isCompleting = false) }
                }
        }
    }
}
