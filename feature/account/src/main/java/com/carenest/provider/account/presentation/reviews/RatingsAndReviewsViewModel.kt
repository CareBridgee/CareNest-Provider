package com.carenest.provider.account.presentation.reviews

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.account.R
import com.carenest.provider.account.domain.model.NurseReview
import com.carenest.provider.account.domain.usecase.GetNurseReviewsUseCase
import com.carenest.provider.account.presentation.model.ReviewFilter
import com.carenest.provider.account.presentation.model.ReviewUiModel
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class RatingsAndReviewsViewModel @Inject constructor(
    private val getNurseReviewsUseCase: GetNurseReviewsUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel(),
    StateHolder<RatingsAndReviewsUiState> by DefaultStateHolder(RatingsAndReviewsUiState()),
    EffectPublisher<RatingsAndReviewsEffect> by DefaultEffectPublisher() {

    private val allFetchedReviews = mutableListOf<NurseReview>()

    init {
        loadInitial()
    }

    fun onIntent(intent: RatingsAndReviewsIntent) {
        when (intent) {
            RatingsAndReviewsIntent.BackClicked ->
                sendEffect(RatingsAndReviewsEffect.NavigateBack)
            is RatingsAndReviewsIntent.FilterSelected ->
                onFilterSelected(intent.filter)
            RatingsAndReviewsIntent.LoadMoreClicked ->
                loadNextPage()
            RatingsAndReviewsIntent.RetryClicked ->
                loadInitial()
        }
    }

    private fun loadInitial() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null, currentPage = 0) }
            allFetchedReviews.clear()

            getNurseReviewsUseCase(page = 0, size = PAGE_SIZE, sort = SORT_BY_RECENT)
                .fold(
                    onSuccess = { pageData ->
                        allFetchedReviews.addAll(pageData.reviews)
                        val displayReviews = applyFilter(allFetchedReviews, currentState.selectedFilter)
                        updateState {
                            copy(
                                isLoading = false,
                                reviews = displayReviews,
                                totalReviews = pageData.totalElements,
                                currentPage = 0,
                                isLastPage = pageData.isLast,
                                error = null,
                            )
                        }
                    },
                    onFailure = { error ->
                        updateState {
                            copy(
                                isLoading = false,
                                error = error.message ?: context.getString(R.string.reviews_error_title),
                            )
                        }
                    },
                )
        }
    }

    private fun loadNextPage() {
        val state = currentState
        if (state.isLoading || state.isLoadingMore || state.isLastPage) return

        viewModelScope.launch {
            updateState { copy(isLoadingMore = true) }
            val nextPage = state.currentPage + 1

            getNurseReviewsUseCase(page = nextPage, size = PAGE_SIZE, sort = SORT_BY_RECENT)
                .fold(
                    onSuccess = { pageData ->
                        allFetchedReviews.addAll(pageData.reviews)
                        val displayReviews = applyFilter(allFetchedReviews, currentState.selectedFilter)
                        updateState {
                            copy(
                                isLoadingMore = false,
                                reviews = displayReviews,
                                totalReviews = pageData.totalElements,
                                currentPage = nextPage,
                                isLastPage = pageData.isLast,
                            )
                        }
                    },
                    onFailure = {
                        updateState { copy(isLoadingMore = false) }
                    },
                )
        }
    }

    private fun onFilterSelected(filter: ReviewFilter) {
        val filtered = applyFilter(allFetchedReviews, filter)
        updateState { copy(selectedFilter = filter, reviews = filtered) }
    }

    private fun applyFilter(reviews: List<NurseReview>, filter: ReviewFilter): List<ReviewUiModel> {
        val filteredList = when (filter) {
            ReviewFilter.MostRecent -> reviews
            ReviewFilter.Critical -> reviews.filter { it.rating <= CRITICAL_RATING_THRESHOLD }
        }
        return filteredList.map { mapToUiModel(it) }
    }

    private fun mapToUiModel(domain: NurseReview): ReviewUiModel {
        val authorName = if (domain.isAnonymous) {
            context.getString(R.string.reviews_anonymous_patient)
        } else {
            context.getString(R.string.reviews_verified_patient)
        }

        val initials = if (domain.isAnonymous) "A" else "P"
        val formattedDate = formatDate(domain.createdAt)

        return ReviewUiModel(
            id = domain.id,
            authorName = authorName,
            dateText = formattedDate,
            bodyText = domain.reviewText,
            serviceName = null,
            initials = initials,
            rating = domain.rating,
        )
    }

    private fun formatDate(isoTimestamp: String): String {
        if (isoTimestamp.isBlank()) return ""
        return try {
            val instant = Instant.parse(isoTimestamp)
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
                .withZone(ZoneId.systemDefault())
            formatter.format(instant)
        } catch (e: Exception) {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val date = inputFormat.parse(isoTimestamp.take(10))
                val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                if (date != null) outputFormat.format(date) else isoTimestamp
            } catch (e2: Exception) {
                isoTimestamp
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 10
        const val SORT_BY_RECENT = "createdAt,desc"
        const val CRITICAL_RATING_THRESHOLD = 2
    }
}
