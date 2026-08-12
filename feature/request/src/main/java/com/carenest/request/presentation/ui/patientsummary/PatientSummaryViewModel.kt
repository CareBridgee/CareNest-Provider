package com.carenest.request.presentation.ui.patientsummary

import androidx.lifecycle.ViewModel
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PatientSummaryViewModel @Inject constructor() : ViewModel(),
    StateHolder<PatientSummaryUiState> by DefaultStateHolder(PatientSummaryUiState()),
    EffectPublisher<PatientSummaryEffect> by DefaultEffectPublisher() {

    private var loadedRequestId: String? = null

    fun onIntent(intent: PatientSummaryIntent) {
        when (intent) {
            is PatientSummaryIntent.Load -> {
                if (loadedRequestId == intent.requestId) return
                loadedRequestId = intent.requestId
                // Prepared for real API / repository fetch integration
            }

            PatientSummaryIntent.BackClicked -> {
                sendEffect(PatientSummaryEffect.NavigateBack)
            }

            is PatientSummaryIntent.CallEmergencyContact -> {
                sendEffect(PatientSummaryEffect.InitiateCall(intent.phoneNumber))
            }
        }
    }
}
