package com.carenest.provider.profile.presentation.completeprofile.reupload_document_screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R

@Composable
fun RejectionNoticeCard(
    rejectionReason: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Theme.colors.errorContainer.copy(alpha = 0.2f),
                shape = Theme.shapes.extraLarge
            )
            .border(
                width = Theme.spacing.extraSmall / 4,
                color = Theme.colors.error.copy(alpha = 0.1f),
                shape = Theme.shapes.extraLarge
            )
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = Theme.colors.error
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
        ) {
            Text(
                text = stringResource(R.string.previous_upload_rejected),
                style = Theme.typography.body.small,
                fontWeight = FontWeight.SemiBold,
                color = Theme.colors.error
            )

            Text(
                text = rejectionReason,
                style = Theme.typography.hint.large,
                color = Theme.colors.secondaryFont
            )
        }
    }
}
