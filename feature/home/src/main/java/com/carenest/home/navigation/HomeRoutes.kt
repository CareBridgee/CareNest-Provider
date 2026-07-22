package com.carenest.home.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeRoutes : NavKey{
    @Serializable
    data object Home : HomeRoutes
}