package com.carenest.provider.profile.presentation.ui.registration

import android.net.Uri
import androidx.annotation.DrawableRes
import com.carenest.provider.designsystem.components.toast.ToastType
import com.carenest.provider.profile.domain.model.VerificationStatus

enum class Gender {
    MALE,
    FEMALE,
    UNKNOWN,
}

data class StepperState(
    val totalSteps: Int = 0,
    val remainingSteps: Int = 0,
    val currentStepTitle: String = "",
)

data class PersonalInfoState(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val nationalId: String = "",
    val gender: Gender = Gender.UNKNOWN,
    val profilePhoto: Attachment? = null,
) {
    val profilePhotoUri: Uri? get() = profilePhoto?.uri
}

data class Attachment(
    val uri: Uri,
    val name: String,
    val mimeType: String,
)

data class VerificationDocumentsUiState(
    val nationalIdFront: Attachment? = null,
    val nationalIdBack: Attachment? = null,
    val licenseNumber: String = "",
    val nursingLicense: Attachment? = null,
    val professionalCertificate: Attachment? = null,
    val yearsOfExp: Int = 0,
    val primarySpeciality: String = "",
)

data class ServiceUi(
    @DrawableRes val icon: Int,
    val title: String,
    val id: String = "",
    val description: String? = null,
)

data class ServicesUiState(
    val availableServices: List<ServiceUi> = emptyList(),
    val selectedServices: List<ServiceUi> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class ApplicationReviewUiState(
    val education: String = "",
    val isCertified: Boolean = false,
)

data class RegistrationUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val stepperState: StepperState = StepperState(),
    val personalInfoState: PersonalInfoState = PersonalInfoState(),
    val verificationDocumentsUiState: VerificationDocumentsUiState = VerificationDocumentsUiState(),
    val servicesUiState: ServicesUiState = ServicesUiState(),
    val applicationReviewUiState: ApplicationReviewUiState = ApplicationReviewUiState(),
    val currentPage: Int = 0,
    val errorMessage: String? = null,
) {
    val hasError = errorMessage != null
}

sealed interface RegistrationEffect {
    data object NavigateToNextStep : RegistrationEffect
    data object NavigateToPreviousStep : RegistrationEffect
    data object OpenProfilePhotoPicker : RegistrationEffect
    data object OpenNationalIdFrontPicker : RegistrationEffect
    data object OpenNationalIdBackPicker : RegistrationEffect
    data object OpenCertificatePicker : RegistrationEffect
    data object OpenLicensePicker : RegistrationEffect
    data object OpenGenderSelection : RegistrationEffect
    data object ShowCalendar : RegistrationEffect
    data class SubmissionSucceeded(
        val nurseId: String,
        val verificationStatus: VerificationStatus,
    ) : RegistrationEffect
    data class ShowMessage(
        val message: String,
        val type: ToastType = ToastType.Error,
    ) : RegistrationEffect
}

sealed interface RegistrationIntent {
    data object OnProfilePhotoClick : RegistrationIntent
    data object OnGenderClick : RegistrationIntent
    data object OnDateOfBirthClick : RegistrationIntent
    data object OnNationalIdFrontClick : RegistrationIntent
    data object OnNationalIdBackClick : RegistrationIntent
    data object OnNursingLicenseClick : RegistrationIntent
    data object OnProfessionalCertificateClick : RegistrationIntent
    data object OnRemoveNationalIdFront : RegistrationIntent
    data object OnRemoveNationalIdBack : RegistrationIntent
    data object OnRemoveNursingLicense : RegistrationIntent
    data object OnRemoveProfessionalCertificate : RegistrationIntent
    data object OnRetryServices : RegistrationIntent
    data class OnProfilePhotoPicked(val attachment: Attachment) : RegistrationIntent
    data class OnFirstNameChanged(val firstName: String) : RegistrationIntent
    data class OnLastNameChanged(val lastName: String) : RegistrationIntent
    data class OnDateOfBirthChanged(val dateOfBirth: String) : RegistrationIntent
    data class OnNationalIdChanged(val nationalId: String) : RegistrationIntent
    data class OnGenderChanged(val gender: Gender) : RegistrationIntent
    data class OnNationalIdFrontPicked(val attachment: Attachment) : RegistrationIntent
    data class OnNationalIdBackPicked(val attachment: Attachment) : RegistrationIntent
    data class OnNursingLicensePicked(val attachment: Attachment) : RegistrationIntent
    data class OnProfessionalCertificatePicked(val attachment: Attachment) : RegistrationIntent
    data class OnLicenseNumberChanged(val licenseNumber: String) : RegistrationIntent
    data class OnYearsOfExpChanged(val years: String) : RegistrationIntent
    data class OnPrimarySpecialityChanged(val speciality: String) : RegistrationIntent
    data class OnServiceToggle(val service: ServiceUi) : RegistrationIntent
    data class OnCertificationToggle(val isCertified: Boolean) : RegistrationIntent
    data class OnContinueClicked(val currentPage: Int) : RegistrationIntent
    data class OnPageChanged(val page: Int) : RegistrationIntent
    data object OnBackClicked : RegistrationIntent
    data object OnSubmitApplication : RegistrationIntent
}
