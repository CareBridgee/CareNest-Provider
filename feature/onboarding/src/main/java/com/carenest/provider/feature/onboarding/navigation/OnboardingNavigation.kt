package com.carenest.provider.feature.onboarding.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.core.navigation.replaceWith
import com.carenest.provider.feature.onboarding.presentation.onboarding.OnboardingScreen
import com.carenest.provider.feature.onboarding.presentation.splash.SplashScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

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
) {
    entry<SplashRoute> {
        SplashScreen(
            onNavigateToOnboarding = { backStack.replaceWith(OnboardingRoute) },
            onNavigateToAuthentication = onAuthenticationRequested,
        )
    }
    entry<OnboardingRoute> {
        OnboardingScreen(onNavigateToAuthentication = onAuthenticationRequested)
    }
}
