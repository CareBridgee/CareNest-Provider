package com.carenest.request.presentation.ui.patientsummary

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.request.domain.model.PatientMedicalSummary
import com.carenest.request.presentation.ui.patientsummary.components.AllergiesCard
import com.carenest.request.presentation.ui.patientsummary.components.EmergencyContactsCard
import com.carenest.request.presentation.ui.patientsummary.components.MedicalConditionsCard
import com.carenest.request.presentation.ui.patientsummary.components.MedicalHistoryCard
import com.carenest.request.presentation.ui.patientsummary.components.MedicationsCard
import com.carenest.request.presentation.ui.patientsummary.components.MobilityCareNotesCard
import com.carenest.request.presentation.ui.patientsummary.components.PatientHeaderCard
import com.carenest.request.presentation.ui.patientsummary.components.PersonalInformationCard

@Composable
fun PatientSummaryScreen(
    requestId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PatientSummaryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(requestId) {
        viewModel.onIntent(PatientSummaryIntent.Load(requestId))
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            PatientSummaryEffect.NavigateBack -> onBack()
            is PatientSummaryEffect.InitiateCall -> {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${effect.phoneNumber}")
                }
                context.startActivity(intent)
            }
        }
    }

    PatientSummaryContent(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier,
    )
}

@Composable
private fun PatientSummaryContent(
    state: PatientSummaryUiState,
    onIntent: (PatientSummaryIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val patient = state.patient ?: PatientMedicalSummary()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Theme.colors.backGround,
        topBar = {
            CareNestTopBar(
                title = stringResource(R.string.patient_summary_title),
                leading = TopBarLeading.Back { onIntent(PatientSummaryIntent.BackClicked) },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
            ) {
                PatientHeaderCard(patient = patient)

                PersonalInformationCard(patient = patient)

                MedicalConditionsCard(conditions = patient.medicalConditions)

                AllergiesCard(allergies = patient.allergies)

                MedicationsCard(medications = patient.medications)

                MobilityCareNotesCard(
                    mobilityStatus = patient.mobilityStatus,
                    mobilityNotes = patient.mobilityNotes,
                )

                MedicalHistoryCard(
                    medicalHistory = patient.medicalHistory,
                    previousSurgeries = patient.previousSurgeries,
                    previousHospitalizations = patient.previousHospitalizations,
                )

                EmergencyContactsCard(
                    contacts = patient.emergencyContacts,
                    onCallClick = { phone ->
                        onIntent(PatientSummaryIntent.CallEmergencyContact(phone))
                    },
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1200)
@Composable
private fun PatientSummaryScreenPreview() {
    com.carenest.provider.designsystem.theme.SpTheme {
        PatientSummaryContent(
            state = PatientSummaryUiState(
                isLoading = false,
                patient = PatientMedicalSummary(
                    profileId = "pat-123",
                    firstName = "Eleanor",
                    lastName = "Vance",
                    profileImageUrl = "",
                    dateOfBirth = "1952-05-15",
                    gender = "Female",
                    bloodType = "O+",
                    height = 165.0,
                    weight = 68.0,
                    mobilityStatus = "Assisted",
                    mobilityNotes = "Uses a walker for long distances, requires minor assistance when standing from a seated position.",
                    previousSurgeries = "Appendectomy",
                    previousHospitalizations = "Pneumonia",
                    allergies = listOf("Penicillin", "Peanuts"),
                    medicalConditions = listOf("Diabetes", "Hypertension", "Heart disease"),
                    medications = listOf("Metformin", "Lisinopril", "Aspirin"),
                    medicalHistory = listOf(
                        com.carenest.request.domain.model.MedicalHistoryItem(
                            type = "2021 • Previous Surgery",
                            description = "Appendectomy",
                        ),
                        com.carenest.request.domain.model.MedicalHistoryItem(
                            type = "2022 • Hospitalization",
                            description = "Hospitalized for pneumonia",
                        ),
                    ),
                    emergencyContacts = listOf(
                        com.carenest.request.domain.model.EmergencyContactItem(
                            name = "Sarah Vance",
                            relationship = "Daughter",
                            phoneNumber = "+1 555 123 4567",
                        ),
                    ),
                ),
            ),
            onIntent = {},
        )
    }
}
