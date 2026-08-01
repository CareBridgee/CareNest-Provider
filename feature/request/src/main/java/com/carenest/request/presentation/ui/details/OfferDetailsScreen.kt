package com.carenest.request.presentation.ui.details

import android.content.Intent
import android.net.Uri
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
            }

            is OfferDetailsEffect.ShowSummary -> {
                // Show summary dialog or bottom sheet
            }

            is OfferDetailsEffect.OpenMaps -> {
                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(effect.address)}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                } else {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(effect.address)}"))
                    context.startActivity(browserIntent)
                }
            }
        }
    }

    OfferDetailsContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun OfferDetailsContent(
    state: OfferDetailsUiState,
    onIntent: (OfferDetailsIntent) -> Unit,
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
                    onIntent = onIntent,
                )

                else -> BasicText(
                    text = stringResource(R.string.request_not_found),
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
                    modifier = Modifier.align(Alignment.Center),
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
    onIntent: (OfferDetailsIntent) -> Unit,
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
                onCopyAddressClicked = { onIntent(OfferDetailsIntent.CopyAddressClicked) },
                onMapClicked = { onIntent(OfferDetailsIntent.OpenInMapsClicked) })
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
                    serviceType = "Wound Care",
                    serviceImage = ""
                ),
            ),
            onIntent = {},
        )
    }
}
