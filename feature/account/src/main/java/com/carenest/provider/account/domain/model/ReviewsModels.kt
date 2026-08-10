package com.carenest.provider.account.domain.model

data class NurseReview(
    val id: String,
    val bookingId: String?,
    val profileId: String?,
    val nurseId: String,
    val rating: Int,
    val reviewText: String,
    val isAnonymous: Boolean,
    val createdAt: String,
    val updatedAt: String?,
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
