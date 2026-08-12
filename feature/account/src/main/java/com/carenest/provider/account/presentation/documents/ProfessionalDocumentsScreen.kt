package com.carenest.provider.account.presentation.documents

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.components.ProfessionalDocumentCard
import com.carenest.provider.account.presentation.components.ProfessionalDocumentsLoadingSkeleton
import com.carenest.provider.account.presentation.components.ProviderAccountTopBar
import com.carenest.provider.account.presentation.model.DocumentUploadTarget
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.toast.ToastHost
import com.carenest.provider.designsystem.components.toast.ToastType
import com.carenest.provider.designsystem.components.toast.rememberToastState
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.R as DesignSystemR

@Composable
fun ProfessionalDocumentsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfessionalDocumentsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toastState = rememberToastState()
    var pendingPickerTarget by remember { mutableStateOf<DocumentUploadTarget?>(null) }
    val documentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        val target = pendingPickerTarget
        pendingPickerTarget = null
        if (uri != null && target != null) {
            persistReadPermission(context, uri)
            viewModel.onIntent(
                ProfessionalDocumentsIntent.DocumentFilePicked(
                    PickedDocumentFile(
                        target = target,
                        uri = uri,
                        fileName = getFileName(context, uri),
                        mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream",
                    ),
                ),
            )
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            ProfessionalDocumentsEffect.NavigateBack -> onNavigateBack()
            is ProfessionalDocumentsEffect.OpenDocument -> {
                val opened = openDocumentUrl(context, effect.url)
                if (!opened) {
                    toastState.show(
                        message = resolveMessage(context, "documents_error_no_viewer"),
                        type = ToastType.Error,
                    )
                }
            }
            is ProfessionalDocumentsEffect.OpenDocumentPicker -> {
                pendingPickerTarget = effect.target
                documentPicker.launch(arrayOf("*/*"))
            }
            is ProfessionalDocumentsEffect.ShowMessage -> {
                toastState.show(
                    message = resolveMessage(context, effect.message),
                    type = effect.type,
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ProfessionalDocumentsContent(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier.fillMaxSize(),
        )
        ToastHost(state = toastState)
    }
}

@Composable
fun ProfessionalDocumentsContent(
    state: ProfessionalDocumentsUiState,
    onIntent: (ProfessionalDocumentsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        ProviderAccountTopBar(onNavigateBack = { onIntent(ProfessionalDocumentsIntent.BackClicked) })
        when {
            state.isLoading -> ProfessionalDocumentsLoadingSkeleton()
            state.documents.isEmpty() && state.errorMessage != null -> {
                DocumentsErrorContent(
                    message = resolveMessage(LocalContext.current, state.errorMessage),
                    onRetry = { onIntent(ProfessionalDocumentsIntent.RetryClicked) },
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Theme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item { DocumentsHeader() }
                    items(state.documents, key = { it.id }) { document ->
                        ProfessionalDocumentCard(
                            document = document,
                            title = stringResource(document.titleRes),
                            supportingText = stringResource(document.supportingTextRes),
                            viewLabel = stringResource(R.string.documents_view),
                            replaceLabel = stringResource(R.string.documents_edit),
                            verifiedLabel = stringResource(R.string.documents_verified),
                            pendingLabel = stringResource(R.string.documents_pending),
                            rejectedLabel = stringResource(R.string.documents_rejected),
                            missingLabel = stringResource(R.string.documents_missing),
                            onViewClick = { target ->
                                onIntent(ProfessionalDocumentsIntent.ViewDocumentClicked(target))
                            },
                            onReplaceClick = { target ->
                                onIntent(ProfessionalDocumentsIntent.ReplaceDocumentClicked(target))
                            },
                        )
                    }
                    state.documents.firstMissingTarget()?.let { target ->
                        item {
                            UploadDocumentCard(
                                enabled = state.uploadingTarget == null,
                                onClick = {
                                    onIntent(ProfessionalDocumentsIntent.ReplaceDocumentClicked(target))
                                },
                            )
                        }
                    }
                    item { PendingDocumentInfo() }
                }
            }
        }
    }
}

@Composable
private fun DocumentsHeader() {
    BasicText(
        text = stringResource(R.string.documents_title),
        style = Theme.typography.title.copy(color = Theme.colors.primaryFont),
    )
    Spacer(Modifier.height(Theme.spacing.small))
    BasicText(
        text = stringResource(R.string.documents_subtitle),
        style = Theme.typography.body.small.copy(
            color = Theme.colors.secondaryFont,
            fontWeight = FontWeight.Normal,
        ),
    )
    Spacer(Modifier.height(Theme.spacing.medium))
}

@Composable
private fun DocumentsErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BasicText(
            text = message,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
            ),
        )
        Spacer(Modifier.height(Theme.spacing.medium))
        PrimaryButton(
            caption = stringResource(R.string.documents_retry),
            onClick = onRetry,
        )
    }
}

@Composable
private fun UploadDocumentCard(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = Theme.colors.onDisable
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)),
                    ),
                    cornerRadius = CornerRadius(24.dp.toPx()),
                )
            }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Theme.colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_account_upload),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .width(26.dp)
                    .height(32.dp),
            )
        }
        Spacer(Modifier.height(Theme.spacing.medium))
        BasicText(
            text = stringResource(R.string.documents_upload),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.tint,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            text = stringResource(R.string.documents_upload_limits),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

@Composable
private fun PendingDocumentInfo() {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.primaryVariant)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = null,
            tint = Theme.colors.onPrimaryVariant,
        )
        Spacer(Modifier.padding(Theme.spacing.small))
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
            BasicText(
                text = stringResource(R.string.documents_pending_question),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.onPrimaryVariant,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            BasicText(
                text = stringResource(R.string.documents_pending_answer),
                style = Theme.typography.hint.large.copy(
                    color = Theme.colors.onPrimaryVariant,
                    fontWeight = FontWeight.Normal,
                ),
            )
        }
    }
}

private fun List<com.carenest.provider.account.presentation.model.ProfessionalDocumentUiModel>.firstMissingTarget(): DocumentUploadTarget? = asSequence()
    .flatMap { it.files.asSequence() }
    .firstOrNull { it.url.isNullOrBlank() }
    ?.target

private fun openDocumentUrl(context: Context, url: String): Boolean =
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }.isSuccess

private fun getFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor.use { current ->
            if (current != null && current.moveToFirst()) {
                val index = current.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = current.getString(index)
            }
        }
    }
    if (result == null) {
        result = uri.path
        val lastSlash = result?.lastIndexOf('/')
        if (lastSlash != null && lastSlash != -1) {
            result = result.substring(lastSlash + 1)
        }
    }
    return result ?: "upload"
}

private fun persistReadPermission(context: Context, uri: Uri) {
    runCatching {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )
    }
}

private fun resolveMessage(context: Context, message: String): String {
    val resId = context.resources.getIdentifier(message, "string", context.packageName)
    return if (resId != 0) context.getString(resId) else message
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun ProfessionalDocumentsLightPreview() {
    SpTheme(isDarkTheme = false) {
        ProfessionalDocumentsContent(ProfessionalDocumentsUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun ProfessionalDocumentsDarkPreview() {
    SpTheme(isDarkTheme = true) {
        ProfessionalDocumentsContent(ProfessionalDocumentsUiState(), {})
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun ProfessionalDocumentsLoadingPreview() {
    SpTheme(isDarkTheme = false) {
        ProfessionalDocumentsContent(ProfessionalDocumentsUiState(isLoading = true), {})
    }
}
