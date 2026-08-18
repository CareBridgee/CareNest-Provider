package com.carenest.provider.account.presentation.profilemenu

import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.model.MenuItemId
import com.carenest.provider.account.presentation.model.MenuItemUiModel
import com.carenest.provider.designsystem.R as DesignSystemR

data class ProfileMenuUiState(
    val isLoading: Boolean = false,
    val fullName: String = "",
    val avatarUrl: String? = null,
    val specialty: String = "",
    val rating: String = "0",
    val reviewCount: Int = 0,
    val menuItems: List<MenuItemUiModel> = sampleProfileMenuItems,
)

sealed interface ProfileMenuIntent {
    data object ProfileCardClicked : ProfileMenuIntent
    data class MenuItemClicked(val id: MenuItemId) : ProfileMenuIntent
    data object SettingsClicked : ProfileMenuIntent
    data object EarningsClicked : ProfileMenuIntent
    data object PayoutsClicked : ProfileMenuIntent
    data object WalletClicked : ProfileMenuIntent
    data object LogoutClicked : ProfileMenuIntent
    data object RefreshProfile : ProfileMenuIntent
}

sealed interface ProfileMenuEffect {
    data object OpenPublicProfile : ProfileMenuEffect
    data object OpenDocuments : ProfileMenuEffect
    data object OpenRatingsAndReviews : ProfileMenuEffect
    data object OpenEarnings : ProfileMenuEffect
    data object OpenPayouts : ProfileMenuEffect
    data object OpenWallet : ProfileMenuEffect
    data object OpenSupport : ProfileMenuEffect
    data object OpenSettings : ProfileMenuEffect
    data object Logout : ProfileMenuEffect
}

private val sampleProfileMenuItems = listOf(
    MenuItemUiModel(
        MenuItemId.ProfessionalInfo,
        R.string.profile_menu_professional_info,
        R.string.profile_menu_professional_info_subtitle,
        DesignSystemR.drawable.ic_account_professional_info,
    ),
    MenuItemUiModel(
        MenuItemId.Documents,
        R.string.profile_menu_documents,
        R.string.profile_menu_documents_subtitle,
        DesignSystemR.drawable.ic_document_text,
        showVerifiedDot = true,
    ),
    MenuItemUiModel(
        MenuItemId.Settings,
        R.string.profile_menu_app_settings,
        R.string.profile_menu_app_settings_subtitle,
        DesignSystemR.drawable.ic_account_settings,
    ),
    MenuItemUiModel(
        MenuItemId.Reviews,
        R.string.profile_menu_reviews,
        R.string.profile_menu_reviews_subtitle,
        DesignSystemR.drawable.ic_account_reviews,
    ),
    MenuItemUiModel(
        MenuItemId.Earnings,
        R.string.profile_menu_earnings,
        R.string.profile_menu_earnings_subtitle,
        DesignSystemR.drawable.ic_wallet,
    ),
)
