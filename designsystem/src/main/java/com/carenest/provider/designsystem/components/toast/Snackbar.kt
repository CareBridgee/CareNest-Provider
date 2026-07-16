package com.carenest.provider.designsystem.components.toast

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class SnackbarVisuals(
    override val message: String,
    val type: ToastType,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
) : SnackbarVisuals {
    override val actionLabel: String? = null
    override val withDismissAction: Boolean = false
}

suspend fun SnackbarHostState.showSnack(
    message: String,
    type: ToastType = ToastType.Info,
) {
    showSnackbar(SnackbarVisuals(message = message, type = type))
}

@Composable
fun SnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState = hostState, modifier = modifier) { data ->
        val type = (data.visuals as? com.carenest.provider.designsystem.components.toast.SnackbarVisuals)?.type ?: ToastType.Info
        ToastCard(message = data.visuals.message, type = type)
    }
}

@Preview
@Composable
private fun SnackbarVisualsPreview() {
    SnackbarVisuals(
        message = "This is snackbar",
        type = ToastType.Error
    )
}
