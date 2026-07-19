package com.carenest.provider.profile.presentation.completeprofile.reupload_document_screen.composable

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.designsystem.R as RD

@Composable
fun UploadZone(
    selectedFileUri: Uri?,
    onPickFile: () -> Unit,
    onRemoveFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedFileUri != null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .border(
                    width = Theme.spacing.extraSmall / 2,
                    color = Theme.colors.primary,
                    shape = Theme.shapes.extraLarge
                )
                .background(
                    color = Theme.colors.backGround,
                    shape = Theme.shapes.extraLarge
                )
                .padding(Theme.spacing.extraLarge),
            contentAlignment = Alignment.Center
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                AsyncImage(
                    model = selectedFileUri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.586f)
                        .clip(Theme.shapes.large),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .offset(
                            x = Theme.spacing.small,
                            y = -Theme.spacing.small
                        )
                        .size(Theme.size.small + Theme.spacing.extraSmall)
                        .background(
                            color = Theme.colors.error,
                            shape = CircleShape
                        )
                        .clickable { onRemoveFile() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = Theme.colors.onError,
                        modifier = Modifier.size(Theme.size.iconSmall)
                    )
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .border(
                    width = Theme.spacing.extraSmall / 2,
                    color = Theme.colors.onDisable,
                    shape = Theme.shapes.extraLarge
                )
                .background(
                    color = Theme.colors.backGround,
                    shape = Theme.shapes.extraLarge
                )
                .clickable { onPickFile() }
                .padding(Theme.spacing.extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(Theme.size.large)
                    .background(
                        color = Theme.colors.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_add_a_photo),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(Theme.size.iconMedium + Theme.spacing.extraSmall)
                )
            }

            Text(
                text = stringResource(R.string.upload_new_photo),
                style = Theme.typography.body.small,
                fontWeight = FontWeight.SemiBold,
                color = Theme.colors.primary
            )

            Text(
                text = stringResource(R.string.upload_file_types),
                style = Theme.typography.hint.large,
                color = Theme.colors.secondaryFont
            )
        }
    }
}
