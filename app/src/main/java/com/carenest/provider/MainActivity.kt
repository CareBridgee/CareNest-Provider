package com.carenest.provider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpTheme(languageCode = Locale.current.language) {
                CareNestApp()
            }
        }
    }
}

@Composable
fun CareNestApp() {
    AppNavigation()
}
