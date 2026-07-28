package com.carenest.provider.account.presentation.documents

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileUpload
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.account.R
import com.carenest.provider.account.presentation.components.ProfessionalDocumentsLoadingSkeleton
import com.carenest.provider.account.presentation.components.ProfessionalDocumentCard
import com.carenest.provider.account.presentation.components.ProviderAccountTopBar
import com.carenest.provider.account.presentation.model.DocumentStatus
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun ProfessionalDocumentsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenDocument: (String) -> Unit = {},
    onUploadDocument: () -> Unit = {},
    viewModel: ProfessionalDocumentsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            ProfessionalDocumentsEffect.NavigateBack -> onNavigateBack()
            is ProfessionalDocumentsEffect.OpenDocument -> onOpenDocument(effect.id)
            ProfessionalDocumentsEffect.UploadDocument -> onUploadDocument()
        }
    }
    ProfessionalDocumentsContent(state, viewModel::onIntent, modifier)
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
        if (state.isLoading) {
            ProfessionalDocumentsLoadingSkeleton()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
            item {
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
            items(state.documents, key = { it.id }) { document ->
                ProfessionalDocumentCard(
                    document = document,
                    title = stringResource(document.titleRes),
                    uploadedDate = stringResource(document.uploadedDateRes),
                    actionLabel = stringResource(
                        if (document.status == DocumentStatus.Verified) {
                            R.string.documents_view
                        } else {
                            R.string.documents_edit
                        },
                    ),
                    verifiedLabel = stringResource(R.string.documents_verified),
                    pendingLabel = stringResource(R.string.documents_pending),
                    onClick = {
                        onIntent(ProfessionalDocumentsIntent.DocumentClicked(document.id))
                    },
                )
            }
            item {
                UploadDocumentCard(
                    onClick = { onIntent(ProfessionalDocumentsIntent.UploadDocumentClicked) },
                )
            }
            item { PendingDocumentInfo() }
            }
        }
    }
}

@Composable
private fun UploadDocumentCard(onClick: () -> Unit) {
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
            .clickable(onClick = onClick)
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
                imageVector = Icons.Rounded.FileUpload,
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(32.dp),
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
