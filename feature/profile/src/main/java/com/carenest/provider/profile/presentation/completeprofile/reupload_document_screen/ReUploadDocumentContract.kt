package com.carenest.provider.profile.presentation.completeprofile.reupload_document_screen

import android.net.Uri

data class ReUploadDocumentState(
    val documentType: String = "",
    val rejectionReason: String = "",
    val currentStep: Int = 2,
    val totalSteps: Int = 3,
    val selectedFileUri: Uri? = null,
    val selectedFileName: String? = null,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val uploadError: String? = null
) {
    val hasFileSelected: Boolean get() = selectedFileUri != null
    val stepProgress: Float get() = currentStep.toFloat() / totalSteps.toFloat()
}

sealed interface ReUploadDocumentIntent {
    object OnBackClick : ReUploadDocumentIntent
    data class OnFileSelected(val uri: Uri, val fileName: String) : ReUploadDocumentIntent
    object OnRemoveFile : ReUploadDocumentIntent
    object OnUpdateDocumentClick : ReUploadDocumentIntent
    object OnCancelClick : ReUploadDocumentIntent
    object OnPickFile : ReUploadDocumentIntent
}

sealed interface ReUploadDocumentEvent {
    object NavigateBack : ReUploadDocumentEvent
    object UploadSuccess : ReUploadDocumentEvent
    data class ShowError(val message: String) : ReUploadDocumentEvent
    object LaunchFilePicker : ReUploadDocumentEvent
}
