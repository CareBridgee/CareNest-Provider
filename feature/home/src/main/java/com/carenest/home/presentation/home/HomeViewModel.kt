package com.carenest.home.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.home.domain.model.RequestStatus
import com.carenest.home.domain.usecase.GetEarningsSummaryUseCase
import com.carenest.home.domain.usecase.GetIncomingRequestsUseCase
import com.carenest.home.domain.usecase.GetNurseProfileUseCase
import com.carenest.home.domain.usecase.SendOfferToPatientUseCase
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.home.domain.model.NurseRequest
import com.carenest.home.domain.usecase.ListenReservationEventsUseCase
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import com.carenest.provider.core.network.socket.model.ReservationEventType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getIncomingRequests: GetIncomingRequestsUseCase,
    private val getEarningsSummary: GetEarningsSummaryUseCase,
    private val sendOfferToPatient: SendOfferToPatientUseCase,
    private val listenReservationEvents: ListenReservationEventsUseCase,
    private val getNurseProfile: GetNurseProfileUseCase,
    private val nurseSocketClient: NurseSocketClient,
) : ViewModel(),
    StateHolder<HomeUiState> by DefaultStateHolder(HomeUiState()),
    EffectPublisher<HomeEffect> by DefaultEffectPublisher() {

    private var fetchJob: Job? = null
    private var socketJob: Job? = null
    private var offerEventListenerJob: Job? = null
    private var offerTimerJob: Job? = null

    init {
        getNurseData()
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
                    completeOfferAccepted(reqId)
                }
            }
        }
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnlineToggled -> handleOnlineToggle(intent.isOnline)
            is HomeIntent.CardClicked -> handleCardClick(intent.requestId)
            is HomeIntent.EditRateClicked -> openEditRateModal(intent.requestId)
            is HomeIntent.MakeOfferClicked -> startMakeOffer(intent.requestId)
            is HomeIntent.EditRateChanged -> {
                updateState { copy(editRateDraft = intent.rate) }
            }
            is HomeIntent.TabSelected -> {
                updateState { copy(selectedTab = intent.index) }
            }
            HomeIntent.SaveRateClicked -> saveEditedRate()
            HomeIntent.DismissModal -> dismissModal()
            HomeIntent.ViewAllRequestsClicked -> sendEffect(HomeEffect.NavigateToRequestList)
        }
    }

    private fun getNurseData(){
        viewModelScope.launch {
            getNurseProfile().onSuccess { profile ->
                updateState { copy(nurseName = profile.name, nurseAvatar = profile.avatarUrl) }
            }
        }
    }

    private fun handleOnlineToggle(isOnline: Boolean) {
        fetchJob?.cancel()
        socketJob?.cancel()
        offerEventListenerJob?.cancel()
        stopOfferTimer()

        updateState {
            copy(
                isOnline = isOnline,
                isLoading = isOnline,
                requests = if (isOnline) requests else emptyList(),
                selectedCardId = null,
                activeModal = ActiveModal.None,
                offerRequestId = null,
                offerCountdown = null,
            )
        }

        if (isOnline) {
            nurseSocketClient.connect()
            viewModelScope.launch {
                // Testing coordinates provided by user
                nurseSocketClient.updateAvailability(true, 30.2361926, 31.4790023)
            }

            // Stream real-time socket requests
            socketJob = viewModelScope.launch {
                nurseSocketClient.nearbyRequests.collect { socketReq ->
                    val newRequest = NurseRequest(
                        id = socketReq.serviceRequestId,
                        patientName = socketReq.serviceName ?: "Patient Request",
                        patientImage = "",
                        serviceType = socketReq.serviceName ?: "Nursing Visit",
                        serviceImage = "",
                        baseRate = (socketReq.estimatedPrice ?: 50.0).toFloat(),
                        distanceMiles = (socketReq.distanceKm ?: 0.0).toFloat(),
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
                        copy(requests = updatedList)
                    }
                }
            }

            fetchJob = viewModelScope.launch {
                Log.d("HomeViewModel", "Fetching initial requests via REST...")
                coroutineScope {
                    val requestsDeferred = async { getIncomingRequests() }
                    val earningsDeferred = async { getEarningsSummary() }

                    val requestsResult = requestsDeferred.await()
                    val earningsSummary = earningsDeferred.await().getOrNull()

                    val fetchedRequests = requestsResult.getOrDefault(emptyList())
                    Log.d("HomeViewModel", "REST fetch completed. Found ${fetchedRequests.size} requests.")

                    updateState {
                        copy(
                            isLoading = false,
                            requests = (fetchedRequests + requests).distinctBy { it.id },
                            earnings = earningsSummary?.todayEarnings ?: earnings,
                            changePercent = earningsSummary?.changePercent ?: changePercent,
                            jobsToday = earningsSummary?.jobsToday ?: jobsToday,
                            rating = earningsSummary?.rating ?: rating,
                        )
                    }
                }
            }
        } else {
            viewModelScope.launch {
                nurseSocketClient.updateAvailability(false)
            }
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
                activeModal = ActiveModal.EditRate,
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
                activeModal = ActiveModal.None,
                editingRequestId = null,
            )
        }
    }

    private fun startMakeOffer(requestId: String) {
        val request = currentState.requests.find { it.id == requestId }
        val price = (request?.baseRate ?: currentState.editRateDraft).toDouble()

        updateState {
            copy(
                activeModal = ActiveModal.MakeOffer,
                offerRequestId = requestId,
                offerCountdown = OFFER_TIMEOUT_SECONDS,
                selectedCardId = requestId,
            )
        }

        // Send real offer over socket
        viewModelScope.launch {
            try {
                sendOfferToPatient(
                    requestId = requestId,
                    proposedPrice = price,
                    message = "Offer submitted by nurse"
                )
            } catch (_: Exception) { }
        }

        // Listen for real-time reservation offer events from server
        offerEventListenerJob?.cancel()
        offerEventListenerJob = viewModelScope.launch {
            listenReservationEvents(requestId).collect { event ->
                when (event.eventType) {
                    ReservationEventType.OFFER_ACCEPTED -> {
                        stopOfferTimer()
                        updateState { copy(activeModal = ActiveModal.OfferSuccess) }
                        delay(SUCCESS_DISPLAY_MS)
                        completeOfferAccepted(requestId)
                    }
                    ReservationEventType.OFFER_COUNTERED -> {
                        stopOfferTimer()
                        val offer = event.asOfferResponse()
                        if (offer != null) {
                            updateState {
                                copy(
                                    editRateDraft = offer.proposedPrice.toFloat(),
                                    activeModal = ActiveModal.EditRate
                                )
                            }
                        }
                    }
                    ReservationEventType.OFFER_REJECTED, ReservationEventType.REQUEST_CANCELLED -> {
                        stopOfferTimer()
                        completeOfferTimeout(requestId)
                    }
                    else -> { }
                }
            }
        }

        startOfferCountdown(requestId)
    }

    private fun startOfferCountdown(requestId: String) {
        offerTimerJob?.cancel()
        offerTimerJob = viewModelScope.launch {
            for (remainingSeconds in OFFER_TIMEOUT_SECONDS downTo 0) {
                updateState { copy(offerCountdown = remainingSeconds) }
                if (remainingSeconds == 0) {
                    completeOfferTimeout(requestId)
                    return@launch
                }
                delay(1000L)
            }
        }
    }

    private fun stopOfferTimer() {
        offerTimerJob?.cancel()
        offerTimerJob = null
    }

    private fun completeOfferAccepted(requestId: String) {
        offerEventListenerJob?.cancel()
        stopOfferTimer()
        updateState {
            copy(
                requests = requests.map { request ->
                    if (request.id == requestId) {
                        request.copy(
                            status = RequestStatus.ACCEPTED,
                            progressStep = 1,
                        )
                    } else {
                        request
                    }
                },
                activeModal = ActiveModal.None,
                offerRequestId = null,
                offerCountdown = null,
                selectedCardId = null,
            )
        }
        sendEffect(HomeEffect.StartActiveReservationService(requestId))
        sendEffect(HomeEffect.NavigateToOfferConfirmed(requestId))
    }

    private fun completeOfferTimeout(requestId: String) {
        offerEventListenerJob?.cancel()
        stopOfferTimer()
        updateState {
            copy(
                requests = requests.map { request ->
                    if (request.id == requestId) {
                        request.copy(status = RequestStatus.CANCELED)
                    } else {
                        request
                    }
                },
                activeModal = ActiveModal.None,
                offerRequestId = null,
                offerCountdown = null,
                selectedCardId = null,
            )
        }
    }

    private fun dismissModal() {
        stopOfferTimer()
        if (currentState.activeModal == ActiveModal.MakeOffer) {
            offerEventListenerJob?.cancel()
        }
        updateState {
            copy(
                activeModal = ActiveModal.None,
                editingRequestId = null,
                offerRequestId = null,
                offerCountdown = null,
                socketErrorMessage = null,
                socketErrorCode = null,
            )
        }
    }

    override fun onCleared() {
        fetchJob?.cancel()
        socketJob?.cancel()
        offerEventListenerJob?.cancel()
        stopOfferTimer()
        super.onCleared()
    }

    private companion object {
        const val OFFER_TIMEOUT_SECONDS = 20
        const val SUCCESS_DISPLAY_MS = 1_200L
    }
}
