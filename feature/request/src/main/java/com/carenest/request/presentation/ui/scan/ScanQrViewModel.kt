package com.carenest.request.presentation.ui.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.request.domain.usecase.CompleteRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanQrViewModel @Inject constructor(
    private val completeRequestUseCase: CompleteRequestUseCase
) : ViewModel(),
    StateHolder<ScanQrUiState> by DefaultStateHolder(ScanQrUiState()),
    EffectPublisher<ScanQrEffect> by DefaultEffectPublisher() {

    private var requestId: String? = null

    private fun handleQrScanned(code: String) {
        val currentRequestId = requestId ?: return
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null, scannedCode = code) }
            
            completeRequestUseCase(currentRequestId, code)
                .onSuccess {
                    updateState { copy(isLoading = false, isSuccess = true) }
                    sendEffect(ScanQrEffect.NavigateToSuccess)
                }
                .onFailure { error ->
                    updateState { copy(isLoading = false, error = error.message) }
                    sendEffect(ScanQrEffect.ShowError(error.message ?: "Verification failed"))
                }
        }
    }

    fun onIntent(intent: ScanQrIntent) {
        when (intent) {
            is ScanQrIntent.Load -> this.requestId = intent.requestId
            is ScanQrIntent.QrScanned -> handleQrScanned(intent.code)
            ScanQrIntent.BackClicked -> sendEffect(ScanQrEffect.NavigateBack)
            ScanQrIntent.Retry -> updateState { copy(error = null, scannedCode = null) }
        }
    }
}
