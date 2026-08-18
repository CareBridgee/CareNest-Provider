package com.carenest.request.presentation.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.request.domain.usecase.CompleteServiceRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanQrViewModel @Inject constructor(
    private val completeRequestUseCase: CompleteServiceRequestUseCase
) : ViewModel(),
    StateHolder<ScanQrUiState> by DefaultStateHolder(ScanQrUiState()),
    EffectPublisher<ScanQrEffect> by DefaultEffectPublisher() {

    private var requestId: String? = null
    private var isScanLocked = false

    private fun handleQrScanned(code: String) {
        val currentRequestId = requestId ?: return
        if (isScanLocked) return
        isScanLocked = true
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null, scannedCode = code) }

            completeRequestUseCase(currentRequestId, code)
                .onSuccess {
                    updateState { copy(isLoading = false, isSuccess = true) }
                    sendEffect(ScanQrEffect.NavigateToSuccess)
                }
                .onFailure { error ->
                    val message = when (error) {
                        is java.net.UnknownHostException, is java.net.ConnectException -> "Check your internet connection and try again."
                        is io.ktor.client.plugins.ResponseException -> "Unable to verify the code right now. Please try again."
                        else -> "Verification failed. Please ensure you've scanned the correct QR code."
                    }
                    updateState { copy(isLoading = false, error = message) }
                    sendEffect(ScanQrEffect.ShowError(message))
                }
        }
    }

    fun onIntent(intent: ScanQrIntent) {
        when (intent) {
            is ScanQrIntent.Load -> this.requestId = intent.requestId
            is ScanQrIntent.QrScanned -> handleQrScanned(intent.code)
            ScanQrIntent.BackClicked -> sendEffect(ScanQrEffect.NavigateBack)
            ScanQrIntent.Retry -> {
                isScanLocked = false
                updateState { copy(error = null, scannedCode = null) }
            }
        }
    }
}
