package com.carenest.provider.designsystem.components.request

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.dialog.CareNestDialog
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun SocketErrorDialog(
    errorMessage: String,
    errorCode: String? = null,
    onDismiss: () -> Unit,
) {
    val fullMessage = if (!errorCode.isNullOrBlank()) {
        "$errorMessage\n\n${stringResource(R.string.connection_error_code, errorCode)}"
    } else {
        errorMessage
    }

    CareNestDialog(
        title = stringResource(R.string.connection_alert_title),
        message = fullMessage,
        confirmText = stringResource(R.string.common_ok),
        dismissText = null,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        confirmColor = Theme.colors.primary,
    )
}
