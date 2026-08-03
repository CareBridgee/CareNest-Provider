package com.carenest.chat.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface ChatRoutes : NavKey {

    @Serializable
    data class Chat(val requestId: String) : ChatRoutes
}