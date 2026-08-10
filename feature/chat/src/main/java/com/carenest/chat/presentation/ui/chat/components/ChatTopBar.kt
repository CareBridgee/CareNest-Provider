package com.carenest.chat.presentation.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.chat.R
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun ChatTopBar(
    participantName: String,
    photoUrl: String? = null,
    isOnline: Boolean,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.surface)
            .padding(Theme.spacing.space12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.chat_back_content_description),
                tint = Theme.colors.primary,
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Theme.colors.primaryContainer),
        ) {
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Theme.colors.onPrimaryContainer,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            if (isOnline) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Theme.colors.success, CircleShape)
                        .align(Alignment.BottomEnd),
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = participantName,
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Theme.colors.primaryFont,
            )

            Text(
                text = if (isOnline) {
                    stringResource(R.string.chat_status_active_now)
                } else {
                    stringResource(R.string.chat_status_offline)
                },
                style = Theme.typography.hint.small,
                color = Theme.colors.secondaryFont,
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Theme.colors.primaryVariant)
                .clickable(onClick = onCallClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = Theme.colors.onPrimaryVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        ChatTopBar(
            participantName = "Elena Vance",
            photoUrl = null,
            isOnline = true,
            onBackClick = {},
            onCallClick = {},
        )
    }
}