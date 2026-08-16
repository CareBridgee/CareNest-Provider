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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import coil3.compose.AsyncImage
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.textfield.CustomTextField
import com.carenest.provider.designsystem.components.upload.UploadCard
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.util.noRippleClickable
import com.carenest.provider.profile.presentation.ui.registration.Attachment
import com.carenest.provider.profile.presentation.ui.registration.VerificationDocumentsUiState
import com.carenest.provider.profile.presentation.ui.registration.VerificationDocumentsValidation

@Composable
fun VerificationDocumentsComponent(
    state: VerificationDocumentsUiState,
    onNationalIdFrontClick: () -> Unit,
    onNationalIdBackClick: () -> Unit,
    onNursingLicenseClick: () -> Unit,
    onProfessionalCertificateClick: () -> Unit,
    onRemoveNationalIdFront: () -> Unit,
    onRemoveNationalIdBack: () -> Unit,
    onRemoveNursingLicense: () -> Unit,
    onRemoveProfessionalCertificate: () -> Unit,
    onLicenseNumberChanged: (String) -> Unit,
    onYearsOfExpChanged: (String) -> Unit,
    onPrimarySpecialityChanged: (String) -> Unit,
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
            text = stringResource(com.carenest.provider.profile.R.string.verification_docs_title),
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
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
            title = stringResource(com.carenest.provider.profile.R.string.national_id_front_title),
            description = stringResource(com.carenest.provider.profile.R.string.national_id_front_desc),
            iconPainter = painterResource(id = R.drawable.ic_id_card),
            isUploaded = state.nationalIdFront != null,
            uploadContent = {
                UploadButton(onClick = onNationalIdFrontClick)
            },
            uploadedContent = {
                state.nationalIdFront?.let {
                    UploadedFileItem(
                        attachment = it,
                        onReplace = onNationalIdFrontClick,
                        onRemove = onRemoveNationalIdFront
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        UploadCard(
            title = stringResource(com.carenest.provider.profile.R.string.national_id_back_title),
            description = stringResource(com.carenest.provider.profile.R.string.national_id_back_desc),
            iconPainter = painterResource(id = R.drawable.ic_id_card),
            isUploaded = state.nationalIdBack != null,
            uploadContent = { UploadButton(onClick = onNationalIdBackClick) },
            uploadedContent = {
                state.nationalIdBack?.let {
                    UploadedFileItem(
                        attachment = it,
                        onReplace = onNationalIdBackClick,
                        onRemove = onRemoveNationalIdBack,
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        CustomTextField(
            text = state.licenseNumber,
            onTextChange = onLicenseNumberChanged,
            title = stringResource(com.carenest.provider.profile.R.string.license_number_label),
            hint = stringResource(com.carenest.provider.profile.R.string.license_number_hint),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.licenseNumber.isNotBlank() &&
                !VerificationDocumentsValidation.isValidLicenseNumber(state.licenseNumber),
            errorMessage = if (state.licenseNumber.isNotBlank() &&
                !VerificationDocumentsValidation.isValidLicenseNumber(state.licenseNumber)
            ) {
                stringResource(com.carenest.provider.profile.R.string.error_license_number_invalid)
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
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
                state.nursingLicense?.let {
                    UploadedFileItem(
                        attachment = it,
                        onReplace = onNursingLicenseClick,
                        onRemove = onRemoveNursingLicense
                    )
                }
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
                state.professionalCertificate?.let {
                    UploadedFileItem(
                        attachment = it,
                        onReplace = onProfessionalCertificateClick,
                        onRemove = onRemoveProfessionalCertificate
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        CustomTextField(
            text = state.yearsOfExp?.toString().orEmpty(),
            onTextChange = {
                if (it.all { char -> char.isDigit() }) {
                    onYearsOfExpChanged(it)
                }
            },
            title = stringResource(com.carenest.provider.profile.R.string.years_of_exp_label),
            hint = stringResource(com.carenest.provider.profile.R.string.years_of_exp_hint),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.yearsOfExp?.let {
                !VerificationDocumentsValidation.isValidYearsOfExperience(it)
            } == true,
            errorMessage = state.yearsOfExp?.takeIf {
                !VerificationDocumentsValidation.isValidYearsOfExperience(it)
            }?.let {
                stringResource(com.carenest.provider.profile.R.string.error_years_of_exp_range)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        CustomTextField(
            text = state.primarySpeciality,
            onTextChange = onPrimarySpecialityChanged,
            title = stringResource(com.carenest.provider.profile.R.string.primary_speciality_label),
            hint = stringResource(com.carenest.provider.profile.R.string.primary_speciality_hint),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = state.primarySpeciality.isNotBlank() &&
                !VerificationDocumentsValidation.isValidPrimarySpeciality(state.primarySpeciality),
            errorMessage = if (state.primarySpeciality.isNotBlank() &&
                !VerificationDocumentsValidation.isValidPrimarySpeciality(state.primarySpeciality)
            ) {
                stringResource(com.carenest.provider.profile.R.string.error_primary_speciality_invalid)
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

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
private fun UploadedFileItem(
    attachment: Attachment,
    onReplace: () -> Unit,
    onRemove: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Theme.shapes.medium)
                .background(Theme.colors.surfaceVariant)
                .padding(Theme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (attachment.mimeType.startsWith("image/")) {
                AsyncImage(
                    model = attachment.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(Theme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_document_text),
                    contentDescription = null,
                    tint = Theme.colors.tint,
                    modifier = Modifier.size(Theme.size.iconMedium)
                )
            }
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(Theme.shapes.medium)
                    .border(1.dp, Theme.colors.tint, Theme.shapes.medium)
                    .noRippleClickable { onReplace() }
                    .padding(vertical = Theme.spacing.small),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = stringResource(com.carenest.provider.profile.R.string.replace_action),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.tint,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(Theme.shapes.medium)
                    .border(1.dp, Theme.colors.error, Theme.shapes.medium)
                    .noRippleClickable { onRemove() }
                    .padding(vertical = Theme.spacing.small),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = stringResource(com.carenest.provider.profile.R.string.remove_action),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.error,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerificationDocumentsComponentPreview() {
    SpTheme {
        VerificationDocumentsComponent(
            state = VerificationDocumentsUiState(),
            onNationalIdFrontClick = {},
            onNationalIdBackClick = {},
            onNursingLicenseClick = {},
            onProfessionalCertificateClick = {},
            onRemoveNationalIdFront = {},
            onRemoveNationalIdBack = {},
            onRemoveNursingLicense = {},
            onRemoveProfessionalCertificate = {},
            onLicenseNumberChanged = {},
            onYearsOfExpChanged = {},
            onPrimarySpecialityChanged = {}
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun VerificationDocumentsComponentWithDataPreview() {
    SpTheme {

        VerificationDocumentsComponent(
            state = VerificationDocumentsUiState(
                nationalIdFront = Attachment(
                    uri = Uri.EMPTY,
                    name = "national_id.pdf",
                    mimeType = "application/pdf"
                ),
                nationalIdBack = Attachment(
                    uri = Uri.EMPTY,
                    name = "national_id_back.pdf",
                    mimeType = "application/pdf"
                ),
                licenseNumber = "RN-12345",
                nursingLicense = Attachment(
                    uri = Uri.EMPTY,
                    name = "nursing_license.pdf",
                    mimeType = "application/pdf"
                ),
                yearsOfExp = 5,
                primarySpeciality = "Pediatric Care"
            ),
            onNationalIdFrontClick = {},
            onNationalIdBackClick = {},
            onNursingLicenseClick = {},
            onProfessionalCertificateClick = {},
            onRemoveNationalIdFront = {},
            onRemoveNationalIdBack = {},
            onRemoveNursingLicense = {},
            onRemoveProfessionalCertificate = {},
            onLicenseNumberChanged = {},
            onYearsOfExpChanged = {},
            onPrimarySpecialityChanged = {}
        )

    }
}
