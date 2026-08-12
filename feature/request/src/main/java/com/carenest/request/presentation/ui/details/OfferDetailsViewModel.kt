package com.carenest.request.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import com.carenest.provider.core.network.socket.model.ReservationEventType
import com.carenest.request.R
import com.carenest.request.domain.usecase.GetRequestContractUseCase
import com.carenest.request.domain.model.Offer
import com.carenest.request.domain.repository.PatientGeocodingRepository
import com.carenest.request.presentation.UiText
import com.carenest.request.presentation.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfferDetailsViewModel @Inject constructor(
    private val getRequestContract: GetRequestContractUseCase,
    private val nurseSocketClient: NurseSocketClient,
    private val patientGeocodingRepository: PatientGeocodingRepository,
) : ViewModel(),
    StateHolder<OfferDetailsUiState> by DefaultStateHolder(OfferDetailsUiState()),
    EffectPublisher<OfferDetailsEffect> by DefaultEffectPublisher() {

    private var loadedRequestId: String? = null
    private var socketJob: Job? = null
    private var geocodingJob: Job? = null
    private var copyAddressWhenResolved = false

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
                val offer = currentState.offer ?: return
                val hasCoordinates = offer.patientInfo.latitude != null &&
                    offer.patientInfo.longitude != null
                if (hasCoordinates && currentState.patientLocation == null) {
                    copyAddressWhenResolved = true
                    if (!currentState.isAddressLoading) {
                        reverseGeocodePatientLocation(offer)
                    }
                } else {
                    withBestAddress { sendEffect(OfferDetailsEffect.CopyToClipboard(it)) }
                }
            }
            OfferDetailsIntent.ViewSummaryClicked -> {
                val targetRequestId = loadedRequestId ?: currentState.offer?.offerId
                if (targetRequestId.isNullOrBlank()) {
                    sendEffect(
                        OfferDetailsEffect.ShowError(
                            UiText.StringResource(R.string.request_not_found)
                        )
                    )
                } else {
                    sendEffect(OfferDetailsEffect.NavigateToPatientSummary(targetRequestId))
                }
            }
            OfferDetailsIntent.OpenInMapsClicked -> {
                val patient = currentState.offer?.patientInfo ?: return
                val address = currentState.patientLocation?.address
                    ?.takeIf(String::isNotBlank)
                    ?: patient.fullAddress()
                val hasCoordinates = patient.latitude != null && patient.longitude != null
                if (!hasCoordinates && address.isBlank()) {
                    sendEffect(
                        OfferDetailsEffect.ShowError(
                            UiText.StringResource(R.string.patient_address_unavailable)
                        )
                    )
                } else {
                    sendEffect(
                        OfferDetailsEffect.OpenMaps(
                            latitude = patient.latitude,
                            longitude = patient.longitude,
                            address = address,
                        )
                    )
                }
            }
            OfferDetailsIntent.MoreClicked -> {
                // Handle more options if needed
            }
            OfferDetailsIntent.DismissRequestCancelledNotice -> {
                updateState { copy(isRequestCancelledByPatient = false) }
                sendEffect(OfferDetailsEffect.NavigateBack)
            }
        }
    }

    private fun loadContract(requestId: String) {
        if (loadedRequestId == requestId) return
        loadedRequestId = requestId

        observeSocketEvents(requestId)
        geocodingJob?.cancel()
        copyAddressWhenResolved = false

        viewModelScope.launch {
            updateState {
                copy(
                    isLoading = true,
                    patientLocation = null,
                    isAddressLoading = false,
                )
            }
            getRequestContract(requestId)
                .onSuccess { contract ->
                    updateState { copy(isLoading = false, offer = contract) }
                    reverseGeocodePatientLocation(contract)
                    if (contract.serviceRequestStatus.equals("COMPLETED", ignoreCase = true)) {
                        sendEffect(OfferDetailsEffect.NavigateToVisitCompleted(contract.offerId))
                    } else if (contract.serviceRequestStatus.equals("CANCELLED", ignoreCase = true) ||
                        contract.serviceRequestStatus.equals("CANCELED", ignoreCase = true)
                    ) {
                        updateState { copy(isRequestCancelledByPatient = true) }
                    }
                }
                .onFailure { error ->
                    loadedRequestId = null
                    updateState { copy(isLoading = false, offer = null) }
                    sendEffect(OfferDetailsEffect.ShowError(error.toUiText()))
                }
        }
    }

    private fun observeSocketEvents(requestId: String) {
        socketJob?.cancel()
        socketJob = viewModelScope.launch {
            nurseSocketClient.connect()
            nurseSocketClient.subscribeToReservation(requestId)

            launch {
                nurseSocketClient.reservationEvents.collect { event ->
                    val eventResId = event.effectiveReservationId
                    if (eventResId == requestId || eventResId.isNullOrEmpty()) {
                        when (event.eventType) {
                            ReservationEventType.REQUEST_CANCELLED,
                            ReservationEventType.OFFER_REJECTED -> {
                                updateState { copy(isRequestCancelledByPatient = true) }
                            }
                            ReservationEventType.COMPLETED -> {
                                sendEffect(OfferDetailsEffect.NavigateToVisitCompleted(requestId))
                            }
                            else -> {}
                        }
                    }
                }
            }

            launch {
                nurseSocketClient.notifications.collect { notif ->
                    if (notif.relatedEntityId == requestId &&
                        (notif.title.contains("Cancel", ignoreCase = true) || notif.message.contains("Cancel", ignoreCase = true))
                    ) {
                        updateState { copy(isRequestCancelledByPatient = true) }
                    }
                }
            }
        }
    }

    private fun withBestAddress(block: (String) -> Unit) {
        val patient = currentState.offer?.patientInfo ?: return
        val address = currentState.patientLocation?.address
            ?.takeIf(String::isNotBlank)
            ?: patient.fullAddress()
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

    private fun com.carenest.request.domain.model.PatientInfo.fullAddress(): String =
        listOf(addressLine, addressDetail)
            .filter(String::isNotBlank)
            .joinToString(", ")

    private fun reverseGeocodePatientLocation(offer: Offer) {
        val latitude = offer.patientInfo.latitude ?: return
        val longitude = offer.patientInfo.longitude ?: return

        geocodingJob?.cancel()
        geocodingJob = viewModelScope.launch {
            updateState { copy(isAddressLoading = true) }
            patientGeocodingRepository.reverseGeocode(latitude, longitude)
                .onSuccess { location ->
                    if (currentState.offer?.offerId == offer.offerId) {
                        updateState {
                            copy(
                                patientLocation = location,
                                isAddressLoading = false,
                            )
                        }
                        if (copyAddressWhenResolved) {
                            copyAddressWhenResolved = false
                            val address = location.address.takeIf(String::isNotBlank)
                                ?: offer.patientInfo.fullAddress()
                            if (address.isNotBlank()) {
                                sendEffect(OfferDetailsEffect.CopyToClipboard(address))
                            } else {
                                sendEffect(
                                    OfferDetailsEffect.ShowError(
                                        UiText.StringResource(R.string.patient_address_unavailable)
                                    )
                                )
                            }
                        }
                    }
                }
                .onFailure {
                    if (currentState.offer?.offerId == offer.offerId) {
                        updateState { copy(isAddressLoading = false) }
                        if (copyAddressWhenResolved) {
                            copyAddressWhenResolved = false
                            withBestAddress {
                                sendEffect(OfferDetailsEffect.CopyToClipboard(it))
                            }
                        }
                    }
                }
        }
    }

    override fun onCleared() {
        socketJob?.cancel()
        geocodingJob?.cancel()
        loadedRequestId?.let { id ->
            viewModelScope.launch {
                nurseSocketClient.unsubscribeFromReservation(id)
            }
        }
        super.onCleared()
    }
}
