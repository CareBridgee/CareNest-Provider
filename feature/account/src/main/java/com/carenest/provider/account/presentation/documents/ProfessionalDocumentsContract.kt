package com.carenest.provider.account.presentation.documents

import android.net.Uri
import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.model.DocumentUploadTarget
import com.carenest.provider.account.presentation.model.DocumentStatus
import com.carenest.provider.account.presentation.model.ProfessionalDocumentFileUiModel
import com.carenest.provider.account.presentation.model.ProfessionalDocumentUiModel
import com.carenest.provider.designsystem.components.toast.ToastType
import com.carenest.provider.profile.domain.model.NurseProfile
import com.carenest.provider.profile.domain.model.VerificationStatus
import com.carenest.provider.designsystem.R as DesignSystemR

data class ProfessionalDocumentsUiState(
    val isLoading: Boolean = false,
    val documents: List<ProfessionalDocumentUiModel> = emptyList(),
    val errorMessage: String? = null,
    val uploadingTarget: DocumentUploadTarget? = null,
)

data class PickedDocumentFile(
    val target: DocumentUploadTarget,
    val uri: Uri,
    val fileName: String,
    val mimeType: String,
)

sealed interface ProfessionalDocumentsIntent {
    data object BackClicked : ProfessionalDocumentsIntent
    data object RetryClicked : ProfessionalDocumentsIntent
    data class ViewDocumentClicked(val target: DocumentUploadTarget) : ProfessionalDocumentsIntent
    data class ReplaceDocumentClicked(val target: DocumentUploadTarget) : ProfessionalDocumentsIntent
    data class DocumentFilePicked(val file: PickedDocumentFile) : ProfessionalDocumentsIntent
}

sealed interface ProfessionalDocumentsEffect {
    data object NavigateBack : ProfessionalDocumentsEffect
    data class OpenDocument(val url: String) : ProfessionalDocumentsEffect
    data class OpenDocumentPicker(val target: DocumentUploadTarget) : ProfessionalDocumentsEffect
    data class ShowMessage(
        val message: String,
        val type: ToastType = ToastType.Error,
    ) : ProfessionalDocumentsEffect
}

internal fun NurseProfile.toProfessionalDocuments(
    uploadingTarget: DocumentUploadTarget? = null,
): List<ProfessionalDocumentUiModel> {
    val status = verificationStatus.toDocumentStatus()
    return listOf(
    ProfessionalDocumentUiModel(
        id = "national-id",
        titleRes = R.string.documents_national_id,
            supportingTextRes = documentSupportingText(
                nationalIdFrontUrl,
                nationalIdBackUrl,
            ),
        iconRes = DesignSystemR.drawable.ic_id_card,
            status = status,
            files = listOf(
                ProfessionalDocumentFileUiModel(
                    target = DocumentUploadTarget.NationalIdFront,
                    labelRes = R.string.documents_national_id_front,
                    url = nationalIdFrontUrl,
                    isUploading = uploadingTarget == DocumentUploadTarget.NationalIdFront,
                ),
                ProfessionalDocumentFileUiModel(
                    target = DocumentUploadTarget.NationalIdBack,
                    labelRes = R.string.documents_national_id_back,
                    url = nationalIdBackUrl,
                    isUploading = uploadingTarget == DocumentUploadTarget.NationalIdBack,
                ),
            ),
    ),
    ProfessionalDocumentUiModel(
        id = "nursing-license",
        titleRes = R.string.documents_nursing_license,
            supportingTextRes = documentSupportingText(licenseImageUrl),
        iconRes = DesignSystemR.drawable.ic_document_text,
            status = status,
            files = listOf(
                ProfessionalDocumentFileUiModel(
                    target = DocumentUploadTarget.NursingLicense,
                    labelRes = R.string.documents_nursing_license,
                    url = licenseImageUrl,
                    isUploading = uploadingTarget == DocumentUploadTarget.NursingLicense,
                ),
            ),
    ),
    ProfessionalDocumentUiModel(
            id = "professional-certificate",
            titleRes = R.string.documents_professional_certificate,
            supportingTextRes = documentSupportingText(professionalCertificateUrl),
        iconRes = DesignSystemR.drawable.ic_account_acls_certificate,
            status = status,
            files = listOf(
                ProfessionalDocumentFileUiModel(
                    target = DocumentUploadTarget.ProfessionalCertificate,
                    labelRes = R.string.documents_professional_certificate,
                    url = professionalCertificateUrl,
                    isUploading = uploadingTarget == DocumentUploadTarget.ProfessionalCertificate,
                ),
            ),
    ),
)
}

private fun VerificationStatus.toDocumentStatus(): DocumentStatus = when (this) {
    VerificationStatus.APPROVED -> DocumentStatus.Verified
    VerificationStatus.UNDER_REVIEW -> DocumentStatus.Pending
    VerificationStatus.REJECTED -> DocumentStatus.Rejected
}

private fun documentSupportingText(vararg urls: String?): Int = when {
    urls.all { it.isNullOrBlank() } -> R.string.documents_not_uploaded
    urls.any { it.isNullOrBlank() } -> R.string.documents_partially_uploaded
    else -> R.string.documents_uploaded
}
