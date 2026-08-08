package com.carenest.provider.core.network.socket.model

import kotlinx.serialization.Serializable

@Serializable
data class SocketErrorPayload(
    val code: String,
    val message: String,
    val timestamp: String? = null
)
