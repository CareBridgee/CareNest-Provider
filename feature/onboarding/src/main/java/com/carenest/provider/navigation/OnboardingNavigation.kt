package com.carenest.provider.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.core.navigation.replaceWith
import com.carenest.provider.core.datastore.AuthenticationSession
import com.carenest.provider.presentation.onboarding.OnboardingScreen
import com.carenest.provider.presentation.splash.SplashScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val onboardingNavigationSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(SplashRoute::class, SplashRoute.serializer())
        subclass(OnboardingRoute::class, OnboardingRoute.serializer())
    }
}

fun providerOnboardingStartRoute(): NavKey = SplashRoute

fun EntryProviderScope<NavKey>.providerOnboardingEntries(
    backStack: SnapshotStateList<NavKey>,
    onAuthenticationRequested: () -> Unit,
    onAuthenticatedSessionRestored: (AuthenticationSession) -> Unit,
) {
    entry<SplashRoute> {
        SplashScreen(
            onNavigateToOnboarding = { backStack.replaceWith(OnboardingRoute) },
            onNavigateToAuthentication = onAuthenticationRequested,
            onNavigateToAuthenticatedSession = onAuthenticatedSessionRestored,
        )
    }
    entry<OnboardingRoute> {
        OnboardingScreen(onNavigateToAuthentication = onAuthenticationRequested)
    }
}
