package com.carenest.home.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.home.domain.model.RequestStatus
import com.carenest.home.domain.usecase.GetEarningsSummaryUseCase
import com.carenest.home.domain.usecase.GetIncomingRequestsUseCase
import com.carenest.home.domain.usecase.SendOfferToPatientUseCase
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.home.domain.model.NurseRequest
import com.carenest.home.domain.usecase.ListenReservationEventsUseCase
import com.carenest.home.domain.usecase.GetAvailabilityUseCase
import com.carenest.home.domain.usecase.GetCurrentLocationUseCase
import com.carenest.home.domain.usecase.GetServiceRequestPreviewUseCase
import com.carenest.home.domain.usecase.UpdateAvailabilityUseCase
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import com.carenest.provider.core.network.socket.model.ReservationEventType
import com.carenest.provider.core.network.socket.model.patientDisplayName
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.profile.domain.usecase.GetNurseUseCase
import com.carenest.provider.profile.domain.usecase.LoadServiceTypesUseCase
import com.carenest.provider.profile.domain.model.VerificationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.text.isNotBlank

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getIncomingRequests: GetIncomingRequestsUseCase,
    private val getEarningsSummary: GetEarningsSummaryUseCase,
    private val sendOfferToPatient: SendOfferToPatientUseCase,
    private val listenReservationEvents: ListenReservationEventsUseCase,
    private val authenticationSessionStore: AuthenticationSessionStore,
    private val getNurse: GetNurseUseCase,
    private val loadServiceTypes: LoadServiceTypesUseCase,
    private val getServiceRequestPreviewUseCase: GetServiceRequestPreviewUseCase,
    private val nurseSocketClient: NurseSocketClient,
    private val getAvailability: GetAvailabilityUseCase,
    private val updateAvailability: UpdateAvailabilityUseCase,
    private val getCurrentLocation: GetCurrentLocationUseCase,
) : ViewModel(),
    StateHolder<HomeUiState> by DefaultStateHolder(
        HomeUiState(
            nurseAvatar = authenticationSessionStore.currentSession?.profileImageUrl,
        ),
    ),
    EffectPublisher<HomeEffect> by DefaultEffectPublisher() {

    private var fetchJob: Job? = null
    private var socketJob: Job? = null
    private var offerEventListenerJob: Job? = null
    private var offerTimerJob: Job? = null
    private var profileJob: Job? = null
    private var hasResolvedProviderApproval = false
    private var serviceImagesById: Map<String, String> = emptyMap()

    val isOnline = getAvailability()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        getNurseData()
        loadServiceImages()
        observeSocketErrors()
        observeNotifications()
        observeAvailability()
    }

    private fun observeAvailability() {
        viewModelScope.launch {
            isOnline.collect { online ->
                when {
                    currentState.isProviderApproved -> applyAvailabilityChange(online)
                    !online -> applyAvailabilityChange(false)
                }
            }
        }
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
        if (intent.requiresProviderApproval && !currentState.isProviderApproved) return

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
            HomeIntent.RefreshProfile -> getNurseData()
        }
    }

    private fun getNurseData(){
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            val savedSession = authenticationSessionStore.currentSession
                ?: authenticationSessionStore.session.first()
            val nurseId = savedSession?.nurseId
            if (nurseId == null) {
                applyAvailabilityChange(false)
                return@launch
            }
            savedSession.profileImageUrl?.takeIf(String::isNotBlank)?.let { cachedUrl ->
                updateState { copy(nurseAvatar = cachedUrl) }
            }
            getNurse(nurseId).onSuccess { profile ->
                authenticationSessionStore.updateProfileImageUrl(
                    nurseId = nurseId,
                    profileImageUrl = profile.profileImageUrl,
                )
                val isProviderApproved = profile.verificationStatus == VerificationStatus.APPROVED
                val isFirstApprovalResult = !hasResolvedProviderApproval
                hasResolvedProviderApproval = true
                val fullName = listOfNotNull(
                    profile.firstName?.takeIf(String::isNotBlank),
                    profile.lastName?.takeIf(String::isNotBlank),
                ).joinToString(" ")
                updateState {
                    copy(
                        nurseName = fullName,
                        nurseAvatar = profile.profileImageUrl,
                        isProviderApproved = isProviderApproved,
                    )
                }
                if (isProviderApproved) {
                    if (isFirstApprovalResult) {
                        applyAvailabilityChange(isOnline.value)
                    }
                } else {
                    applyAvailabilityChange(false)
                    if (isOnline.value) updateAvailability(false)
                }
            }.onFailure { error ->
                if (error is CancellationException) return@onFailure
                if (!hasResolvedProviderApproval) applyAvailabilityChange(false)
            }
        }
    }

    private fun loadServiceImages() {
        viewModelScope.launch {
            loadServiceTypes().onSuccess { serviceTypes ->
                serviceImagesById = serviceTypes.mapNotNull { serviceType ->
                    val imageUrl = serviceType.imageUrl?.takeIf(String::isNotBlank)
                        ?: return@mapNotNull null
                    serviceType.id to imageUrl
                }.toMap()
                updateState {
                    copy(
                        requests = requests.map { request ->
                            val resolvedImage = request.serviceImage.takeIf(String::isNotBlank)
                                ?: request.serviceTypeId?.let(serviceImagesById::get)
                            request.copy(serviceImage = resolvedImage.orEmpty())
                        }
                    )
                }
            }
        }
    }

    private fun handleOnlineToggle(isOnline: Boolean) {
        viewModelScope.launch {
            updateAvailability(isOnline)
        }
    }

    private fun applyAvailabilityChange(isOnline: Boolean) {
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
                val location = getCurrentLocation()
                nurseSocketClient.updateAvailability(
                    available = true,
                    lat = location?.latitude,
                    lng = location?.longitude
                )
            }

            // Stream real-time socket requests
            socketJob = viewModelScope.launch {
                nurseSocketClient.nearbyRequests.collect { socketReq ->
                    val newRequest = NurseRequest(
                        id = socketReq.serviceRequestId,
                        patientName = socketReq.patientDisplayName(),
                        patientImage = socketReq.patientProfileImageUrl.orEmpty(),
                        serviceTypeId = socketReq.serviceTypeId,
                        serviceType = socketReq.serviceName ?: "Nursing Visit",
                        serviceImage = listOf(
                            socketReq.serviceImageUrl,
                            socketReq.serviceTypeImageUrl,
                            socketReq.serviceTypeId?.let(serviceImagesById::get),
                        ).firstOrNull { !it.isNullOrBlank() }.orEmpty(),
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
                    enrichPatientPreview(newRequest.id)
                }
            }

            fetchJob = viewModelScope.launch {
                Log.d("HomeViewModel", "Fetching initial requests via REST...")
                coroutineScope {
                    val requestsDeferred = async { getIncomingRequests() }
                    val earningsDeferred = async { getEarningsSummary() }

                    val requestsResult = requestsDeferred.await()
                    val earningsSummary = earningsDeferred.await().getOrNull()

                    val fetchedRequests = requestsResult.getOrDefault(emptyList()).map { request ->
                        val resolvedImage = request.serviceImage.takeIf(String::isNotBlank)
                            ?: request.serviceTypeId?.let(serviceImagesById::get)
                        request.copy(serviceImage = resolvedImage.orEmpty())
                    }
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
                    fetchedRequests.forEach { enrichPatientPreview(it.id) }
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
        if (!currentState.isProviderApproved) return

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

    private fun enrichPatientPreview(requestId: String) {
        viewModelScope.launch {
            try {
                val preview = getServiceRequestPreviewUseCase(requestId)
                val patient = preview.patient
                val patientName = patient?.fullName
                val patientImageUrl = patient?.profileImageUrl

                updateState {
                    copy(
                        requests = requests.map { request ->
                            if (request.id != requestId) return@map request
                            request.copy(
                                patientName = patientName?.takeIf(String::isNotBlank)
                                    ?: request.patientName,
                                patientImage = patientImageUrl?.takeIf(String::isNotBlank)
                                    ?: request.patientImage,
                                serviceTypeId = preview.serviceTypeId ?: request.serviceTypeId,
                                serviceType = preview.serviceName ?: request.serviceType,
                                serviceImage = preview.serviceImageUrl?.takeIf(String::isNotBlank)
                                    ?: preview.serviceTypeId?.let(serviceImagesById::get)
                                    ?: request.serviceTypeId?.let(serviceImagesById::get)
                                    ?: request.serviceImage,
                            )
                        }
                    )
                }
            } catch (_: Exception) {

            }
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

private val HomeIntent.requiresProviderApproval: Boolean
    get() = when (this) {
        HomeIntent.RefreshProfile,
        HomeIntent.DismissModal -> false
        else -> true
    }
