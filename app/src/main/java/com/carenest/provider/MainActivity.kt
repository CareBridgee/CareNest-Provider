package com.carenest.provider

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import com.carenest.provider.core.datastore.AppPreferences
import com.carenest.provider.core.datastore.AppPreferencesState
import com.carenest.provider.core.datastore.AppThemeMode
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var authenticationSessionStore: AuthenticationSessionStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences by appPreferences.state.collectAsState(
                initial = AppPreferencesState(),
            )
            val systemDarkTheme = isSystemInDarkTheme()
            val isDarkTheme = when (preferences.themeMode) {
                AppThemeMode.System -> systemDarkTheme
                AppThemeMode.Light -> false
                AppThemeMode.Dark -> true
            }

            LaunchedEffect(preferences.languageCode) {
                val requestedLocales = LocaleListCompat.forLanguageTags(
                    preferences.languageCode,
                )
                if (
                    AppCompatDelegate.getApplicationLocales().toLanguageTags() !=
                    requestedLocales.toLanguageTags()
                ) {
                    AppCompatDelegate.setApplicationLocales(requestedLocales)
                }
            }

            SpTheme(
                isDarkTheme = isDarkTheme,
                languageCode = preferences.languageCode,
            ) {
                CareNestApp(
                    authenticationSessionStore = authenticationSessionStore,
                    onExitApp = { finish() },
                )
            }
        }
    }
}

@Composable
fun CareNestApp(
    authenticationSessionStore: AuthenticationSessionStore,
    onExitApp: () -> Unit,
) {
    AppNavigation(
        authenticationState = authenticationSessionStore.state,
        onExitApp = onExitApp,
        modifier = Modifier.fillMaxSize(),
    )
}
