package com.carenest.chat.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    val serviceRequestId: String,
    val senderUserId: String,
    val senderName: String? = null,
    val senderPhone: String? = null,
    val content: String,
    val createdAt: String,
)

@Serializable
data class SendMessageRequestDto(
    val content: String,
)
