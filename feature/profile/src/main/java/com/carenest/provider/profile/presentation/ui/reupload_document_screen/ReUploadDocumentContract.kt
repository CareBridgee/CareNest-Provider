package com.carenest.provider.profile.presentation.ui.reupload_document_screen

import android.net.Uri

data class ReUploadDocumentState(
    val nurseId: String = "",
    val documentType: String = "",
    val rejectionReason: String = "",
    val currentStep: Int = 2,
    val totalSteps: Int = 3,
    val selectedFileUri: Uri? = null,
    val selectedFileName: String? = null,
    val selectedMimeType: String = "application/octet-stream",
    val isUploading: Boolean = false,
    val uploadError: String? = null,
) {
    val hasFileSelected: Boolean get() = selectedFileUri != null
    val stepProgress: Float get() = currentStep.toFloat() / totalSteps.toFloat()
}

sealed interface ReUploadDocumentIntent {
    data object OnBackClick : ReUploadDocumentIntent
    data class OnFileSelected(val uri: Uri, val fileName: String, val mimeType: String) : ReUploadDocumentIntent
    data object OnRemoveFile : ReUploadDocumentIntent
    data object OnUpdateDocumentClick : ReUploadDocumentIntent
    data object OnCancelClick : ReUploadDocumentIntent
    data object OnPickFile : ReUploadDocumentIntent
}

sealed interface ReUploadDocumentEvent {
    data object NavigateBack : ReUploadDocumentEvent
    data object UploadSuccess : ReUploadDocumentEvent
    data class ShowError(val message: String) : ReUploadDocumentEvent
    data object LaunchFilePicker : ReUploadDocumentEvent
}
