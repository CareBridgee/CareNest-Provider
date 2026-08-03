package com.carenest.provider.account.presentation.profilemenu

import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.model.MenuItemId
import com.carenest.provider.account.presentation.model.MenuItemUiModel
import com.carenest.provider.designsystem.R as DesignSystemR

data class ProfileMenuUiState(
    val isLoading: Boolean = false,
    val menuItems: List<MenuItemUiModel> = sampleProfileMenuItems,
)

sealed interface ProfileMenuIntent {
    data object ProfileCardClicked : ProfileMenuIntent
    data class MenuItemClicked(val id: MenuItemId) : ProfileMenuIntent
    data object SettingsClicked : ProfileMenuIntent
    data object AvailabilitySettingsClicked : ProfileMenuIntent
    data object EarningsClicked : ProfileMenuIntent
    data object PayoutsClicked : ProfileMenuIntent
    data object LogoutClicked : ProfileMenuIntent
}

sealed interface ProfileMenuEffect {
    data object OpenPublicProfile : ProfileMenuEffect
    data object OpenDocuments : ProfileMenuEffect
    data object OpenRatingsAndReviews : ProfileMenuEffect
    data object OpenEarnings : ProfileMenuEffect
    data object OpenPayouts : ProfileMenuEffect
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
        MenuItemId.Availability,
        R.string.profile_menu_availability,
        R.string.profile_menu_availability_subtitle,
        DesignSystemR.drawable.ic_account_availability,
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
    MenuItemUiModel(
        MenuItemId.Payouts,
        R.string.profile_menu_payouts,
        R.string.profile_menu_payouts_subtitle,
        DesignSystemR.drawable.ic_document_text,
    ),
    MenuItemUiModel(
        MenuItemId.Support,
        R.string.profile_menu_support,
        R.string.profile_menu_support_subtitle,
        DesignSystemR.drawable.ic_account_support_chat,
    ),
)
