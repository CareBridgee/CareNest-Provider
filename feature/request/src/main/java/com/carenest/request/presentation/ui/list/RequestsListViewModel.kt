package com.carenest.request.presentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import com.carenest.provider.core.network.socket.model.ReservationEventType
import com.carenest.provider.core.network.socket.model.patientDisplayName
import com.carenest.request.domain.model.Request
import com.carenest.request.domain.model.RequestStatus
import com.carenest.request.domain.usecase.CreateOfferUseCase
import com.carenest.request.domain.usecase.GetIncomingRequestsUseCase
import com.carenest.request.domain.usecase.GetPatientSummaryUseCase
import com.carenest.request.domain.usecase.ListenReservationEventsUseCase
import com.carenest.request.domain.usecase.WithdrawOfferUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestsListViewModel @Inject constructor(
    private val getIncomingRequests: GetIncomingRequestsUseCase,
    private val getPatientSummary: GetPatientSummaryUseCase,
    private val withdrawOffer: WithdrawOfferUseCase,
    private val createOffer: CreateOfferUseCase,
    private val listenReservationEvents: ListenReservationEventsUseCase,
    private val nurseSocketClient: NurseSocketClient,
) : ViewModel(),
    StateHolder<RequestsListUiState> by DefaultStateHolder(RequestsListUiState()),
    EffectPublisher<RequestsListEffect> by DefaultEffectPublisher() {

    private var offerTimerJob: Job? = null
    private var eventListenerJob: Job? = null
    private var socketJob: Job? = null
    private var currentOfferId: String? = null

    init {
        loadRequests()
        observeSocketErrors()
        observeNotifications()
    }

    private fun observeSocketErrors() {
        viewModelScope.launch {
            nurseSocketClient.socketErrors.collect { errorPayload ->
                updateState {
                    copy(
                        socketErrorMessage = errorPayload.message,
                        socketErrorCode = errorPayload.code
                    )
                }
            }
        }
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            nurseSocketClient.notifications.collect { notification ->
                val reqId = notification.relatedEntityId
                if (!reqId.isNullOrEmpty() &&
                    (notification.title.contains("Accepted", ignoreCase = true) || notification.message.contains("accepted", ignoreCase = true))
                ) {
                    handleOfferAccepted(reqId)
                }
            }
        }
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
        nurseSocketClient.connect()

        socketJob?.cancel()
        socketJob = viewModelScope.launch {
            nurseSocketClient.nearbyRequests.collect { socketReq ->
                val newRequest = Request(
                    id = socketReq.serviceRequestId,
                    patientName = socketReq.patientDisplayName(),
                    patientImage = socketReq.patientProfileImageUrl.orEmpty(),
                    serviceName = socketReq.serviceName ?: "Nursing Visit",
                    basePrice = (socketReq.estimatedPrice ?: 50.0).toFloat(),
                    patientAddress = socketReq.distanceKm?.let { "$it km" } ?: "Nearby",
                    serviceImage = "",
                    status = RequestStatus.ESTIMATED
                )
                updateState {
                    val updatedList = requests.toMutableList()
                    val existingIndex = updatedList.indexOfFirst { it.id == newRequest.id }
                    if (existingIndex != -1) {
                        updatedList[existingIndex] = newRequest
                    } else {
                        updatedList.add(0, newRequest)
                    }
                    copy(isLoading = false, requests = updatedList)
                }
                enrichPatientPreview(newRequest.id)
            }
        }

        viewModelScope.launch {
            getIncomingRequests().onSuccess { initialRequests ->
                updateState {
                    val combined = (initialRequests + requests).distinctBy { it.id }
                    copy(isLoading = false, requests = combined)
                }
                initialRequests.forEach { enrichPatientPreview(it.id) }
            }.onFailure {
                updateState { copy(isLoading = false) }
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
                    ReservationEventType.OFFER_CREATED -> {
                        nurseSocketClient.subscribeToReservationAfterOffer(requestId)
                    }
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
        viewModelScope.launch { nurseSocketClient.unsubscribeFromReservation(requestId) }
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

    private fun enrichPatientPreview(requestId: String) {
        viewModelScope.launch {
            getPatientSummary(requestId).onSuccess { patient ->
                updateState {
                    copy(
                        requests = requests.map { request ->
                            if (request.id != requestId) return@map request
                            request.copy(
                                patientName = patient.fullName.takeIf(String::isNotBlank)
                                    ?: request.patientName,
                                patientImage = patient.profileImageUrl.takeIf(String::isNotBlank)
                                    ?: request.patientImage,
                            )
                        },
                    )
                }
            }
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
                socketErrorMessage = null,
                socketErrorCode = null,
            )
        }
    }

    private fun stopActiveOfferJobs() {
        offerTimerJob?.cancel()
        eventListenerJob?.cancel()
    }

    override fun onCleared() {
        socketJob?.cancel()
        stopActiveOfferJobs()
        super.onCleared()
    }

    private companion object {
        const val OFFER_TIMEOUT_SECONDS = 20
    }
}
