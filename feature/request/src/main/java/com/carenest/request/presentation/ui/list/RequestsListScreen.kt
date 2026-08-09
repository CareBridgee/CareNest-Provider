package com.carenest.request.presentation.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.core.network.socket.service.ActiveReservationService
import com.carenest.provider.designsystem.components.bottomnav.LocalBottomNavigationContentPadding
import com.carenest.provider.designsystem.components.request.EditRateBottomSheet
import com.carenest.provider.designsystem.components.request.MakeOfferDialog
import com.carenest.provider.designsystem.components.request.NurseRequestsLoadingSkeleton
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.model.RequestStatus
import com.carenest.request.presentation.ui.list.components.PatientRequestCard
import com.carenest.request.presentation.ui.list.components.RequestsListHeader

@Composable
fun RequestsListScreen(
    onBack: () -> Unit,
    onOfferConfirmed: (String) -> Unit,
    onViewDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RequestsListViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is RequestsListEffect.StartActiveReservationService -> {
                ActiveReservationService.startService(context, effect.requestId)
            }
            is RequestsListEffect.NavigateToOfferConfirmed -> {
                ActiveReservationService.startService(context, effect.requestId)
                onOfferConfirmed(effect.requestId)
            }
            is RequestsListEffect.NavigateToRequestDetails -> onViewDetails(effect.requestId)
        }
    }

    RequestsListContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun RequestsListContent(
    state: RequestsListUiState,
    onIntent: (RequestsListIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bottomNavigationContentPadding = LocalBottomNavigationContentPadding.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CareNestTopBar(
                title = stringResource(R.string.requests_list_title),
                leading = TopBarLeading.Back(onBack),
            )
            if (state.isLoading) {
                Column(modifier = Modifier.padding(Theme.spacing.medium)) {
                    NurseRequestsLoadingSkeleton()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(Theme.spacing.medium),
                    contentPadding = PaddingValues(bottom = bottomNavigationContentPadding),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                ) {
                    item {
                        RequestsListHeader(
                            pendingCount = state.requests.count { it.status == RequestStatus.ESTIMATED }
                        )
                    }

                    if (state.requests.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Theme.spacing.large),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No Requests Available",
                                    style = Theme.typography.body.large.copy(
                                        color = Theme.colors.secondaryFont,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))
                                Text(
                                    text = "Incoming requests from nearby patients will appear here in real-time.",
                                    style = Theme.typography.body.medium.copy(color = Theme.colors.hint),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(items = state.requests, key = { it.id }) { request: Request ->
                            PatientRequestCard(
                                request = request,
                                isExpanded = state.selectedCardId == request.id,
                                onClick = { onIntent(RequestsListIntent.CardClicked(request.id)) },
                                onEditClick = { onIntent(RequestsListIntent.EditRateClicked(request.id)) },
                                onMakeOfferClick = {
                                    onIntent(RequestsListIntent.MakeOfferClicked(request.id))
                                },
                                modifier = Modifier.padding(top = Theme.spacing.space12)
                            )
                        }
                    }
                }
            }
        }

        if (state.activeModal == RequestsListModal.EditRate) {
            EditRateBottomSheet(
                currentRate = state.editPriceDraft,
                minRate = 25f,
                maxRate = 120f,
                onRateChange = { onIntent(RequestsListIntent.EditRateChanged(it)) },
                onSave = { onIntent(RequestsListIntent.SaveRateClicked) },
                onDismiss = { onIntent(RequestsListIntent.DismissModal) },
            )
        }

        if (state.activeModal == RequestsListModal.MakeOffer || state.activeModal == RequestsListModal.OfferSuccess) {
            MakeOfferDialog(
                countdownSeconds = state.offerCountdown ?: 0,
                isSuccess = state.activeModal == RequestsListModal.OfferSuccess,
                onDismiss = { onIntent(RequestsListIntent.DismissModal) },
            )
        }

        if (state.socketErrorMessage != null) {
            com.carenest.provider.designsystem.components.request.SocketErrorDialog(
                errorMessage = state.socketErrorMessage,
                errorCode = state.socketErrorCode,
                onDismiss = { onIntent(RequestsListIntent.DismissModal) },
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun RequestsListPreview() {
    SpTheme {
        RequestsListContent(
            state = RequestsListUiState(
                isLoading = false,
                requests = listOf(
                    Request(
                        id = "1",
                        patientName = "Anonymous Patient",
                        serviceName = "Home Care",
                        serviceImage = "",
                        basePrice = 120f,
                        patientAddress = "",
                        status = RequestStatus.ESTIMATED,
                    )
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}
