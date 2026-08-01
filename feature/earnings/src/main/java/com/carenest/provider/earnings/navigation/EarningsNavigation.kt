package com.carenest.provider.earnings.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.earnings.presentation.EarningsScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val earningsSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(EarningsRoutes.ServiceEarnings::class, EarningsRoutes.ServiceEarnings.serializer())
    }
}

fun providerEarningsStartRoute(): NavKey = EarningsRoutes.ServiceEarnings

fun EntryProviderScope<NavKey>.providerEarningsEntries(
    backStack: SnapshotStateList<NavKey>,
    onNavigateToPayouts: () -> Unit
) {
    entry<EarningsRoutes.ServiceEarnings> {
        EarningsScreen(
            onNavigateToPayouts = onNavigateToPayouts
        )
    }
}
