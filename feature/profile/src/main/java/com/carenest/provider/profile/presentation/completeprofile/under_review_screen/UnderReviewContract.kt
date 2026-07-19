package com.carenest.provider.profile.presentation.completeprofile.under_review_screen

sealed interface UnderReviewIntent {
    object OnBackClick : UnderReviewIntent
    object OnGoToHomeClick : UnderReviewIntent
    object OnContactSupportClick : UnderReviewIntent
    object OnBackToLoginClick : UnderReviewIntent
}


data class UnderReviewState(
    val underReviewState: ReviewState = ReviewState.Success,
    val error: String? = null
)

enum class ReviewState{
    UnderReview,
    Success,
    Error
}

sealed interface UnderReviewEvent{
    object OnBackClick : UnderReviewEvent
    object OnGoToHomeClick : UnderReviewEvent
    object OnContactSupportClick : UnderReviewEvent
    object OnBackToLoginClick : UnderReviewEvent
}