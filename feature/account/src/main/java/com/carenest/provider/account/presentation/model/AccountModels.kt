package com.carenest.provider.account.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MenuItemUiModel(
    val id: MenuItemId,
    @param:StringRes val titleRes: Int,
    @param:StringRes val subtitleRes: Int,
    @param:DrawableRes val iconRes: Int,
    val showVerifiedDot: Boolean = false,
    val subtitle: String? = null,
)

enum class MenuItemId {
    ProfessionalInfo,
    Documents,
    Settings,
    Reviews,
    Earnings
}

enum class DocumentStatus {
    Verified,
    Pending,
    Rejected,
}

enum class DocumentUploadTarget {
    NationalIdFront,
    NationalIdBack,
    NursingLicense,
    ProfessionalCertificate,
}

data class ProfessionalDocumentUiModel(
    val id: String,
    @param:StringRes val titleRes: Int,
    @param:StringRes val supportingTextRes: Int,
    @param:DrawableRes val iconRes: Int,
    val status: DocumentStatus,
    val files: List<ProfessionalDocumentFileUiModel>,
)

data class ProfessionalDocumentFileUiModel(
    val target: DocumentUploadTarget,
    @param:StringRes val labelRes: Int,
    val url: String?,
    val isUploading: Boolean = false,
)

enum class ReviewFilter {
    MostRecent,
    Critical,
}

data class ReviewUiModel(
    val id: String,
    val authorName: String,
    val dateText: String,
    val bodyText: String,
    val serviceName: String? = null,
    val initials: String,
    val rating: Int,
)
