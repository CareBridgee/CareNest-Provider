package com.carenest.provider.core.network.socket.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponse(
    val id: String,
    val serviceRequestId: String,
    val senderUserId: String,
    val senderName: String? = null,
    val senderPhone: String? = null,
    val content: String,
    val createdAt: String? = null
)
