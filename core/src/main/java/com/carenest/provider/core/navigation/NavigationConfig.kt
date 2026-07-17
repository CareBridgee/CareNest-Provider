package com.carenest.provider.core.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object NavigationConfig {

    /**
     * Registers all navigation destinations (NavKeys) for serialization.
     *
     * Each feature module should register its own routes here.
     */
    val serializer = SerializersModule {
        polymorphic(NavKey::class) {
            // Register routes here.
            // Example:
            // subclass(HomeRoute::class, HomeRoute.serializer())
        }
    }

    /**
     * Configuration used by rememberNavBackStack()
     * to restore the navigation stack.
     */
    val savedStateConfiguration = SavedStateConfiguration {
        serializersModule = serializer
    }
}