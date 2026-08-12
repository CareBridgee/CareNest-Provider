package com.carenest.provider.auth.presentation.auth.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.auth.domain.validation.PhoneNumberValidationError
import com.carenest.provider.auth.domain.validation.SupportedPhoneCountry
import com.carenest.provider.auth.presentation.auth.AuthUiError
import com.carenest.provider.auth.presentation.auth.localizedMessage
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.R

import com.carenest.provider.auth.presentation.auth.login.Country

@Composable
fun PhoneNumberSection(
    phone: String,
    onPhoneChange: (String) -> Unit,
    selectedCountry: Country,
    isDropdownExpanded: Boolean,
    onCountryClick: () -> Unit,
    onCountrySelect: (Country) -> Unit,
    validationError: PhoneNumberValidationError?,
    errorMessage: AuthUiError?,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.phone_input_label),
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primary,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        PhoneInputField(
            phone = phone,
            onPhoneChange = onPhoneChange,
            selectedCountry = selectedCountry,
            isDropdownExpanded = isDropdownExpanded,
            onCountryClick = onCountryClick,
            onCountrySelect = onCountrySelect,
            isError = validationError != null || errorMessage != null,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.phone_input_carrier_charges),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
            )
        )

        val localizedValidationError = when (validationError) {
            PhoneNumberValidationError.Required -> stringResource(R.string.validation_phone_required)
            PhoneNumberValidationError.InvalidLength -> stringResource(
                R.string.login_validation_phone_length,
                selectedCountry.phoneConfig.nationalDigitLength,
                selectedCountry.code,
            )
            PhoneNumberValidationError.InvalidFormat -> stringResource(
                when (selectedCountry.phoneConfig) {
                    SupportedPhoneCountry.EGYPT -> R.string.login_validation_egypt_phone_prefix
                    SupportedPhoneCountry.SAUDI_ARABIA -> R.string.login_validation_saudi_phone_prefix
                    SupportedPhoneCountry.UAE -> R.string.login_validation_uae_phone_prefix
                }
            )
            null -> null
        }
        val displayedError = localizedValidationError ?: errorMessage.localizedMessage()
        if (displayedError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayedError,
                style = Theme.typography.body.medium.copy(color = Theme.colors.error)
            )
        }
    }
}
