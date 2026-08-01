package com.carenest.provider.payouts.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.provider.payouts.presentation.PayoutsScreen
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val payoutsSerializers = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(PayoutsRoutes.PayoutsAndWithdrawals::class, PayoutsRoutes.PayoutsAndWithdrawals.serializer())
    }
}

fun providerPayoutsStartRoute(): NavKey = PayoutsRoutes.PayoutsAndWithdrawals

fun EntryProviderScope<NavKey>.providerPayoutsEntries(
    backStack: SnapshotStateList<NavKey>,
    onNavigateBackToEarnings: () -> Unit
) {
    entry<PayoutsRoutes.PayoutsAndWithdrawals> {
        PayoutsScreen(
            onNavigateBackToEarnings = onNavigateBackToEarnings
        )
    }
}
