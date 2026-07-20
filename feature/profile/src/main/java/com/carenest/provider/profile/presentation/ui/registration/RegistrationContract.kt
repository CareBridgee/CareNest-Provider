package com.carenest.provider.profile.presentation.ui.registration

import android.net.Uri
import androidx.annotation.DrawableRes
import com.carenest.provider.designsystem.components.toast.ToastType


enum class Gender(val gender: String) {
    MALE("M"),
    FEMALE("F"),
    UNKNOWN("NOT PROVIDED"),

}

data class StepperState(
    val totalSteps: Int = 0,
    val remainingSteps: Int = 0,
    val currentStepTitle: String = "",
)

//region personalInfo

data class PersonalInfoState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val dateOfBirth: String = "",
    val nationalId: String = "",
    val gender: Gender = Gender.UNKNOWN,
    val profilePhotoUri: Uri? = null,
)
//endregion


//region verificationDocs

data class Attachment(
    val uri: Uri,
    val name: String,
    val mimeType: String,
)


data class VerificationDocumentsUiState(
    val nationalId: Attachment? = null,
    val nursingLicense: Attachment? = null,
    val professionalCertificate: Attachment? = null,
    val yearsOfExp: Int = 0,
    val primarySpeciality: String = ""
)

//endregion


//region services

data class ServiceUi(
    @DrawableRes val icon: Int,
    val title: String,
)

data class ServicesUiState(
    val availableServices: List<ServiceUi> = emptyList(),
    val selectedServices: List<ServiceUi> = emptyList(),
)

//endregion


//region applicationReview

data class ApplicationReviewUiState(
    val education: String = "",
    val isCertified: Boolean = false
)

//endregion


data class RegistrationUiState(
    val isLoading: Boolean = true,
    val stepperState: StepperState = StepperState(),
    val personalInfoState: PersonalInfoState = PersonalInfoState(),
    val verificationDocumentsUiState: VerificationDocumentsUiState = VerificationDocumentsUiState(),
    val servicesUiState: ServicesUiState = ServicesUiState(),
    val applicationReviewUiState: ApplicationReviewUiState = ApplicationReviewUiState(),
    val errorMessage:String? = null
){
    val hasError = errorMessage != null
}


sealed interface RegistrationEffect {
    data object NavigateToNextStep: RegistrationEffect
    data object NavigateToPreviousStep: RegistrationEffect
    data object OpenProfilePhotoPicker: RegistrationEffect
    data object OpenNIDPicker: RegistrationEffect
    data object OpenCertificatePicker: RegistrationEffect
    data object OpenLicensePicker: RegistrationEffect
    data object OpenGenderSelection: RegistrationEffect
    data object ShowCalendar: RegistrationEffect
    data class ShowMessage(val message: String, val type: ToastType = ToastType.Error): RegistrationEffect



}

sealed interface RegistrationIntent {
    data object OnProfilePhotoClick : RegistrationIntent
    data object OnGenderClick : RegistrationIntent
    data object OnDateOfBirthClick : RegistrationIntent
    data object OnNationalIdClick : RegistrationIntent
    data object OnNursingLicenseClick : RegistrationIntent
    data object OnProfessionalCertificateClick : RegistrationIntent
    data object OnRemoveNationalId : RegistrationIntent
    data object OnRemoveNursingLicense : RegistrationIntent
    data object OnRemoveProfessionalCertificate : RegistrationIntent
    data class OnProfilePhotoPicked(val uri: Uri) : RegistrationIntent
    data class OnFirstNameChanged(val firstName: String) : RegistrationIntent
    data class OnLastNameChanged(val lastName: String) : RegistrationIntent
    data class OnEmailChanged(val email: String) : RegistrationIntent
    data class OnLocationChanged(val location: String) : RegistrationIntent
    data class OnDateOfBirthChanged(val dateOfBirth: String) : RegistrationIntent
    data class OnNationalIdChanged(val nationalId: String) : RegistrationIntent
    data class OnGenderChanged(val gender: Gender) : RegistrationIntent
    data class OnNationalIdDocumentPicked(val attachment: Attachment) : RegistrationIntent
    data class OnNursingLicensePicked(val attachment: Attachment) : RegistrationIntent
    data class OnProfessionalCertificatePicked(val attachment: Attachment) : RegistrationIntent
    data class OnYearsOfExpChanged(val years: String) : RegistrationIntent
    data class OnPrimarySpecialityChanged(val speciality: String) : RegistrationIntent
    data class OnServiceToggle(val service: ServiceUi) : RegistrationIntent
    data class OnCertificationToggle(val isCertified: Boolean) : RegistrationIntent
    data class OnContinueClicked(val currentPage: Int) : RegistrationIntent
    data object OnBackClicked : RegistrationIntent
    data object OnSubmitApplication : RegistrationIntent
}