package com.carenest.provider.account.presentation.reviews

import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.model.ReviewFilter
import com.carenest.provider.account.presentation.model.ReviewUiModel

data class RatingsAndReviewsUiState(
    val isLoading: Boolean = false,
    val totalReviews: Int = 128,
    val selectedFilter: ReviewFilter = ReviewFilter.MostRecent,
    val distribution: List<RatingDistributionUiModel> = listOf(
        RatingDistributionUiModel(5, .92f, 92),
        RatingDistributionUiModel(4, .06f, 6),
        RatingDistributionUiModel(3, .01f, 1),
        RatingDistributionUiModel(2, .01f, 1),
        RatingDistributionUiModel(1, 0f, 0),
    ),
    val reviews: List<ReviewUiModel> = sampleReviews,
)

data class RatingDistributionUiModel(
    val stars: Int,
    val progress: Float,
    val percentage: Int,
)

sealed interface RatingsAndReviewsIntent {
    data object BackClicked : RatingsAndReviewsIntent
    data class FilterSelected(val filter: ReviewFilter) : RatingsAndReviewsIntent
    data object LoadMoreClicked : RatingsAndReviewsIntent
}

sealed interface RatingsAndReviewsEffect {
    data object NavigateBack : RatingsAndReviewsEffect
    data object LoadMoreReviews : RatingsAndReviewsEffect
}

private val sampleReviews = listOf(
    ReviewUiModel(
        "arthur",
        R.string.reviews_arthur_name,
        R.string.reviews_arthur_date,
        R.string.reviews_arthur_body,
        R.string.reviews_arthur_service,
        "AG",
        5,
    ),
    ReviewUiModel(
        "eleanor",
        R.string.reviews_eleanor_name,
        R.string.reviews_eleanor_date,
        R.string.reviews_eleanor_body,
        R.string.reviews_eleanor_service,
        "EP",
        5,
    ),
    ReviewUiModel(
        "michael",
        R.string.reviews_michael_name,
        R.string.reviews_michael_date,
        R.string.reviews_michael_body,
        R.string.reviews_michael_service,
        "MW",
        4,
    ),
)
