package com.carenest.provider.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.carenest.chat.navigation.ChatRoutes
import com.carenest.chat.navigation.chatSerializers
import com.carenest.chat.navigation.providerChatEntries
import com.carenest.home.navigation.HomeRoutes
import com.carenest.home.navigation.homeSerializers
import com.carenest.home.navigation.providerHomeEntries
import com.carenest.provider.R
import com.carenest.provider.account.navigation.AccountRoutes
import com.carenest.provider.account.navigation.accountNavigationSerializers
import com.carenest.provider.account.navigation.providerAccountEntries
import com.carenest.provider.auth.domain.util.AuthenticationDestination
import com.carenest.provider.auth.navigation.authNavigationSerializers
import com.carenest.provider.auth.navigation.providerAuthEntries
import com.carenest.provider.auth.navigation.providerAuthStartRoute
import com.carenest.provider.core.datastore.AuthenticationSession
import com.carenest.provider.core.datastore.AuthenticationSessionDestination
import com.carenest.provider.core.datastore.AuthenticationState
import com.carenest.provider.core.navigation.goBack
import com.carenest.provider.core.navigation.navigate
import com.carenest.provider.core.navigation.replaceWith
import com.carenest.provider.core.network.socket.service.ActiveReservationService
import com.carenest.provider.designsystem.components.bottomnav.BottomNavItem
import com.carenest.provider.designsystem.components.bottomnav.LocalBottomNavigationContentPadding
import com.carenest.provider.designsystem.components.bottomnav.SPBottomNavigation
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.earnings.navigation.EarningsRoutes
import com.carenest.provider.earnings.navigation.earningsSerializers
import com.carenest.provider.earnings.navigation.providerEarningsEntries
import com.carenest.provider.earnings.navigation.providerEarningsStartRoute
import com.carenest.provider.payouts.navigation.PayoutsRoutes
import com.carenest.provider.payouts.navigation.payoutsSerializers
import com.carenest.provider.payouts.navigation.providerPayoutsEntries
import com.carenest.provider.payouts.navigation.providerPayoutsStartRoute
import com.carenest.provider.profile.navigation.profileCompletionNavigationSerializers
import com.carenest.provider.profile.navigation.providerProfileCompletionEntries
import com.carenest.provider.profile.navigation.providerProfileCompletionStartRoute
import com.carenest.provider.profile.navigation.providerProfileReviewRoute
import com.carenest.request.navigation.RequestRoutes
import com.carenest.request.navigation.providerRequestEntries
import com.carenest.request.navigation.requestSerializers
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import com.carenest.provider.designsystem.R as DesignSystemR
import com.carenest.provider.account.R as AccountR

private val appNavigationSerializers = SerializersModule {
    include(onboardingNavigationSerializers)
    include(authNavigationSerializers)
    include(profileCompletionNavigationSerializers)
    include(homeSerializers)
    include(requestSerializers)
    include(accountNavigationSerializers)
    include(earningsSerializers)
    include(payoutsSerializers)
    include(chatSerializers)

    polymorphic(NavKey::class) {
        subclass(ProviderDashboardRoute::class, ProviderDashboardRoute.serializer())
        subclass(ProviderInfoRoute::class, ProviderInfoRoute.serializer())
    }
}

private val appSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = appNavigationSerializers
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    authenticationState: Flow<AuthenticationState>,
    targetRequestId: String? = null,
    onExitApp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val backStack: SnapshotStateList<NavKey> = rememberSerializable(
        serializer = SnapshotStateListSerializer(PolymorphicSerializer(NavKey::class)),
        configuration = appSavedStateConfiguration,
    ) {
        mutableStateListOf<NavKey>().apply { navigate(providerOnboardingStartRoute()) }
    }

    LaunchedEffect(authenticationState) {
        var hadAuthenticatedSession = false
        authenticationState.collect { state ->
            if (state.isAuthenticated) {
                hadAuthenticatedSession = true
            } else if (hadAuthenticatedSession) {
                hadAuthenticatedSession = false
                backStack.replaceWith(providerAuthStartRoute())
            }
        }
    }

    LaunchedEffect(targetRequestId) {
        if (!targetRequestId.isNullOrBlank()) {
            val currentRoute = backStack.lastOrNull()
            if (currentRoute !is RequestRoutes.OfferConfirmed || (currentRoute as RequestRoutes.OfferConfirmed).requestId != targetRequestId) {
                backStack.navigate(RequestRoutes.OfferConfirmed(targetRequestId))
            }
        }
    }

    fun exitCurrentRoot() {
        if (!backStack.goBack()) {
            onExitApp()
        }
    }

    fun restoreAuthenticatedSession(session: AuthenticationSession) {
        when (session.destination) {
            AuthenticationSessionDestination.COMPLETE_PROFILE ->
                backStack.replaceWith(providerProfileCompletionStartRoute())
            AuthenticationSessionDestination.UNDER_REVIEW,
            AuthenticationSessionDestination.REJECTED -> {
                val nurseId = session.nurseId
                if (nurseId != null) {
                    backStack.replaceWith(providerProfileReviewRoute(nurseId))
                } else {
                    backStack.replaceWith(providerAuthStartRoute())
                }
            }
            AuthenticationSessionDestination.APPROVED -> {
                val activeId = if (!targetRequestId.isNullOrBlank()) {
                    targetRequestId
                } else {
                    ActiveReservationService.getActiveReservationId(context)
                }
                if (!activeId.isNullOrBlank()) {
                    backStack.replaceWith(RequestRoutes.OfferConfirmed(activeId))
                } else {
                    backStack.replaceWith(HomeRoutes.Home)
                }
            }
        }
    }

    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
        providerOnboardingEntries(
            backStack = backStack,
            onAuthenticationRequested = {
                backStack.replaceWith(providerAuthStartRoute())
            },
            onAuthenticatedSessionRestored = ::restoreAuthenticatedSession,
        )
        providerAuthEntries(
            backStack = backStack,
            onAuthenticationSuccess = { destination ->
                when (destination) {
                    AuthenticationDestination.CompleteProfile ->
                        backStack.replaceWith(providerProfileCompletionStartRoute())
                    is AuthenticationDestination.UnderReview ->
                        backStack.replaceWith(providerProfileReviewRoute(destination.nurseId))
                    is AuthenticationDestination.Rejected ->
                        backStack.replaceWith(providerProfileReviewRoute(destination.nurseId))
                    is AuthenticationDestination.Approved ->
                        backStack.replaceWith(HomeRoutes.Home)
                }
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
            },
            onOpenChat = { requestId ->
                backStack.navigate(ChatRoutes.Chat(requestId))
            }
        )
        providerAccountEntries(
            onOpenPublicProfile = { backStack.navigate(AccountRoutes.PublicProfile) },
            onOpenDocuments = { backStack.navigate(AccountRoutes.ProfessionalDocuments) },
            onOpenRatingsAndReviews = { backStack.navigate(AccountRoutes.RatingsAndReviews) },
            onOpenSettings = { backStack.navigate(AccountRoutes.Settings) },
            onOpenEarnings = { backStack.navigate(providerEarningsStartRoute()) },
            onOpenPayouts = { backStack.navigate(providerPayoutsStartRoute()) },
            onOpenWallet = { backStack.navigate(AccountRoutes.Wallet) },
            onOpenSupport = {
                backStack.navigate(
                    ProviderInfoRoute(ProviderInfoDestination.CONTACT_SUPPORT),
                )
            },
            onLogout = {
                backStack.replaceWith(providerAuthStartRoute())
            },
            onNavigateBack = {
                backStack.goBack()
            },
        )

        providerEarningsEntries(
            backStack = backStack,
            onNavigateToPayouts = {
                backStack.navigate(providerPayoutsStartRoute())
            },
            onNavigateBack = {
                backStack.goBack()
            }
        )

        providerPayoutsEntries(
            backStack = backStack,
            onNavigateBackToEarnings = {
                backStack.goBack()
            }
        )

        providerChatEntries(
            backStack = backStack
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

    val currentRoute = backStack.lastOrNull()
    val isBottomNavigationRoute =
        currentRoute == HomeRoutes.Home ||
            currentRoute == RequestRoutes.RequestList ||
            currentRoute == EarningsRoutes.ServiceEarnings ||
            currentRoute == AccountRoutes.ProfileMenu
    val selectedBottomNavIndex = when (currentRoute) {
        is RequestRoutes -> 1
        is EarningsRoutes, is PayoutsRoutes -> 2
        is AccountRoutes -> 3
        else -> 0
    }
    val navEntries = rememberDecoratedNavEntries(
        backStack = backStack,
        entryProvider = entryProvider,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
    )
    val layoutDirection = LocalLayoutDirection.current
    val navigationBarBottomInset = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    Scaffold(
        modifier = modifier,
        containerColor = Theme.colors.backGround,
        bottomBar = {
            if (isBottomNavigationRoute) {
                ProviderBottomNavigation(
                    selectedIndex = selectedBottomNavIndex,
                    onItemSelected = { index ->
                        when (index) {
                            0 -> if (currentRoute !is HomeRoutes.Home) {
                                backStack.replaceWith(HomeRoutes.Home)
                            }
                            1 -> if (currentRoute != RequestRoutes.RequestList) {
                                backStack.replaceWith(RequestRoutes.RequestList)
                            }
                            2 -> if (currentRoute !is EarningsRoutes) {
                                backStack.replaceWith(providerEarningsStartRoute())
                            }
                            3 -> if (currentRoute != AccountRoutes.ProfileMenu) {
                                backStack.replaceWith(AccountRoutes.ProfileMenu)
                            }
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        val bottomNavigationContentPadding =
            (innerPadding.calculateBottomPadding() - navigationBarBottomInset)
                .coerceAtLeast(0.dp)
        CompositionLocalProvider(
            LocalBottomNavigationContentPadding provides bottomNavigationContentPadding,
        ) {
            NavDisplay(
                modifier = Modifier
                    .fillMaxSize()
                    .absolutePadding(
                        left = innerPadding.calculateLeftPadding(layoutDirection),
                        top = innerPadding.calculateTopPadding(),
                        right = innerPadding.calculateRightPadding(layoutDirection),
                        bottom = navigationBarBottomInset,
                    ),
                entries = navEntries,
                onBack = ::exitCurrentRoot,
            )
        }
    }
}

@Composable
private fun ProviderBottomNavigation(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    val items = listOf(
        BottomNavItem(
            label = stringResource(AccountR.string.bottom_nav_home),
            iconRes = DesignSystemR.drawable.ic_home,
            selectedIconRes = DesignSystemR.drawable.ic_home_selected,
        ),
        BottomNavItem(
            label = stringResource(AccountR.string.bottom_nav_active_jobs),
            iconRes = DesignSystemR.drawable.ic_work_outline,
            selectedIconRes = DesignSystemR.drawable.ic_work,
        ),
        BottomNavItem(
            label = stringResource(AccountR.string.bottom_nav_earnings),
            iconRes = DesignSystemR.drawable.ic_wallet_outline,
            selectedIconRes = DesignSystemR.drawable.ic_wallet,
        ),
        BottomNavItem(
            label = stringResource(AccountR.string.bottom_nav_profile),
            iconRes = DesignSystemR.drawable.ic_profile,
            selectedIconRes = DesignSystemR.drawable.ic_profile_selected,
        ),
    )
    SPBottomNavigation(
        items = items,
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
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
