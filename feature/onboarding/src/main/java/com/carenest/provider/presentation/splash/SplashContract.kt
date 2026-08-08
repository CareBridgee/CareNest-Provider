package com.carenest.provider.presentation.splash

import com.carenest.provider.core.datastore.AuthenticationSession

data class SplashState(
    val isLoading: Boolean = true,
    val isOnboardingCompleted: Boolean? = null,
)

sealed interface SplashIntent {
    data object Initialize : SplashIntent
}

sealed interface SplashEffect {
    data object NavigateToOnboarding : SplashEffect
    data object NavigateToAuthentication : SplashEffect
    data class NavigateToAuthenticatedSession(
        val session: AuthenticationSession,
    ) : SplashEffect
}
