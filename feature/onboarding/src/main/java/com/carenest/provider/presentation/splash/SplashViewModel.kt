package com.carenest.provider.feature.onboarding.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.feature.onboarding.domain.usecase.GetOnboardingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal const val SPLASH_MINIMUM_DURATION_MILLIS = 2_000L

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getOnboardingStatus: GetOnboardingStatusUseCase,
) : ViewModel(),
    StateHolder<SplashState> by DefaultStateHolder(SplashState()),
    EffectPublisher<SplashEffect> by DefaultEffectPublisher() {

    private var hasInitialized = false
    private var hasNavigated = false

    init {
        onIntent(SplashIntent.Initialize)
    }

    fun onIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.Initialize -> initialize()
        }
    }

    private fun initialize() {
        if (hasInitialized) return
        hasInitialized = true

        viewModelScope.launch {
            val onboardingStatus = async { getOnboardingStatus().first() }
            delay(SPLASH_MINIMUM_DURATION_MILLIS)
            val isCompleted = onboardingStatus.await()

            updateState {
                copy(
                    isLoading = false,
                    isOnboardingCompleted = isCompleted,
                )
            }
            navigateOnce(isCompleted)
        }
    }

    private fun navigateOnce(isCompleted: Boolean) {
        if (hasNavigated) return
        hasNavigated = true

        sendEffect(
            if (isCompleted) {
                SplashEffect.NavigateToAuthentication
            } else {
                SplashEffect.NavigateToOnboarding
            },
        )
    }
}
