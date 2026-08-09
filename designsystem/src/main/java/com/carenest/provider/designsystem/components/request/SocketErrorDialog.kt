package com.carenest.provider.designsystem.components.request

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun SocketErrorDialog(
    errorMessage: String,
    errorCode: String? = null,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Connection Alert",
                style = Theme.typography.title.copy(color = Theme.colors.primaryFont)
            )
        },
        text = {
            Column {
                Text(
                    text = errorMessage,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont)
                )
                if (!errorCode.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Error Code: $errorCode",
                        style = Theme.typography.body.small.copy(color = Theme.colors.hint)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "OK",
                    style = Theme.typography.body.medium.copy(color = Theme.colors.primary)
                )
            }
        }
    )
}
