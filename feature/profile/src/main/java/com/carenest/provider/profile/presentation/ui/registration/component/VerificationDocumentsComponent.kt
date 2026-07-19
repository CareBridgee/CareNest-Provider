package com.carenest.provider.profile.presentation.ui.registration.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.button.ButtonIconPosition
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.textfield.CustomTextField
import com.carenest.provider.designsystem.components.upload.UploadCard
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.util.noRippleClickable
import com.carenest.provider.profile.presentation.ui.registration.Attachment
import com.carenest.provider.profile.presentation.ui.registration.VerificationDocumentsUiState

@Composable
fun VerificationDocumentsComponent(
    state: VerificationDocumentsUiState,
    onNationalIdClick: () -> Unit,
    onNursingLicenseClick: () -> Unit,
    onProfessionalCertificateClick: () -> Unit,
    onYearsOfExpChanged: (String) -> Unit,
    onPrimarySpecialityChanged: (String) -> Unit,
    onContinueClick: () -> Unit,
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
            text = stringResource(com.carenest.provider.profile.R.string.verification_docs_title),
            style = Theme.typography.displayMedium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.small))

        BasicText(
            text = stringResource(com.carenest.provider.profile.R.string.verification_docs_subtitle),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        UploadCard(
            title = stringResource(com.carenest.provider.profile.R.string.national_id_card_title),
            description = stringResource(com.carenest.provider.profile.R.string.national_id_card_desc),
            iconPainter = painterResource(id = R.drawable.ic_id_card),
            isUploaded = state.nationalId != null,
            uploadContent = {
                UploadButton(onClick = onNationalIdClick)
            },
            uploadedContent = {
                state.nationalId?.let { UploadedFileItem(attachment = it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        UploadCard(
            title = stringResource(com.carenest.provider.profile.R.string.nursing_license_title),
            description = stringResource(com.carenest.provider.profile.R.string.nursing_license_desc),
            iconPainter = painterResource(id = R.drawable.ic_assignment),
            isUploaded = state.nursingLicense != null,
            uploadContent = {
                UploadButton(onClick = onNursingLicenseClick)
            },
            uploadedContent = {
                state.nursingLicense?.let { UploadedFileItem(attachment = it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        UploadCard(
            title = stringResource(com.carenest.provider.profile.R.string.professional_cert_title),
            description = stringResource(com.carenest.provider.profile.R.string.professional_cert_desc),
            iconPainter = painterResource(id = R.drawable.ic_badge),
            isUploaded = state.professionalCertificate != null,
            uploadContent = {
                UploadButton(onClick = onProfessionalCertificateClick)
            },
            uploadedContent = {
                state.professionalCertificate?.let { UploadedFileItem(attachment = it) }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        CustomTextField(
            text = if (state.yearsOfExp == 0) "" else state.yearsOfExp.toString(),
            onTextChange = onYearsOfExpChanged,
            title = stringResource(com.carenest.provider.profile.R.string.years_of_exp_label),
            hint = stringResource(com.carenest.provider.profile.R.string.years_of_exp_hint),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        CustomTextField(
            text = state.primarySpeciality,
            onTextChange = onPrimarySpecialityChanged,
            title = stringResource(com.carenest.provider.profile.R.string.primary_speciality_label),
            hint = stringResource(com.carenest.provider.profile.R.string.primary_speciality_hint),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        PrimaryButton(
            caption = stringResource(com.carenest.provider.profile.R.string.next),
            onClick = onContinueClick,
            modifier = Modifier.fillMaxWidth(),
            iconPainter = painterResource(id = R.drawable.ic_chevron_right),
            iconPosition = ButtonIconPosition.End
        )

        Spacer(modifier = Modifier.height(Theme.spacing.large))

        BasicText(
            text = stringResource(
                com.carenest.provider.profile.R.string.step_indicator,
                2,
                3,
                stringResource(com.carenest.provider.profile.R.string.verification_docs_title)
            ),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun UploadButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .border(1.dp, Theme.colors.tint, Theme.shapes.medium)
            .noRippleClickable { onClick() }
            .padding(vertical = Theme.spacing.small),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                text = "+ " + stringResource(com.carenest.provider.profile.R.string.upload_document),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.tint,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
private fun UploadedFileItem(attachment: Attachment) {
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
            painter = painterResource(id = R.drawable.ic_document_text),
            contentDescription = null,
            tint = Theme.colors.tint,
            modifier = Modifier.size(Theme.size.iconMedium)
        )
        BasicText(
            text = attachment.name,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(Theme.size.iconMedium)
                .clip(CircleShape)
                .background(Theme.colors.success, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = Theme.colors.surface,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationDocumentsComponentPreview() {
    SpTheme {
        VerificationDocumentsComponent(
            state = VerificationDocumentsUiState(),
            onNationalIdClick = {},
            onNursingLicenseClick = {},
            onProfessionalCertificateClick = {},
            onYearsOfExpChanged = {},
            onPrimarySpecialityChanged = {},
            onContinueClick = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun VerificationDocumentsComponentWithDataPreview() {
    SpTheme {

        VerificationDocumentsComponent(
            state = VerificationDocumentsUiState(
                nationalId = Attachment(
                    uri = Uri.EMPTY,
                    name = "national_id.pdf",
                    mimeType = "application/pdf",
                    size = 1024
                ),
                nursingLicense = Attachment(
                    uri = Uri.EMPTY,
                    name = "nursing_license.pdf",
                    mimeType = "application/pdf",
                    size = 2048
                ),
                yearsOfExp = 5,
                primarySpeciality = "Pediatric Care"
            ),
            onNationalIdClick = {},
            onNursingLicenseClick = {},
            onProfessionalCertificateClick = {},
            onYearsOfExpChanged = {},
            onPrimarySpecialityChanged = {},
            onContinueClick = {}
        )

    }
}
