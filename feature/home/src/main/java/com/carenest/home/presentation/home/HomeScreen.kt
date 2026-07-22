package com.carenest.home.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.home.R
import com.carenest.home.presentation.home.components.EditRateBottomSheet
import com.carenest.home.presentation.home.components.MakeOfferDialog
import com.carenest.home.presentation.home.components.NurseRequestCard
import com.carenest.home.presentation.home.components.NurseRequestsLoadingSkeleton
import com.carenest.home.presentation.home.components.OnlineToggleCard
import com.carenest.provider.designsystem.components.emptystate.EmptyState
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CareNestTopBar(
                title = "Home",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    ){innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Theme.colors.backGround),
        ) {
            NurseRequestsTopBar()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Spacer(Modifier.height(8.dp))
                    OnlineToggleCard(
                        isOnline = state.isOnline,
                        onToggle = { onIntent(HomeIntent.OnlineToggled(it)) },
                    )
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    AnimatedContent(
                        targetState = when {
                            !state.isOnline -> ContentPhase.Offline
                            state.isLoading -> ContentPhase.Loading
                            else -> ContentPhase.List
                        },
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                        },
                        label = "requestsContent",
                    ) { phase ->
                        when (phase) {
                            ContentPhase.Offline -> OfflineEmptyState(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                            )

                            ContentPhase.Loading -> Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                BasicText(
                                    text = stringResource(R.string.nurse_requests_loading),
                                    style = Theme.typography.body.medium.copy(
                                        color = Theme.colors.secondaryFont,
                                    ),
                                    modifier = Modifier.padding(bottom = 16.dp),
                                )
                                NurseRequestsLoadingSkeleton()
                            }

                            ContentPhase.List -> Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                BasicText(
                                    text = stringResource(
                                        R.string.nurse_requests_incoming,
                                    ) + " (${state.requests.size})",
                                    style = Theme.typography.body.large.copy(
                                        color = Theme.colors.primaryFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                    ),
                                    modifier = Modifier.padding(bottom = 12.dp),
                                )
                            }
                        }
                    }
                }

                if (state.isOnline && !state.isLoading) {
                    items(
                        items = state.requests,
                        key = { it.id },
                    ) { request ->
                        NurseRequestCard(
                            request = request,
                            isExpanded = state.selectedCardId == request.id,
                            onClick = { onIntent(HomeIntent.CardClicked(request.id)) },
                            onEditClick = { onIntent(HomeIntent.EditRateClicked(request.id)) },
                            onMakeOfferClick = { onIntent(HomeIntent.MakeOfferClicked(request.id)) },
                        )
                    }

                    item {
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }

        if (state.activeModal == ActiveModal.EditRate) {
            EditRateBottomSheet(
                currentRate = state.editRateDraft,
                minRate = 25f,
                maxRate = 120f,
                onRateChange = { onIntent(HomeIntent.EditRateChanged(it)) },
                onSave = { onIntent(HomeIntent.SaveRateClicked) },
                onDismiss = { onIntent(HomeIntent.DismissModal) },
            )
        }

        if (state.activeModal == ActiveModal.MakeOffer || state.activeModal == ActiveModal.OfferSuccess) {
            MakeOfferDialog(
                countdownSeconds = state.offerCountdown ?: 0,
                isSuccess = state.activeModal == ActiveModal.OfferSuccess,
                onDismiss = { onIntent(HomeIntent.DismissModal) },
            )
        }

    }

}

@Composable
private fun NurseRequestsTopBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        BasicText(
            text = stringResource(R.string.nurse_requests_title),
            style = Theme.typography.title.copy(
                color = Theme.colors.tint,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            ),
        )
        Spacer(Modifier.height(2.dp))
        BasicText(
            text = stringResource(R.string.nurse_requests_subtitle),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontSize = 13.sp,
            ),
        )
    }
}

@Composable
private fun OfflineEmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        EmptyState(
            title = stringResource(R.string.nurse_requests_offline_title),
            description = stringResource(R.string.nurse_requests_offline_description),
            icon = Icons.Default.WifiOff,
            accentColor = Theme.colors.secondaryFont,
        )
    }
}

private enum class ContentPhase {
    Offline,
    Loading,
    List,
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeOfflinePreview() {
    SpTheme(isDarkTheme = false) {
        HomeContent(
            state = HomeUiState(isOnline = false),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeLoadingPreview() {
    SpTheme(isDarkTheme = false) {
        HomeContent(
            state = HomeUiState(isOnline = true, isLoading = true),
            onIntent = {},
        )
    }
}
