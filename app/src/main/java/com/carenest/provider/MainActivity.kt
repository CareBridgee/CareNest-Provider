package com.carenest.provider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.profile.presentation.ui.registration.RegistrationScreen
import com.carenest.provider.ui.theme.CareNestProviderTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CareNestProviderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SpTheme {
                        RegistrationScreen(
                            onNavigateToApplicationUnderReview = { TODO() }
                        )
                    }
                }
            }
        }
    }
}