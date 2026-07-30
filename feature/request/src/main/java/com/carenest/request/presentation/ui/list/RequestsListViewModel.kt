package com.carenest.request.presentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.request.domain.model.RequestStatus
import com.carenest.request.domain.usecase.GetIncomingRequestsUseCase
import com.carenest.request.domain.usecase.SendOfferToPatientUseCase
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class RequestsListViewModel @Inject constructor(
    private val getIncomingRequests: GetIncomingRequestsUseCase,
    private val sendOfferToPatient: SendOfferToPatientUseCase,
) : ViewModel(),
    StateHolder<RequestsListUiState> by DefaultStateHolder(RequestsListUiState()),
    EffectPublisher<RequestsListEffect> by DefaultEffectPublisher() {

    private var offerTimerJob: Job? = null

    init {
        loadRequests()
    }

    fun onIntent(intent: RequestsListIntent) {
        when (intent) {
            is RequestsListIntent.CardClicked -> handleCardClick(intent.requestId)
            is RequestsListIntent.EditRateClicked -> openEditRateModal(intent.requestId)
            is RequestsListIntent.MakeOfferClicked -> startMakeOffer(intent.requestId)
            is RequestsListIntent.EditRateChanged -> {
                updateState { copy(editRateDraft = intent.rate) }
            }
            RequestsListIntent.SaveRateClicked -> saveEditedRate()
            RequestsListIntent.DismissModal -> dismissModal()
        }
    }

    private fun loadRequests() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            val requests = getIncomingRequests().getOrDefault(emptyList())
            updateState { copy(isLoading = false, requests = requests) }
        }
    }

    private fun handleCardClick(requestId: String) {
        val request = currentState.requests.find { it.id == requestId } ?: return
        if (request.status != RequestStatus.ESTIMATED) return

        updateState {
            copy(selectedCardId = if (selectedCardId == requestId) null else requestId)
        }
    }

    private fun openEditRateModal(requestId: String) {
        val request = currentState.requests.find { it.id == requestId } ?: return

        updateState {
            copy(
                activeModal = RequestsListModal.EditRate,
                editingRequestId = requestId,
                editRateDraft = request.baseRate,
                selectedCardId = requestId,
            )
        }
    }

    private fun saveEditedRate() {
        val requestId = currentState.editingRequestId ?: return
        val newRate = currentState.editRateDraft

        updateState {
            copy(
                requests = requests.map { request ->
                    if (request.id == requestId) request.copy(baseRate = newRate) else request
                },
                activeModal = RequestsListModal.None,
                editingRequestId = null,
            )
        }
    }

    private fun startMakeOffer(requestId: String) {
        val (willAccept, acceptAtSecond) = sendOfferToPatient(requestId)

        offerTimerJob?.cancel()
        updateState {
            copy(
                activeModal = RequestsListModal.MakeOffer,
                offerRequestId = requestId,
                offerCountdown = OFFER_TIMEOUT_SECONDS,
                selectedCardId = requestId,
            )
        }

        offerTimerJob = viewModelScope.launch {
            for (elapsedSecond in 1..OFFER_TIMEOUT_SECONDS) {
                delay(1_000)

                if (willAccept && elapsedSecond == acceptAtSecond) {
                    completeOfferAccepted(requestId)
                    return@launch
                }

                updateState {
                    copy(offerCountdown = OFFER_TIMEOUT_SECONDS - elapsedSecond)
                }
            }

            completeOfferTimeout(requestId)
        }
    }

    private fun completeOfferAccepted(requestId: String) {
        offerTimerJob?.cancel()
        updateState {
            copy(
                requests = requests.map { request ->
                    if (request.id == requestId) {
                        request.copy(status = RequestStatus.ACCEPTED, progressStep = 1)
                    } else {
                        request
                    }
                },
                activeModal = RequestsListModal.None,
                offerRequestId = null,
                offerCountdown = null,
                selectedCardId = null,
            )
        }
        sendEffect(RequestsListEffect.NavigateToOfferConfirmed(requestId))
    }

    private fun completeOfferTimeout(requestId: String) {
        offerTimerJob?.cancel()
        updateState {
            copy(
                requests = requests.map { request ->
                    if (request.id == requestId) request.copy(status = RequestStatus.CANCELED) else request
                },
                activeModal = RequestsListModal.None,
                offerRequestId = null,
                offerCountdown = null,
                selectedCardId = null,
            )
        }
    }

    private fun dismissModal() {
        if (currentState.activeModal == RequestsListModal.MakeOffer) {
            offerTimerJob?.cancel()
        }
        updateState {
            copy(
                activeModal = RequestsListModal.None,
                editingRequestId = null,
                offerRequestId = null,
                offerCountdown = null,
            )
        }
    }

    override fun onCleared() {
        offerTimerJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val OFFER_TIMEOUT_SECONDS = 10
    }
}
