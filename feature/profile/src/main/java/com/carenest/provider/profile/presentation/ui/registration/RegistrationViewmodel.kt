package com.carenest.provider.profile.presentation.ui.registration

import androidx.lifecycle.ViewModel
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.toast.ToastType
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
                    currentStepTitle = "step_personal_info"
                ),
                servicesUiState = servicesUiState.copy(
                    availableServices = listOf(
                        // I will remove them once we get a services response from the server, this is for testing only
                        ServiceUi(R.drawable.ic_syringe, "service_injection"),
                        ServiceUi(R.drawable.ic_pill, "service_iv_therapy"),
                        ServiceUi(R.drawable.ic_category_sales, "service_blood_collection"),
                        ServiceUi(R.drawable.ic_check, "service_wound_dressing"),
                        ServiceUi(R.drawable.ic_assignment, "service_catheter_care"),
                        ServiceUi(R.drawable.ic_elderly, "service_elderly_care"),
                        ServiceUi(R.drawable.ic_category_kids, "service_child_care"),
                        ServiceUi(R.drawable.ic_profile, "service_post_surgery_care"),
                        ServiceUi(R.drawable.ic_category_women, "service_maternal_care"),
                        ServiceUi(R.drawable.ic_physical_therapy, "service_physiotherapy"),
                        ServiceUi(R.drawable.ic_heart_beat, "service_ecg_service"),
                        ServiceUi(R.drawable.ic_home, "service_home_assessment")
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

            is RegistrationIntent.OnEmailChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(email = intent.email))
            }

            is RegistrationIntent.OnLocationChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(location = intent.location))
            }

            is RegistrationIntent.OnDateOfBirthChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(dateOfBirth = intent.dateOfBirth))
            }

            is RegistrationIntent.OnNationalIdChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(nationalId = intent.nationalId))
            }

            RegistrationIntent.OnGenderClick -> {
                sendEffect(RegistrationEffect.OpenGenderSelection)
            }

            is RegistrationIntent.OnGenderChanged -> updateState {
                copy(personalInfoState = personalInfoState.copy(gender = intent.gender))
            }

            RegistrationIntent.OnDateOfBirthClick -> {
                sendEffect(RegistrationEffect.ShowCalendar)
            }

            is RegistrationIntent.OnProfilePhotoPicked -> updateState {
                copy(personalInfoState = personalInfoState.copy(profilePhotoUri = intent.uri))
            }

            RegistrationIntent.OnProfilePhotoClick -> {
                sendEffect(RegistrationEffect.OpenProfilePhotoPicker)
            }

            RegistrationIntent.OnNationalIdClick -> {
                sendEffect(RegistrationEffect.OpenNIDPicker)
            }

            RegistrationIntent.OnNursingLicenseClick -> {
                sendEffect(RegistrationEffect.OpenLicensePicker)
            }

            RegistrationIntent.OnProfessionalCertificateClick -> {
                sendEffect(RegistrationEffect.OpenCertificatePicker)
            }

            RegistrationIntent.OnRemoveNationalId -> updateState {
                copy(verificationDocumentsUiState = verificationDocumentsUiState.copy(nationalId = null))
            }

            RegistrationIntent.OnRemoveNursingLicense -> updateState {
                copy(verificationDocumentsUiState = verificationDocumentsUiState.copy(nursingLicense = null))
            }

            RegistrationIntent.OnRemoveProfessionalCertificate -> updateState {
                copy(
                    verificationDocumentsUiState = verificationDocumentsUiState.copy(
                        professionalCertificate = null
                    )
                )
            }

            is RegistrationIntent.OnNationalIdDocumentPicked -> updateState {
                copy(verificationDocumentsUiState = verificationDocumentsUiState.copy(nationalId = intent.attachment))
            }

            is RegistrationIntent.OnNursingLicensePicked -> updateState {
                copy(verificationDocumentsUiState = verificationDocumentsUiState.copy(nursingLicense = intent.attachment))
            }

            is RegistrationIntent.OnProfessionalCertificatePicked -> updateState {
                copy(
                    verificationDocumentsUiState = verificationDocumentsUiState.copy(
                        professionalCertificate = intent.attachment
                    )
                )
            }

            is RegistrationIntent.OnYearsOfExpChanged -> updateState {
                copy(
                    verificationDocumentsUiState = verificationDocumentsUiState.copy(
                        yearsOfExp = intent.years.toIntOrNull() ?: 0
                    )
                )
            }

            is RegistrationIntent.OnPrimarySpecialityChanged -> updateState {
                copy(
                    verificationDocumentsUiState = verificationDocumentsUiState.copy(
                        primarySpeciality = intent.speciality
                    )
                )
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

            is RegistrationIntent.OnContinueClicked -> {
                if (intent.currentPage < 0) {
                    updateStepperState(intent.currentPage + 1)
                } else if (validateCurrentStep(intent.currentPage)) {
                    val nextStep = intent.currentPage + 1
                    updateStepperState(nextStep)
                    sendEffect(RegistrationEffect.NavigateToNextStep)
                }
            }

            RegistrationIntent.OnBackClicked -> {
                sendEffect(RegistrationEffect.NavigateToPreviousStep)
            }

            RegistrationIntent.OnSubmitApplication -> {
                if (validateCurrentStep(3)) {
                    sendEffect(RegistrationEffect.NavigateToNextStep)
                }
            }
        }
    }

    private fun validateCurrentStep(currentPage: Int): Boolean {
        val state = state.value
        return when (currentPage) {
            0 -> { // Personal Info
                val personalInfo = state.personalInfoState
                if (personalInfo.firstName.isEmpty() || personalInfo.lastName.isEmpty() ||
                    personalInfo.email.isEmpty() || personalInfo.location.isEmpty() ||
                    personalInfo.dateOfBirth.isEmpty() || personalInfo.nationalId.isEmpty() ||
                    personalInfo.gender == Gender.UNKNOWN || personalInfo.profilePhotoUri == null
                ) {
                    sendEffect(
                        RegistrationEffect.ShowMessage(
                            "error_fill_all_fields",
                            ToastType.Error
                        )
                    )
                    false
                } else if (!validateNidWithDob(personalInfo.nationalId, personalInfo.dateOfBirth)) {
                    sendEffect(
                        RegistrationEffect.ShowMessage(
                            "error_nid_dob_mismatch",
                            ToastType.Error
                        )
                    )
                    false
                } else {
                    true
                }
            }

            1 -> { // Verification Documents
                val docs = state.verificationDocumentsUiState
                if (docs.nationalId == null || docs.nursingLicense == null ||
                    docs.professionalCertificate == null || docs.yearsOfExp <= 0 ||
                    docs.primarySpeciality.isEmpty()
                ) {
                    sendEffect(
                        RegistrationEffect.ShowMessage(
                            "error_upload_all_docs",
                            ToastType.Error
                        )
                    )
                    false
                } else {
                    true
                }
            }

            2 -> { // Services
                if (state.servicesUiState.selectedServices.isEmpty()) {
                    sendEffect(
                        RegistrationEffect.ShowMessage(
                            "error_select_service",
                            ToastType.Error
                        )
                    )
                    false
                } else {
                    true
                }
            }

            3 -> { // Review
                if (!state.applicationReviewUiState.isCertified) {
                    sendEffect(
                        RegistrationEffect.ShowMessage(
                            "error_certify_information",
                            ToastType.Error
                        )
                    )
                    false
                } else {
                    true
                }
            }

            else -> true
        }
    }

    private fun updateStepperState(page: Int) {
        val title = when (page) {
            0 -> "step_personal_info"
            1 -> "step_documents"
            2 -> "step_services"
            3 -> "step_review"
            else -> ""
        }
        updateState {
            copy(
                stepperState = stepperState.copy(
                    currentStepTitle = title
                )
            )
        }
    }

    private fun validateNidWithDob(nid: String, dob: String): Boolean {
        if (nid.length != 14) return false
        val parts = dob.split("/")
        if (parts.size != 3) return false

        val month = parts[0]
        val day = parts[1]
        val year = parts[2]

        val nidYear = nid.substring(1, 3)
        val nidMonth = nid.substring(3, 5)
        val nidDay = nid.substring(5, 7)
        val nidCentury = nid.substring(0, 1)

        val centuryPrefix = if (nidCentury == "2") "19" else if (nidCentury == "3") "20" else ""
        val fullNidYear = centuryPrefix + nidYear

        return fullNidYear == year && nidMonth == month && nidDay == day
    }


}
