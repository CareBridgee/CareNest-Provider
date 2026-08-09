package com.carenest.request.presentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.core.network.socket.model.ReservationEventType
import com.carenest.request.domain.model.RequestStatus
import com.carenest.request.domain.usecase.CreateOfferUseCase
import com.carenest.request.domain.usecase.GetIncomingRequestsUseCase
import com.carenest.request.domain.usecase.ListenReservationEventsUseCase
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
    private val withdrawOffer: WithdrawOfferUseCase,
    private val createOffer: CreateOfferUseCase,
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
            is RequestsListIntent.MakeOfferClicked -> handleMakeOffer(intent.requestId)
            is RequestsListIntent.EditRateChanged -> updateEditPriceDraft(intent.rate)
            is RequestsListIntent.ViewDetailsClicked -> navigateToDetails(intent.requestId)
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

    private fun updateEditPriceDraft(rate: Float) {
        updateState { copy(editPriceDraft = rate) }
    }

    private fun navigateToDetails(requestId: String) {
        sendEffect(RequestsListEffect.NavigateToRequestDetails(requestId))
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

    private fun handleMakeOffer(requestId: String) {
        val request = currentState.requests.find { it.id == requestId }
        val price = (request?.basePrice ?: currentState.editPriceDraft).toDouble()

        setupOfferUiState(requestId)

        viewModelScope.launch {
            try {
                createOffer(requestId, price)
            } catch (_: Exception) { }
        }

        startReservationEventListener(requestId)
        startOfferCountdown(requestId)
    }

    private fun setupOfferUiState(requestId: String) {
        updateState {
            copy(
                activeModal = RequestsListModal.MakeOffer,
                offerRequestId = requestId,
                offerCountdown = OFFER_TIMEOUT_SECONDS,
                selectedCardId = requestId,
            )
        }
    }

    private fun startReservationEventListener(requestId: String) {
        eventListenerJob?.cancel()
        eventListenerJob = viewModelScope.launch {
            listenReservationEvents(requestId).collect { event ->
                when (event.eventType) {
                    ReservationEventType.OFFER_ACCEPTED -> {
                        handleOfferAccepted(requestId)
                    }
                    ReservationEventType.OFFER_REJECTED, ReservationEventType.REQUEST_CANCELLED -> {
                        handleOfferTimeout(requestId)
                    }
                    else -> { }
                }
            }
        }
    }

    private fun startOfferCountdown(requestId: String) {
        offerTimerJob?.cancel()
        offerTimerJob = viewModelScope.launch {
            for (remainingSeconds in OFFER_TIMEOUT_SECONDS downTo 0) {
                updateState { copy(offerCountdown = remainingSeconds) }
                
                if (remainingSeconds == 0) {
                    handleOfferTimeout(requestId)
                    return@launch
                }
                
                delay(1000L)
            }
        }
    }

    private fun handleOfferAccepted(requestId: String) {
        viewModelScope.launch {
            stopActiveOfferJobs()
            updateState { copy(activeModal = RequestsListModal.OfferSuccess) }
            delay(1500)
            updateRequestsStatus(requestId, RequestStatus.ACCEPTED)
            clearOfferState()
            sendEffect(RequestsListEffect.StartActiveReservationService(requestId))
            sendEffect(RequestsListEffect.NavigateToOfferConfirmed(requestId))
        }
    }

    private fun handleOfferTimeout(requestId: String) {
        stopActiveOfferJobs()
        performWithdrawal(requestId)
        updateRequestsStatus(requestId, RequestStatus.CANCELED)
        clearOfferState()
    }

    private fun performWithdrawal(requestId: String) {
        val idToWithdraw = currentOfferId ?: requestId
        viewModelScope.launch {
            try {
                withdrawOffer(idToWithdraw)
            } catch (_: Exception) {}
        }
    }

    private fun updateRequestsStatus(requestId: String, status: RequestStatus) {
        updateState {
            copy(
                requests = requests.map { request ->
                    if (request.id == requestId) request.copy(status = status) else request
                }
            )
        }
    }

    private fun clearOfferState() {
        updateState {
            copy(
                activeModal = RequestsListModal.None,
                offerRequestId = null,
                offerCountdown = null,
                selectedCardId = null,
            )
        }
    }

    private fun dismissModal() {
        stopActiveOfferJobs()
        updateState {
            copy(
                activeModal = RequestsListModal.None,
                editingRequestId = null,
                offerRequestId = null,
                offerCountdown = null,
            )
        }
    }

    private fun stopActiveOfferJobs() {
        offerTimerJob?.cancel()
        eventListenerJob?.cancel()
    }

    override fun onCleared() {
        stopActiveOfferJobs()
        super.onCleared()
    }

    private companion object {
        const val OFFER_TIMEOUT_SECONDS = 20
    }
}
