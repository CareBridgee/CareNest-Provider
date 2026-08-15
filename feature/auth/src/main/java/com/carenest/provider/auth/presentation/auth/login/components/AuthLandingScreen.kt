package com.carenest.provider.auth.presentation.auth.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.auth.presentation.auth.login.LoginIntent
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.R as DR

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.carenest.provider.auth.presentation.auth.login.google.GoogleSignInHelper

@Composable
fun AuthLandingScreen(onEvent: (LoginIntent) -> Unit) {
    val context = LocalContext.current
    val googleSignInHelper = remember(context) { GoogleSignInHelper(context) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        println("GoogleAuthUI: Activity launcher result code=${result.resultCode}, intentData=${result.data}")
        Log.i("GoogleAuthUI", "Google Sign-In activity launcher returned with resultCode=${result.resultCode}")
        googleSignInHelper.parseGoogleAccount(result.data).fold(
            onSuccess = { payload ->
                println("GoogleAuthUI: Google account payload retrieved. Sending GoogleSignInClicked event...")
                onEvent(
                    LoginIntent.GoogleSignInClicked(
                        idToken = payload.idToken,
                        firstName = payload.firstName,
                        lastName = payload.lastName,
                        email = payload.email,
                        profileImageUrl = payload.profileImageUrl,
                    )
                )
            },
            onFailure = { error ->
                println("GoogleAuthUI: Google Sign-In failed with error=${error.message}")
                onEvent(LoginIntent.GoogleSignInFailed(error.message))
            },
        )
    }

    fun launchGoogleSignIn() {
        println("GoogleAuthUI: Google Sign-In button clicked")
        Log.i("GoogleAuthUI", "Google Sign-In button clicked. Launching GMS Sign-In intent...")
        try {
            val intent = googleSignInHelper.client.signInIntent
            println("GoogleAuthUI: Launching Google Sign-In intent=$intent")
            googleSignInLauncher.launch(intent)
        } catch (e: Throwable) {
            println("GoogleAuthUI: Error launching Google Sign-In intent: ${e.message}")
            Log.e("GoogleAuthUI", "Failed to launch Google Sign-In intent: ${e.message}", e)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        val minHeight = maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = minHeight)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Content: Logo, Title, and Description
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(modifier = Modifier.height(18.dp))
                    Image(
                        painter = painterResource(DR.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(150.dp),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Theme.colors.primary),
                    )

                    Spacer(modifier = Modifier.height(58.dp))

                    BasicText(
                        text = stringResource(R.string.app_name_careconnect),
                        style = Theme.typography.display.copy(
                            color = Theme.colors.primary,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BasicText(
                        text = stringResource(R.string.app_auth_description),
                        style = Theme.typography.title.copy(
                            fontSize = 16.sp,
                            color = Theme.colors.primary,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                // Bottom Content: Action Buttons & Terms Agreement
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SecondaryButton(
                        caption = stringResource(R.string.auth_continue_google),
                        iconPainter = painterResource(id = DR.drawable.ic_google),
                        changeIconColor = false,
                        onClick = { launchGoogleSignIn() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SecondaryButton(
                        caption = stringResource(R.string.auth_continue_phone),
                        iconPainter = painterResource(id = DR.drawable.ic_call),
                        onClick = { onEvent(LoginIntent.ContinueWithPhoneClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    BasicText(
                        text = buildAnnotatedString {
                            val fullText = stringResource(R.string.auth_terms_agreement)
                            withStyle(style = SpanStyle(color = Theme.colors.hint)) {
                                append(fullText)
                            }
                        },
                        style = Theme.typography.body.medium.copy(
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthLandingScreenPreview() {
    SpTheme {
        AuthLandingScreen(onEvent = {})
    }
}
