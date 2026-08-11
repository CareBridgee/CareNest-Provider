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

@Composable
fun AuthLandingScreen(onEvent: (LoginIntent) -> Unit) {
    var showGoogleUnavailableDialog by rememberSaveable { mutableStateOf(false) }

    if (showGoogleUnavailableDialog) {
        AlertDialog(
            onDismissRequest = { showGoogleUnavailableDialog = false },
            shape = RoundedCornerShape(24.dp),
            containerColor = Theme.colors.surface,
            tonalElevation = 0.dp,
            title = {
                Text(
                    text = stringResource(R.string.auth_google_unavailable_title),
                    style = Theme.typography.title.copy(fontWeight = FontWeight.Bold),
                    color = Theme.colors.primaryFont,
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.auth_google_unavailable_message),
                    style = Theme.typography.body.medium,
                    color = Theme.colors.secondaryFont,
                )
            },
            confirmButton = {
                TextButton(onClick = { showGoogleUnavailableDialog = false }) {
                    Text(
                        text = stringResource(R.string.auth_google_unavailable_action),
                        style = Theme.typography.body.medium.copy(fontWeight = FontWeight.SemiBold),
                        color = Theme.colors.primary,
                    )
                }
            },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Image(
                        painter = painterResource(DR.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.spacing.medium)
                    .weight(0.3f)
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BasicText(
                    text = stringResource(R.string.app_name_careconnect),
                    style = Theme.typography.display.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start
                    )
                )

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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SecondaryButton(
                    caption = stringResource(R.string.auth_continue_google),
                    iconPainter = painterResource(id = DR.drawable.ic_google),
                    changeIconColor = false,
                    onClick = { showGoogleUnavailableDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                SecondaryButton(
                    caption = stringResource(R.string.auth_continue_phone),
                    iconPainter = painterResource(id = DR.drawable.ic_call),
                    onClick = { onEvent(LoginIntent.ContinueWithPhoneClicked) },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

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
            
            Spacer(modifier = Modifier.height(32.dp))
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
