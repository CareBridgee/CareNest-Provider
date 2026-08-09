package com.carenest.request.presentation.ui.offerconfirmed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.request.domain.usecase.CancelRequestUseCase
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
class OfferConfirmedViewModel @Inject constructor(
    private val getRequestContract: GetRequestContractUseCase,
    private val cancelRequest: CancelRequestUseCase,
) : ViewModel(),
    StateHolder<OfferConfirmedUiState> by DefaultStateHolder(OfferConfirmedUiState()),
    EffectPublisher<OfferConfirmedEffect> by DefaultEffectPublisher() {

    private var loadedRequestId: String? = null

    fun onIntent(intent: OfferConfirmedIntent) {
        when (intent) {
            is OfferConfirmedIntent.Load -> loadContract(intent.offerId)
            OfferConfirmedIntent.ViewDetailsClicked -> {
                val requestId = currentState.offer?.offerId ?: return
                sendEffect(OfferConfirmedEffect.NavigateToDetails(requestId))
            }
            OfferConfirmedIntent.CancelClicked -> {
                updateState { copy(cancelDialog = cancelDialog.copy(isVisible = true)) }
            }
            is OfferConfirmedIntent.ReasonSelected -> {
                updateState { copy(cancelDialog = cancelDialog.copy(selectedReason = intent.reason)) }
            }
            is OfferConfirmedIntent.NoteChanged -> {
                updateState { copy(cancelDialog = cancelDialog.copy(note = intent.note)) }
            }
            is OfferConfirmedIntent.DismissCancelDialog -> {
                updateState { copy(cancelDialog = CancelDialogUiState()) }
            }
            is OfferConfirmedIntent.ConfirmCancelClicked -> confirmCancel()
            is OfferConfirmedIntent.OnShowQrCodeClicked -> {
                sendEffect(OfferConfirmedEffect.NavigateToQrCode)
            }
            is OfferConfirmedIntent.OnCallClicked -> onCallNurseClicked()
            is OfferConfirmedIntent.OnMessageClicked -> onMessageNurseClicked()

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
                        sendEffect(OfferConfirmedEffect.NavigateToVisitCompleted(contract.offerId))
                    }
                }
                .onFailure { error ->
                    loadedRequestId = null
                    updateState { copy(isLoading = false, offer = null) }
                    sendEffect(OfferConfirmedEffect.ShowError(error.toUiText()))
                }
        }
    }

    private fun confirmCancel() {
        val requestId = currentState.offer?.offerId ?: return
        val reason = currentState.cancelDialog.selectedReason ?: return

        viewModelScope.launch {
            updateState { copy(cancelDialog = cancelDialog.copy(isSubmitting = true)) }
            cancelRequest(requestId, reason, currentState.cancelDialog.note)
                .onSuccess {
                    updateState { copy(cancelDialog = CancelDialogUiState()) }
                    sendEffect(OfferConfirmedEffect.NavigateBackToList)
                }
                .onFailure { error ->
                    updateState {
                        copy(cancelDialog = cancelDialog.copy(isSubmitting = false))
                    }
                    sendEffect(OfferConfirmedEffect.ShowError(error.toUiText()))
                }
        }
    }

    private fun onCallNurseClicked() {
        val phoneNumber = currentState.offer?.patientInfo?.phone
        if (phoneNumber.isNullOrBlank()) {
            sendEffect(
                OfferConfirmedEffect.ShowError(
                    UiText.StringResource(R.string.patient_phone_unavailable)
                )
            )
            return
        }
        sendEffect(OfferConfirmedEffect.InitiateCall(phoneNumber))
    }

    private fun onMessageNurseClicked() {
        val targetId = currentState.offer?.reservationId.takeIf { !it.isNullOrBlank() }
            ?: currentState.offer?.offerId
        if (targetId.isNullOrBlank()) {
            sendEffect(
                OfferConfirmedEffect.ShowError(
                    UiText.StringResource(R.string.chat_reservation_unavailable)
                )
            )
            return
        }
        sendEffect(OfferConfirmedEffect.OpenChat(targetId))
    }

}
