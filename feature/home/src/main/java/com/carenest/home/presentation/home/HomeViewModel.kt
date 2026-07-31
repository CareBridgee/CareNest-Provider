package com.carenest.home.presentation.home

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
    private val getNurseProfile: GetNurseProfileUseCase,
) : ViewModel(),
    StateHolder<HomeUiState> by DefaultStateHolder(HomeUiState()),
    EffectPublisher<HomeEffect> by DefaultEffectPublisher() {

    private var fetchJob: Job? = null
    private var offerTimerJob: Job? = null

    init {
       getNurseData()
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
        offerTimerJob?.cancel()

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
            fetchJob = viewModelScope.launch {
                coroutineScope {
                    val requestsDeferred = async { getIncomingRequests() }
                    val earningsDeferred = async { getEarningsSummary() }

                    val requestsResult = requestsDeferred.await()
                    val earningsSummary = earningsDeferred.await().getOrNull()

                    updateState {
                        copy(
                            isLoading = false,
                            requests = requestsResult.getOrDefault(emptyList()),
                            earnings = earningsSummary?.todayEarnings ?: earnings,
                            changePercent = earningsSummary?.changePercent ?: changePercent,
                            jobsToday = earningsSummary?.jobsToday ?: jobsToday,
                            rating = earningsSummary?.rating ?: rating,
                        )
                    }
                }
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
        val (willAccept, acceptAtSecond) = sendOfferToPatient(requestId)

        offerTimerJob?.cancel()
        updateState {
            copy(
                activeModal = ActiveModal.MakeOffer,
                offerRequestId = requestId,
                offerCountdown = OFFER_TIMEOUT_SECONDS,
                offerWillAccept = willAccept,
                offerAcceptAtSecond = acceptAtSecond,
                selectedCardId = requestId,
            )
        }

        offerTimerJob = viewModelScope.launch {
            for (elapsedSecond in 1..OFFER_TIMEOUT_SECONDS) {
                delay(1_000)

                if (willAccept && elapsedSecond == acceptAtSecond) {
                    updateState { copy(activeModal = ActiveModal.OfferSuccess) }
                    delay(SUCCESS_DISPLAY_MS)
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
        sendEffect(HomeEffect.NavigateToOfferConfirmed(requestId))
    }

    private fun completeOfferTimeout(requestId: String) {
        offerTimerJob?.cancel()
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
        if (currentState.activeModal == ActiveModal.MakeOffer) {
            offerTimerJob?.cancel()
        }
        updateState {
            copy(
                activeModal = ActiveModal.None,
                editingRequestId = null,
                offerRequestId = null,
                offerCountdown = null,
            )
        }
    }

    override fun onCleared() {
        fetchJob?.cancel()
        offerTimerJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val OFFER_TIMEOUT_SECONDS = 10
        const val SUCCESS_DISPLAY_MS = 1_200L
    }
}
