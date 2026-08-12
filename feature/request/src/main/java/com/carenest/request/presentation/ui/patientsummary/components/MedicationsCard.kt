package com.carenest.request.presentation.ui.patientsummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun MedicationsCard(
    medications: List<String>,
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
                text = stringResource(R.string.patient_summary_current_medications),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            if (medications.isEmpty()) {
                Text(
                    text = stringResource(R.string.patient_summary_no_medications),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.hint,
                    ),
                )
            } else {
                Column {
                    medications.forEachIndexed { index, medication ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Theme.spacing.small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Theme.colors.primaryContainer.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(RD.drawable.ic_pill),
                                    contentDescription = null,
                                    tint = Theme.colors.primary,
                                    modifier = Modifier.size(18.dp),
                                )
                            }

                            Spacer(modifier = Modifier.width(Theme.spacing.medium))

                            Text(
                                text = medication,
                                style = Theme.typography.body.medium.copy(
                                    color = Theme.colors.primaryFont,
                                    fontWeight = FontWeight.Medium,
                                ),
                            )
                        }

                        if (index < medications.lastIndex) {
                            HorizontalDivider(
                                color = Theme.colors.divider,
                                thickness = 1.dp,
                                modifier = Modifier.padding(start = 52.dp),
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
private fun MedicationsCardPreview() {
    com.carenest.provider.designsystem.theme.SpTheme {
        MedicationsCard(
            medications = listOf("Metformin", "Lisinopril", "Aspirin"),
        )
    }
}
