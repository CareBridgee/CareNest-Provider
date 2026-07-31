package com.carenest.provider.account.presentation.settings

import com.carenest.provider.core.datastore.AppThemeMode

data class SettingsUiState(
    val isLoading: Boolean = true,
    val themeMode: AppThemeMode = AppThemeMode.System,
    val isDarkModeEnabled: Boolean = false,
    val languageCode: String = "en",
)

sealed interface SettingsIntent {
    data object BackClicked : SettingsIntent
    data object LanguageClicked : SettingsIntent
    data class LanguageSelected(val languageCode: String) : SettingsIntent
    data class DarkModeChanged(val enabled: Boolean) : SettingsIntent
    data class ResolvedThemeChanged(val isDark: Boolean) : SettingsIntent
    data object PrivacyPolicyClicked : SettingsIntent
}

sealed interface SettingsEffect {
    data object NavigateBack : SettingsEffect
    data object OpenLanguage : SettingsEffect
    data object OpenPrivacyPolicy : SettingsEffect
}
