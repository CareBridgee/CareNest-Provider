package com.carenest.provider.account.presentation.publicprofile

import android.net.Uri
import com.carenest.provider.designsystem.components.toast.ToastType
import com.carenest.provider.profile.domain.model.NurseProfile
import com.carenest.provider.profile.domain.model.VerificationStatus

data class PublicProfileUiState(
    val isLoading: Boolean = false,
    val isSavingProfile: Boolean = false,
    val isUploadingProfileImage: Boolean = false,
    val profile: NurseProfile? = null,
    val cachedProfileImageUrl: String? = null,
    val errorMessage: String? = null,
    val isEditBioSheetVisible: Boolean = false,
    val bioDraft: String = "",
    val specializationDraft: String = "",
    val yearsOfExperienceDraft: String = "",
    val specializationError: String? = null,
    val yearsOfExperienceError: String? = null,
) {
    val isVerified: Boolean
        get() = profile?.verificationStatus == VerificationStatus.APPROVED

    val fullName: String
        get() = listOfNotNull(
            profile?.firstName?.takeIf(String::isNotBlank),
            profile?.lastName?.takeIf(String::isNotBlank),
        ).joinToString(" ").ifBlank { "" }

    val specialization: String
        get() = profile?.specialization.orEmpty()

    val bio: String
        get() = profile?.bio.orEmpty()

    val serviceNames: List<String>
        get() = profile?.services
            .orEmpty()
            .filter { it.isActive }
            .map { it.serviceName }
            .filter(String::isNotBlank)

    val profileImageUrl: String?
        get() = profile?.profileImageUrl ?: cachedProfileImageUrl

    val ratingText: String
        get() = profile?.ratingAvg?.let { rating ->
            if (rating % 1.0 == 0.0) {
                rating.toInt().toString()
            } else {
                "%.1f".format(rating)
            }
        } ?: "0"

    val reviewCount: Int
        get() = profile?.totalReviews ?: 0

    val yearsOfExperience: Int
        get() = profile?.yearsOfExperience ?: 0
}

data class PickedProfileImage(
    val uri: Uri,
    val fileName: String,
    val mimeType: String,
)

sealed interface PublicProfileIntent {
    data object BackClicked : PublicProfileIntent
    data object RetryClicked : PublicProfileIntent
    data object EditBioClicked : PublicProfileIntent
    data object DismissEditBio : PublicProfileIntent
    data class BioChanged(val bio: String) : PublicProfileIntent
    data class SpecializationChanged(val specialization: String) : PublicProfileIntent
    data class YearsOfExperienceChanged(val yearsOfExperience: String) : PublicProfileIntent
    data object SaveBioClicked : PublicProfileIntent
    data object ProfileImageClicked : PublicProfileIntent
    data class ProfileImagePicked(val image: PickedProfileImage) : PublicProfileIntent
    data object ShareProfileClicked : PublicProfileIntent
    data object SettingsClicked : PublicProfileIntent
}

sealed interface PublicProfileEffect {
    data object NavigateBack : PublicProfileEffect
    data object OpenProfileImagePicker : PublicProfileEffect
    data class ShareProfile(val nurseId: String, val name: String, val specialization: String) : PublicProfileEffect
    data object OpenSettings : PublicProfileEffect
    data class ShowMessage(
        val message: String,
        val type: ToastType = ToastType.Error,
    ) : PublicProfileEffect
}
