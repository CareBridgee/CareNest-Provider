package com.carenest.provider.profile.presentation.ui.reupload_document_screen

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.LocalContext
import com.carenest.provider.designsystem.components.toast.SnackbarHost
import com.carenest.provider.designsystem.components.toast.showSnack
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.profile.presentation.ui.reupload_document_screen.composable.CaptureTipsSection
import com.carenest.provider.profile.presentation.ui.reupload_document_screen.composable.DocumentProgressBar
import com.carenest.provider.profile.presentation.ui.reupload_document_screen.composable.ReUploadActionButtons
import com.carenest.provider.profile.presentation.ui.reupload_document_screen.composable.RejectionNoticeCard
import com.carenest.provider.profile.presentation.ui.reupload_document_screen.composable.UploadZone

@Composable
fun ReUploadDocumentScreen(
    nurseId: String,
    documentField: String,
    rejectionReason: String,
    modifier: Modifier = Modifier,
    viewModel: ReUploadDocumentViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onUploadSuccess: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(nurseId, documentField, rejectionReason) {
        viewModel.configure(nurseId, documentField, rejectionReason)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.onIntent(
                ReUploadDocumentIntent.OnFileSelected(
                    uri = it,
                    fileName = getDisplayName(context.contentResolver, it),
                    mimeType = context.contentResolver.getType(it) ?: "application/octet-stream",
                )
            )
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {

            ReUploadDocumentEvent.NavigateBack ->
                onBackClick()

            ReUploadDocumentEvent.LaunchFilePicker ->
                launcher.launch(
                    arrayOf(
                        "image/*",
                        "application/pdf"
                    )
                )

            ReUploadDocumentEvent.UploadSuccess -> {
                onUploadSuccess()
            }

            is ReUploadDocumentEvent.ShowError -> {
                val message = context.resources.getIdentifier(
                    effect.message,
                    "string",
                    context.packageName,
                ).let { id -> if (id != 0) context.getString(id) else effect.message }
                scope.launch { snackbarHostState.showSnack(message) }
            }
        }
    }

    ReUploadDocumentScreenContent(
        state = state,
        onBackClick = {
            viewModel.onIntent(ReUploadDocumentIntent.OnBackClick)
        },
        onPickFile = {
            viewModel.onIntent(ReUploadDocumentIntent.OnPickFile)
        },
        onRemoveFile = {
            viewModel.onIntent(ReUploadDocumentIntent.OnRemoveFile)
        },
        onUpdateDocumentClick = {
            viewModel.onIntent(ReUploadDocumentIntent.OnUpdateDocumentClick)
        },
        onCancelClick = {
            viewModel.onIntent(ReUploadDocumentIntent.OnCancelClick)
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
private fun ReUploadDocumentScreenContent(
    state: ReUploadDocumentState,
    onBackClick: () -> Unit,
    onPickFile: () -> Unit,
    onRemoveFile: () -> Unit,
    onUpdateDocumentClick: () -> Unit,
    onCancelClick: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    modifier: Modifier = Modifier,
    avatarUrl: String? = null
) {
    val topBarTitle = if (state.documentType.isNotEmpty()) {
        stringResource(R.string.reupload_title, state.documentType)
    } else {
        stringResource(R.string.reupload_title_default)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CareNestTopBar(
                title = topBarTitle,
                leading = TopBarLeading.Back(onBackClick),
                trailingAvatarUrl = avatarUrl,
                modifier = Modifier.fillMaxWidth()
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Theme.colors.backGround
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = Theme.spacing.large,
                    vertical = Theme.spacing.large
                ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraLarge)
        ) {
            RejectionNoticeCard(
                rejectionReason = state.rejectionReason.ifEmpty {
                    stringResource(R.string.default_rejection_reason)
                },
                modifier = Modifier.fillMaxWidth()
            )

            CaptureTipsSection(
                modifier = Modifier.fillMaxWidth()
            )

            UploadZone(
                selectedFileUri = state.selectedFileUri,
                onPickFile = onPickFile,
                onRemoveFile = onRemoveFile,
                modifier = Modifier.fillMaxWidth()
            )

            DocumentProgressBar(
                progress = state.stepProgress,
                currentStep = state.currentStep,
                totalSteps = state.totalSteps,
                modifier = Modifier.fillMaxWidth()
            )

            ReUploadActionButtons(
                hasFileSelected = state.hasFileSelected,
                isUploading = state.isUploading,
                onUpdateDocumentClick = onUpdateDocumentClick,
                onCancelClick = onCancelClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Theme.spacing.large))
        }
    }
}

private fun getDisplayName(resolver: android.content.ContentResolver, uri: Uri): String {
    resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0) return cursor.getString(index)
        }
    }
    return uri.lastPathSegment ?: "upload"
}


@Preview(showBackground = true)
@Composable
private fun ReUploadDocumentEmptyLightPreview() {
    SpTheme(isDarkTheme = false) {
        ReUploadDocumentScreenContent(
            state = ReUploadDocumentState(
                documentType = "Nursing License",
                rejectionReason = "The document was too blurry. Please ensure all text is legible and edges are visible.",
                currentStep = 2,
                totalSteps = 3
            ),
            onBackClick = {},
            onPickFile = {},
            onRemoveFile = {},
            onUpdateDocumentClick = {},
            onCancelClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReUploadDocumentWithFileLightPreview() {
    SpTheme(isDarkTheme = false) {
        ReUploadDocumentScreenContent(
            state = ReUploadDocumentState(
                documentType = "Nursing License",
                rejectionReason = "The document was too blurry. Please ensure all text is legible and edges are visible.",
                currentStep = 2,
                totalSteps = 3,
                selectedFileUri = Uri.parse("https://example.com/sample.jpg"),
                selectedFileName = "nursing_license.jpg"
            ),
            onBackClick = {},
            onPickFile = {},
            onRemoveFile = {},
            onUpdateDocumentClick = {},
            onCancelClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReUploadDocumentEmptyDarkPreview() {
    SpTheme(isDarkTheme = true) {
        ReUploadDocumentScreenContent(
            state = ReUploadDocumentState(
                documentType = "Nursing License",
                rejectionReason = "The document was too blurry. Please ensure all text is legible and edges are visible.",
                currentStep = 2,
                totalSteps = 3
            ),
            onBackClick = {},
            onPickFile = {},
            onRemoveFile = {},
            onUpdateDocumentClick = {},
            onCancelClick = {}
        )
    }
}
