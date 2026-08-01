package com.carenest.provider.payouts.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface PayoutsRoutes : NavKey {
    @Serializable
    data object PayoutsAndWithdrawals : PayoutsRoutes
}
