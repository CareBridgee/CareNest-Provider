package com.carenest.provider.account.presentation.reviews

import com.carenest.provider.account.presentation.model.ReviewFilter
import com.carenest.provider.account.presentation.model.ReviewUiModel

data class RatingsAndReviewsUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val totalReviews: Int = 0,
    val selectedFilter: ReviewFilter = ReviewFilter.MostRecent,
    val distribution: List<RatingDistributionUiModel> = emptyList(),
    val reviews: List<ReviewUiModel> = emptyList(),
    val currentPage: Int = 0,
    val isLastPage: Boolean = false,
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
    data object RetryClicked : RatingsAndReviewsIntent
}

sealed interface RatingsAndReviewsEffect {
    data object NavigateBack : RatingsAndReviewsEffect
}
