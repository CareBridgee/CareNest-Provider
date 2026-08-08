package com.carenest.provider.profile.presentation.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.toast.ToastType
import com.carenest.provider.profile.data.file.ContentUriFileReader
import com.carenest.provider.profile.domain.model.NurseRegistration
import com.carenest.provider.profile.domain.model.RegistrationSubmission
import com.carenest.provider.profile.domain.model.UserUpdate
import com.carenest.provider.profile.domain.usecase.LoadServiceTypesUseCase
import com.carenest.provider.profile.domain.usecase.SubmitRegistrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegistrationViewmodel @Inject constructor(
    private val loadServiceTypes: LoadServiceTypesUseCase,
    private val submitRegistration: SubmitRegistrationUseCase,
    private val fileReader: ContentUriFileReader,
) : ViewModel(),
    StateHolder<RegistrationUiState> by DefaultStateHolder(RegistrationUiState()),
    EffectPublisher<RegistrationEffect> by DefaultEffectPublisher() {

    init {
        updateState {
            copy(
                stepperState = StepperState(4, 3, "step_personal_info"),
            )
        }
        loadServices()
    }

    fun onIntent(intent: RegistrationIntent) {
        when (intent) {
            is RegistrationIntent.OnFirstNameChanged -> updatePersonal { copy(firstName = intent.firstName) }
            is RegistrationIntent.OnLastNameChanged -> updatePersonal { copy(lastName = intent.lastName) }
            is RegistrationIntent.OnEmailChanged -> updatePersonal { copy(email = intent.email) }
            is RegistrationIntent.OnLocationChanged -> updatePersonal { copy(location = intent.location) }
            is RegistrationIntent.OnDateOfBirthChanged -> updatePersonal { copy(dateOfBirth = intent.dateOfBirth) }
            is RegistrationIntent.OnNationalIdChanged -> updatePersonal { copy(nationalId = intent.nationalId) }
            is RegistrationIntent.OnGenderChanged -> updatePersonal { copy(gender = intent.gender) }
            is RegistrationIntent.OnProfilePhotoPicked -> updatePersonal {
                copy(profilePhoto = intent.attachment)
            }
            RegistrationIntent.OnGenderClick -> sendEffect(RegistrationEffect.OpenGenderSelection)
            RegistrationIntent.OnDateOfBirthClick -> sendEffect(RegistrationEffect.ShowCalendar)
            RegistrationIntent.OnProfilePhotoClick -> sendEffect(RegistrationEffect.OpenProfilePhotoPicker)
            RegistrationIntent.OnNationalIdFrontClick -> sendEffect(RegistrationEffect.OpenNationalIdFrontPicker)
            RegistrationIntent.OnNationalIdBackClick -> sendEffect(RegistrationEffect.OpenNationalIdBackPicker)
            RegistrationIntent.OnNursingLicenseClick -> sendEffect(RegistrationEffect.OpenLicensePicker)
            RegistrationIntent.OnProfessionalCertificateClick -> sendEffect(RegistrationEffect.OpenCertificatePicker)
            RegistrationIntent.OnRemoveNationalIdFront -> updateDocuments { copy(nationalIdFront = null) }
            RegistrationIntent.OnRemoveNationalIdBack -> updateDocuments { copy(nationalIdBack = null) }
            RegistrationIntent.OnRemoveNursingLicense -> updateDocuments { copy(nursingLicense = null) }
            RegistrationIntent.OnRemoveProfessionalCertificate -> updateDocuments { copy(professionalCertificate = null) }
            is RegistrationIntent.OnNationalIdFrontPicked -> updateDocuments { copy(nationalIdFront = intent.attachment) }
            is RegistrationIntent.OnNationalIdBackPicked -> updateDocuments { copy(nationalIdBack = intent.attachment) }
            is RegistrationIntent.OnNursingLicensePicked -> updateDocuments { copy(nursingLicense = intent.attachment) }
            is RegistrationIntent.OnProfessionalCertificatePicked -> updateDocuments {
                copy(professionalCertificate = intent.attachment)
            }
            is RegistrationIntent.OnLicenseNumberChanged -> updateDocuments {
                copy(licenseNumber = intent.licenseNumber)
            }
            is RegistrationIntent.OnYearsOfExpChanged -> updateDocuments {
                copy(yearsOfExp = intent.years.toIntOrNull() ?: 0)
            }
            is RegistrationIntent.OnPrimarySpecialityChanged -> updateDocuments {
                copy(primarySpeciality = intent.speciality)
            }
            is RegistrationIntent.OnServiceToggle -> updateState {
                val selected = servicesUiState.selectedServices
                val updated = if (selected.any { it.id == intent.service.id }) {
                    selected.filterNot { it.id == intent.service.id }
                } else selected + intent.service
                copy(servicesUiState = servicesUiState.copy(selectedServices = updated))
            }
            is RegistrationIntent.OnCertificationToggle -> updateState {
                copy(applicationReviewUiState = applicationReviewUiState.copy(isCertified = intent.isCertified))
            }
            is RegistrationIntent.OnContinueClicked -> {
                if (validateCurrentStep(intent.currentPage)) {
                    updateStepperState(intent.currentPage + 1)
                    sendEffect(RegistrationEffect.NavigateToNextStep)
                }
            }
            RegistrationIntent.OnBackClicked -> sendEffect(RegistrationEffect.NavigateToPreviousStep)
            RegistrationIntent.OnSubmitApplication -> submit()
            RegistrationIntent.OnRetryServices -> loadServices()
        }
    }

    private fun loadServices() {
        if (currentState.servicesUiState.isLoading) return
        viewModelScope.launch {
            updateState {
                copy(servicesUiState = servicesUiState.copy(isLoading = true, errorMessage = null))
            }
            loadServiceTypes().fold(
                onSuccess = { services ->
                    updateState {
                        copy(
                            servicesUiState = servicesUiState.copy(
                                isLoading = false,
                                availableServices = services.map {
                                    ServiceUi(R.drawable.ic_services, it.name, it.id, it.description)
                                },
                                errorMessage = null,
                            )
                        )
                    }
                },
                onFailure = { error ->
                    updateState {
                        copy(
                            servicesUiState = servicesUiState.copy(
                                isLoading = false,
                                errorMessage = error.userMessage(),
                            )
                        )
                    }
                },
            )
        }
    }

    private fun submit() {
        if (currentState.isSubmitting || !validateCurrentStep(3)) return
        val snapshot = currentState
        viewModelScope.launch {
            updateState { copy(isSubmitting = true, errorMessage = null) }
            val submission = runCatching { withContext(Dispatchers.IO) { snapshot.toSubmission() } }
                .getOrElse { error ->
                    updateState { copy(isSubmitting = false, errorMessage = error.userMessage()) }
                    sendEffect(RegistrationEffect.ShowMessage(error.userMessage()))
                    return@launch
                }

            submitRegistration(submission).fold(
                onSuccess = { nurse ->
                    updateState { copy(isSubmitting = false) }
                    sendEffect(RegistrationEffect.SubmissionSucceeded(nurse.id, nurse.verificationStatus))
                },
                onFailure = { error ->
                    val message = error.userMessage()
                    updateState { copy(isSubmitting = false, errorMessage = message) }
                    sendEffect(RegistrationEffect.ShowMessage(message))
                },
            )
        }
    }

    private fun RegistrationUiState.toSubmission(): RegistrationSubmission {
        val personal = personalInfoState
        val docs = verificationDocumentsUiState
        return RegistrationSubmission(
            user = UserUpdate(
                firstName = personal.firstName.trim(),
                lastName = personal.lastName.trim(),
                email = personal.email.trim().takeIf(String::isNotEmpty),
                dateOfBirth = personal.dateOfBirth.toBackendDate(),
                gender = personal.gender.name,
                profileImage = fileReader.readAttachment(requireNotNull(personal.profilePhoto)),
            ),
            nurse = NurseRegistration(
                nationalId = personal.nationalId,
                licenseNumber = docs.licenseNumber.trim(),
                nationalIdFront = fileReader.readAttachment(requireNotNull(docs.nationalIdFront)),
                nationalIdBack = fileReader.readAttachment(requireNotNull(docs.nationalIdBack)),
                licenseImage = fileReader.readAttachment(requireNotNull(docs.nursingLicense)),
                professionalCertificate = fileReader.readAttachment(requireNotNull(docs.professionalCertificate)),
                specialization = docs.primarySpeciality.trim(),
                yearsOfExperience = docs.yearsOfExp,
            ),
            serviceTypeIds = servicesUiState.selectedServices.map { it.id },
        )
    }

    private fun ContentUriFileReader.readAttachment(attachment: Attachment) =
        read(attachment.uri, attachment.name, attachment.mimeType)

    private fun validateCurrentStep(page: Int): Boolean {
        val state = currentState
        val message = when (page) {
            0 -> {
                val personal = state.personalInfoState
                when {
                    personal.firstName.isBlank() || personal.lastName.isBlank() ||
                        personal.dateOfBirth.isBlank() || personal.nationalId.isBlank() ||
                        personal.gender == Gender.UNKNOWN || personal.profilePhotoUri == null ->
                        "error_fill_all_fields"
                    !validateNidWithDob(personal.nationalId, personal.dateOfBirth) ->
                        "error_nid_dob_mismatch"
                    else -> null
                }
            }
            1 -> {
                val docs = state.verificationDocumentsUiState
                if (docs.nationalIdFront == null || docs.nationalIdBack == null ||
                    docs.licenseNumber.isBlank() || docs.nursingLicense == null ||
                    docs.professionalCertificate == null || docs.yearsOfExp <= 0 ||
                    docs.primarySpeciality.isBlank()
                ) "error_upload_all_docs" else null
            }
            2 -> when {
                state.servicesUiState.isLoading -> "services_loading"
                state.servicesUiState.errorMessage != null -> "error_services_unavailable"
                state.servicesUiState.selectedServices.isEmpty() -> "error_select_service"
                else -> null
            }
            3 -> if (!state.applicationReviewUiState.isCertified) "error_certify_information" else null
            else -> null
        }
        if (message != null) sendEffect(RegistrationEffect.ShowMessage(message, ToastType.Error))
        return message == null
    }

    private fun updatePersonal(transform: PersonalInfoState.() -> PersonalInfoState) {
        updateState { copy(personalInfoState = personalInfoState.transform()) }
    }

    private fun updateDocuments(transform: VerificationDocumentsUiState.() -> VerificationDocumentsUiState) {
        updateState { copy(verificationDocumentsUiState = verificationDocumentsUiState.transform()) }
    }

    private fun updateStepperState(page: Int) {
        val title = listOf("step_personal_info", "step_documents", "step_services", "step_review")
            .getOrElse(page) { "" }
        updateState { copy(stepperState = stepperState.copy(currentStepTitle = title)) }
    }

    private fun validateNidWithDob(nid: String, dob: String): Boolean {
        if (!nid.matches(Regex("\\d{14}"))) return false
        val parts = dob.split("/")
        if (parts.size != 3) return false
        val century = when (nid.first()) { '2' -> "19"; '3' -> "20"; else -> return false }
        return century + nid.substring(1, 3) == parts[2] &&
            nid.substring(3, 5) == parts[0] && nid.substring(5, 7) == parts[1]
    }
}

private fun String.toBackendDate(): String {
    val source = SimpleDateFormat("MM/dd/yyyy", Locale.US).apply { isLenient = false }
    val target = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return target.format(requireNotNull(source.parse(this)) { "Invalid date of birth" })
}

private fun Throwable.userMessage(): String = message?.takeIf(String::isNotBlank) ?: "error_unknown"
