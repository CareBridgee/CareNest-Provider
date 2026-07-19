package com.carenest.presentation.ui.auth.login.components

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
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

import com.carenest.presentation.ui.auth.login.Country

@Composable
fun PhoneNumberSection(
    phone: String,
    onPhoneChange: (String) -> Unit,
    selectedCountry: Country,
    isDropdownExpanded: Boolean,
    onCountryClick: () -> Unit,
    onCountrySelect: (Country) -> Unit,
    errorMessage: String?
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
            onCountrySelect = onCountrySelect
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.phone_input_carrier_charges),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont
            )
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                style = Theme.typography.body.medium.copy(color = Theme.colors.error)
            )
        }
    }
}
