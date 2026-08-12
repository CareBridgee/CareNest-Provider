package com.carenest.request.presentation.ui.patientsummary

import com.carenest.request.domain.model.PatientMedicalSummary

data class PatientSummaryUiState(
    val isLoading: Boolean = false,
    val patient: PatientMedicalSummary? = null,
)

sealed interface PatientSummaryIntent {
    data class Load(val requestId: String) : PatientSummaryIntent
    data object BackClicked : PatientSummaryIntent
    data class CallEmergencyContact(val phoneNumber: String) : PatientSummaryIntent
}

sealed interface PatientSummaryEffect {
    data object NavigateBack : PatientSummaryEffect
    data class InitiateCall(val phoneNumber: String) : PatientSummaryEffect
}
