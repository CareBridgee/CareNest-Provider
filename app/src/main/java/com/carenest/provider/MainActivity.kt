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
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import com.carenest.provider.core.network.socket.service.ActiveReservationService

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var appPreferences: AppPreferences

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
                    initialRequestId = targetRequestId.value,
                    onExitApp = { finish() }
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
    initialRequestId: String? = null,
    onExitApp: () -> Unit
) {
    AppNavigation(
        initialRequestId = initialRequestId,
        onExitApp = onExitApp,
        modifier = Modifier.fillMaxSize(),
    )
}
