package com.carenest.request.presentation.ui.patientsummary.components

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
import com.carenest.request.domain.model.PatientMedicalSummary
import java.time.LocalDate
import java.time.Period

@Composable
fun PersonalInformationCard(
    patient: PatientMedicalSummary,
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
                text = stringResource(R.string.patient_summary_personal_info),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = Theme.typography.body.small.letterSpacing,
                ),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            Column(
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                ) {
                    PersonalInfoGridItem(
                        iconRes = RD.drawable.ic_calendar,
                        label = stringResource(R.string.patient_summary_age),
                        value = patient.dateOfBirth?.let { parseAgeString(it) } ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                    PersonalInfoGridItem(
                        iconRes = RD.drawable.ic_person,
                        label = stringResource(R.string.patient_summary_gender),
                        value = patient.gender ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                ) {
                    PersonalInfoGridItem(
                        iconRes = RD.drawable.ic_heart_beat,
                        label = stringResource(R.string.patient_summary_blood_type),
                        value = patient.bloodType ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                    PersonalInfoGridItem(
                        iconRes = RD.drawable.ic_badge,
                        label = stringResource(R.string.patient_summary_height),
                        value = patient.height?.let {
                            stringResource(R.string.patient_summary_height_unit, formatDecimal(it))
                        } ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                ) {
                    PersonalInfoGridItem(
                        iconRes = RD.drawable.ic_clock,
                        label = stringResource(R.string.patient_summary_weight),
                        value = patient.weight?.let {
                            stringResource(R.string.patient_summary_weight_unit, formatDecimal(it))
                        } ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                    PersonalInfoGridItem(
                        iconRes = RD.drawable.ic_elderly,
                        label = stringResource(R.string.patient_summary_mobility),
                        value = patient.mobilityStatus ?: "—",
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoGridItem(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
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
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = Theme.colors.primary,
                modifier = Modifier.size(18.dp),
            )
        }

        Spacer(modifier = Modifier.width(Theme.spacing.small))

        Column {
            Text(
                text = label,
                style = Theme.typography.hint.medium.copy(
                    color = Theme.colors.secondaryFont,
                ),
            )
            Text(
                text = value,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

private fun formatDecimal(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        "%.1f".format(value)
    }
}

private fun parseAgeString(dob: String): String {
    return runCatching {
        val birthDate = LocalDate.parse(dob.substringBefore('T'))
        Period.between(birthDate, LocalDate.now()).years.coerceAtLeast(0).toString()
    }.getOrDefault(dob)
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PersonalInformationCardPreview() {
    com.carenest.provider.designsystem.theme.SpTheme {
        PersonalInformationCard(
            patient = PatientMedicalSummary(
                dateOfBirth = "1952-05-15",
                gender = "Female",
                bloodType = "O+",
                height = 165.0,
                weight = 68.0,
                mobilityStatus = "Assisted",
            ),
        )
    }
}
