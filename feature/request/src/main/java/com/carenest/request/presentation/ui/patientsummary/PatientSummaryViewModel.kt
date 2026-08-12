package com.carenest.request.presentation.ui.patientsummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.request.domain.usecase.GetPatientSummaryUseCase
import com.carenest.request.presentation.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientSummaryViewModel @Inject constructor(
    private val getPatientSummary: GetPatientSummaryUseCase,
) : ViewModel(),
    StateHolder<PatientSummaryUiState> by DefaultStateHolder(PatientSummaryUiState()),
    EffectPublisher<PatientSummaryEffect> by DefaultEffectPublisher() {

    private var loadedRequestId: String? = null

    fun onIntent(intent: PatientSummaryIntent) {
        when (intent) {
            is PatientSummaryIntent.Load -> loadPatientSummary(intent.requestId)

            PatientSummaryIntent.BackClicked -> {
                sendEffect(PatientSummaryEffect.NavigateBack)
            }

            is PatientSummaryIntent.CallEmergencyContact -> {
                sendEffect(PatientSummaryEffect.InitiateCall(intent.phoneNumber))
            }
        }
    }

    private fun loadPatientSummary(requestId: String) {
        if (loadedRequestId == requestId) return
        loadedRequestId = requestId

        viewModelScope.launch {
            updateState { copy(isLoading = true, patient = null, errorMessage = null) }
            getPatientSummary(requestId)
                .onSuccess { patientSummary ->
                    updateState {
                        copy(
                            isLoading = false,
                            patient = patientSummary,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    loadedRequestId = null
                    val message = error.toUiText()
                    updateState {
                        copy(
                            isLoading = false,
                            patient = null,
                            errorMessage = message,
                        )
                    }
                    sendEffect(PatientSummaryEffect.ShowError(message))
                }
        }
    }
}
