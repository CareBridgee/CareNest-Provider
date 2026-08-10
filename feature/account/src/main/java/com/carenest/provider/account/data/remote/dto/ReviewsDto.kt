package com.carenest.provider.account.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NurseReviewDto(
    val id: String,
    val serviceRequestId: String? = null,
    val bookingId: String? = null,
    val profileId: String? = null,
    val nurseId: String? = null,
    val rating: Int = 0,
    val reviewText: String? = null,
    val isAnonymous: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

@Serializable
data class SortDto(
    val sorted: Boolean = false,
    val empty: Boolean = true,
    val unsorted: Boolean = true,
)

@Serializable
data class PageableDto(
    val offset: Long = 0,
    val pageNumber: Int = 0,
    val pageSize: Int = 20,
    val paged: Boolean = true,
    val unpaged: Boolean = false,
    val sort: SortDto? = null,
)

@Serializable
data class ReviewsPageResponseDto(
    val totalElements: Int = 0,
    val totalPages: Int = 0,
    val first: Boolean = true,
    val last: Boolean = true,
    val size: Int = 20,
    val number: Int = 0,
    val numberOfElements: Int = 0,
    val empty: Boolean = true,
    val content: List<NurseReviewDto> = emptyList(),
    val pageable: PageableDto? = null,
    val sort: SortDto? = null,
)
