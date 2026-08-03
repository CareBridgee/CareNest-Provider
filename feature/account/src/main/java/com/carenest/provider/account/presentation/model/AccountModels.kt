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
}

data class ProfessionalDocumentUiModel(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val uploadedDateRes: Int,
    @DrawableRes val iconRes: Int,
    val status: DocumentStatus,
)

enum class ReviewFilter {
    MostRecent,
    TopRated,
    Critical,
    WithPhotos,
}

data class ReviewUiModel(
    val id: String,
    @StringRes val authorRes: Int,
    @StringRes val dateRes: Int,
    @StringRes val bodyRes: Int,
    @StringRes val serviceRes: Int,
    val initials: String,
    val rating: Int,
)
