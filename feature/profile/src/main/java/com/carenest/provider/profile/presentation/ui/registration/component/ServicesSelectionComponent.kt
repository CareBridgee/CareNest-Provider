package com.carenest.provider.profile.presentation.ui.registration.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.progressSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.components.shimmer.ShimmerLine
import com.carenest.provider.designsystem.components.shimmer.ShimmerPlaceholder
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.util.noRippleClickable
import com.carenest.provider.profile.presentation.ui.registration.ServiceUi
import com.carenest.provider.profile.presentation.ui.registration.ServicesUiState

@Composable
fun ServicesSelectionComponent(
    state: ServicesUiState,
    onServiceToggle: (ServiceUi) -> Unit,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .verticalScroll(scrollState)
            .padding(horizontal = Theme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicText(
            text = stringResource(com.carenest.provider.profile.R.string.services_title),
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.small))

        BasicText(
            text = stringResource(com.carenest.provider.profile.R.string.services_subtitle),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        when {
            state.isLoading -> ServicesGridLoadingSkeleton()
            state.errorMessage != null -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                ) {
                    BasicText(
                        text = when (state.errorMessage) {
                            "error_services_unavailable" -> stringResource(
                                com.carenest.provider.profile.R.string.error_services_unavailable
                            )
                            else -> state.errorMessage
                        },
                        style = Theme.typography.body.medium.copy(color = Theme.colors.error),
                    )
                    SecondaryButton(
                        caption = stringResource(com.carenest.provider.profile.R.string.retry),
                        onClick = onRetry,
                    )
                }
            }
            else -> ServicesGrid(
                services = state.availableServices,
                selectedServices = state.selectedServices,
                onServiceToggle = onServiceToggle,
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.large))

        InfoBanner(
            text = stringResource(com.carenest.provider.profile.R.string.services_info_banner),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))
    }
}

@Composable
private fun ServicesGridLoadingSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .progressSemantics(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            ) {
                repeat(2) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(104.dp)
                            .padding(Theme.spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                    ) {
                        ShimmerPlaceholder(
                            modifier = Modifier.size(40.dp),
                            shape = Theme.shapes.medium,
                        )
                        ShimmerLine(Modifier.width(92.dp), height = 16.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ServicesGrid(
    services: List<ServiceUi>,
    selectedServices: List<ServiceUi>,
    onServiceToggle: (ServiceUi) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        services.chunked(2).forEach { rowServices ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                rowServices.forEach { service ->
                    ServiceCard(
                        service = service,
                        isSelected = selectedServices.contains(service),
                        onClick = { onServiceToggle(service) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowServices.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: ServiceUi,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Theme.colors.primaryContainer else Theme.colors.surface
    val borderColor = if (isSelected) Theme.colors.primary else Theme.colors.divider

    Column(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = Theme.shapes.large, clip = false)
            .clip(Theme.shapes.large)
            .background(backgroundColor)
            .border(2.dp, borderColor, Theme.shapes.large)
            .noRippleClickable { onClick() }
            .padding(Theme.spacing.medium),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.disable),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = service.icon),
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(24.dp)
            )
        }
        BasicText(
            text = service.title,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            )
        )
    }
}

@Composable
private fun InfoBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(Theme.shapes.medium)
            .background(Theme.colors.infoContainer)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            tint = Theme.colors.tint,
            modifier = Modifier.size(20.dp)
        )
        BasicText(
            text = text,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ServicesSelectionComponentPreview() {
    val mockServices = listOf(
        ServiceUi(R.drawable.ic_syringe, "Injection"),
        ServiceUi(R.drawable.ic_pill, "IV Therapy"),
        ServiceUi(R.drawable.ic_category_sales, "Blood Collection"),
        ServiceUi(R.drawable.ic_check, "Wound Dressing"),
        ServiceUi(R.drawable.ic_assignment, "Catheter Care"),
        ServiceUi(R.drawable.ic_elderly, "Elderly Care"),
        ServiceUi(R.drawable.ic_category_kids, "Child Care"),
        ServiceUi(R.drawable.ic_profile, "Post-Surgery Care"),
        ServiceUi(R.drawable.ic_category_women, "Maternal Care"),
        ServiceUi(R.drawable.ic_physical_therapy, "Physiotherapy"),
        ServiceUi(R.drawable.ic_heart_beat, "ECG Service"),
        ServiceUi(R.drawable.ic_home, "Home Assessment")
    )

    SpTheme {
        ServicesSelectionComponent(
            state = ServicesUiState(
                availableServices = mockServices,
                selectedServices = listOf(mockServices[0], mockServices[1], mockServices[2])
            ),
            onServiceToggle = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ServicesSelectionComponentDarkPreview() {
    val mockServices = listOf(
        ServiceUi(R.drawable.ic_syringe, "Injection"),
        ServiceUi(R.drawable.ic_pill, "IV Therapy"),
        ServiceUi(R.drawable.ic_category_sales, "Blood Collection"),
        ServiceUi(R.drawable.ic_check, "Wound Dressing"),
        ServiceUi(R.drawable.ic_assignment, "Catheter Care"),
        ServiceUi(R.drawable.ic_elderly, "Elderly Care"),
        ServiceUi(R.drawable.ic_category_kids, "Child Care"),
        ServiceUi(R.drawable.ic_profile, "Post-Surgery Care"),
        ServiceUi(R.drawable.ic_category_women, "Maternal Care"),
        ServiceUi(R.drawable.ic_physical_therapy, "Physiotherapy"),
        ServiceUi(R.drawable.ic_heart_beat, "ECG Service"),
        ServiceUi(R.drawable.ic_home, "Home Assessment")
    )

    SpTheme {
        ServicesSelectionComponent(
            state = ServicesUiState(
                availableServices = mockServices,
                selectedServices = listOf(mockServices[0], mockServices[1], mockServices[2])
            ),
            onServiceToggle = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ServicesSelectionBottomPreview() {
    SpTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.backGround)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            InfoBanner(
                text = stringResource(com.carenest.provider.profile.R.string.services_info_banner),
                modifier = Modifier.fillMaxWidth()
            )

            PrimaryButton(
                caption = stringResource(
                    com.carenest.provider.profile.R.string.continue_to_step,
                    4,
                    3
                ),
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ServicesSelectionBottomDarkPreview() {
    SpTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.backGround)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            InfoBanner(
                text = stringResource(com.carenest.provider.profile.R.string.services_info_banner),
                modifier = Modifier.fillMaxWidth()
            )

            PrimaryButton(
                caption = stringResource(
                    com.carenest.provider.profile.R.string.continue_to_step,
                    4,
                    3
                ),
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
