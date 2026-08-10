package com.carenest.provider.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.carenest.provider.account.presentation.model.DocumentUploadTarget
import com.carenest.provider.account.presentation.model.DocumentStatus
import com.carenest.provider.account.presentation.model.ProfessionalDocumentUiModel
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.R as DesignSystemR

@Composable
fun DocumentStatusBadge(
    status: DocumentStatus,
    verifiedLabel: String,
    pendingLabel: String,
    rejectedLabel: String,
    modifier: Modifier = Modifier,
) {
    val containerColor = when (status) {
        DocumentStatus.Verified -> Theme.colors.successContainer
        DocumentStatus.Pending -> Theme.colors.warningContainer
        DocumentStatus.Rejected -> Theme.colors.errorContainer
    }
    val contentColor = when (status) {
        DocumentStatus.Verified -> Theme.colors.onSuccessContainer
        DocumentStatus.Pending -> Theme.colors.onWarningContainer
        DocumentStatus.Rejected -> Theme.colors.onErrorContainer
    }
    val label = when (status) {
        DocumentStatus.Verified -> verifiedLabel
        DocumentStatus.Pending -> pendingLabel
        DocumentStatus.Rejected -> rejectedLabel
    }
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (status == DocumentStatus.Verified) {
                Icons.Rounded.CheckCircle
            } else {
                Icons.Rounded.MoreHoriz
            },
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(14.dp),
        )
        Spacer(Modifier.width(Theme.spacing.extraSmall))
        BasicText(
            text = label,
            style = Theme.typography.hint.small.copy(
                color = contentColor,
                fontWeight = FontWeight.Normal,
            ),
        )
    }
}

@Composable
fun ProfessionalDocumentCard(
    document: ProfessionalDocumentUiModel,
    title: String,
    supportingText: String,
    viewLabel: String,
    replaceLabel: String,
    verifiedLabel: String,
    pendingLabel: String,
    rejectedLabel: String,
    missingLabel: String,
    onViewClick: (DocumentUploadTarget) -> Unit,
    onReplaceClick: (DocumentUploadTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, Theme.shapes.extraLarge)
            .padding(Theme.spacing.medium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DocumentIcon(document)
            Spacer(Modifier.width(Theme.spacing.medium))
            Column(Modifier.weight(1f)) {
                BasicText(
                    text = title,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = Theme.typography.body.medium.lineHeight,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                BasicText(
                    text = supportingText,
                    style = Theme.typography.hint.large.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
            Spacer(Modifier.width(Theme.spacing.small))
            DocumentStatusBadge(
                status = document.status,
                verifiedLabel = verifiedLabel,
                pendingLabel = pendingLabel,
                rejectedLabel = rejectedLabel,
            )
        }
        Spacer(Modifier.height(Theme.spacing.medium))
        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
            document.files.forEach { file ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    BasicText(
                        text = stringResource(file.labelRes),
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (file.isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Theme.colors.primary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            if (file.url.isNullOrBlank()) {
                                BasicText(
                                    text = missingLabel,
                                    style = Theme.typography.hint.large.copy(
                                        color = Theme.colors.hint,
                                        fontWeight = FontWeight.Normal,
                                    ),
                                )
                            } else {
                                BasicText(
                                    text = viewLabel,
                                    style = Theme.typography.body.small.copy(
                                        color = Theme.colors.tint,
                                        fontWeight = FontWeight.Medium,
                                    ),
                                    modifier = Modifier.clickable {
                                        onViewClick(file.target)
                                    },
                                )
                            }
                            BasicText(
                                text = replaceLabel,
                                style = Theme.typography.body.small.copy(
                                    color = Theme.colors.tint,
                                    fontWeight = FontWeight.Medium,
                                ),
                                modifier = Modifier.clickable {
                                    onReplaceClick(file.target)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentIcon(document: ProfessionalDocumentUiModel) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(Theme.shapes.medium)
            .background(Theme.colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(document.iconRes),
            contentDescription = null,
            tint = if (
                document.iconRes == DesignSystemR.drawable.ic_account_acls_certificate
            ) {
                Color.Unspecified
            } else {
                Theme.colors.tint
            },
            modifier = if (
                document.iconRes == DesignSystemR.drawable.ic_account_acls_certificate
            ) {
                Modifier
                    .width(19.dp)
                    .height(25.dp)
            } else {
                Modifier.size(25.dp)
            },
        )
    }
}
