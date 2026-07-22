package com.carenest.home.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.carenest.home.presentation.home.HomeScreen
import com.carenest.provider.core.navigation.navigate
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


val homeSerializers = SerializersModule {
    polymorphic(NavKey::class){
        subclass(HomeRoutes.Home::class, HomeRoutes.Home.serializer())
    }
}

fun providerHomeStartRoute(): NavKey = HomeRoutes.Home

fun EntryProviderScope<NavKey>.providerHomeEntries(
    backStack: SnapshotStateList<NavKey>,
) {
    entry<HomeRoutes.Home> {
        HomeScreen()
    }
}