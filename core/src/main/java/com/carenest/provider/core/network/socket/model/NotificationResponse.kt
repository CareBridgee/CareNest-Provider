package com.carenest.provider.core.network.socket.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean = false,
    val relatedEntityType: String? = null,
    val relatedEntityId: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
