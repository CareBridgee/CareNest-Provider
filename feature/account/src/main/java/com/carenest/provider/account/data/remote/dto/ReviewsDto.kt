package com.carenest.provider.account.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NurseReviewDto(
    val id: String,
    val bookingId: String? = null,
    val profileId: String? = null,
    val nurseId: String,
    val rating: Int,
    val reviewText: String? = null,
    val isAnonymous: Boolean = false,
    val createdAt: String,
    val updatedAt: String? = null,
)

@Serializable
data class ReviewsPageResponseDto(
    val totalElements: Int = 0,
    val totalPages: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
    val size: Int = 10,
    val number: Int = 0,
    val numberOfElements: Int = 0,
    val empty: Boolean = true,
    val content: List<NurseReviewDto> = emptyList(),
)
