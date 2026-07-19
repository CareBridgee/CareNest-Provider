package com.carenest.provider.profile.presentation.ui.auth.login.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.carenest.provider.profile.R
import com.carenest.provider.designsystem.components.button.PrimaryButton

@Composable
fun ContinueButton(
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    PrimaryButton(
        caption = stringResource(id = R.string.phone_input_next_btn),
        onClick = onClick,
        isDisabled = !isEnabled,
        isLoading = isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}
