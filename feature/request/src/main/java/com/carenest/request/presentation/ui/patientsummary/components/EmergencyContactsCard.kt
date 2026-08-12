package com.carenest.request.presentation.ui.patientsummary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.carenest.request.domain.model.EmergencyContactItem

@Composable
fun EmergencyContactsCard(
    contacts: List<EmergencyContactItem>,
    onCallClick: (String) -> Unit,
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
                text = stringResource(R.string.patient_summary_emergency_contacts),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            if (contacts.isEmpty()) {
                Text(
                    text = stringResource(R.string.patient_summary_no_contacts),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.hint,
                    ),
                )
            } else {
                Column(
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Theme.spacing.small),
                ) {
                    contacts.forEach { contact ->
                        EmergencyContactRow(
                            contact = contact,
                            onCallClick = onCallClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergencyContactRow(
    contact: EmergencyContactItem,
    onCallClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.infoContainer.copy(alpha = 0.5f))
            .padding(Theme.spacing.medium),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val displayName = if (contact.relationship.isNotBlank()) {
                    "${contact.name} (${contact.relationship})"
                } else {
                    contact.name
                }

                Text(
                    text = displayName.ifBlank { "—" },
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                if (contact.phoneNumber.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))
                    Text(
                        text = contact.phoneNumber,
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.secondaryFont,
                        ),
                    )
                }
            }

            if (contact.phoneNumber.isNotBlank()) {
                Spacer(modifier = Modifier.width(Theme.spacing.small))
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.primary)
                        .clickable { onCallClick(contact.phoneNumber) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(RD.drawable.ic_call),
                        contentDescription = "Call ${contact.name}",
                        tint = Theme.colors.onPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun EmergencyContactsCardPreview() {
    com.carenest.provider.designsystem.theme.SpTheme {
        EmergencyContactsCard(
            contacts = listOf(
                EmergencyContactItem(name = "Sarah Vance", relationship = "Daughter", phoneNumber = "+1 555 123 4567"),
            ),
            onCallClick = {},
        )
    }
}
