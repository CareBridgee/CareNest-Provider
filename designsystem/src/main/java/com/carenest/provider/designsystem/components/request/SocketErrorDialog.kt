package com.carenest.provider.designsystem.components.request

import androidx.compose.runtime.Composable
import com.carenest.provider.designsystem.components.dialog.CareNestDialog
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun SocketErrorDialog(
    errorMessage: String,
    errorCode: String? = null,
    onDismiss: () -> Unit,
) {
    val fullMessage = if (!errorCode.isNullOrBlank()) {
        "$errorMessage\n\nError Code: $errorCode"
    } else {
        errorMessage
    }

    CareNestDialog(
        title = "Connection Alert",
        message = fullMessage,
        confirmText = "OK",
        dismissText = null,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        confirmColor = Theme.colors.primary,
    )
}
