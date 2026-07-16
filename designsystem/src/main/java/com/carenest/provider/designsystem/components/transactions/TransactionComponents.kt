package com.carenest.provider.designsystem.components.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.util.bounceClick

// State data models
data class EarningsState(
    val totalAmount: String,
    val growthPercentage: Int,
    val jobsCount: Int
)

enum class TransactionStatus {
    Completed, Processing, Canceled
}

data class TransactionItem(
    val id: String,
    val serviceName: String,
    val patientName: String,
    val date: String,
    val duration: String,
    val amount: String,
    val status: TransactionStatus,
    val icon: Int
)

@Composable
fun EarningsHeader(
    state: EarningsState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.onPrimaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(Theme.spacing.large)
        ) {
            BasicText(
                text = stringResource(id = R.string.earnings_total_title),
                style = Theme.typography.body.medium.copy(color = Color.White.copy(alpha = 0.8f))
            )
            BasicText(
                text = state.totalAmount,
                style = Theme.typography.displayMedium.copy(
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
            ) {
                // Growth pill
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trending_up),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    BasicText(
                        text = stringResource(
                            id = R.string.earnings_vs_last_month,
                            state.growthPercentage
                        ),
                        style = Theme.typography.body.small.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Jobs pill
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    BasicText(
                        text = stringResource(id = R.string.earnings_jobs_suffix, state.jobsCount),
                        style = Theme.typography.body.small.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItemCard(
    item: TransactionItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val cardShape = Theme.shapes.large

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, cardShape)
            .bounceClick(shape = cardShape, onClick = onClick)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.secondary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(Theme.spacing.medium))

        // Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            BasicText(
                text = item.serviceName,
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont,
                    fontSize = 18.sp
                )
            )
            BasicText(
                text = stringResource(id = R.string.patient_label, item.patientName),
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
            )
            BasicText(
                text = "${item.date} • ${item.duration}",
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.hint,
                    fontSize = 11.sp
                )
            )
        }

        // Amount & Status
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BasicText(
                text = item.amount,
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.onPrimaryContainer,
                    fontSize = 20.sp
                )
            )

            val (containerColor, contentColor, labelRes) = when (item.status) {
                TransactionStatus.Completed -> Triple(
                    Theme.colors.successContainer,
                    Theme.colors.onSuccessContainer,
                    R.string.status_completed
                )

                TransactionStatus.Processing -> Triple(
                    Theme.colors.processingContainer,
                    Theme.colors.onProcessingContainer,
                    R.string.status_processing
                )

                TransactionStatus.Canceled -> Triple(
                    Theme.colors.errorContainer,
                    Theme.colors.onErrorContainer,
                    R.string.status_canceled
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(containerColor)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                BasicText(
                    text = stringResource(id = labelRes),
                    style = Theme.typography.body.small.copy(
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TransactionHistorySection(
    earnings: EarningsState,
    transactions: List<TransactionItem>,
    onStatementClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        EarningsHeader(state = earnings)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                text = stringResource(id = R.string.transactions_title),
                style = Theme.typography.title.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )

            Row(
                modifier = Modifier.clickable { onStatementClick() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_download),
                    contentDescription = null,
                    tint = Theme.colors.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                BasicText(
                    text = stringResource(id = R.string.transactions_statement),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Chips/Filters (Mock)
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
        ) {
            FilterChip(
                selected = true,
                onClick = {},
                label = { Text(stringResource(id = R.string.transactions_all_services)) },
                leadingIcon = { Icon(Icons.Default.AddBox, null, Modifier.size(18.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Theme.colors.onPrimaryContainer,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
            FilterChip(
                selected = false,
                onClick = {},
                label = { Text(stringResource(id = R.string.transactions_this_month)) },
                leadingIcon = { Icon(Icons.Default.CalendarToday, null, Modifier.size(18.dp)) }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
        ) {
            transactions.forEach { item ->
                TransactionItemCard(item = item)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionHistoryPreview() {
    val earnings = EarningsState(
        totalAmount = "$4,280.50",
        growthPercentage = 12,
        jobsCount = 34
    )

    val transactions = listOf(
        TransactionItem(
            "1",
            "Wound Care",
            "Eleanor Rigby",
            "Oct 24, 2023",
            "2.5 hours",
            "$125.00",
            TransactionStatus.Completed,
            R.drawable.ic_syringe
        ),
        TransactionItem(
            "2",
            "Health Assessment",
            "Arthur Dent",
            "Oct 23, 2023",
            "1.0 hour",
            "$85.00",
            TransactionStatus.Completed,
            R.drawable.ic_heart_beat
        ),
        TransactionItem(
            "3",
            "Meds Management",
            "Sarah Connor",
            "Oct 22, 2023",
            "1.5 hours",
            "$110.00",
            TransactionStatus.Processing,
            R.drawable.ic_pill
        ),
        TransactionItem(
            "4",
            "Physical Therapy",
            "James Bond",
            "Oct 20, 2023",
            "2.0 hours",
            "$150.00",
            TransactionStatus.Completed,
            R.drawable.ic_physical_therapy
        ),
        TransactionItem(
            "5",
            "Elderly Companionship",
            "Rose Dawson",
            "Oct 19, 2023",
            "4.0 hours",
            "$200.00",
            TransactionStatus.Canceled,
            R.drawable.ic_elderly
        )
    )

    SpTheme {
        Surface(color = Theme.colors.backGround) {
            TransactionHistorySection(earnings = earnings, transactions = transactions)
        }
    }
}
