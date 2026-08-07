package com.carenest.request.presentation.ui.offerconfirmed

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.request.domain.model.Offer
import com.carenest.request.presentation.ui.components.CancelRequestDialog
import com.carenest.request.presentation.ui.components.SuccessCheckBadge
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.request.R
import com.carenest.request.presentation.ui.offerconfirmed.components.OfferConfirmedActionButtons
import com.carenest.provider.designsystem.R as RD
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.domain.model.PatientInfo
import com.carenest.request.presentation.asString
import com.carenest.request.presentation.ui.offerconfirmed.components.CancellationInfoBanner
import com.carenest.request.presentation.ui.offerconfirmed.components.PatientCard

@Composable
fun OfferConfirmedScreen(
    requestId: String,
    onViewDetails: (String) -> Unit,
    onCancelled: () -> Unit,
    onShowQrCode: () -> Unit,
    onOpenChat: (String) -> Unit,
    onVisitCompleted: (String) -> Unit,
    viewModel: OfferConfirmedViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(requestId) {
        viewModel.onIntent(OfferConfirmedIntent.Load(requestId))
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is OfferConfirmedEffect.NavigateToDetails -> onViewDetails(effect.offerId)
            is OfferConfirmedEffect.NavigateToVisitCompleted -> onVisitCompleted(effect.requestId)
            is OfferConfirmedEffect.NavigateToQrCode -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.visit_code_generated, effect.visitCode.code),
                    Toast.LENGTH_LONG,
                ).show()
                onShowQrCode()
            }
            OfferConfirmedEffect.NavigateBackToList -> onCancelled()
            is OfferConfirmedEffect.ShowError -> {
                Toast.makeText(context, effect.message.asString(context), Toast.LENGTH_LONG).show()
            }
            is OfferConfirmedEffect.InitiateCall -> {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${effect.phoneNumber}")
                }
                context.startActivity(intent)
            }
            is OfferConfirmedEffect.OpenChat -> {
                onOpenChat(effect.patientId)
            }
        }
    }

    OfferConfirmedContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun OfferConfirmedContent(
    state: OfferConfirmedUiState,
    onIntent: (OfferConfirmedIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Theme.colors.backGround,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(color = Theme.colors.primary)
                state.offer != null -> OfferConfirmedBody(
                    contract = state.offer,
                    isCancelling = state.cancelDialog.isSubmitting,
                    isGeneratingVisitCode = state.isGeneratingVisitCode,
                    onIntent = onIntent,
                )
                else -> BasicText(
                    text = stringResource(R.string.request_not_found),
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                )
            }
        }

        if (state.cancelDialog.isVisible) {
            CancelRequestDialog(
                selectedReason = state.cancelDialog.selectedReason,
                onReasonSelected = { onIntent(OfferConfirmedIntent.ReasonSelected(it)) },
                note = state.cancelDialog.note,
                onNoteChange = { onIntent(OfferConfirmedIntent.NoteChanged(it)) },
                onDismiss = { onIntent(OfferConfirmedIntent.DismissCancelDialog) },
                onConfirm = { onIntent(OfferConfirmedIntent.ConfirmCancelClicked) },
                isSubmitting = state.cancelDialog.isSubmitting,
            )
        }
    }
}

@Composable
private fun OfferConfirmedBody(
    contract: Offer,
    isCancelling: Boolean,
    isGeneratingVisitCode: Boolean,
    onIntent: (OfferConfirmedIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Theme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Theme.spacing.extraLarge))

        SuccessCheckBadge()

        Spacer(Modifier.height(Theme.spacing.medium))

        BasicText(
            text = stringResource(R.string.offer_confirmed_title),
            style = Theme.typography.displayMedium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(Modifier.height(6.dp))

        BasicText(
            text = stringResource(R.string.offer_confirmed_subtitle),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(Modifier.height(Theme.spacing.large))

        CancellationInfoBanner(
            message = stringResource(R.string.offer_confirmed_cancellation_info),
        )

        Spacer(Modifier.height(Theme.spacing.large))

        PatientCard(
            name = contract.patientInfo.name,
            estimatedArrivalTime = contract.estimatedArrival,
            onCallClick = {onIntent(OfferConfirmedIntent.OnCallClicked)},
            onMessageClick = {onIntent(OfferConfirmedIntent.OnMessageClicked)},
        )

        Spacer(Modifier.height(Theme.spacing.medium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            InfoCard(
                icon = painterResource(RD.drawable.ic_location),
                label = stringResource(R.string.distance),
                value = contract.distanceMiles?.let {
                    stringResource(R.string.nurse_requests_distance_miles, it)
                } ?: stringResource(R.string.not_available),
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                icon = painterResource(RD.drawable.ic_id_card),
                label = stringResource(R.string.service),
                value = contract.serviceType,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(Theme.spacing.extraLarge))

        OfferConfirmedActionButtons(
            onShowQrCodeClick = { onIntent(OfferConfirmedIntent.OnShowQrCodeClicked) },
            onCancelClick = { onIntent(OfferConfirmedIntent.CancelClicked) },
            onShowOfferDetailsClick = { onIntent(OfferConfirmedIntent.ViewDetailsClicked) },
            isCancelling = isCancelling,
            isGeneratingVisitCode = isGeneratingVisitCode,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InfoCard(
    icon: Painter,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Theme.colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont, fontWeight = FontWeight.Normal),
        )
        BasicText(
            text = value,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.SemiBold
            ),
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun OfferConfirmedPreview() {
    SpTheme {
        OfferConfirmedContent(
            state = OfferConfirmedUiState(
                isLoading = false,
                offer = Offer(
                    offerId = "OFF-001",
                    patientInfo = PatientInfo(
                        id = "PAT-001",
                        name = "Sarah Johnson",
                        age = 68,
                        image = "https://picsum.photos/200",
                        phone = "+1 234 567 8901",
                        addressLine = "",
                        addressDetail = "",
                        summery = "",
                        distanceMiles = 3.5f
                    ),
                    visitDate = "Aug 2, 2026",
                    visitTime = "10:30 AM",
                    distanceMiles = 3.5f,
                    estimatedArrival = "15 min",
                    estimatedDuration = "1 hr",
                    totalAmount = 120f,
                    serviceType = "Home Nursing",
                    serviceImage = "https://picsum.photos/300/200"
                ),
            ), onIntent = {}
        )
    }
}
