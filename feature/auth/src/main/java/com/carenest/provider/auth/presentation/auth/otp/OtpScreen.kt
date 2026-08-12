package com.carenest.provider.auth.presentation.auth.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.provider.auth.domain.util.AuthenticationDestination
import com.carenest.provider.auth.domain.validation.PhoneValidator
import com.carenest.provider.auth.presentation.auth.localizedMessage
import com.carenest.provider.auth.presentation.auth.otp.components.OtpTextField
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun OtpScreen(
    phone: String,
    otp: String? = null,
    viewModel: OtpViewModel = hiltViewModel(),
    onAuthenticationSuccess: (AuthenticationDestination) -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(phone, otp) {
        viewModel.onEvent(OtpIntent.PhoneNumberChanged(phone))
        otp?.let {
            viewModel.onEvent(OtpIntent.OtpCodeChanged(it))
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is OtpEffect.AuthenticationSucceeded -> onAuthenticationSuccess(effect.destination)
            is OtpEffect.NavigateBack -> onNavigateBack()
        }
    }

    OtpScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun OtpScreenContent(
    state: OtpState,
    onEvent: (OtpIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {

        CareNestTopBar(
            title = stringResource(R.string.otp_app_name),
            leading = TopBarLeading.Back {
                onEvent(OtpIntent.BackClicked)
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Theme.colors.primary.copy(alpha = .1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhoneAndroid,
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BasicText(
                text = stringResource(R.string.otp_title),
                style = Theme.typography.display.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            BasicText(
                text = stringResource(R.string.otp_subtitle),
                modifier = Modifier.fillMaxWidth(),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            BasicText(
                text = PhoneValidator.formatInternationalNumber(state.phoneNumber),
                modifier = Modifier.fillMaxWidth(),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.secondaryFont,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Theme.colors.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    OtpTextField(
                        otpValue = state.otpCode,
                        onOtpValueChange = {
                            onEvent(OtpIntent.OtpCodeChanged(it))
                        }
                    )

                    state.errorMessage.localizedMessage()?.let { errorMessage ->

                        Spacer(modifier = Modifier.height(16.dp))

                        BasicText(
                            text = errorMessage,
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.error,
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        caption = stringResource(
                            if (state.canRetryDestination) {
                                R.string.otp_retry_routing_btn
                            } else {
                                R.string.otp_verify_btn
                            }
                        ),
                        onClick = {
                            onEvent(
                                if (state.canRetryDestination) {
                                    OtpIntent.RetryDestinationResolution
                                } else {
                                    OtpIntent.VerifyOtpClicked
                                }
                            )
                        },
                        isLoading = state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OtpResendCountdown(
                remainingSeconds = state.remainingSeconds,
                isResending = state.isResending,
                onResend = { onEvent(OtpIntent.ResendClicked) },
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OtpResendCountdown(
    remainingSeconds: Int,
    isResending: Boolean,
    onResend: () -> Unit,
) {
    val displayedSeconds = remainingSeconds.coerceAtLeast(0)
    val canResend = displayedSeconds == 0 && !isResending

    BasicText(
        text = if (displayedSeconds > 0) {
            val minutes = displayedSeconds / 60
            val seconds = displayedSeconds % 60
            stringResource(
                R.string.auth_otp_resend_timer,
                "%02d:%02d".format(minutes, seconds),
            )
        } else {
            stringResource(R.string.auth_otp_resend_code)
        },
        modifier = Modifier.clickable(enabled = canResend, onClick = onResend),
        style = Theme.typography.body.large.copy(
            color = if (canResend) Theme.colors.primary else Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun OtpScreenPreview() {
    SpTheme {
        OtpScreenContent(
            state = OtpState(
                phoneNumber = "+1 555 000 0000",
                otpCode = ""
            ),
            onEvent = {}
        )
    }
}