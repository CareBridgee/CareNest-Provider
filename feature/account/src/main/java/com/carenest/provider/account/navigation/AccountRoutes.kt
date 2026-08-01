package com.carenest.provider.account.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AccountRoutes : NavKey {
    @Serializable
    data object ProfileMenu : AccountRoutes

    @Serializable
    data object PublicProfile : AccountRoutes

    @Serializable
    data object ProfessionalDocuments : AccountRoutes

    @Serializable
    data object Settings : AccountRoutes

    @Serializable
    data object RatingsAndReviews : AccountRoutes

    @Serializable
    data object Wallet : AccountRoutes
}
