package com.carenest.provider.earnings.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface EarningsRoutes : NavKey {
    @Serializable
    data object ServiceEarnings : EarningsRoutes
}
