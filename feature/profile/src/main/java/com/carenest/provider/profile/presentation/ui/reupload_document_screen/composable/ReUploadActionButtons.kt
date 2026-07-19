package com.carenest.provider.profile.presentation.ui.reupload_document_screen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R

@Composable
fun ReUploadActionButtons(
    hasFileSelected: Boolean,
    isUploading: Boolean,
    onUpdateDocumentClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        PrimaryButton(
            caption = stringResource(R.string.update_document),
            onClick = onUpdateDocumentClick,
            isDisabled = !hasFileSelected,
            isLoading = isUploading,
            modifier = Modifier.fillMaxWidth()
        )

        PrimaryButton(
            caption = stringResource(R.string.cancel),
            onClick = onCancelClick,
            containerColor = Theme.colors.primaryContainer,
            contentColor = Theme.colors.primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
