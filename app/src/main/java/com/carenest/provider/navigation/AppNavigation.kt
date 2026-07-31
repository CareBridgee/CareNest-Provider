package com.carenest.provider.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.carenest.home.navigation.HomeRoutes
import com.carenest.home.navigation.homeSerializers
import com.carenest.home.navigation.providerHomeEntries
import com.carenest.home.navigation.providerHomeStartRoute
import com.carenest.provider.R
import com.carenest.provider.auth.navigation.authNavigationSerializers
import com.carenest.provider.auth.navigation.providerAuthEntries
import com.carenest.provider.auth.navigation.providerAuthStartRoute
import com.carenest.provider.core.navigation.goBack
import com.carenest.provider.core.navigation.navigate
import com.carenest.provider.core.navigation.replaceWith
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.navigation.profileCompletionNavigationSerializers
import com.carenest.provider.profile.navigation.providerProfileCompletionEntries
import com.carenest.provider.profile.navigation.providerProfileCompletionStartRoute
import com.carenest.request.navigation.RequestRoutes
import com.carenest.request.navigation.providerRequestEntries
import com.carenest.request.navigation.providerRequestStartRoute
import com.carenest.request.navigation.requestSerializers
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private val appNavigationSerializers = SerializersModule {
    include(onboardingNavigationSerializers)
    include(authNavigationSerializers)
    include(profileCompletionNavigationSerializers)
    include(homeSerializers)
    include(requestSerializers)

    polymorphic(NavKey::class) {
        subclass(ProviderDashboardRoute::class, ProviderDashboardRoute.serializer())
        subclass(ProviderInfoRoute::class, ProviderInfoRoute.serializer())
    }
}

private val appSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = appNavigationSerializers
}

@Composable
fun AppNavigation(
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStack: SnapshotStateList<NavKey> = rememberSerializable(
        serializer = SnapshotStateListSerializer(PolymorphicSerializer(NavKey::class)),
        configuration = appSavedStateConfiguration,
    ) {
        mutableStateListOf<NavKey>().apply { navigate(providerRequestStartRoute()) }
    }

    fun exitCurrentRoot() {
        if (!backStack.goBack()) {
            onExitApp()
        }
    }

    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
        providerOnboardingEntries(
            backStack = backStack,
            onAuthenticationRequested = {
                backStack.replaceWith(providerAuthStartRoute())
            },
        )
        providerAuthEntries(
            backStack = backStack,
            onAuthenticationSuccess = {
                backStack.replaceWith(providerProfileCompletionStartRoute())
            },
        )
        providerProfileCompletionEntries(
            backStack = backStack,
            onBackToAuthentication = {
                backStack.replaceWith(providerAuthStartRoute())
            },
            onOpenDashboard = {
                backStack.replaceWith(HomeRoutes.Home)
            },
            onOpenContactSupport = {
                backStack.navigate(
                    ProviderInfoRoute(ProviderInfoDestination.CONTACT_SUPPORT),
                )
            },
            onOpenCommunityGuidelines = {
                backStack.navigate(
                    ProviderInfoRoute(ProviderInfoDestination.COMMUNITY_GUIDELINES),
                )
            },
            onExitRequested = ::exitCurrentRoot,
        )

        providerHomeEntries(
            backStack = backStack,
            onViewAllRequests = {
                backStack.navigate(RequestRoutes.RequestList)
            },
            onOfferConfirmed = { requestId ->
                backStack.navigate(RequestRoutes.OfferConfirmed(requestId))
            }
        )

        providerRequestEntries(
            backStack = backStack,
            onNavigateHome = {
                backStack.replaceWith(HomeRoutes.Home)
            }
        )

        entry<ProviderInfoRoute> { route ->
            val title = when (route.destination) {
                ProviderInfoDestination.CONTACT_SUPPORT -> {
                    stringResource(R.string.contact_support_title)
                }

                ProviderInfoDestination.COMMUNITY_GUIDELINES -> {
                    stringResource(R.string.community_guidelines_title)
                }
            }
            val message = when (route.destination) {
                ProviderInfoDestination.CONTACT_SUPPORT -> {
                    stringResource(R.string.contact_support_message)
                }

                ProviderInfoDestination.COMMUNITY_GUIDELINES -> {
                    stringResource(R.string.community_guidelines_message)
                }
            }

            AppPlaceholderScreen(
                title = title,
                message = message,
                onNavigateBack = { backStack.goBack() },
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
        onBack = ::exitCurrentRoot,
    )
}

@Composable
private fun AppPlaceholderScreen(
    title: String,
    message: String,
    onNavigateBack: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        CareNestTopBar(
            title = title,
            leading = onNavigateBack?.let { TopBarLeading.Back(it) },
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Theme.spacing.large),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = message,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}
