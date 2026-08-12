package com.carenest.request.presentation.ui.details

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.model.PatientInfo
import com.carenest.request.presentation.asString
import com.carenest.request.presentation.ui.details.composable.LocationSection
import com.carenest.request.presentation.ui.details.composable.PatientSection
import com.carenest.request.presentation.ui.details.composable.PaymentSection
import com.carenest.request.presentation.ui.details.composable.ServiceSection
import com.carenest.provider.designsystem.R as RD

@Composable
fun OfferDetailsScreen(
    requestId: String,
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    onVisitCompleted: (String) -> Unit,
    onOpenPatientLocation: (Double, Double, String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OfferDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(requestId) {
        viewModel.onIntent(OfferDetailsIntent.Load(requestId))
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            OfferDetailsEffect.NavigateBack -> onBack()
            is OfferDetailsEffect.NavigateToVisitCompleted -> onVisitCompleted(effect.requestId)
            is OfferDetailsEffect.InitiateCall -> {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${effect.phone}")
                }
                context.startActivity(intent)
            }

            is OfferDetailsEffect.OpenChat -> {
                onOpenChat(effect.patientId)
            }

            is OfferDetailsEffect.CopyToClipboard -> {
                clipboardManager.setText(AnnotatedString(effect.text))
                Toast.makeText(
                    context,
                    context.getString(R.string.request_details_address_copied),
                    Toast.LENGTH_SHORT,
                ).show()
            }

            is OfferDetailsEffect.ShowSummary -> {
                Toast.makeText(context, effect.summary, Toast.LENGTH_LONG).show()
            }

            is OfferDetailsEffect.OpenMaps -> {
                val hasCoordinates = effect.latitude != null && effect.longitude != null
                val query = if (hasCoordinates) {
                    val label = effect.address.takeIf(String::isNotBlank)
                    if (label == null) {
                        "${effect.latitude},${effect.longitude}"
                    } else {
                        "${effect.latitude},${effect.longitude}(${Uri.encode(label)})"
                    }
                } else {
                    Uri.encode(effect.address)
                }
                val gmmIntentUri = Uri.parse("geo:0,0?q=$query")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                } else {
                    val browserQuery = if (hasCoordinates) {
                        "${effect.latitude},${effect.longitude}"
                    } else {
                        effect.address
                    }
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(browserQuery)}")
                    )
                    context.startActivity(browserIntent)
                }
            }

            is OfferDetailsEffect.ShowError -> {
                Toast.makeText(context, effect.message.asString(context), Toast.LENGTH_LONG).show()
            }
        }
    }

    OfferDetailsContent(
        state = state,
        onIntent = viewModel::onIntent,
        onOpenPatientLocation = onOpenPatientLocation,
        modifier = modifier,
    )
}

@Composable
private fun OfferDetailsContent(
    state: OfferDetailsUiState,
    onIntent: (OfferDetailsIntent) -> Unit,
    onOpenPatientLocation: (Double, Double, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Theme.colors.backGround,
        topBar = {
            CareNestTopBar(
                title = stringResource(R.string.offer_details_title),
                leading = TopBarLeading.Back { onIntent(OfferDetailsIntent.BackClicked) },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    color = Theme.colors.primary,
                    modifier = Modifier.align(Alignment.Center),
                )

                state.offer != null -> RequestDetailsBody(
                    offer = state.offer,
                    patientLocation = state.patientLocation,
                    isAddressLoading = state.isAddressLoading,
                    onIntent = onIntent,
                    onOpenPatientLocation = onOpenPatientLocation,
                )

                else -> BasicText(
                    text = stringResource(R.string.request_not_found),
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            if (state.isRequestCancelledByPatient) {
                com.carenest.provider.designsystem.components.dialog.CareNestDialog(
                    title = stringResource(R.string.cancellation_dialog_title),
                    message = stringResource(R.string.cancellation_dialog_message),
                    confirmText = stringResource(R.string.cancellation_dialog_ok),
                    dismissText = null,
                    onConfirm = { onIntent(OfferDetailsIntent.DismissRequestCancelledNotice) },
                    onDismiss = { onIntent(OfferDetailsIntent.DismissRequestCancelledNotice) },
                )
            }
        }
    }
}

@Composable
private fun SectionContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.surfaceVariant, Theme.shapes.medium)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        content = content
    )
}

@Composable
private fun RequestDetailsBody(
    offer: Offer,
    patientLocation: com.carenest.request.domain.model.PatientLocationDetails?,
    isAddressLoading: Boolean,
    onIntent: (OfferDetailsIntent) -> Unit,
    onOpenPatientLocation: (Double, Double, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Theme.shapes.medium)
                .background(Theme.colors.primaryContainer.copy(alpha = 0.4f))
                .padding(Theme.spacing.medium), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(RD.drawable.ic_calender),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(Theme.spacing.medium))
            Column {
                BasicText(
                    text = offer.visitDate,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Normal
                    )
                )
                BasicText(
                    text = offer.visitTime,
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        SectionContainer {
            PatientSection(
                offer = offer,
                onCallClick = { onIntent(OfferDetailsIntent.CallClicked) },
                onMessageClick = { onIntent(OfferDetailsIntent.MessageClicked) })
        }

        SectionContainer {
            ServiceSection(
                offer = offer,
                onViewSummaryClicked = { onIntent(OfferDetailsIntent.ViewSummaryClicked) })
        }

        SectionContainer {
            LocationSection(
                offer = offer,
                location = patientLocation,
                isAddressLoading = isAddressLoading,
                onCopyAddressClicked = { onIntent(OfferDetailsIntent.CopyAddressClicked) },
                onMapClicked = {
                    val latitude = offer.patientInfo.latitude
                    val longitude = offer.patientInfo.longitude
                    if (latitude != null && longitude != null) {
                        onOpenPatientLocation(
                            latitude,
                            longitude,
                            patientLocation?.address
                                ?.takeIf(String::isNotBlank)
                                ?: offer.patientInfo.addressLine,
                            if (patientLocation == null) offer.patientInfo.addressDetail else "",
                        )
                    } else {
                        onIntent(OfferDetailsIntent.OpenInMapsClicked)
                    }
                },
            )
        }

        SectionContainer { PaymentSection(offer = offer) }
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun OfferDetailsPreview() {
    SpTheme {
        OfferDetailsContent(
            state = OfferDetailsUiState(
                isLoading = false,
                offer = Offer(
                    offerId = "req-001",
                    visitDate = "Today, Nov 24",
                    visitTime = "2:30 PM",
                    distanceMiles = 2.4f,
                    estimatedArrival = "10:30 AM",
                    estimatedDuration = "45 mins",
                    patientInfo = PatientInfo(
                        id = "pat-001",
                        name = "Eleanor Vance",
                        age = 72,
                        image = "",
                        phone = "+1 (310) 555-0142",
                        addressLine = "1224 Oakwood Heights",
                        addressDetail = "Apt 4B, Beverly Hills, CA 90210",
                        summery = "Patient needs wound care post-surgery.",
                        distanceMiles = 2.4f
                    ),
                    totalAmount = 95f,
                    serviceType = "E2E Nursing Service 662736",
                    serviceImage = ""
                ),
            ),
            onIntent = {},
            onOpenPatientLocation = { _, _, _, _ -> },
        )
    }
}
