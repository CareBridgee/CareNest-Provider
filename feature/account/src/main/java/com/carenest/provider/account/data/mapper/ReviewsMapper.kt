package com.carenest.provider.account.data.mapper

import com.carenest.provider.account.data.remote.dto.NurseReviewDto
import com.carenest.provider.account.data.remote.dto.ReviewsPageResponseDto
import com.carenest.provider.account.domain.model.NurseReview
import com.carenest.provider.account.domain.model.NurseReviewsPage

fun ReviewsPageResponseDto.toDomain(): NurseReviewsPage = NurseReviewsPage(
    totalElements = totalElements,
    totalPages = totalPages,
    isFirst = first,
    isLast = last,
    pageNumber = number,
    pageSize = size,
    reviews = content.map { it.toDomain() },
)

private fun NurseReviewDto.toDomain(): NurseReview = NurseReview(
    id = id,
    serviceRequestId = serviceRequestId ?: bookingId,
    bookingId = bookingId ?: serviceRequestId,
    profileId = profileId,
    nurseId = nurseId,
    rating = rating,
    reviewText = reviewText.orEmpty(),
    isAnonymous = isAnonymous,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
