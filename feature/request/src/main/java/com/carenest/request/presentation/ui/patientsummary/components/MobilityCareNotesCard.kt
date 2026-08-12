package com.carenest.request.presentation.ui.patientsummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R

@Composable
fun MobilityCareNotesCard(
    mobilityStatus: String?,
    mobilityNotes: String?,
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
                text = stringResource(R.string.patient_summary_mobility_care_notes),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Theme.shapes.medium)
                    .background(Theme.colors.infoContainer.copy(alpha = 0.5f))
                    .padding(Theme.spacing.medium),
            ) {
                val annotatedText = buildAnnotatedString {
                    if (!mobilityStatus.isNullOrBlank()) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Theme.colors.primaryFont)) {
                            append("Mobility Status: ")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = Theme.colors.primaryFont)) {
                            append(mobilityStatus)
                        }
                    }

                    if (!mobilityNotes.isNullOrBlank()) {
                        if (!mobilityStatus.isNullOrBlank()) {
                            append("\n")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Theme.colors.primaryFont)) {
                            append("Notes: ")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = Theme.colors.primaryFont)) {
                            append(mobilityNotes)
                        }
                    }

                    if (mobilityStatus.isNullOrBlank() && mobilityNotes.isNullOrBlank()) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = Theme.colors.hint)) {
                            append("—")
                        }
                    }
                }

                Text(
                    text = annotatedText,
                    style = Theme.typography.body.medium,
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun MobilityCareNotesCardPreview() {
    com.carenest.provider.designsystem.theme.SpTheme {
        MobilityCareNotesCard(
            mobilityStatus = "Assisted",
            mobilityNotes = "Uses a walker for long distances, requires minor assistance when standing from a seated position.",
        )
    }
}
