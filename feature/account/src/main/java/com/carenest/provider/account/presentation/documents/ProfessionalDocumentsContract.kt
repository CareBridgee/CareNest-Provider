package com.carenest.provider.account.presentation.documents

import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.model.DocumentStatus
import com.carenest.provider.account.presentation.model.ProfessionalDocumentUiModel
import com.carenest.provider.designsystem.R as DesignSystemR

data class ProfessionalDocumentsUiState(
    val isLoading: Boolean = false,
    val documents: List<ProfessionalDocumentUiModel> = sampleDocuments,
)

sealed interface ProfessionalDocumentsIntent {
    data object BackClicked : ProfessionalDocumentsIntent
    data class DocumentClicked(val id: String) : ProfessionalDocumentsIntent
    data object UploadDocumentClicked : ProfessionalDocumentsIntent
}

sealed interface ProfessionalDocumentsEffect {
    data object NavigateBack : ProfessionalDocumentsEffect
    data class OpenDocument(val id: String) : ProfessionalDocumentsEffect
    data object UploadDocument : ProfessionalDocumentsEffect
}

private val sampleDocuments = listOf(
    ProfessionalDocumentUiModel(
        id = "national-id",
        titleRes = R.string.documents_national_id,
        uploadedDateRes = R.string.documents_uploaded_oct_12,
        iconRes = DesignSystemR.drawable.ic_id_card,
        status = DocumentStatus.Verified,
    ),
    ProfessionalDocumentUiModel(
        id = "nursing-license",
        titleRes = R.string.documents_nursing_license,
        uploadedDateRes = R.string.documents_uploaded_oct_14,
        iconRes = DesignSystemR.drawable.ic_document_text,
        status = DocumentStatus.Pending,
    ),
    ProfessionalDocumentUiModel(
        id = "acls",
        titleRes = R.string.documents_acls,
        uploadedDateRes = R.string.documents_uploaded_sep_28,
        iconRes = DesignSystemR.drawable.ic_badge,
        status = DocumentStatus.Verified,
    ),
)
