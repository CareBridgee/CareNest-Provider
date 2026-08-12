package com.carenest.home.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.carenest.home.R
import com.carenest.home.presentation.home.components.AvailableRequestsHeader
import com.carenest.home.presentation.home.components.EarningsSection
import com.carenest.home.presentation.home.components.HomeGreetingBar
import com.carenest.home.presentation.home.components.NoRequestsEmptyState
import com.carenest.home.presentation.home.components.LocationPermissionHandler
import com.carenest.home.presentation.home.components.NotificationPermissionHandler
import com.carenest.home.presentation.home.components.NurseRequestCard
import com.carenest.home.presentation.home.components.OfflineEmptyState
import com.carenest.home.presentation.home.components.OnlineToggleCard
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.core.network.socket.service.ActiveReservationService
import com.carenest.provider.designsystem.components.request.EditRateBottomSheet
import com.carenest.provider.designsystem.components.request.MakeOfferDialog
import com.carenest.provider.designsystem.components.request.NurseRequestsLoadingSkeleton
import com.carenest.provider.designsystem.components.bottomnav.LocalBottomNavigationContentPadding
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

private enum class ContentPhase {
    Offline, Loading, Empty, List,
}

@Composable
fun HomeScreen(
    onNavigateToRequests: () -> Unit,
    onOfferConfirmed: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.onIntent(HomeIntent.RefreshProfile)
        onPauseOrDispose { }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            HomeEffect.NavigateToRequestList -> onNavigateToRequests()
            is HomeEffect.StartActiveReservationService -> {
                ActiveReservationService.startService(context, effect.requestId)
            }
            is HomeEffect.NavigateToOfferConfirmed -> {
                ActiveReservationService.startService(context, effect.requestId)
                onOfferConfirmed(effect.requestId)
            }
        }
    }

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
    var requestNotificationPermission by remember { mutableStateOf(false) }
    var showNotificationRationale by remember { mutableStateOf(false) }

    var requestLocationPermission by remember { mutableStateOf(false) }
    var showLocationRationale by remember { mutableStateOf(false) }

    val handleToggleOnline: (Boolean) -> Unit = { isOnline ->
        if (isOnline) {
            requestNotificationPermission = true
        } else {
            requestNotificationPermission = false
            showNotificationRationale = false
            requestLocationPermission = false
            showLocationRationale = false
            onIntent(HomeIntent.OnlineToggled(false))
        }
    }

    if (requestNotificationPermission) {
        NotificationPermissionHandler(
            onPermissionGranted = {
                requestNotificationPermission = false
                showNotificationRationale = false
                requestLocationPermission = true
            },
            onPermissionDenied = {
                showNotificationRationale = true
            },
            showRationale = showNotificationRationale,
            onRationaleDismissed = {
                showNotificationRationale = false
                requestNotificationPermission = false
                requestLocationPermission = true
            }
        )
    }

    if (requestLocationPermission) {
        LocationPermissionHandler(
            onPermissionGranted = {
                requestLocationPermission = false
                showLocationRationale = false
                onIntent(HomeIntent.OnlineToggled(true))
            },
            onPermissionDenied = {
                showLocationRationale = true
            },
            showRationale = showLocationRationale,
            onRationaleDismissed = {
                showLocationRationale = false
                requestLocationPermission = false
                onIntent(HomeIntent.OnlineToggled(true))
            }
        )
    }

    val bottomNavigationContentPadding = LocalBottomNavigationContentPadding.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Theme.colors.backGround,
        topBar = {
            HomeGreetingBar(
                name = state.nurseName,
                avatarUrl = state.nurseAvatar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.spacing.medium),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Theme.colors.backGround),
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Theme.spacing.medium),
                contentPadding = PaddingValues(bottom = bottomNavigationContentPadding),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                item {
                    Spacer(Modifier.height(Theme.spacing.extraSmall))
                    OnlineToggleCard(
                        isOnline = state.isOnline,
                        onToggle = handleToggleOnline,
                    )
                }

                item {
                    EarningsSection(
                        earnings = "$" + state.earnings.toString(),
                        changePercent = state.changePercent.toString() + "%",
                        jobsToday = state.jobsToday,
                        rating = state.rating.toString()
                    )
                }

                item {
                    Spacer(Modifier.height(Theme.spacing.extraSmall))
                    AnimatedContent(
                        targetState = when {
                            !state.isOnline -> ContentPhase.Offline
                            state.isLoading -> ContentPhase.Loading
                            state.requests.isEmpty() -> ContentPhase.Empty
                            else -> ContentPhase.List
                        },
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                        },
                        label = "requestsContent",
                    ) { phase ->
                        when (phase) {
                            ContentPhase.Offline -> OfflineEmptyState()

                            ContentPhase.Loading -> Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                BasicText(
                                    text = stringResource(R.string.nurse_requests_loading),
                                    style = Theme.typography.body.medium.copy(
                                        color = Theme.colors.secondaryFont,
                                    ),
                                    modifier = Modifier.padding(bottom = Theme.spacing.medium),
                                )
                                NurseRequestsLoadingSkeleton()
                            }

                            ContentPhase.Empty -> Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AvailableRequestsHeader(
                                    onViewAllClick = { onIntent(HomeIntent.ViewAllRequestsClicked) },
                                    modifier = Modifier.padding(
                                        top = Theme.spacing.extraSmall,
                                        bottom = Theme.spacing.small,
                                    ),
                                )
                                NoRequestsEmptyState()
                            }

                            ContentPhase.List -> Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                AvailableRequestsHeader(
                                    onViewAllClick = { onIntent(HomeIntent.ViewAllRequestsClicked) },
                                    modifier = Modifier.padding(
                                        top = Theme.spacing.extraSmall,
                                        bottom = Theme.spacing.small,
                                    ),
                                )
                            }
                        }
                    }
                }

                if (state.isOnline && !state.isLoading && state.requests.isNotEmpty()) {
                    items(
                        items = state.requests,
                        key = { it.id },
                    ) { request ->
                        NurseRequestCard(
                            request = request,
                            isExpanded = state.selectedCardId == request.id,
                            onClick = { onIntent(HomeIntent.CardClicked(request.id)) },
                            onEditClick = { onIntent(HomeIntent.EditRateClicked(request.id)) },
                            onMakeOfferClick = {
                                onIntent(HomeIntent.MakeOfferClicked(request.id))
                            },
                        )
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

        if (state.socketErrorMessage != null) {
            com.carenest.provider.designsystem.components.request.SocketErrorDialog(
                errorMessage = state.socketErrorMessage,
                errorCode = state.socketErrorCode,
                onDismiss = { onIntent(HomeIntent.DismissModal) },
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeOfflinePreview() {
    SpTheme(isDarkTheme = false) {
        HomeContent(
            state = HomeUiState(isOnline = false, nurseName = "Hend"),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun HomeLoadingPreview() {
    SpTheme(isDarkTheme = false) {
        HomeContent(
            state = HomeUiState(isOnline = true, isLoading = true, nurseName = "Hend"),
            onIntent = {},
        )
    }
}
