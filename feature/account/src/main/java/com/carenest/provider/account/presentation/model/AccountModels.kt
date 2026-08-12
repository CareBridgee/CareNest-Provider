package com.carenest.provider.account.presentation.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MenuItemUiModel(
    val id: MenuItemId,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int,
    @DrawableRes val iconRes: Int,
    val showVerifiedDot: Boolean = false,
)

enum class MenuItemId {
    ProfessionalInfo,
    Documents,
    Availability,
    Reviews,
    Earnings,
    Payouts,
    Wallet,
    Support,
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
    @StringRes val titleRes: Int,
    @StringRes val supportingTextRes: Int,
    @DrawableRes val iconRes: Int,
    val status: DocumentStatus,
    val files: List<ProfessionalDocumentFileUiModel>,
)

data class ProfessionalDocumentFileUiModel(
    val target: DocumentUploadTarget,
    @StringRes val labelRes: Int,
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
