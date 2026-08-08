package com.carenest.request.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R

@Composable
fun CancelRequestDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    isSubmitting: Boolean = false,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Theme.colors.surface)
                .padding(24.dp),
        ) {
            BasicText(
                text = stringResource(R.string.cancel_request_title),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(Modifier.height(8.dp))

            BasicText(
                text = stringResource(R.string.cancel_request_message),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                ),
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SecondaryButton(
                    caption = stringResource(R.string.cancel_request_dismiss),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    isDisabled = isSubmitting,
                )
                PrimaryButton(
                    caption = stringResource(R.string.cancel_request_confirm),
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    containerColor = Theme.colors.error,
                    contentColor = Theme.colors.onError,
                    isDisabled = isSubmitting,
                    isLoading = isSubmitting,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CancelRequestDialogPreview() {
    SpTheme {
        CancelRequestDialog(
            onDismiss = {},
            onConfirm = {},
        )
    }
}
