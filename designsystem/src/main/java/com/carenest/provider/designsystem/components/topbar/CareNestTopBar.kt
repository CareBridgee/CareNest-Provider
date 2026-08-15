package com.carenest.provider.designsystem.components.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import coil3.compose.AsyncImage
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.theme.Theme

sealed interface TopBarLeading {
    data class Back(val onBackClick: () -> Unit) : TopBarLeading
}

@Composable
fun CareNestTopBar(
    title: String,
    modifier: Modifier = Modifier,
    leading: TopBarLeading? = null,
    trailingAvatarUrl: String? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .shadow(
                    elevation = Theme.spacing.extraSmall,
                    clip = false
                )
                .background(Theme.colors.surface)
                .padding(
                    horizontal = Theme.spacing.large,
                    vertical = Theme.spacing.small + Theme.spacing.extraSmall
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
        if (leading is TopBarLeading.Back) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.back),
                tint = Theme.colors.primary,
                modifier = Modifier
                    .size(Theme.size.iconMedium)
                    .clickable(onClick = leading.onBackClick)
            )
            Spacer(modifier = Modifier.size(Theme.spacing.small + Theme.spacing.extraSmall))
        }

        Text(
            text = title,
            style = Theme.typography.title.copy(
                color = Theme.colors.primary
            ),
            modifier = Modifier.weight(1f)
        )

        if (trailingAvatarUrl != null) {
            AsyncImage(
                model = trailingAvatarUrl.takeIf(String::isNotBlank),
                contentDescription = stringResource(R.string.profile_avatar_content_description),
                placeholder = painterResource(R.drawable.nurse_image),
                error = painterResource(R.drawable.nurse_image),
                fallback = painterResource(R.drawable.nurse_image),
                modifier = Modifier
                    .size(Theme.size.medium)
                    .clip(CircleShape)
                    .border(Theme.spacing.extraSmall / 4, Theme.colors.onDisable, CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        }
    }
}
