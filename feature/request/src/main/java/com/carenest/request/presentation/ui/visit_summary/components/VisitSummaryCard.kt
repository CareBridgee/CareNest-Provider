package com.carenest.request.presentation.ui.visit_summary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.VisitSummary
import com.carenest.provider.designsystem.R as RD


@Composable
fun VisitSummaryCard(summary: VisitSummary, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.visit_completed_summary_title),
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Theme.colors.primary,
            )
            if (summary.isVerified) {
                Text(
                    text = stringResource(R.string.visit_completed_verified_badge),
                    style = Theme.typography.hint.small,
                    color = Theme.colors.primary,
                    modifier = Modifier.background(
                        Theme.colors.primaryContainer, RoundedCornerShape(20.dp)
                    )
                        .padding(Theme.spacing.space10, Theme.spacing.extraSmall),
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(top = Theme.spacing.medium))

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = Theme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryField(
                label = stringResource(R.string.visit_completed_professional_label),
                value = summary.professionalName,
                modifier = Modifier.weight(1f),
                icon = painterResource(RD.drawable.ic_personality)
            )
            SummaryField(
                label = stringResource(R.string.visit_completed_service_type_label),
                value = summary.serviceType,
                modifier = Modifier.weight(1f),
                icon = painterResource(RD.drawable.ic_service)
            )
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryField(
                label = stringResource(R.string.visit_completed_duration_label),
                value = stringResource(
                    R.string.visit_completed_duration_value,
                    summary.durationMinutes
                ),
                modifier = Modifier.weight(1f),
                icon = painterResource(RD.drawable.ic_clock)
            )
            SummaryField(
                label = stringResource(R.string.visit_completed_date_label),
                value = summary.completedDate,
                modifier = Modifier.weight(1f),
                icon = painterResource(RD.drawable.ic_date)
            )
        }
    }
}

@Composable
private fun SummaryField(
    label: String,
    value: String,
    icon: Painter,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = Theme.typography.body.small.copy(
                fontWeight = FontWeight.Normal
            ),
            color = Theme.colors.secondaryFont,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = icon,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.padding(top = Theme.spacing.space6).size(16.dp)
            )

            Text(
                text = value,
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Theme.colors.primaryFont,
                modifier = Modifier.padding(top = Theme.spacing.space6)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SpTheme {
        VisitSummaryCard(
            summary = VisitSummary(
                requestId = "",
                professionalName = "Professional Name",
                serviceType = "Service Type",
                durationMinutes = 60,
                completedDate = "30 jun",
                totalAmount = 85.0,
                isVerified = true,
            )
        )
    }
}