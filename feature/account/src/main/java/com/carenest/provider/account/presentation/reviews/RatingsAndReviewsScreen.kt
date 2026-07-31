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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
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
import com.carenest.provider.account.presentation.components.RatingsAndReviewsLoadingSkeleton
import com.carenest.provider.account.presentation.components.RatingDistributionRow
import com.carenest.provider.account.presentation.components.RatingStars
import com.carenest.provider.account.presentation.components.ReviewCard
import com.carenest.provider.account.presentation.components.ReviewFilterChip
import com.carenest.provider.account.presentation.model.ReviewFilter
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
            RatingsAndReviewsEffect.LoadMoreReviews -> onLoadMore()
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
            items(state.reviews, key = { it.id }) { review ->
                ReviewCard(
                    review = review,
                    modifier = Modifier.padding(horizontal = Theme.spacing.medium),
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Theme.spacing.medium),
                    contentAlignment = Alignment.Center,
                ) {
                    OutlinedButton(
                        onClick = { onIntent(RatingsAndReviewsIntent.LoadMoreClicked) },
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

@Composable
private fun RatingSummaryCard(
    state: RatingsAndReviewsUiState,
    modifier: Modifier = Modifier,
) {
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
                .padding(Theme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BasicText(
                text = stringResource(R.string.account_rating_value),
                style = Theme.typography.display.copy(
                    color = Theme.colors.tint,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                ),
            )
            RatingStars(rating = 5, iconSize = 20)
            BasicText(
                text = pluralStringResource(
                    R.plurals.reviews_count,
                    state.totalReviews,
                    state.totalReviews,
                ),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                ),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
            state.distribution.forEach {
                RatingDistributionRow(it.stars, it.progress, it.percentage)
            }
        }
    }
}

private fun ReviewFilter.labelRes(): Int = when (this) {
    ReviewFilter.MostRecent -> R.string.reviews_filter_recent
    ReviewFilter.TopRated -> R.string.reviews_filter_top
    ReviewFilter.Critical -> R.string.reviews_filter_critical
    ReviewFilter.WithPhotos -> R.string.reviews_filter_photos
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun RatingsLightPreview() {
    SpTheme(isDarkTheme = false) {
        RatingsAndReviewsContent(RatingsAndReviewsUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun RatingsDarkPreview() {
    SpTheme(isDarkTheme = true) {
        RatingsAndReviewsContent(RatingsAndReviewsUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun RatingsLoadingPreview() {
    SpTheme(isDarkTheme = false) {
        RatingsAndReviewsContent(RatingsAndReviewsUiState(isLoading = true), {})
    }
}
