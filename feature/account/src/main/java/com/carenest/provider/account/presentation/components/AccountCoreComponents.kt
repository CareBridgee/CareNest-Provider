package com.carenest.provider.account.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.account.R
import com.carenest.provider.designsystem.R as DesignSystemR
import com.carenest.provider.designsystem.theme.Theme
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun ProviderAccountTopBar(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.account_brand),
    avatarUrl: String? = null,
    showSettings: Boolean = false,
    onSettingsClick: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp)
            .background(Theme.colors.surface)
            .padding(horizontal = Theme.spacing.large, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onNavigateBack != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.account_back),
                tint = Theme.colors.tint,
                modifier = Modifier
                    .size(Theme.size.iconMedium)
                    .clickable(onClick = onNavigateBack),
            )
            Spacer(Modifier.width(Theme.spacing.medium))
        }
        AsyncImage(
            model = avatarUrl?.takeIf(String::isNotBlank),
            contentDescription = stringResource(R.string.account_profile_photo),
            placeholder = painterResource(DesignSystemR.drawable.nurse_image),
            error = painterResource(DesignSystemR.drawable.nurse_image),
            fallback = painterResource(DesignSystemR.drawable.nurse_image),
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Spacer(Modifier.width(Theme.spacing.medium))
        BasicText(
            text = title,
            style = Theme.typography.body.large.copy(
                color = Theme.colors.tint,
                fontWeight = FontWeight.SemiBold,
            ),
            modifier = Modifier.weight(1f),
        )
        if (showSettings) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = stringResource(R.string.account_settings),
                tint = Theme.colors.tint,
                modifier = Modifier
                    .size(26.dp)
                    .clickable(onClick = onSettingsClick),
            )
        }
    }
}

@Composable
fun ProviderStatisticCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .padding(horizontal = Theme.spacing.small, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BasicText(
            text = value,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.tint,
                fontWeight = FontWeight.Medium,
            ),
        )
        BasicText(
            text = label,
            style = Theme.typography.hint.large.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Normal,
            ),
        )
    }
}

@Composable
fun InitialsAvatar(
    initials: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Theme.colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = initials,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.tint,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}
