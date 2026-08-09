package com.carenest.request.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.request.domain.usecase.GetRequestContractUseCase
import com.carenest.request.R
import com.carenest.request.presentation.UiText
import com.carenest.request.presentation.toUiText
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
                val phone = currentState.offer?.patientInfo?.phone
                if (phone.isNullOrBlank()) {
                    sendEffect(
                        OfferDetailsEffect.ShowError(
                            UiText.StringResource(R.string.patient_phone_unavailable)
                        )
                    )
                } else {
                    sendEffect(OfferDetailsEffect.InitiateCall(phone))
                }
            }
            OfferDetailsIntent.MessageClicked -> {
                val targetId = currentState.offer?.reservationId.takeIf { !it.isNullOrBlank() }
                    ?: currentState.offer?.offerId
                if (targetId.isNullOrBlank()) {
                    sendEffect(
                        OfferDetailsEffect.ShowError(
                            UiText.StringResource(R.string.chat_reservation_unavailable)
                        )
                    )
                } else {
                    sendEffect(OfferDetailsEffect.OpenChat(targetId))
                }
            }
            OfferDetailsIntent.CopyAddressClicked -> {
                withAddress { sendEffect(OfferDetailsEffect.CopyToClipboard(it)) }
            }
            OfferDetailsIntent.ViewSummaryClicked -> {
                val summary = currentState.offer?.patientInfo?.summery
                if (summary.isNullOrBlank()) {
                    sendEffect(
                        OfferDetailsEffect.ShowError(
                            UiText.StringResource(R.string.patient_report_unavailable)
                        )
                    )
                } else {
                    sendEffect(OfferDetailsEffect.ShowSummary(summary))
                }
            }
            OfferDetailsIntent.OpenInMapsClicked -> {
                withAddress { sendEffect(OfferDetailsEffect.OpenMaps(it)) }
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
            getRequestContract(requestId)
                .onSuccess { contract ->
                    updateState { copy(isLoading = false, offer = contract) }
                    if (contract.serviceRequestStatus.equals("COMPLETED", ignoreCase = true)) {
                        sendEffect(OfferDetailsEffect.NavigateToVisitCompleted(contract.offerId))
                    }
                }
                .onFailure { error ->
                    loadedRequestId = null
                    updateState { copy(isLoading = false, offer = null) }
                    sendEffect(OfferDetailsEffect.ShowError(error.toUiText()))
                }
        }
    }

    private fun withAddress(block: (String) -> Unit) {
        val patient = currentState.offer?.patientInfo ?: return
        val address = listOf(patient.addressLine, patient.addressDetail)
            .filter(String::isNotBlank)
            .joinToString(", ")
        if (address.isBlank()) {
            sendEffect(
                OfferDetailsEffect.ShowError(
                    UiText.StringResource(R.string.patient_address_unavailable)
                )
            )
        } else {
            block(address)
        }
    }

}
