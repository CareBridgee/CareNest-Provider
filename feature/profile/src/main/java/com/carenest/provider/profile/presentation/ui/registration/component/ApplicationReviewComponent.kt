package com.carenest.provider.profile.presentation.ui.registration.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .verticalScroll(scrollState)
            .padding(horizontal = Theme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicText(
            text = stringResource(com.carenest.provider.profile.R.string.review_title),
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.small))

        BasicText(
            text = stringResource(com.carenest.provider.profile.R.string.review_subtitle),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        // Personal Info Card
        SummaryCard(
            title = stringResource(com.carenest.provider.profile.R.string.personal_info_summary),
            icon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_profile),
            onEditClick = onEditPersonalInfo
        ) {
            InfoRow(label = stringResource(com.carenest.provider.profile.R.string.full_name_label), value = "${state.personalInfoState.firstName} ${state.personalInfoState.lastName}")
            if (state.personalInfoState.phoneNumber.isNotBlank()) {
                InfoRow(
                    label = stringResource(com.carenest.provider.profile.R.string.phone_number_label),
                    value = state.personalInfoState.phoneNumber,
                    forceLeftToRight = true,
                )
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        // Professional Info Card
        SummaryCard(
            title = stringResource(com.carenest.provider.profile.R.string.professional_info_summary),
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
                            text = stringResource(com.carenest.provider.profile.R.string.license_label),
                            style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
                        )
                        BasicText(
                            text = state.verificationDocumentsUiState.licenseNumber,
                            style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Theme.spacing.medium))

            InfoRow(
                label = stringResource(com.carenest.provider.profile.R.string.experience_label),
                value = stringResource(
                    com.carenest.provider.profile.R.string.experience_value_format,
                    state.verificationDocumentsUiState.yearsOfExp,
                    state.verificationDocumentsUiState.primarySpeciality
                )
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        // Selected Services Card
        SummaryCard(
            title = stringResource(com.carenest.provider.profile.R.string.selected_services_summary),
            icon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_services),
            onEditClick = onEditServices
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
            ) {
                state.servicesUiState.selectedServices.forEach { service ->
                    ServiceTag(service.title)
                }
            }
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        // Documents Card
        SummaryCard(
            title = stringResource(com.carenest.provider.profile.R.string.documents_summary),
            icon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_document_text),
            onEditClick = onEditDocuments
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                state.verificationDocumentsUiState.nursingLicense?.let { DocumentItem(name = it.name) }
                state.verificationDocumentsUiState.nationalIdFront?.let { DocumentItem(name = it.name) }
                state.verificationDocumentsUiState.nationalIdBack?.let { DocumentItem(name = it.name) }
                state.verificationDocumentsUiState.professionalCertificate?.let { DocumentItem(name = it.name) }
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
                    text = stringResource(com.carenest.provider.profile.R.string.privacy_info),
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
                text = stringResource(com.carenest.provider.profile.R.string.certify_accuracy),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    forceLeftToRight: Boolean = false,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
        )
        val valueStyle = Theme.typography.body.medium.copy(
            color = Theme.colors.primaryFont,
            fontWeight = FontWeight.SemiBold,
        )
        BasicText(
            text = value,
            style = if (forceLeftToRight) {
                valueStyle.copy(textDirection = TextDirection.Ltr)
            } else {
                valueStyle
            },
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
            onCertificationToggle = {}
        )
    }
}
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun ApplicationReviewComponentDarkPreview() {
    SpTheme {
        ApplicationReviewComponent(
            state = RegistrationUiState(),
            onEditPersonalInfo = {},
            onEditProfessionalInfo = {},
            onEditServices = {},
            onEditDocuments = {},
            onCertificationToggle = {}
        )
    }
}
