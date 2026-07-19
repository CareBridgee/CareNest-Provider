package com.carenest.presentation.ui.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.presentation.core.mvi.ObserveEffect

import com.carenest.presentation.ui.auth.login.components.AuthLandingScreen
import com.carenest.presentation.ui.auth.login.components.PhoneInputScreen

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToOtp: (String, OtpDeliveryMethod) -> Unit
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is LoginEffect.NavigateToOtp -> onNavigateToOtp(effect.phone, effect.method)
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
        LoginStep.LANDING -> AuthLandingScreen(onEvent)
        LoginStep.PHONE_INPUT -> PhoneInputScreen(state, onEvent)
    }
}
