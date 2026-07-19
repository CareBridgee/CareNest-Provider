package com.carenest.provider.profile.presentation.ui.registration.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.components.button.ButtonIconPosition
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.presentation.ui.registration.RegistrationUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApplicationReviewComponent(
    state: RegistrationUiState,
    onEditPersonalInfo: () -> Unit,
    onEditProfessionalInfo: () -> Unit,
    onEditServices: () -> Unit,
    onEditDocuments: () -> Unit,
    onCertificationToggle: (Boolean) -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .verticalScroll(scrollState)
            .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicText(
            text = "Review Your Application",
            style = Theme.typography.displayMedium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.small))

        BasicText(
            text = "Please take a moment to ensure all details are correct. You can edit any section before final submission.",
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        // Personal Info Card
        SummaryCard(
            title = "Personal Info",
            icon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_profile),
            onEditClick = onEditPersonalInfo
        ) {
            InfoRow(label = "Full Name", value = "${state.personalInfoState.firstName} ${state.personalInfoState.lastName}".ifEmpty { "Sarah J. Cunningham" })
            InfoRow(label = "Email Address", value = state.personalInfoState.email.ifEmpty { "sarah.c@careconnect.com" })
            InfoRow(label = "Phone Number", value = state.personalInfoState.phoneNumber.ifEmpty { "+1 (555) 012-3456" })
            InfoRow(label = "Location", value = state.personalInfoState.location.ifEmpty { "Chicago, IL" })
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        // Professional Info Card
        SummaryCard(
            title = "Professional Info",
            icon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_work),
            onEditClick = onEditProfessionalInfo
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Theme.shapes.medium)
                    .background(Theme.colors.surfaceVariant)
                    .padding(Theme.spacing.medium)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                    Icon(
                        painter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_id_card),
                        contentDescription = null,
                        tint = Theme.colors.tint,
                        modifier = Modifier.size(Theme.size.iconMedium)
                    )
                    Column {
                        BasicText(
                            text = "License Number",
                            style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
                        )
                        BasicText(
                            text = state.verificationDocumentsUiState.nursingLicense?.name ?: "RN-992834-IL",
                            style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            InfoRow(label = "Experience", value = "${if (state.verificationDocumentsUiState.yearsOfExp == 0) 8 else state.verificationDocumentsUiState.yearsOfExp} Years in Critical Care & Home Healthcare")
            InfoRow(label = "Education", value = state.applicationReviewUiState.education.ifEmpty { "B.S. in Nursing, University of Illinois" })
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        // Selected Services Card
        SummaryCard(
            title = "Selected Services",
            icon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_services),
            onEditClick = onEditServices
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
            ) {
                if (state.servicesUiState.selectedServices.isEmpty()) {
                    listOf("Wound Care", "IV Therapy", "Elderly Care", "Post-Op Recovery", "Medication Management").forEach {
                        ServiceTag(it)
                    }
                } else {
                    state.servicesUiState.selectedServices.forEach { service ->
                        ServiceTag(service.title)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        // Documents Card
        SummaryCard(
            title = "Documents",
            icon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_document_text),
            onEditClick = onEditDocuments
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                DocumentItem(name = state.verificationDocumentsUiState.nursingLicense?.name ?: "Nursing_License.pdf")
                DocumentItem(name = state.verificationDocumentsUiState.nationalId?.name ?: "Background_Check_Consent.pdf")
                DocumentItem(name = "Government_ID_Front.jpg")
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        // Privacy Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Theme.shapes.medium)
                .background(Theme.colors.surfaceVariant)
                .padding(Theme.spacing.medium)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                Icon(
                    painter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_info),
                    contentDescription = null,
                    tint = Theme.colors.tint,
                    modifier = Modifier.size(Theme.size.iconSmall)
                )
                BasicText(
                    text = "Your privacy is our priority. All information is encrypted and will only be used for our verification process. Approval typically takes 24-48 business hours.",
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
        ) {
            Checkbox(
                checked = state.applicationReviewUiState.isCertified,
                onCheckedChange = onCertificationToggle,
                colors = CheckboxDefaults.colors(
                    checkedColor = Theme.colors.primary,
                    uncheckedColor = Theme.colors.hint
                )
            )
            BasicText(
                text = "I certify that the above information is accurate and true.",
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        PrimaryButton(
            caption = "Submit Application",
            onClick = onSubmitClick,
            modifier = Modifier.fillMaxWidth(),
            iconPainter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_chevron_right),
            iconPosition = ButtonIconPosition.End,
            isDisabled = !state.applicationReviewUiState.isCertified
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
        )
        BasicText(
            text = value,
            style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont, fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun ServiceTag(service: String) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Theme.colors.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        BasicText(
            text = service,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun DocumentItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surfaceVariant)
            .padding(Theme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_document_text),
            contentDescription = null,
            tint = Theme.colors.tint,
            modifier = Modifier.size(Theme.size.iconMedium)
        )
        BasicText(
            text = name,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Theme.colors.success, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApplicationReviewComponentPreview() {
    SpTheme {
        ApplicationReviewComponent(
            state = RegistrationUiState(),
            onEditPersonalInfo = {},
            onEditProfessionalInfo = {},
            onEditServices = {},
            onEditDocuments = {},
            onCertificationToggle = {},
            onSubmitClick = {}
        )
    }
}
