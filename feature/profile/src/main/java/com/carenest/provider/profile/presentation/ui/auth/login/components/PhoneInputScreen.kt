package com.carenest.presentation.ui.auth.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.ui.auth.login.LoginIntent
import com.carenest.presentation.ui.auth.login.LoginState
import com.carenest.presentation.ui.auth.login.LoginStep
import com.carenest.presentation.R

@Composable
fun PhoneInputScreen(state: LoginState, onEvent: (LoginIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        ScreenTopBar(
            title = stringResource(id = R.string.phone_input_topbar_title),
            showLeadingIcon = true,
            onLeadingClick = { onEvent(LoginIntent.BackClicked) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.phone_input_subtitle),
                style = Theme.typography.title.copy(
                    fontSize = 20.sp,
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            PhoneNumberSection(
                phone = state.phoneNumber,
                onPhoneChange = { onEvent(LoginIntent.PhoneNumberChanged(it)) },
                selectedCountry = state.selectedCountry,
                isDropdownExpanded = state.isCountryDropdownExpanded,
                onCountryClick = { onEvent(LoginIntent.ToggleCountryDropdown) },
                onCountrySelect = { onEvent(LoginIntent.CountryCodeChanged(it)) },
                errorMessage = state.errorMessage
            )

            Spacer(modifier = Modifier.height(32.dp))

            SecureCareInfoCard()

            Spacer(modifier = Modifier.height(16.dp))

            OtpMethodSelector(
                selectedMethod = state.selectedOtpMethod,
                onMethodSelect = { onEvent(LoginIntent.OtpMethodChanged(it)) }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            ContinueButton(
                isLoading = state.isLoading,
                isEnabled = state.phoneNumber.isNotBlank() && state.errorMessage == null,
                onClick = { onEvent(LoginIntent.RequestOtpClicked) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun PhoneInputScreenPreview() {
    SpTheme {
        PhoneInputScreen(
            state = LoginState(currentStep = LoginStep.PHONE_INPUT, phoneNumber = "000 000 0000"),
            onEvent = {}
        )
    }
}