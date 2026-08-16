package com.carenest.provider.account.presentation.publicprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.datastore.AuthenticationSession
import com.carenest.provider.core.datastore.AuthenticationSessionDestination
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.designsystem.components.toast.ToastType
import com.carenest.provider.profile.data.file.ContentUriFileReader
import com.carenest.provider.profile.domain.model.NurseProfile
import com.carenest.provider.profile.domain.model.NurseUpdate
import com.carenest.provider.profile.domain.model.VerificationStatus
import com.carenest.provider.profile.domain.usecase.GetNurseUseCase
import com.carenest.provider.profile.domain.usecase.UpdateNurseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val authenticationSessionStore: AuthenticationSessionStore,
    private val getNurse: GetNurseUseCase,
    private val updateNurse: UpdateNurseUseCase,
    private val fileReader: ContentUriFileReader,
) : ViewModel(),
    StateHolder<PublicProfileUiState> by DefaultStateHolder(
        PublicProfileUiState(
            cachedProfileImageUrl = authenticationSessionStore.currentSession?.profileImageUrl,
        ),
    ),
    EffectPublisher<PublicProfileEffect> by DefaultEffectPublisher() {

    private var nurseId: String? = null

    init {
        loadProfile()
    }

    fun onIntent(intent: PublicProfileIntent) {
        when (intent) {
            PublicProfileIntent.BackClicked -> sendEffect(PublicProfileEffect.NavigateBack)
            PublicProfileIntent.RetryClicked -> loadProfile()
            PublicProfileIntent.EditBioClicked -> openEditBio()
            PublicProfileIntent.DismissEditBio -> updateState { copy(isEditBioSheetVisible = false) }
            is PublicProfileIntent.BioChanged -> updateState { copy(bioDraft = intent.bio) }
            is PublicProfileIntent.SpecializationChanged -> updateState {
                copy(specializationDraft = intent.specialization)
            }
            is PublicProfileIntent.YearsOfExperienceChanged -> updateState {
                copy(yearsOfExperienceDraft = intent.yearsOfExperience.filter(Char::isDigit))
            }
            PublicProfileIntent.SaveBioClicked -> saveProfileDetails()
            PublicProfileIntent.ProfileImageClicked -> {
                if (!currentState.isUploadingProfileImage) {
                    sendEffect(PublicProfileEffect.OpenProfileImagePicker)
                }
            }
            is PublicProfileIntent.ProfileImagePicked -> updateProfileImage(intent.image)
            PublicProfileIntent.ShareProfileClicked -> sendEffect(PublicProfileEffect.ShareProfile)
            PublicProfileIntent.SettingsClicked -> sendEffect(PublicProfileEffect.OpenSettings)
        }
    }

    private fun loadProfile() {
        if (currentState.isLoading) return
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            val savedSession = authenticationSessionStore.currentSession
                ?: authenticationSessionStore.session.first()
            updateState {
                copy(
                    cachedProfileImageUrl = savedSession?.profileImageUrl
                        ?: cachedProfileImageUrl,
                )
            }
            val resolvedNurseId = resolveNurseId(savedSession)
            if (resolvedNurseId.isNullOrBlank()) {
                updateState { copy(isLoading = false, errorMessage = "profile_error_missing_session") }
                sendEffect(PublicProfileEffect.ShowMessage("profile_error_missing_session"))
                return@launch
            }

            getNurse(resolvedNurseId).fold(
                onSuccess = { profile ->
                    nurseId = profile.id
                    updateSavedSession(profile)
                    updateState {
                        copy(
                            isLoading = false,
                            profile = profile,
                            cachedProfileImageUrl = profile.profileImageUrl,
                            errorMessage = null,
                        )
                    }
                },
                onFailure = { error ->
                    val message = error.userMessage()
                    updateState { copy(isLoading = false, errorMessage = message) }
                    sendEffect(PublicProfileEffect.ShowMessage(message))
                },
            )
        }
    }

    private fun openEditBio() {
        val profile = currentState.profile ?: return
        updateState {
            copy(
                isEditBioSheetVisible = true,
                bioDraft = profile.bio.orEmpty(),
                specializationDraft = profile.specialization.orEmpty(),
                yearsOfExperienceDraft = profile.yearsOfExperience?.toString().orEmpty(),
            )
        }
    }

    private fun saveProfileDetails() {
        val snapshot = currentState
        if (snapshot.isSavingProfile) return
        val resolvedNurseId = nurseId ?: snapshot.profile?.id ?: return
        val specialization = snapshot.specializationDraft.trim()
        val years = snapshot.yearsOfExperienceDraft.toIntOrNull()
        when {
            specialization.isBlank() -> {
                sendEffect(PublicProfileEffect.ShowMessage("profile_error_specialization_required"))
                return
            }
            years == null -> {
                sendEffect(PublicProfileEffect.ShowMessage("profile_error_invalid_years"))
                return
            }
        }

        viewModelScope.launch {
            updateState { copy(isSavingProfile = true, errorMessage = null) }
            updateNurse(
                resolvedNurseId,
                NurseUpdate(
                    specialization = specialization,
                    yearsOfExperience = years,
                    bio = snapshot.bioDraft.trim(),
                ),
            ).fold(
                onSuccess = { profile ->
                    updateSavedSession(profile)
                    updateState {
                        copy(
                            isSavingProfile = false,
                            isEditBioSheetVisible = false,
                            profile = profile,
                            cachedProfileImageUrl = profile.profileImageUrl,
                            errorMessage = null,
                        )
                    }
                    sendEffect(PublicProfileEffect.ShowMessage("profile_saved", ToastType.Success))
                },
                onFailure = { error ->
                    val message = error.userMessage()
                    updateState { copy(isSavingProfile = false, errorMessage = message) }
                    sendEffect(PublicProfileEffect.ShowMessage(message))
                },
            )
        }
    }

    private fun updateProfileImage(image: PickedProfileImage) {
        val resolvedNurseId = nurseId ?: currentState.profile?.id ?: return
        if (currentState.isUploadingProfileImage) return
        if (!image.mimeType.startsWith("image/")) {
            sendEffect(PublicProfileEffect.ShowMessage("profile_error_invalid_image"))
            return
        }

        viewModelScope.launch {
            updateState { copy(isUploadingProfileImage = true, errorMessage = null) }
            val file = runCatching {
                withContext(Dispatchers.IO) {
                    fileReader.read(image.uri, image.fileName, image.mimeType)
                }
            }.getOrElse { error ->
                val message = error.userMessage()
                updateState { copy(isUploadingProfileImage = false, errorMessage = message) }
                sendEffect(PublicProfileEffect.ShowMessage(message))
                return@launch
            }

            updateNurse(resolvedNurseId, NurseUpdate(profileImage = file)).fold(
                onSuccess = { profile ->
                    updateSavedSession(profile)
                    updateState {
                        copy(
                            isUploadingProfileImage = false,
                            profile = profile,
                            cachedProfileImageUrl = profile.profileImageUrl,
                            errorMessage = null,
                        )
                    }
                    sendEffect(PublicProfileEffect.ShowMessage("profile_photo_updated", ToastType.Success))
                },
                onFailure = { error ->
                    val message = error.userMessage()
                    updateState { copy(isUploadingProfileImage = false, errorMessage = message) }
                    sendEffect(PublicProfileEffect.ShowMessage(message))
                },
            )
        }
    }

    private fun resolveNurseId(savedSession: AuthenticationSession?): String? {
        nurseId?.takeIf(String::isNotBlank)?.let { return it }
        val sessionNurseId = savedSession?.nurseId
        nurseId = sessionNurseId
        return sessionNurseId
    }

    private suspend fun updateSavedSession(profile: NurseProfile) {
        val state = authenticationSessionStore.state.first()
        val credentials = state.credentials ?: return
        val session = state.session ?: return
        authenticationSessionStore.completeAuthentication(
            expectedCredentials = credentials,
            session = AuthenticationSession(
                destination = profile.verificationStatus.toSessionDestination(),
                nurseId = profile.id,
                phoneNumber = session.phoneNumber,
                profileImageUrl = profile.profileImageUrl,
            ),
        )
    }

    private fun VerificationStatus.toSessionDestination(): AuthenticationSessionDestination = when (this) {
        VerificationStatus.UNDER_REVIEW -> AuthenticationSessionDestination.UNDER_REVIEW
        VerificationStatus.APPROVED -> AuthenticationSessionDestination.APPROVED
        VerificationStatus.REJECTED -> AuthenticationSessionDestination.REJECTED
    }
}

private fun Throwable.userMessage(): String = message?.takeIf(String::isNotBlank) ?: "error_unknown"
