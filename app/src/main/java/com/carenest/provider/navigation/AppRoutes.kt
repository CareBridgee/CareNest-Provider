package com.carenest.provider.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal data object ProviderDashboardRoute : NavKey

@Serializable
internal data class ProviderInfoRoute(val destination: ProviderInfoDestination) : NavKey

@Serializable
internal enum class ProviderInfoDestination {
    CONTACT_SUPPORT,
    COMMUNITY_GUIDELINES,
}
