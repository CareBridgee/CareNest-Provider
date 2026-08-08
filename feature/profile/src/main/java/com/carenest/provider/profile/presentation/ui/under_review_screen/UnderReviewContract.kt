package com.carenest.provider.profile.presentation.ui.under_review_screen

import com.carenest.provider.profile.domain.model.FailedStep

sealed interface UnderReviewIntent {
    data object OnBackClick : UnderReviewIntent
    data object OnGoToHomeClick : UnderReviewIntent
    data object OnContactSupportClick : UnderReviewIntent
    data object OnBackToLoginClick : UnderReviewIntent
    data object OnRetry : UnderReviewIntent
}

data class UnderReviewState(
    val nurseId: String = "",
    val profileImageUrl: String? = null,
    val underReviewState: ReviewState = ReviewState.UnderReview,
    val isLoading: Boolean = false,
    val rejectionReason: String = "",
    val failedSteps: List<FailedStep> = emptyList(),
    val error: String? = null,
)

enum class ReviewState { UnderReview, Success, Error }

sealed interface UnderReviewEvent {
    data object OnBackClick : UnderReviewEvent
    data object OnGoToHomeClick : UnderReviewEvent
    data object OnContactSupportClick : UnderReviewEvent
    data object OnBackToLoginClick : UnderReviewEvent
}
