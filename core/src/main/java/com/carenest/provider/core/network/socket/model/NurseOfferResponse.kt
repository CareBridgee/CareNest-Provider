package com.carenest.provider.core.network.socket.model

import kotlinx.serialization.Serializable

@Serializable
data class NurseSummaryDto(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val ratingAvg: Double? = null,
    val totalReviews: Int? = null
)

@Serializable
data class NurseOfferResponse(
    val id: String,
    val serviceRequestId: String,
    val nurse: NurseSummaryDto? = null,
    val proposedPrice: Double,
    val proposedDate: String? = null,
    val proposedTime: String? = null,
    val message: String? = null,
    val status: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
