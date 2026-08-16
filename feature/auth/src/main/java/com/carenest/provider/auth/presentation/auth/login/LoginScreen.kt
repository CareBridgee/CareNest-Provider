package com.carenest.provider.auth.presentation.auth.login

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.provider.auth.domain.util.AuthenticationDestination
import com.carenest.provider.auth.presentation.auth.localizedMessage
import com.carenest.provider.auth.presentation.auth.login.components.AuthLandingScreen
import com.carenest.provider.auth.presentation.auth.login.components.PhoneInputScreen
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.dialog.CareNestDialog
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToOtp: (String, OtpDeliveryMethod, String?, String?) -> Unit,
    onAuthenticationSuccess: (AuthenticationDestination) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    BackHandler(enabled = state.currentStep == LoginStep.PHONE_INPUT) {
        viewModel.onEvent(LoginIntent.BackClicked)
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is LoginEffect.NavigateToOtp -> onNavigateToOtp(
                effect.phone,
                effect.method,
                effect.otp,
                effect.pendingToken,
            )
            is LoginEffect.NavigateToDestination -> onAuthenticationSuccess(effect.destination)
        }
    }

    LoginScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
internal fun LoginScreenContent(
    state: LoginState,
    onEvent: (LoginIntent) -> Unit
) {
    when (state.currentStep) {
        LoginStep.LANDING -> AuthLandingScreen(
            onEvent = onEvent,
            isLoading = state.isLoading,
        )
        LoginStep.PHONE_INPUT -> PhoneInputScreen(state, onEvent)
    }

    state.errorMessage?.localizedMessage()?.let { message ->
        CareNestDialog(
            title = stringResource(com.carenest.provider.designsystem.R.string.auth_error_dialog_title),
            message = message,
            confirmText = stringResource(com.carenest.provider.designsystem.R.string.auth_error_dialog_confirm),
            onConfirm = { onEvent(LoginIntent.DismissErrorDialog) },
            onDismiss = { onEvent(LoginIntent.DismissErrorDialog) },
            dismissText = null,
            confirmColor = Theme.colors.primary,
        )
    }
}
