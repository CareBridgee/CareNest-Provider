package com.carenest.request.presentation.ui.scan

data class ScanQrUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val scannedCode: String? = null
)

sealed interface ScanQrIntent {
    data class Load(val requestId: String) : ScanQrIntent
    data class QrScanned(val code: String) : ScanQrIntent
    data object BackClicked : ScanQrIntent
    data object Retry : ScanQrIntent
}

sealed interface ScanQrEffect {
    data object NavigateBack : ScanQrEffect
    data class ShowError(val message: String) : ScanQrEffect
    data object NavigateToSuccess : ScanQrEffect
}
