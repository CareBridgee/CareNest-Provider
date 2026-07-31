package com.carenest.request.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.request.domain.usecase.GetRequestContractUseCase
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OfferDetailsViewModel @Inject constructor(
    private val getRequestContract: GetRequestContractUseCase,
) : ViewModel(),
    StateHolder<OfferDetailsUiState> by DefaultStateHolder(OfferDetailsUiState()),
    EffectPublisher<OfferDetailsEffect> by DefaultEffectPublisher() {

    private var loadedRequestId: String? = null

    fun onIntent(intent: OfferDetailsIntent) {
        when (intent) {
            is OfferDetailsIntent.Load -> loadContract(intent.requestId)
            OfferDetailsIntent.BackClicked -> sendEffect(OfferDetailsEffect.NavigateBack)
            OfferDetailsIntent.CallClicked -> {
                currentState.offer?.patientInfo?.phone?.let {
                    sendEffect(OfferDetailsEffect.InitiateCall(it))
                }
            }
            OfferDetailsIntent.MessageClicked -> {
                currentState.offer?.patientInfo?.id?.let {
                    sendEffect(OfferDetailsEffect.OpenChat(it))
                }
            }
            OfferDetailsIntent.CopyAddressClicked -> {
                val address = "${currentState.offer?.patientInfo?.addressLine}, ${currentState.offer?.patientInfo?.addressDetail}"
                sendEffect(OfferDetailsEffect.CopyToClipboard(address))
            }
            OfferDetailsIntent.ViewSummaryClicked -> {
                currentState.offer?.patientInfo?.summery?.let {
                    sendEffect(OfferDetailsEffect.ShowSummary(it))
                }
            }
            OfferDetailsIntent.OpenInMapsClicked -> {
                val address = "${currentState.offer?.patientInfo?.addressLine}, ${currentState.offer?.patientInfo?.addressDetail}"
                sendEffect(OfferDetailsEffect.OpenMaps(address))
            }
            OfferDetailsIntent.MoreClicked -> {
                // Handle more options if needed
            }
        }
    }

    private fun loadContract(requestId: String) {
        if (loadedRequestId == requestId) return
        loadedRequestId = requestId

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val contract = getRequestContract(requestId).getOrNull()
            updateState { copy(isLoading = false, offer = contract) }
        }
    }
}
