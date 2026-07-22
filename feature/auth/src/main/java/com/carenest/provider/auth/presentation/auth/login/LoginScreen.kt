package com.carenest.provider.auth.presentation.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.activity.compose.BackHandler
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.provider.core.mvi.ObserveEffect

import com.carenest.provider.auth.presentation.auth.login.components.AuthLandingScreen
import com.carenest.provider.auth.presentation.auth.login.components.PhoneInputScreen

@Composable
fun LoginScreen(
    viewModel: com.carenest.provider.auth.presentation.auth.login.LoginViewModel = hiltViewModel(),
    onNavigateToOtp: (String, com.carenest.provider.auth.presentation.auth.login.OtpDeliveryMethod) -> Unit
) {
    val state by viewModel.state.collectAsState()

    BackHandler(enabled = state.currentStep == LoginStep.PHONE_INPUT) {
        viewModel.onEvent(LoginIntent.BackClicked)
    }

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
