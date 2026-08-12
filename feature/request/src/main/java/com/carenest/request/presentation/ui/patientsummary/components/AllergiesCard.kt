package com.carenest.request.presentation.ui.patientsummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllergiesCard(
    allergies: List<String>,
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(RD.drawable.ic_info),
                    contentDescription = null,
                    tint = Theme.colors.error,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(Theme.spacing.extraSmall))
                Text(
                    text = stringResource(R.string.patient_summary_allergies),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.error,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            if (allergies.isEmpty()) {
                Text(
                    text = stringResource(R.string.patient_summary_no_allergies),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.hint,
                    ),
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                ) {
                    allergies.forEach { allergy ->
                        Box(
                            modifier = Modifier
                                .clip(Theme.shapes.extraLarge)
                                .background(Theme.colors.errorContainer)
                                .padding(
                                    horizontal = Theme.spacing.medium,
                                    vertical = Theme.spacing.small,
                                ),
                        ) {
                            Text(
                                text = allergy,
                                style = Theme.typography.body.small.copy(
                                    color = Theme.colors.onErrorContainer,
                                    fontWeight = FontWeight.Bold,
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
private fun AllergiesCardPreview() {
    com.carenest.provider.designsystem.theme.SpTheme {
        AllergiesCard(
            allergies = listOf("Penicillin", "Peanuts"),
        )
    }
}
