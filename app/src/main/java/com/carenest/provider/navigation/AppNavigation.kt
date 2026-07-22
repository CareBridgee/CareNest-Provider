package com.carenest.provider.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.carenest.provider.core.navigation.NavigationConfig
import com.carenest.provider.core.navigation.goBack
import com.carenest.provider.core.navigation.navigate
import com.carenest.provider.core.navigation.replaceWith
import com.carenest.provider.feature.onboarding.navigation.OnboardingRoute
import com.carenest.provider.feature.onboarding.navigation.SplashRoute
import com.carenest.provider.feature.onboarding.presentation.onboarding.OnboardingScreen
import com.carenest.provider.feature.onboarding.presentation.splash.SplashScreen
import com.carenest.provider.profile.presentation.ui.registration.RegistrationScreen
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
data object ProviderAuthenticationRoute : NavKey

@Serializable
data object RegistrationRoute : NavKey

@Serializable
data object ApplicationUnderReviewRoute : NavKey

private val appNavigationSerializers = SerializersModule {
    include(NavigationConfig.serializer)

    polymorphic(NavKey::class) {
        subclass(SplashRoute::class, SplashRoute.serializer())
        subclass(OnboardingRoute::class, OnboardingRoute.serializer())
        subclass(ProviderAuthenticationRoute::class, ProviderAuthenticationRoute.serializer())
        subclass(RegistrationRoute::class, RegistrationRoute.serializer())
        subclass(ApplicationUnderReviewRoute::class, ApplicationUnderReviewRoute.serializer())
    }
}

private val appSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = appNavigationSerializers
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val backStack: SnapshotStateList<NavKey> = rememberSerializable(
        serializer = SnapshotStateListSerializer(
            PolymorphicSerializer(NavKey::class),
        ),
        configuration = appSavedStateConfiguration,
    ) {
        mutableStateListOf<NavKey>().apply {
            navigate(SplashRoute)
        }
    }

    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
        entry<SplashRoute> {
            SplashScreen(
                onNavigateToOnboarding = { backStack.replaceWith(OnboardingRoute) },
                onNavigateToAuthentication = {
                    backStack.replaceWith(ProviderAuthenticationRoute)
                },
            )
        }
        entry<OnboardingRoute> {
            OnboardingScreen(
                onNavigateToAuthentication = {
                    backStack.replaceWith(ProviderAuthenticationRoute)
                },
            )
        }
        entry<ProviderAuthenticationRoute> {
            ProviderAuthNavigation(
                onAuthSuccess = {
                    backStack.add(RegistrationRoute)
                }
            )
        }
        entry<RegistrationRoute> {
            RegistrationScreen(
                onNavigateToApplicationUnderReview = {
                    backStack.replaceWith(ApplicationUnderReviewRoute)
                }
            )
        }
    }

    NavDisplay(
        modifier = modifier,
        entries = rememberDecoratedNavEntries(
            backStack = backStack,
            entryProvider = entryProvider,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        ),
        onBack = backStack::goBack,
    )
}


