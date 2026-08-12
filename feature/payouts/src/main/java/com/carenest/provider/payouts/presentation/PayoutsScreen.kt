package com.carenest.provider.payouts.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.carenest.provider.designsystem.components.bottomnav.SPBottomNavigation
import com.carenest.provider.designsystem.components.bottomsheet.BaseBottomSheet
import com.carenest.provider.designsystem.components.emptystate.EmptyState
import com.carenest.provider.designsystem.components.payout.PayoutMethodSelection
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.payouts.domain.model.PayoutItem
import com.carenest.provider.payouts.domain.model.PayoutStatus
import com.carenest.provider.payouts.domain.model.PayoutSummary

@Composable
fun PayoutsScreen(
    onNavigateBackToEarnings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PayoutsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            PayoutsEffect.NavigateBackToEarnings -> onNavigateBackToEarnings()
            is PayoutsEffect.ShowToast -> {}
        }
    }

    PayoutsScreenContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayoutsScreenContent(
    state: PayoutsUiState,
    onIntent: (PayoutsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Theme.colors.backGround,
        topBar = {
            CareNestTopBar(
                title = stringResource(com.carenest.provider.payouts.R.string.top_bar_payouts_title),
                leading = TopBarLeading.Back(
                    onBackClick = { onIntent(PayoutsIntent.BackToServiceEarningsClicked) }
                ),
                trailingAvatarUrl = "https://picsum.photos/200/300"
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Theme.colors.backGround)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Theme.colors.primary)
                    }
                }

                state.isError -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Theme.spacing.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
                        ) {
                            BasicText(
                                text = state.errorMessage ?: stringResource(com.carenest.provider.payouts.R.string.error_occurred),
                                style = Theme.typography.body.large.copy(color = Theme.colors.error)
                            )
                            Button(
                                onClick = { onIntent(PayoutsIntent.RefreshClicked) },
                                colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primary)
                            ) {
                                BasicText(
                                    text = stringResource(com.carenest.provider.payouts.R.string.retry),
                                    style = Theme.typography.body.medium.copy(color = Color.White)
                                )
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Theme.spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))
                            AvailablePayoutCard(
                                summary = state.summary,
                                onWithdrawClick = { onIntent(PayoutsIntent.WithdrawClicked) }
                            )
                        }

                        item {
                            PayoutSummaryRow(summary = state.summary)
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onIntent(PayoutsIntent.BackToServiceEarningsClicked) }
                                    .padding(vertical = Theme.spacing.small),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_arrow_back),
                                    contentDescription = null,
                                    tint = Color(0xFF006168),
                                    modifier = Modifier.size(18.dp)
                                )
                                BasicText(
                                    text = stringResource(com.carenest.provider.payouts.R.string.back_to_service_earnings),
                                    style = Theme.typography.body.medium.copy(
                                        color = Color(0xFF006168),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        item {
                            BasicText(
                                text = stringResource(com.carenest.provider.payouts.R.string.withdraw_history),
                                style = Theme.typography.title.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Theme.colors.primaryFont
                                )
                            )
                        }

                        if (state.isEmpty) {
                            item {
                                EmptyState(
                                    title = stringResource(com.carenest.provider.payouts.R.string.no_withdraw_history),
                                    description = stringResource(com.carenest.provider.payouts.R.string.no_withdraw_history_desc),
                                    modifier = Modifier.padding(vertical = Theme.spacing.large)
                                )
                            }
                        } else {
                            items(
                                items = state.historyList,
                                key = { it.id }
                            ) { item ->
                                PayoutHistoryCard(item = item)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(Theme.spacing.medium))
                        }
                    }
                }
            }
        }

        if (state.isWithdrawalModalOpen) {
            BaseBottomSheet(
                onDismissRequest = { onIntent(PayoutsIntent.DismissModal) }
            ) {
                PayoutMethodSelection(
                    onWithdraw = {
                        onIntent(PayoutsIntent.ConfirmWithdrawal(state.summary.availableBalance))
                    }
                )
            }
        }
    }
}

@Composable
fun AvailablePayoutCard(
    summary: PayoutSummary,
    onWithdrawClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(24.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, cardShape)
            .padding(Theme.spacing.large),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BasicText(
                text = stringResource(com.carenest.provider.payouts.R.string.available_for_payout),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Medium
                )
            )

            BasicText(
                text = summary.availableBalance,
                style = Theme.typography.displayMedium.copy(
                    color = Color(0xFF006168),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(Theme.spacing.small))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF006168))
                    .clickable { onWithdrawClick() }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                BasicText(
                    text = stringResource(com.carenest.provider.payouts.R.string.withdraw_now),
                    style = Theme.typography.body.medium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun PayoutSummaryRow(
    summary: PayoutSummary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        SummaryMiniCard(
            title = stringResource(com.carenest.provider.payouts.R.string.summary_pending),
            amount = summary.pendingAmount,
            modifier = Modifier.weight(1f)
        )
        SummaryMiniCard(
            title = stringResource(com.carenest.provider.payouts.R.string.summary_this_month),
            amount = summary.thisMonthAmount,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryMiniCard(
    title: String,
    amount: String,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, cardShape)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        BasicText(
            text = title,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        )
        BasicText(
            text = amount,
            style = Theme.typography.body.large.copy(
                color = Color(0xFF006168),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
    }
}

@Composable
fun PayoutHistoryCard(
    item: PayoutItem,
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
        // Method Icon Container
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
                tint = Color(0xFF006168),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(Theme.spacing.medium))

        // Title & Date
        Column(
            modifier = Modifier.weight(1f)
        ) {
            BasicText(
                text = item.methodTitle,
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont,
                    fontSize = 16.sp
                )
            )
            BasicText(
                text = item.dateTime,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
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
                    color = Theme.colors.primaryFont,
                    fontSize = 18.sp
                )
            )

            val (bgColor, textColor, label) = when (item.status) {
                PayoutStatus.PENDING -> Triple(Color(0xFFE8ECEF), Color(0xFF5F6D7A), stringResource(com.carenest.provider.payouts.R.string.status_pending_caps))
                PayoutStatus.COMPLETED -> Triple(Color(0xFFE6F7ED), Color(0xFF006168), stringResource(com.carenest.provider.payouts.R.string.status_completed_caps))
                PayoutStatus.FAILED -> Triple(Color(0xFFFCE8E6), Color(0xFFD93025), stringResource(com.carenest.provider.payouts.R.string.status_failed_caps))
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

// ----------------------------------------------------
// Previews
// ----------------------------------------------------

@Preview(name = "Payouts Screen - Success State", showBackground = true, heightDp = 800)
@Composable
private fun PayoutsScreenSuccessPreview() {
    val sampleHistory = listOf(
        PayoutItem("1", "Bank Transfer", "Oct 24, 2023 • 09:15 AM", "$450.00", PayoutStatus.PENDING, R.drawable.ic_bank),
        PayoutItem("2", "Wallet Transfer", "Oct 20, 2023 • 04:30 PM", "$1,200.00", PayoutStatus.COMPLETED, R.drawable.ic_wallet),
        PayoutItem("3", "Bank Transfer", "Oct 15, 2023 • 11:00 AM", "$890.00", PayoutStatus.COMPLETED, R.drawable.ic_bank),
        PayoutItem("4", "Instant Pay", "Oct 12, 2023 • 08:45 AM", "$150.00", PayoutStatus.FAILED, R.drawable.ic_flash),
        PayoutItem("5", "Bank Transfer", "Oct 05, 2023 • 02:20 PM", "$2,100.00", PayoutStatus.COMPLETED, R.drawable.ic_bank)
    )

    SpTheme {
        PayoutsScreenContent(
            state = PayoutsUiState(
                summary = PayoutSummary("$1,248.50", "$320.00", "$4,850.00"),
                historyList = sampleHistory
            ),
            onIntent = {}
        )
    }
}

@Preview(name = "Payouts Screen - Loading State", showBackground = true, heightDp = 800)
@Composable
private fun PayoutsScreenLoadingPreview() {
    SpTheme {
        PayoutsScreenContent(
            state = PayoutsUiState(isLoading = true),
            onIntent = {}
        )
    }
}

@Preview(name = "Payouts Screen - Empty State", showBackground = true, heightDp = 800)
@Composable
private fun PayoutsScreenEmptyPreview() {
    SpTheme {
        PayoutsScreenContent(
            state = PayoutsUiState(isEmpty = true),
            onIntent = {}
        )
    }
}

@Preview(name = "Payouts Screen - Error State", showBackground = true, heightDp = 800)
@Composable
private fun PayoutsScreenErrorPreview() {
    SpTheme {
        PayoutsScreenContent(
            state = PayoutsUiState(isError = true, errorMessage = stringResource(com.carenest.provider.payouts.R.string.error_loading_payouts)),
            onIntent = {}
        )
    }
}
