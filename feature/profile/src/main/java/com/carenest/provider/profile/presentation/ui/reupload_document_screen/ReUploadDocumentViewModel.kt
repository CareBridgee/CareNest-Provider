package com.carenest.provider.profile.presentation.ui.reupload_document_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import com.carenest.provider.profile.data.file.ContentUriFileReader
import com.carenest.provider.profile.domain.model.NurseUpdate
import com.carenest.provider.profile.domain.usecase.UpdateNurseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ReUploadDocumentViewModel @Inject constructor(
    private val updateNurse: UpdateNurseUseCase,
    private val fileReader: ContentUriFileReader,
) : ViewModel(),
    StateHolder<ReUploadDocumentState> by DefaultStateHolder(ReUploadDocumentState()),
    EffectPublisher<ReUploadDocumentEvent> by DefaultEffectPublisher() {

    fun configure(nurseId: String, documentType: String, rejectionReason: String) {
        if (currentState.nurseId == nurseId && currentState.documentType == documentType) return
        updateState {
            copy(nurseId = nurseId, documentType = documentType, rejectionReason = rejectionReason)
        }
    }

    fun onIntent(intent: ReUploadDocumentIntent) {
        when (intent) {
            ReUploadDocumentIntent.OnBackClick,
            ReUploadDocumentIntent.OnCancelClick -> sendEffect(ReUploadDocumentEvent.NavigateBack)
            ReUploadDocumentIntent.OnPickFile -> sendEffect(ReUploadDocumentEvent.LaunchFilePicker)
            is ReUploadDocumentIntent.OnFileSelected -> updateState {
                copy(
                    selectedFileUri = intent.uri,
                    selectedFileName = intent.fileName,
                    selectedMimeType = intent.mimeType,
                    uploadError = null,
                )
            }
            ReUploadDocumentIntent.OnRemoveFile -> updateState {
                copy(selectedFileUri = null, selectedFileName = null, uploadError = null)
            }
            ReUploadDocumentIntent.OnUpdateDocumentClick -> uploadDocument()
        }
    }

    private fun uploadDocument() {
        val snapshot = currentState
        if (!snapshot.hasFileSelected || snapshot.isUploading) return
        viewModelScope.launch {
            updateState { copy(isUploading = true, uploadError = null) }
            val request = runCatching {
                val file = withContext(Dispatchers.IO) {
                    fileReader.read(
                        requireNotNull(snapshot.selectedFileUri),
                        snapshot.selectedFileName.orEmpty(),
                        snapshot.selectedMimeType,
                    )
                }
                when (snapshot.documentType.trim().lowercase()) {
                    "nationalidfront" -> NurseUpdate(nationalIdFront = file)
                    "nationalidback" -> NurseUpdate(nationalIdBack = file)
                    "licenseimage" -> NurseUpdate(licenseImage = file)
                    "professionalcertificate" -> NurseUpdate(professionalCertificate = file)
                    else -> throw IllegalArgumentException("unsupported_rejected_document")
                }
            }.getOrElse { error ->
                failUpload(error)
                return@launch
            }

            updateNurse(snapshot.nurseId, request).fold(
                onSuccess = {
                    updateState { copy(isUploading = false) }
                    sendEffect(ReUploadDocumentEvent.UploadSuccess)
                },
                onFailure = ::failUpload,
            )
        }
    }

    private fun failUpload(error: Throwable) {
        val message = error.message ?: "upload_failed"
        updateState { copy(isUploading = false, uploadError = message) }
        sendEffect(ReUploadDocumentEvent.ShowError(message))
    }
}
