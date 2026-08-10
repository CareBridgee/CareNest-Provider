package com.carenest.provider.account.presentation.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.components.ProviderAccountTopBar
import com.carenest.provider.account.presentation.components.RatingDistributionRow
import com.carenest.provider.account.presentation.components.RatingsAndReviewsLoadingSkeleton
import com.carenest.provider.account.presentation.components.ReviewCard
import com.carenest.provider.account.presentation.components.ReviewFilterChip
import com.carenest.provider.account.presentation.model.ReviewFilter
import com.carenest.provider.account.presentation.model.ReviewUiModel
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun RatingsAndReviewsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onLoadMore: () -> Unit = {},
    viewModel: RatingsAndReviewsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            RatingsAndReviewsEffect.NavigateBack -> onNavigateBack()
        }
    }
    RatingsAndReviewsContent(state, viewModel::onIntent, modifier)
}

@Composable
fun RatingsAndReviewsContent(
    state: RatingsAndReviewsUiState,
    onIntent: (RatingsAndReviewsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        ProviderAccountTopBar(onNavigateBack = { onIntent(RatingsAndReviewsIntent.BackClicked) })
        if (state.isLoading) {
            RatingsAndReviewsLoadingSkeleton()
        } else if (state.error != null && state.reviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Theme.spacing.medium),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                ) {
                    BasicText(
                        text = state.error,
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.error,
                            textAlign = TextAlign.Center,
                        ),
                    )
                    OutlinedButton(
                        onClick = { onIntent(RatingsAndReviewsIntent.RetryClicked) },
                        modifier = Modifier
                            .width(208.dp)
                            .height(44.dp),
                        shape = Theme.shapes.extraLarge,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Theme.colors.tint.copy(alpha = 0.5f),
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Theme.colors.tint,
                        ),
                    ) {
                        BasicText(
                            text = stringResource(R.string.reviews_error_retry),
                            style = Theme.typography.body.small.copy(
                                color = Theme.colors.tint,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                }
            }
        } else if (state.totalReviews == 0 || (state.reviews.isEmpty() && state.selectedFilter == ReviewFilter.MostRecent)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Theme.spacing.medium),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = Theme.spacing.large),
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(Theme.shapes.extraLarge)
                            .background(Theme.colors.infoContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.material3.Icon(
                            painter = androidx.compose.ui.res.painterResource(com.carenest.provider.designsystem.R.drawable.ic_account_reviews),
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = Theme.colors.tint,
                        )
                    }
                    Spacer(Modifier.height(Theme.spacing.large))
                    BasicText(
                        text = stringResource(R.string.reviews_empty_title),
                        style = Theme.typography.title.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                    )
                    Spacer(Modifier.height(Theme.spacing.small))
                    BasicText(
                        text = stringResource(R.string.reviews_empty_subtitle),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.secondaryFont,
                            textAlign = TextAlign.Center,
                        ),
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            ) {
                item {
                    RatingSummaryCard(
                        state = state,
                        modifier = Modifier.padding(horizontal = Theme.spacing.medium),
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = Theme.spacing.medium),
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                    ) {
                        items(ReviewFilter.entries) { filter ->
                            ReviewFilterChip(
                                label = stringResource(filter.labelRes()),
                                selected = filter == state.selectedFilter,
                                onClick = {
                                    onIntent(RatingsAndReviewsIntent.FilterSelected(filter))
                                },
                            )
                        }
                    }
                }
                if (state.reviews.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Theme.spacing.large),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                BasicText(
                                    text = stringResource(R.string.reviews_no_critical_title),
                                    style = Theme.typography.title.copy(
                                        color = Theme.colors.primaryFont,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                    ),
                                )
                                Spacer(Modifier.height(Theme.spacing.small))
                                BasicText(
                                    text = stringResource(R.string.reviews_no_critical_subtitle),
                                    style = Theme.typography.body.small.copy(
                                        color = Theme.colors.secondaryFont,
                                        textAlign = TextAlign.Center,
                                    ),
                                )
                            }
                        }
                    }
                } else {
                    items(state.reviews, key = { it.id }) { review ->
                        ReviewCard(
                            review = review,
                            modifier = Modifier.padding(horizontal = Theme.spacing.medium),
                        )
                    }
                }
                if (!state.isLastPage && state.reviews.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Theme.spacing.medium),
                            contentAlignment = Alignment.Center,
                        ) {
                            OutlinedButton(
                                onClick = { onIntent(RatingsAndReviewsIntent.LoadMoreClicked) },
                                enabled = !state.isLoadingMore,
                                modifier = Modifier
                                    .width(208.dp)
                                    .height(44.dp),
                                shape = Theme.shapes.extraLarge,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Theme.colors.tint.copy(alpha = .25f),
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Theme.colors.tint,
                                ),
                            ) {
                                if (state.isLoadingMore) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Theme.colors.tint,
                                        strokeWidth = 2.dp,
                                    )
                                } else {
                                    BasicText(
                                        text = stringResource(R.string.reviews_load_more),
                                        style = Theme.typography.body.small.copy(
                                            color = Theme.colors.tint,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RatingSummaryCard(
    state: RatingsAndReviewsUiState,
    modifier: Modifier = Modifier,
) {
    val averageRatingText = if (state.averageRating <= 0.0) {
        "0"
    } else if (state.averageRating % 1.0 == 0.0) {
        String.format(java.util.Locale.US, "%.0f", state.averageRating)
    } else {
        String.format(java.util.Locale.US, "%.1f", state.averageRating)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, Theme.shapes.extraLarge)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Theme.shapes.large)
                .background(Theme.colors.infoContainer)
                .padding(vertical = Theme.spacing.large, horizontal = Theme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BasicText(
                text = averageRatingText,
                style = Theme.typography.display.copy(
                    color = Theme.colors.tint,
                    fontWeight = FontWeight.Bold,
                    fontSize = 52.sp,
                    textAlign = TextAlign.Center,
                ),
            )
            Spacer(Modifier.height(Theme.spacing.extraSmall))
            com.carenest.provider.account.presentation.components.StarRatingRow(rating = state.averageRating)
            Spacer(Modifier.height(Theme.spacing.small))
            BasicText(
                text = pluralStringResource(
                    R.plurals.reviews_count,
                    state.totalReviews,
                    state.totalReviews,
                ),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
            )
        }
        if (state.distribution.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                state.distribution.forEach {
                    RatingDistributionRow(it.stars, it.progress, it.percentage)
                }
            }
        }
    }
}

private fun ReviewFilter.labelRes(): Int = when (this) {
    ReviewFilter.MostRecent -> R.string.reviews_filter_recent
    ReviewFilter.Critical -> R.string.reviews_filter_critical
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun RatingsLightPreview() {
    SpTheme(isDarkTheme = false) {
        RatingsAndReviewsContent(
            state = RatingsAndReviewsUiState(
                totalReviews = 5,
                averageRating = 4.2,
                distribution = listOf(
                    RatingDistributionUiModel(5, 0.60f, 60),
                    RatingDistributionUiModel(4, 0.20f, 20),
                    RatingDistributionUiModel(3, 0.00f, 0),
                    RatingDistributionUiModel(2, 0.20f, 20),
                    RatingDistributionUiModel(1, 0.00f, 0),
                ),
                reviews = listOf(
                    ReviewUiModel(
                        id = "1",
                        authorName = "Anonymous Patient",
                        dateText = "Aug 10, 2026",
                        bodyText = "Exceptional care! The nurse was extremely gentle and professional.",
                        serviceName = null,
                        initials = "A",
                        rating = 5,
                    ),
                ),
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun RatingsDarkPreview() {
    SpTheme(isDarkTheme = true) {
        RatingsAndReviewsContent(
            state = RatingsAndReviewsUiState(
                totalReviews = 5,
                averageRating = 4.2,
                distribution = listOf(
                    RatingDistributionUiModel(5, 0.60f, 60),
                    RatingDistributionUiModel(4, 0.20f, 20),
                    RatingDistributionUiModel(3, 0.00f, 0),
                    RatingDistributionUiModel(2, 0.20f, 20),
                    RatingDistributionUiModel(1, 0.00f, 0),
                ),
                reviews = listOf(
                    ReviewUiModel(
                        id = "1",
                        authorName = "Anonymous Patient",
                        dateText = "Aug 10, 2026",
                        bodyText = "Exceptional care! The nurse was extremely gentle and professional.",
                        serviceName = null,
                        initials = "A",
                        rating = 5,
                    ),
                ),
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun RatingsLoadingPreview() {
    SpTheme(isDarkTheme = false) {
        RatingsAndReviewsContent(RatingsAndReviewsUiState(isLoading = true), {})
    }
}
