package com.carenest.provider.profile.presentation.ui.registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.datastore.AuthenticationSession
import com.carenest.provider.core.datastore.AuthenticationSessionDestination
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.toast.ToastType
import com.carenest.provider.profile.data.file.ContentUriFileReader
import com.carenest.provider.profile.data.local.AttachmentDraft
import com.carenest.provider.profile.data.local.RegistrationDraft
import com.carenest.provider.profile.data.local.RegistrationDraftStore
import com.carenest.provider.profile.domain.model.NurseRegistration
import com.carenest.provider.profile.domain.model.RegistrationSubmission
import com.carenest.provider.profile.domain.model.UserUpdate
import com.carenest.provider.profile.domain.model.VerificationStatus
import com.carenest.provider.profile.domain.usecase.LoadServiceTypesUseCase
import com.carenest.provider.profile.domain.usecase.SubmitRegistrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegistrationViewmodel @Inject constructor(
    private val loadServiceTypes: LoadServiceTypesUseCase,
    private val submitRegistration: SubmitRegistrationUseCase,
    private val fileReader: ContentUriFileReader,
    private val draftStore: RegistrationDraftStore,
    private val authenticationSessionStore: AuthenticationSessionStore,
) : ViewModel(),
    StateHolder<RegistrationUiState> by DefaultStateHolder(RegistrationUiState()),
    EffectPublisher<RegistrationEffect> by DefaultEffectPublisher() {

    init {
        updateState {
            copy(
                stepperState = StepperState(4, 3, "step_personal_info"),
            )
        }
        viewModelScope.launch {
            val draft = draftStore.draft.first()
            if (draft != null) restoreDraft(draft)
            loadServices(draft?.selectedServiceIds.orEmpty())
        }
    }

    fun onIntent(intent: RegistrationIntent) {
        when (intent) {
            is RegistrationIntent.OnFirstNameChanged -> updatePersonal {
                copy(firstName = PersonalInfoValidation.sanitizeName(intent.firstName))
            }
            is RegistrationIntent.OnLastNameChanged -> updatePersonal {
                copy(lastName = PersonalInfoValidation.sanitizeName(intent.lastName))
            }
            is RegistrationIntent.OnNationalIdChanged -> updatePersonal {
                val normalizedId = PersonalInfoValidation.normalizeNationalId(intent.nationalId)
                copy(
                    nationalId = normalizedId,
                    dateOfBirth = PersonalInfoValidation.extractPastDateOfBirth(normalizedId).orEmpty(),
                )
            }
            is RegistrationIntent.OnGenderChanged -> updatePersonal { copy(gender = intent.gender) }
            is RegistrationIntent.OnProfilePhotoPicked -> updatePersonal {
                copy(profilePhoto = intent.attachment)
            }
            RegistrationIntent.OnGenderClick -> sendEffect(RegistrationEffect.OpenGenderSelection)
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
                copy(yearsOfExp = intent.years.toIntOrNull())
            }
            is RegistrationIntent.OnPrimarySpecialityChanged -> updateDocuments {
                copy(primarySpeciality = intent.speciality)
            }
            is RegistrationIntent.OnServiceToggle -> {
                updateState {
                    val selected = servicesUiState.selectedServices
                    val updated = if (selected.any { it.id == intent.service.id }) {
                        selected.filterNot { it.id == intent.service.id }
                    } else selected + intent.service
                    copy(servicesUiState = servicesUiState.copy(selectedServices = updated))
                }
                persistDraft()
            }
            is RegistrationIntent.OnCertificationToggle -> {
                updateState {
                    copy(applicationReviewUiState = applicationReviewUiState.copy(isCertified = intent.isCertified))
                }
                persistDraft()
            }
            is RegistrationIntent.OnContinueClicked -> {
                if (validateCurrentStep(intent.currentPage)) {
                    updateStepperState(intent.currentPage + 1)
                    updateState { copy(currentPage = (intent.currentPage + 1).coerceAtMost(3)) }
                    persistDraft()
                    sendEffect(RegistrationEffect.NavigateToNextStep)
                }
            }
            is RegistrationIntent.OnPageChanged -> {
                val page = intent.page.coerceIn(0, 3)
                updateState { copy(currentPage = page) }
                updateStepperState(page)
                persistDraft()
            }
            RegistrationIntent.OnBackClicked -> {
                if (currentState.currentPage > 0) {
                    updateState { copy(currentPage = currentPage - 1) }
                    updateStepperState(currentState.currentPage)
                    persistDraft()
                }
                sendEffect(RegistrationEffect.NavigateToPreviousStep)
            }
            RegistrationIntent.OnSubmitApplication -> submit()
            RegistrationIntent.OnRetryServices -> loadServices()
        }
    }

    private fun loadServices(selectedServiceIds: List<String> = currentState.servicesUiState.selectedServices.map { it.id }) {
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
                                selectedServices = services
                                    .filter { it.id in selectedServiceIds }
                                    .map { ServiceUi(R.drawable.ic_services, it.name, it.id, it.description) },
                                errorMessage = null,
                            )
                        )
                    }
                },
                onFailure = {
                    updateState {
                        copy(
                            servicesUiState = servicesUiState.copy(
                                isLoading = false,
                                errorMessage = "error_services_unavailable",
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
                    draftStore.clear()
                    val authenticationState = authenticationSessionStore.state.first()
                    val currentSession = authenticationState.session
                    val credentials = authenticationState.credentials
                    if (credentials == null) {
                        authenticationSessionStore.clearSession()
                        val message = "Unable to initialize the authenticated session"
                        updateState { copy(isSubmitting = false, errorMessage = message) }
                        sendEffect(RegistrationEffect.ShowMessage(message))
                        return@fold
                    }
                    val completed = authenticationSessionStore.completeAuthentication(
                        expectedCredentials = credentials,
                        session = nurse.toSavedSession(currentSession?.phoneNumber),
                    )
                    if (!completed) {
                        authenticationSessionStore.clearSession()
                        val message = "Unable to initialize the authenticated session"
                        updateState { copy(isSubmitting = false, errorMessage = message) }
                        sendEffect(RegistrationEffect.ShowMessage(message))
                        return@fold
                    }
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
                dateOfBirth = PersonalInfoValidation.toBackendDate(personal.dateOfBirth),
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
                yearsOfExperience = requireNotNull(docs.yearsOfExp),
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
                    personal.firstName.isBlank() -> "error_first_name_required"
                    !PersonalInfoValidation.isValidName(personal.firstName) -> "error_first_name_invalid"
                    personal.lastName.isBlank() -> "error_last_name_required"
                    !PersonalInfoValidation.isValidName(personal.lastName) -> "error_last_name_invalid"
                    PersonalInfoValidation.nationalIdValidationError(personal.nationalId) != null ->
                        when (PersonalInfoValidation.nationalIdValidationError(personal.nationalId)) {
                            NationalIdValidationError.REQUIRED -> "error_national_id_required"
                            NationalIdValidationError.INVALID_LENGTH -> "error_national_id_length"
                            NationalIdValidationError.INVALID_NATIONAL_ID -> "error_invalid_national_id"
                            NationalIdValidationError.INVALID_DATE_OF_BIRTH -> "error_national_id_dob_invalid"
                            NationalIdValidationError.FUTURE_DATE_OF_BIRTH -> "error_dob_must_be_past"
                            null -> null
                        }
                    personal.gender == Gender.UNKNOWN -> "error_gender_required"
                    personal.profilePhotoUri == null -> "error_profile_photo_required"
                    else -> null
                }
            }
            1 -> {
                val docs = state.verificationDocumentsUiState
                when {
                    docs.nationalIdFront == null || docs.nationalIdBack == null ||
                    docs.licenseNumber.isBlank() || docs.nursingLicense == null ||
                    docs.professionalCertificate == null || docs.yearsOfExp == null ||
                    docs.primarySpeciality.isBlank()
                    -> "error_upload_all_docs"
                    !VerificationDocumentsValidation.isValidLicenseNumber(docs.licenseNumber) ->
                        "error_license_number_invalid"
                    !VerificationDocumentsValidation.isValidYearsOfExperience(docs.yearsOfExp) ->
                        "error_years_of_exp_range"
                    !VerificationDocumentsValidation.isValidPrimarySpeciality(docs.primarySpeciality) ->
                        "error_primary_speciality_invalid"
                    else -> null
                }
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
        persistDraft()
    }

    private fun updateDocuments(transform: VerificationDocumentsUiState.() -> VerificationDocumentsUiState) {
        updateState { copy(verificationDocumentsUiState = verificationDocumentsUiState.transform()) }
        persistDraft()
    }

    private fun updateStepperState(page: Int) {
        val title = listOf("step_personal_info", "step_documents", "step_services", "step_review")
            .getOrElse(page) { "" }
        updateState { copy(stepperState = stepperState.copy(currentStepTitle = title)) }
    }

    private fun restoreDraft(draft: RegistrationDraft) {
        val page = draft.currentPage.coerceIn(0, 3)
        val nationalId = PersonalInfoValidation.normalizeNationalId(draft.nationalId)
        updateState {
            copy(
                currentPage = page,
                personalInfoState = PersonalInfoState(
                    firstName = PersonalInfoValidation.sanitizeName(draft.firstName),
                    lastName = PersonalInfoValidation.sanitizeName(draft.lastName),
                    dateOfBirth = PersonalInfoValidation.extractPastDateOfBirth(nationalId).orEmpty(),
                    nationalId = nationalId,
                    gender = runCatching { Gender.valueOf(draft.gender) }.getOrDefault(Gender.UNKNOWN),
                    profilePhoto = draft.profilePhoto?.toAttachment(),
                ),
                verificationDocumentsUiState = VerificationDocumentsUiState(
                    nationalIdFront = draft.nationalIdFront?.toAttachment(),
                    nationalIdBack = draft.nationalIdBack?.toAttachment(),
                    licenseNumber = draft.licenseNumber,
                    nursingLicense = draft.nursingLicense?.toAttachment(),
                    professionalCertificate = draft.professionalCertificate?.toAttachment(),
                    yearsOfExp = draft.yearsOfExp,
                    primarySpeciality = draft.primarySpeciality,
                ),
                applicationReviewUiState = ApplicationReviewUiState(
                    isCertified = draft.isCertified,
                ),
            )
        }
        updateStepperState(page)
    }

    private fun persistDraft() {
        val snapshot = currentState.toDraft()
        viewModelScope.launch { draftStore.save(snapshot) }
    }
}

private fun RegistrationUiState.toDraft() = RegistrationDraft(
    currentPage = currentPage,
    firstName = personalInfoState.firstName,
    lastName = personalInfoState.lastName,
    dateOfBirth = personalInfoState.dateOfBirth,
    nationalId = personalInfoState.nationalId,
    gender = personalInfoState.gender.name,
    profilePhoto = personalInfoState.profilePhoto?.toDraft(),
    nationalIdFront = verificationDocumentsUiState.nationalIdFront?.toDraft(),
    nationalIdBack = verificationDocumentsUiState.nationalIdBack?.toDraft(),
    licenseNumber = verificationDocumentsUiState.licenseNumber,
    nursingLicense = verificationDocumentsUiState.nursingLicense?.toDraft(),
    professionalCertificate = verificationDocumentsUiState.professionalCertificate?.toDraft(),
    yearsOfExp = verificationDocumentsUiState.yearsOfExp,
    primarySpeciality = verificationDocumentsUiState.primarySpeciality,
    selectedServiceIds = servicesUiState.selectedServices.map { it.id },
    isCertified = applicationReviewUiState.isCertified,
)

private fun Attachment.toDraft() = AttachmentDraft(uri.toString(), name, mimeType)

private fun AttachmentDraft.toAttachment() = Attachment(Uri.parse(uri), name, mimeType)

private fun com.carenest.provider.profile.domain.model.NurseProfile.toSavedSession(
    phoneNumber: String?,
) =
    AuthenticationSession(
        destination = when (verificationStatus) {
            VerificationStatus.UNDER_REVIEW -> AuthenticationSessionDestination.UNDER_REVIEW
            VerificationStatus.APPROVED -> AuthenticationSessionDestination.APPROVED
            VerificationStatus.REJECTED -> AuthenticationSessionDestination.REJECTED
        },
        nurseId = id,
        phoneNumber = phoneNumber,
        profileImageUrl = profileImageUrl,
    )

private fun Throwable.userMessage(): String {
    val rawMessage = message?.trim().orEmpty()
    return when {
        rawMessage.isBlank() -> "error_unknown"
        rawMessage.contains("\"dateOfBirth\"", ignoreCase = true) &&
            rawMessage.contains("must be a past date", ignoreCase = true) ->
            "error_dob_must_be_past"
        rawMessage.contains("VALIDATION_FAILED", ignoreCase = true) ||
            rawMessage.trimStart().startsWith("{") ->
            "error_unknown"
        else -> rawMessage
    }
}
