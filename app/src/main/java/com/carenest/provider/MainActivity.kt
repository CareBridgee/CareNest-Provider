package com.carenest.provider

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import com.carenest.provider.core.datastore.AppPreferences
import com.carenest.provider.core.datastore.AppPreferencesState
import com.carenest.provider.core.datastore.AppThemeMode
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.network.socket.service.ActiveReservationService
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

    private var targetRequestId = mutableStateOf<String?>(null)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val reqId = intent?.getStringExtra(ActiveReservationService.EXTRA_SERVICE_REQUEST_ID)
        if (!reqId.isNullOrBlank()) {
            targetRequestId.value = reqId
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
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
