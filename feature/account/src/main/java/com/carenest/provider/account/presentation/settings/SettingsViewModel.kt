package com.carenest.provider.account.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.datastore.AppPreferences
import com.carenest.provider.core.datastore.AppThemeMode
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
) : ViewModel(),
    StateHolder<SettingsUiState> by DefaultStateHolder(SettingsUiState()),
    EffectPublisher<SettingsEffect> by DefaultEffectPublisher() {

    private var resolvedSystemDark = false

    init {
        viewModelScope.launch {
            appPreferences.state.collect { preferences ->
                updateState {
                    copy(
                        isLoading = false,
                        themeMode = preferences.themeMode,
                        isDarkModeEnabled = when (preferences.themeMode) {
                            AppThemeMode.System -> resolvedSystemDark
                            AppThemeMode.Light -> false
                            AppThemeMode.Dark -> true
                        },
                        languageCode = preferences.languageCode,
                    )
                }
            }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.BackClicked -> sendEffect(SettingsEffect.NavigateBack)
            SettingsIntent.LanguageClicked -> sendEffect(SettingsEffect.OpenLanguage)
            is SettingsIntent.LanguageSelected -> {
                updateState { copy(languageCode = intent.languageCode) }
                viewModelScope.launch {
                    appPreferences.setLanguageCode(intent.languageCode)
                }
            }
            is SettingsIntent.DarkModeChanged -> {
                val mode = if (intent.enabled) AppThemeMode.Dark else AppThemeMode.Light
                updateState { copy(themeMode = mode, isDarkModeEnabled = intent.enabled) }
                viewModelScope.launch {
                    appPreferences.setThemeMode(mode)
                }
            }
            is SettingsIntent.ResolvedThemeChanged -> {
                resolvedSystemDark = intent.isDark
                if (currentState.themeMode == AppThemeMode.System) {
                    updateState { copy(isDarkModeEnabled = intent.isDark) }
                }
            }
            SettingsIntent.PrivacyPolicyClicked -> sendEffect(SettingsEffect.OpenPrivacyPolicy)
        }
    }
}
