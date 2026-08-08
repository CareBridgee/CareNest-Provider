package com.carenest.provider.auth.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.auth.presentation.auth.login.LoginScreen
import com.carenest.provider.auth.presentation.auth.otp.OtpScreen
import com.carenest.provider.auth.domain.repository.AuthenticationDestination
import com.carenest.provider.core.navigation.goBack
import com.carenest.provider.core.navigation.navigate
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val authNavigationSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(LoginRoute::class, LoginRoute.serializer())
        subclass(OtpRoute::class, OtpRoute.serializer())
    }
}

fun providerAuthStartRoute(): NavKey = LoginRoute

fun EntryProviderScope<NavKey>.providerAuthEntries(
    backStack: SnapshotStateList<NavKey>,
    onAuthenticationSuccess: (AuthenticationDestination) -> Unit,
) {
    entry<LoginRoute> {
        LoginScreen(
            onNavigateToOtp = { phone, _ -> backStack.navigate(OtpRoute(phone)) },
        )
    }
    entry<OtpRoute> { route ->
        OtpScreen(
            phone = route.phone,
            onAuthenticationSuccess = onAuthenticationSuccess,
            onNavigateBack = { backStack.goBack() },
        )
    }
}
