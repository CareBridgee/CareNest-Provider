package com.carenest.request.presentation.ui.patientsummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.R as RD
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.MedicalHistoryItem

@Composable
fun MedicalHistoryCard(
    medicalHistory: List<MedicalHistoryItem>,
    previousSurgeries: String? = null,
    previousHospitalizations: String? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .border(
                width = 1.dp,
                color = Theme.colors.surfaceVariant.copy(alpha = 0.5f),
                shape = Theme.shapes.extraLarge,
            )
            .padding(Theme.spacing.large),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.patient_summary_medical_history),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            val combinedItems = buildList {
                if (!previousSurgeries.isNullOrBlank()) {
                    add(MedicalHistoryItem(type = "Previous Surgeries", description = previousSurgeries))
                }
                if (!previousHospitalizations.isNullOrBlank()) {
                    add(MedicalHistoryItem(type = "Previous Hospitalizations", description = previousHospitalizations))
                }
                addAll(medicalHistory)
            }

            if (combinedItems.isEmpty()) {
                Text(
                    text = stringResource(R.string.patient_summary_no_history),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.hint,
                    ),
                )
            } else {
                Column {
                    combinedItems.forEachIndexed { index, item ->
                        TimelineRow(
                            item = item,
                            isLast = index == combinedItems.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(
    item: MedicalHistoryItem,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(RD.drawable.ic_heart_beat),
                    contentDescription = null,
                    tint = Theme.colors.onPrimary,
                    modifier = Modifier.size(16.dp),
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Theme.colors.divider),
                )
            }
        }

        Spacer(modifier = Modifier.width(Theme.spacing.medium))

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else Theme.spacing.medium)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.infoContainer.copy(alpha = 0.5f))
                .padding(Theme.spacing.medium),
        ) {
            Column {
                if (item.type.isNotBlank()) {
                    Text(
                        text = item.type,
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.primary,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))
                }

                Text(
                    text = item.description.ifBlank { "—" },
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun MedicalHistoryCardPreview() {
    com.carenest.provider.designsystem.theme.SpTheme {
        MedicalHistoryCard(
            medicalHistory = listOf(
                MedicalHistoryItem(type = "2021 • Previous Surgery", description = "Appendectomy"),
                MedicalHistoryItem(type = "2022 • Hospitalization", description = "Hospitalized for pneumonia"),
            ),
        )
    }
}
