package com.carenest.request.presentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.request.domain.model.RequestStatus
import com.carenest.request.domain.usecase.GetIncomingRequestsUseCase
import com.carenest.request.domain.usecase.ListenReservationEventsUseCase
import com.carenest.request.domain.usecase.SendOfferToPatientUseCase
import com.carenest.request.domain.usecase.WithdrawOfferUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestsListViewModel @Inject constructor(
    private val getIncomingRequests: GetIncomingRequestsUseCase,
    private val sendOfferToPatient: SendOfferToPatientUseCase,
    private val withdrawOffer: WithdrawOfferUseCase,
    private val listenReservationEvents: ListenReservationEventsUseCase,
) : ViewModel(),
    StateHolder<RequestsListUiState> by DefaultStateHolder(RequestsListUiState()),
    EffectPublisher<RequestsListEffect> by DefaultEffectPublisher() {

    private var offerTimerJob: Job? = null
    private var eventListenerJob: Job? = null
    private var currentOfferId: String? = null

    init {
        loadRequests()
    }

    fun onIntent(intent: RequestsListIntent) {
        when (intent) {
            is RequestsListIntent.CardClicked -> handleCardClick(intent.requestId)
            is RequestsListIntent.EditRateClicked -> openEditRateModal(intent.requestId)
            is RequestsListIntent.MakeOfferClicked -> startMakeOffer(intent.requestId)
            is RequestsListIntent.EditRateChanged -> {
                updateState { copy(editPriceDraft = intent.rate) }
            }
            is RequestsListIntent.ViewDetailsClicked -> sendEffect(
                RequestsListEffect.NavigateToRequestDetails(intent.requestId)
            )
            RequestsListIntent.SaveRateClicked -> saveEditedPrice()
            RequestsListIntent.DismissModal -> dismissModal()
        }
    }

    private fun loadRequests() {
        viewModelScope.launch {
            getIncomingRequests().onSuccess { requests ->
                updateState { copy(isLoading = false, requests = requests) }
            }
        }
    }

    private fun handleCardClick(requestId: String) {
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
                editPriceDraft = request.basePrice,
                selectedCardId = requestId,
            )
        }
    }

    private fun saveEditedPrice() {
        val requestId = currentState.editingRequestId ?: return
        val newPrice = currentState.editPriceDraft
        updateState {
            copy(
                requests = requests.map { request ->
                    if (request.id == requestId) request.copy(basePrice = newPrice) else request
                },
                activeModal = RequestsListModal.None,
                editingRequestId = null,
            )
        }
    }

    private fun startMakeOffer(requestId: String) {
        val (willAccept, acceptAtSecond) = sendOfferToPatient(requestId)

        updateState {
            copy(
                activeModal = RequestsListModal.MakeOffer,
                offerRequestId = requestId,
                offerCountdown = OFFER_TIMEOUT_SECONDS,
                selectedCardId = requestId,
            )
        }

        // Listen for socket events to handle real-time acceptance/rejection
        eventListenerJob?.cancel()
        eventListenerJob = viewModelScope.launch {
            listenReservationEvents(requestId).collect { _ ->
                // Handled in timer loop for mock logic, or real events here
            }
        }

        offerTimerJob?.cancel()
        offerTimerJob = viewModelScope.launch {
            for (i in OFFER_TIMEOUT_SECONDS downTo 0) {
                updateState { copy(offerCountdown = i) }
                if (i == acceptAtSecond && willAccept) {
                    completeOfferAccepted(requestId)
                    break
                }
                if (i == 0) {
                    completeOfferTimeout(requestId)
                }
                delay(1.seconds)
            }
        }
    }

    private fun completeOfferAccepted(requestId: String) {
        offerTimerJob?.cancel()
        eventListenerJob?.cancel()
        updateState {
            copy(
                requests = requests.map { request ->
                    if (request.id == requestId) request.copy(status = RequestStatus.ACCEPTED) else request
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
        eventListenerJob?.cancel()
        
        // Use the captured offerId or fallback to requestId if server allows
        val idToWithdraw = currentOfferId ?: requestId
        
        viewModelScope.launch {
            try {
                withdrawOffer(idToWithdraw)
            } catch (_: Exception) {}
        }

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
        offerTimerJob?.cancel()
        eventListenerJob?.cancel()
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
        eventListenerJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val OFFER_TIMEOUT_SECONDS = 20
    }
}