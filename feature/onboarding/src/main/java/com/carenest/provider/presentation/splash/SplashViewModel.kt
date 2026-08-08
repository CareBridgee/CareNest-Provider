package com.carenest.provider.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.core.datastore.AuthenticationSession
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.datastore.TokenManager
import com.carenest.provider.feature.onboarding.domain.usecase.GetOnboardingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal const val SPLASH_MINIMUM_DURATION_MILLIS = 2_500L

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getOnboardingStatus: GetOnboardingStatusUseCase,
    private val tokenManager: TokenManager,
    private val authenticationSessionStore: AuthenticationSessionStore,
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
            val accessToken = async { tokenManager.accessToken.first() }
            val authenticatedSession = async { authenticationSessionStore.session.first() }
            delay(SPLASH_MINIMUM_DURATION_MILLIS)
            val isCompleted = onboardingStatus.await()
            val savedAccessToken = accessToken.await()
            val savedSession = authenticatedSession.await()
                .takeIf { !savedAccessToken.isNullOrBlank() }

            updateState {
                copy(
                    isLoading = false,
                    isOnboardingCompleted = isCompleted,
                )
            }
            navigateOnce(isCompleted, savedSession)
        }
    }

    private fun navigateOnce(
        isCompleted: Boolean,
        savedSession: AuthenticationSession?,
    ) {
        if (hasNavigated) return
        hasNavigated = true

        sendEffect(
            if (!isCompleted) {
                SplashEffect.NavigateToOnboarding
            } else if (savedSession != null) {
                SplashEffect.NavigateToAuthenticatedSession(savedSession)
            } else {
                SplashEffect.NavigateToAuthentication
            },
        )
    }
}
