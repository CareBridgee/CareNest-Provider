package com.carenest.provider.profile.presentation.ui.registration

import androidx.lifecycle.ViewModel
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.designsystem.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RegistrationViewmodel @Inject constructor(

) :
    ViewModel(),
    StateHolder<RegistrationUiState> by DefaultStateHolder(RegistrationUiState()),
    EffectPublisher<RegistrationEffect> by DefaultEffectPublisher() {

    init {
        updateState {
            copy(
                stepperState = StepperState(
                    totalSteps = 4,
                    remainingSteps = 3,
                    currentStepTitle = "Personal Info"
                ),
                servicesUiState = servicesUiState.copy(
                    availableServices = listOf(
                        // I will remove them once we get a services response from the server, this is for testing only
                        ServiceUi(R.drawable.ic_syringe, "Injection"),
                        ServiceUi(R.drawable.ic_pill, "IV Therapy"),
                        ServiceUi(R.drawable.ic_category_sales, "Blood Collection"),
                        ServiceUi(R.drawable.ic_check, "Wound Dressing"),
                        ServiceUi(R.drawable.ic_assignment, "Catheter Care"),
                        ServiceUi(R.drawable.ic_elderly, "Elderly Care"),
                        ServiceUi(R.drawable.ic_category_kids, "Child Care"),
                        ServiceUi(R.drawable.ic_profile, "Post-Surgery Care"),
                        ServiceUi(R.drawable.ic_category_women, "Maternal Care"),
                        ServiceUi(R.drawable.ic_physical_therapy, "Physiotherapy"),
                        ServiceUi(R.drawable.ic_heart_beat, "ECG Service"),
                        ServiceUi(R.drawable.ic_home, "Home Assessment")
                    )
                )
            )
        }
    }

    fun onIntent(intent: RegistrationIntent) {
        when (intent) {
            is RegistrationIntent.OnFirstNameChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(firstName = intent.firstName))
            }
            is RegistrationIntent.OnLastNameChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(lastName = intent.lastName))
            }
            is RegistrationIntent.OnDateOfBirthChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(dateOfBirth = intent.dateOfBirth))
            }
            is RegistrationIntent.OnNationalIdChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(nationalId = intent.nationalId))
            }
            is RegistrationIntent.OnGenderChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(gender = intent.gender))
            }
            is RegistrationIntent.OnProfilePhotoPicked -> updateState {
                copy(personalInfoState = personalInfoState.copy(profilePhotoUri = intent.uri))
            }
            is RegistrationIntent.OnNationalIdDocumentPicked -> updateState {
                copy(verificationDocumentsUiState = verificationDocumentsUiState.copy(nationalId = intent.attachment))
            }
            is RegistrationIntent.OnNursingLicensePicked -> updateState {
                copy(verificationDocumentsUiState = verificationDocumentsUiState.copy(nursingLicense = intent.attachment))
            }
            is RegistrationIntent.OnProfessionalCertificatePicked -> updateState {
                copy(verificationDocumentsUiState = verificationDocumentsUiState.copy(professionalCertificate = intent.attachment))
            }
            is RegistrationIntent.OnYearsOfExpChanged -> updateState {
                copy(verificationDocumentsUiState = verificationDocumentsUiState.copy(yearsOfExp = intent.years.toIntOrNull() ?: 0))
            }
            is RegistrationIntent.OnPrimarySpecialityChanged -> updateState {
                copy(verificationDocumentsUiState = verificationDocumentsUiState.copy(primarySpeciality = intent.speciality))
            }
            is RegistrationIntent.OnServiceToggle -> updateState {
                val currentSelected = servicesUiState.selectedServices
                val newSelected = if (currentSelected.contains(intent.service)) {
                    currentSelected - intent.service
                } else {
                    currentSelected + intent.service
                }
                copy(servicesUiState = servicesUiState.copy(selectedServices = newSelected))
            }
            is RegistrationIntent.OnCertificationToggle -> updateState {
                copy(applicationReviewUiState = applicationReviewUiState.copy(isCertified = intent.isCertified))
            }
            RegistrationIntent.OnContinueClicked -> {
                sendEffect(RegistrationEffect.NavigateToNextStep)
            }
            RegistrationIntent.OnSubmitApplication -> {
                // Handle application submission
            }
        }
    }


}