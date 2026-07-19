package com.carenest.provider.profile.presentation.ui.reupload_document_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReUploadDocumentViewModel @Inject constructor() : ViewModel(),
    StateHolder<ReUploadDocumentState> by DefaultStateHolder(ReUploadDocumentState()),
    EffectPublisher<ReUploadDocumentEvent> by DefaultEffectPublisher() {

    fun onIntent(intent: ReUploadDocumentIntent) {
        when (intent) {
            ReUploadDocumentIntent.OnBackClick -> {
                sendEffect(ReUploadDocumentEvent.NavigateBack)
            }

            ReUploadDocumentIntent.OnCancelClick -> {
                sendEffect(ReUploadDocumentEvent.NavigateBack)
            }

            ReUploadDocumentIntent.OnPickFile -> {
                sendEffect(ReUploadDocumentEvent.LaunchFilePicker)
            }

            is ReUploadDocumentIntent.OnFileSelected -> {
                updateState {
                    copy(
                        selectedFileUri = intent.uri,
                        selectedFileName = intent.fileName,
                        uploadError = null
                    )
                }
            }

            ReUploadDocumentIntent.OnRemoveFile -> {
                updateState {
                    copy(
                        selectedFileUri = null,
                        selectedFileName = null,
                        uploadError = null
                    )
                }
            }

            ReUploadDocumentIntent.OnUpdateDocumentClick -> {
                uploadDocument()
            }
        }
    }

    fun setDocumentInfo(
        documentType: String,
        rejectionReason: String
    ) {
        updateState {
            copy(
                documentType = documentType,
                rejectionReason = rejectionReason
            )
        }
    }

    private fun uploadDocument() {
        if (!currentState.hasFileSelected || currentState.isUploading) return

        updateState {
            copy(
                isUploading = true,
                uploadProgress = 0f,
                uploadError = null
            )
        }

        viewModelScope.launch {
            try {
                repeat(10) { index ->
                    delay(200)

                    updateState {
                        copy(uploadProgress = (index + 1) / 10f)
                    }
                }

                updateState {
                    copy(isUploading = false)
                }

                sendEffect(ReUploadDocumentEvent.UploadSuccess)

            } catch (e: Exception) {

                updateState {
                    copy(
                        isUploading = false,
                        uploadError = e.message ?: "Upload failed"
                    )
                }

                sendEffect(
                    ReUploadDocumentEvent.ShowError(
                        e.message ?: "Upload failed"
                    )
                )
            }
        }
    }
}