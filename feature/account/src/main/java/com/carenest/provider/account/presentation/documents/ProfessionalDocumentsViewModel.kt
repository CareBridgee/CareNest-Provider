package com.carenest.provider.account.presentation.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.account.presentation.model.DocumentUploadTarget
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.designsystem.components.toast.ToastType
import com.carenest.provider.profile.data.file.ContentUriFileReader
import com.carenest.provider.profile.domain.model.NurseUpdate
import com.carenest.provider.profile.domain.model.UploadFile
import com.carenest.provider.profile.domain.usecase.GetNurseUseCase
import com.carenest.provider.profile.domain.usecase.UpdateNurseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfessionalDocumentsViewModel @Inject constructor(
    private val authenticationSessionStore: AuthenticationSessionStore,
    private val getNurse: GetNurseUseCase,
    private val updateNurse: UpdateNurseUseCase,
    private val fileReader: ContentUriFileReader,
) : ViewModel(),
    StateHolder<ProfessionalDocumentsUiState> by DefaultStateHolder(ProfessionalDocumentsUiState()),
    EffectPublisher<ProfessionalDocumentsEffect> by DefaultEffectPublisher() {

    private var nurseId: String? = null

    init {
        loadDocuments()
    }

    fun onIntent(intent: ProfessionalDocumentsIntent) {
        when (intent) {
            ProfessionalDocumentsIntent.BackClicked ->
                sendEffect(ProfessionalDocumentsEffect.NavigateBack)
            ProfessionalDocumentsIntent.RetryClicked -> loadDocuments()
            is ProfessionalDocumentsIntent.ViewDocumentClicked -> openDocument(intent.target)
            is ProfessionalDocumentsIntent.ReplaceDocumentClicked -> {
                if (currentState.uploadingTarget == null) {
                    sendEffect(ProfessionalDocumentsEffect.OpenDocumentPicker(intent.target))
                }
            }
            is ProfessionalDocumentsIntent.DocumentFilePicked -> uploadDocument(intent.file)
        }
    }

    private fun loadDocuments() {
        if (currentState.isLoading) return
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            val resolvedNurseId = resolveNurseId()
            if (resolvedNurseId.isNullOrBlank()) {
                showFailure("documents_error_missing_session")
                return@launch
            }

            getNurse(resolvedNurseId).fold(
                onSuccess = { profile ->
                    nurseId = profile.id
                    updateState {
                        copy(
                            isLoading = false,
                            documents = profile.toProfessionalDocuments(uploadingTarget),
                            errorMessage = null,
                        )
                    }
                },
                onFailure = { error -> showFailure(error.userMessage()) },
            )
        }
    }

    private fun openDocument(target: DocumentUploadTarget) {
        val url = currentState.documents
            .asSequence()
            .flatMap { it.files.asSequence() }
            .firstOrNull { it.target == target }
            ?.url

        if (url.isNullOrBlank()) {
            sendEffect(ProfessionalDocumentsEffect.ShowMessage("documents_error_missing_url"))
        } else {
            sendEffect(ProfessionalDocumentsEffect.OpenDocument(url))
        }
    }

    private fun uploadDocument(file: PickedDocumentFile) {
        val resolvedNurseId = nurseId ?: return
        if (currentState.uploadingTarget != null) return
        viewModelScope.launch {
            updateState {
                copy(
                    uploadingTarget = file.target,
                    documents = documents.mapUploading(file.target),
                    errorMessage = null,
                )
            }

            val uploadFile = runCatching {
                withContext(Dispatchers.IO) {
                    fileReader.read(file.uri, file.fileName, file.mimeType)
                }
            }.getOrElse { error ->
                failUpload(error)
                return@launch
            }

            updateNurse(resolvedNurseId, file.target.toUpdate(uploadFile)).fold(
                onSuccess = { updatedProfile ->
                    getNurse(resolvedNurseId).fold(
                        onSuccess = { refreshedProfile ->
                            nurseId = refreshedProfile.id
                            updateState {
                                copy(
                                    uploadingTarget = null,
                                    documents = refreshedProfile.toProfessionalDocuments(),
                                    errorMessage = null,
                                )
                            }
                            sendEffect(
                                ProfessionalDocumentsEffect.ShowMessage(
                                    "documents_upload_success",
                                    ToastType.Success,
                                ),
                            )
                        },
                        onFailure = { refreshError ->
                            nurseId = updatedProfile.id
                            updateState {
                                copy(
                                    uploadingTarget = null,
                                    documents = updatedProfile.toProfessionalDocuments(),
                                    errorMessage = null,
                                )
                            }
                            sendEffect(
                                ProfessionalDocumentsEffect.ShowMessage(
                                    refreshError.userMessage(),
                                    ToastType.Warning,
                                ),
                            )
                        },
                    )
                },
                onFailure = ::failUpload,
            )
        }
    }

    private suspend fun resolveNurseId(): String? {
        nurseId?.takeIf(String::isNotBlank)?.let { return it }
        val resolved = authenticationSessionStore.session.first()?.nurseId
        nurseId = resolved
        return resolved
    }

    private fun failUpload(error: Throwable) {
        val message = error.userMessage()
        updateState {
            copy(
                uploadingTarget = null,
                documents = documents.mapUploading(null),
                errorMessage = message,
            )
        }
        sendEffect(ProfessionalDocumentsEffect.ShowMessage(message))
    }

    private fun showFailure(message: String) {
        updateState {
            copy(
                isLoading = false,
                uploadingTarget = null,
                errorMessage = message,
            )
        }
        sendEffect(ProfessionalDocumentsEffect.ShowMessage(message))
    }
}

private fun DocumentUploadTarget.toUpdate(file: UploadFile): NurseUpdate = when (this) {
    DocumentUploadTarget.NationalIdFront -> NurseUpdate(nationalIdFront = file)
    DocumentUploadTarget.NationalIdBack -> NurseUpdate(nationalIdBack = file)
    DocumentUploadTarget.NursingLicense -> NurseUpdate(licenseImage = file)
    DocumentUploadTarget.ProfessionalCertificate -> NurseUpdate(professionalCertificate = file)
}

private fun List<com.carenest.provider.account.presentation.model.ProfessionalDocumentUiModel>.mapUploading(
    uploadingTarget: DocumentUploadTarget?,
) = map { document ->
    document.copy(
        files = document.files.map { file ->
            file.copy(isUploading = file.target == uploadingTarget)
        },
    )
}

private fun Throwable.userMessage(): String = message?.takeIf(String::isNotBlank) ?: "error_unknown"
