package com.carenest.provider.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import androidx.savedstate.serialization.SavedStateConfiguration
import com.carenest.provider.core.navigation.NavigationConfig
import com.carenest.provider.core.navigation.goBack
import com.carenest.provider.core.navigation.navigate
import com.carenest.provider.auth.presentation.auth.login.LoginScreen
import com.carenest.provider.auth.presentation.auth.otp.OtpScreen
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
data object LoginRoute : NavKey

@Serializable
data class OtpRoute(val phone: String) : NavKey

private val authNavigationSerializers = SerializersModule {
    include(NavigationConfig.serializer)
    polymorphic(NavKey::class) {
        subclass(LoginRoute::class, LoginRoute.serializer())
        subclass(OtpRoute::class, OtpRoute.serializer())
    }
}

private val authSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = authNavigationSerializers
}

@Composable
fun ProviderAuthNavigation(
    onAuthSuccess: () -> Unit
) {
    val backStack: SnapshotStateList<NavKey> = rememberSerializable(
        serializer = SnapshotStateListSerializer(
            PolymorphicSerializer(NavKey::class),
        ),
        configuration = authSavedStateConfiguration,
    ) {
        mutableStateListOf<NavKey>().apply {
            navigate(LoginRoute)
        }
    }

    val entryProvider: (NavKey) -> NavEntry<NavKey> = entryProvider {
        entry<LoginRoute> {
            LoginScreen(
                onNavigateToOtp = { phone, _ -> 
                    backStack.navigate(OtpRoute(phone))
                }
            )
        }
        entry<OtpRoute> { route ->
            OtpScreen(
                phone = route.phone,
                onNavigateToHome = onAuthSuccess,
                onNavigateBack = backStack::goBack
            )
        }
    }

    NavDisplay(
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
