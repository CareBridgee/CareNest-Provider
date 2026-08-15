package com.carenest.provider.earnings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.bottomnav.BottomNavItem
import com.carenest.provider.designsystem.components.bottomnav.LocalBottomNavigationContentPadding
import com.carenest.provider.designsystem.components.bottomnav.SPBottomNavigation
import com.carenest.provider.designsystem.components.emptystate.EmptyState
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.earnings.domain.model.EarningStatus
import com.carenest.provider.earnings.domain.model.EarningsSummary
import com.carenest.provider.earnings.domain.model.ServiceEarningItem

@Composable
fun EarningsScreen(
    onNavigateToPayouts: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: EarningsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            EarningsEffect.NavigateToPayouts -> onNavigateToPayouts()
        }
    }

    EarningsScreenContent(
        state = state,
        onIntent = viewModel::onIntent,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@Composable
fun EarningsScreenContent(
    state: EarningsUiState,
    onIntent: (EarningsIntent) -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val bottomNavigationContentPadding = LocalBottomNavigationContentPadding.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        CareNestTopBar(
            title = stringResource(com.carenest.provider.earnings.R.string.top_bar_title),
            leading = onNavigateBack?.let { TopBarLeading.Back(it) },
            trailingAvatarUrl = "https://picsum.photos/200/300",
        )
        when {
            state.isLoading -> {
                EarningsLoadingSkeleton(modifier = Modifier.weight(1f))
            }

            state.isError -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(Theme.spacing.medium),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                    ) {
                        BasicText(
                            text = state.errorMessage ?: stringResource(com.carenest.provider.earnings.R.string.error_occurred),
                            style = Theme.typography.body.large.copy(color = Theme.colors.error),
                        )
                        Button(
                            onClick = { onIntent(EarningsIntent.RefreshClicked) },
                            colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primary),
                        ) {
                            BasicText(
                                text = stringResource(com.carenest.provider.earnings.R.string.retry),
                                style = Theme.typography.body.medium.copy(color = Color.White),
                            )
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Theme.spacing.medium),
                    contentPadding = PaddingValues(bottom = bottomNavigationContentPadding),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                ) {
                        item {
                            Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))
                            TotalEarningsSummaryCard(
                                summary = state.summary,
                                onViewPayoutsClick = { onIntent(EarningsIntent.ViewPayoutsClicked) }
                            )
                        }

                        item {
                            BasicText(
                                text = stringResource(com.carenest.provider.earnings.R.string.service_earnings),
                                style = Theme.typography.title.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Theme.colors.primaryFont
                                )
                            )
                        }

                        item {
                            FilterChipsRow(
                                selectedFilter = state.selectedFilter,
                                onFilterSelect = { onIntent(EarningsIntent.FilterSelected(it)) }
                            )
                        }

                        if (state.isEmpty || state.filteredEarnings.isEmpty()) {
                            item {
                                EmptyState(
                                    title = stringResource(com.carenest.provider.earnings.R.string.no_service_earnings),
                                    description = stringResource(com.carenest.provider.earnings.R.string.no_service_earnings_desc),
                                    modifier = Modifier.padding(vertical = Theme.spacing.large)
                                )
                            }
                        } else {
                            items(
                                items = state.filteredEarnings,
                                key = { it.id }
                            ) { item ->
                                ServiceEarningCard(item = item)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(Theme.spacing.medium))
                        }
                }
            }
        }
    }
}

@Composable
fun TotalEarningsSummaryCard(
    summary: EarningsSummary,
    onViewPayoutsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Theme.colors.primaryVariant)
            .padding(Theme.spacing.large)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
        ) {
            BasicText(
                text = stringResource(com.carenest.provider.earnings.R.string.total_earnings, summary.monthName),
                style = Theme.typography.body.medium.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Normal
                )
            )

            BasicText(
                text = summary.totalEarnings,
                style = Theme.typography.displayMedium.copy(
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(Theme.spacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Jobs Chip
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    BasicText(
                        text = stringResource(com.carenest.provider.earnings.R.string.jobs_count, summary.jobsCount),
                        style = Theme.typography.body.medium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                // View Payouts Button
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onViewPayoutsClick() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wallet),
                        contentDescription = null,
                        tint = Theme.colors.primaryVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    BasicText(
                        text = stringResource(com.carenest.provider.earnings.R.string.view_payouts),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.primaryVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChipsRow(
    selectedFilter: ServiceFilter,
    onFilterSelect: (ServiceFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        item {
            FilterChipItem(
                label = stringResource(com.carenest.provider.earnings.R.string.filter_all_services),
                iconRes = R.drawable.ic_services,
                isSelected = selectedFilter == ServiceFilter.ALL_SERVICES,
                onClick = { onFilterSelect(ServiceFilter.ALL_SERVICES) }
            )
        }
        item {
            FilterChipItem(
                label = stringResource(com.carenest.provider.earnings.R.string.filter_this_month),
                iconRes = R.drawable.ic_calendar,
                isSelected = selectedFilter == ServiceFilter.THIS_MONTH,
                onClick = { onFilterSelect(ServiceFilter.THIS_MONTH) }
            )
        }
        item {
            FilterChipItem(
                label = stringResource(com.carenest.provider.earnings.R.string.filter_sort),
                iconRes = R.drawable.ic_work,
                isSelected = selectedFilter == ServiceFilter.SORT,
                onClick = { onFilterSelect(ServiceFilter.SORT) }
            )
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Theme.colors.primaryVariant else Theme.colors.surface
    val contentColor = if (isSelected) Color.White else Theme.colors.primaryFont

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Transparent else Theme.colors.divider,
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        BasicText(
            text = label,
            style = Theme.typography.body.medium.copy(
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}

@Composable
fun ServiceEarningCard(
    item: ServiceEarningItem,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, cardShape)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Service Icon Container
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFE6F3F4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = null,
                tint = Theme.colors.primaryVariant,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(Theme.spacing.medium))

        // Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            BasicText(
                text = item.serviceTitle,
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont,
                    fontSize = 15.sp
                )
            )
            BasicText(
                text = stringResource(com.carenest.provider.earnings.R.string.patient_name_format, item.patientName),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontSize = 13.sp
                )
            )
            BasicText(
                text = stringResource(com.carenest.provider.earnings.R.string.date_duration_format, item.date, item.duration),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.hint,
                    fontSize = 12.sp
                )
            )
        }

        // Amount & Status Badge
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BasicText(
                text = item.amount,
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryVariant,
                    fontSize = 17.sp
                )
            )

            val (bgColor, textColor, label) = when (item.status) {
                EarningStatus.COMPLETED -> Triple(Color(0xFFE6F7ED), Color(0xFF0F9D58), stringResource(com.carenest.provider.earnings.R.string.status_completed))
                EarningStatus.PROCESSING -> Triple(Color(0xFFE8F0FE), Color(0xFF1A73E8), stringResource(com.carenest.provider.earnings.R.string.status_processing))
                EarningStatus.CANCELED -> Triple(Color(0xFFFCE8E6), Color(0xFFD93025), stringResource(com.carenest.provider.earnings.R.string.status_canceled))
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(bgColor)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                BasicText(
                    text = label,
                    style = Theme.typography.body.small.copy(
                        color = textColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

// ----------------------------------------------------
// Previews
// ----------------------------------------------------

@Preview(name = "Earnings Screen - Success State", showBackground = true, heightDp = 800)
@Composable
private fun EarningsScreenSuccessPreview() {
    val sampleList = listOf(
        ServiceEarningItem("1", "Wound Care from Eleanor Rigby", "Eleanor Rigby", "Oct 24, 2023", "2.5 hours", "$125.00", EarningStatus.COMPLETED, R.drawable.ic_syringe),
        ServiceEarningItem("2", "Health Assessment from Arthur Dent", "Arthur Dent", "Oct 23, 2023", "1.0 hour", "$85.00", EarningStatus.COMPLETED, R.drawable.ic_heart_beat),
        ServiceEarningItem("3", "Meds Management from Sarah Connor", "Sarah Connor", "Oct 22, 2023", "1.5 hours", "$110.00", EarningStatus.PROCESSING, R.drawable.ic_pill),
        ServiceEarningItem("4", "Physical Therapy from James Bond", "James Bond", "Oct 20, 2023", "2.0 hours", "$150.00", EarningStatus.COMPLETED, R.drawable.ic_physical_therapy),
        ServiceEarningItem("5", "Elderly Companionship from Rose Dawson", "Rose Dawson", "Oct 19, 2023", "4.0 hours", "$200.00", EarningStatus.CANCELED, R.drawable.ic_elderly)
    )

    SpTheme {
        EarningsScreenContent(
            state = EarningsUiState(
                summary = EarningsSummary("$4,280.50", 34),
                serviceEarnings = sampleList,
                filteredEarnings = sampleList
            ),
            onIntent = {}
        )
    }
}

@Preview(name = "Earnings Screen - Loading State", showBackground = true, heightDp = 800)
@Composable
private fun EarningsScreenLoadingPreview() {
    SpTheme {
        EarningsScreenContent(
            state = EarningsUiState(isLoading = true),
            onIntent = {}
        )
    }
}

@Preview(name = "Earnings Screen - Empty State", showBackground = true, heightDp = 800)
@Composable
private fun EarningsScreenEmptyPreview() {
    SpTheme {
        EarningsScreenContent(
            state = EarningsUiState(isEmpty = true),
            onIntent = {}
        )
    }
}

@Preview(name = "Earnings Screen - Error State", showBackground = true, heightDp = 800)
@Composable
private fun EarningsScreenErrorPreview() {
    SpTheme {
        EarningsScreenContent(
            state = EarningsUiState(isError = true, errorMessage = stringResource(com.carenest.provider.earnings.R.string.error_loading_earnings)),
            onIntent = {}
        )
    }
}
