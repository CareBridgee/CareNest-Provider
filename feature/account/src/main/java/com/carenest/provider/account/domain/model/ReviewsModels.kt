package com.carenest.provider.account.domain.model

data class NurseReview(
    val id: String,
    val serviceRequestId: String? = null,
    val bookingId: String? = null,
    val profileId: String? = null,
    val nurseId: String? = null,
    val rating: Int = 0,
    val reviewText: String = "",
    val isAnonymous: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class NurseReviewsPage(
    val totalElements: Int,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean,
    val pageNumber: Int,
    val pageSize: Int,
    val reviews: List<NurseReview>,
)
