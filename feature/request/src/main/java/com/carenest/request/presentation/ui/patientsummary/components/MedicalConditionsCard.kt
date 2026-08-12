package com.carenest.request.presentation.ui.patientsummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MedicalConditionsCard(
    conditions: List<String>,
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
                text = stringResource(R.string.patient_summary_medical_conditions),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            if (conditions.isEmpty()) {
                Text(
                    text = stringResource(R.string.patient_summary_no_conditions),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.hint,
                    ),
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                ) {
                    conditions.forEach { condition ->
                        Box(
                            modifier = Modifier
                                .clip(Theme.shapes.extraLarge)
                                .background(Theme.colors.disable.copy(alpha = 0.6f))
                                .padding(
                                    horizontal = Theme.spacing.medium,
                                    vertical = Theme.spacing.small,
                                ),
                        ) {
                            Text(
                                text = condition,
                                style = Theme.typography.body.small.copy(
                                    color = Theme.colors.primaryFont,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun MedicalConditionsCardPreview() {
    com.carenest.provider.designsystem.theme.SpTheme {
        MedicalConditionsCard(
            conditions = listOf("Diabetes", "Hypertension", "Heart disease"),
        )
    }
}
