package com.carenest.provider.account.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carenest.provider.account.R
import com.carenest.provider.designsystem.R as DesignSystemR
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun ProfileMenuHero(
    name: String,
    specialty: String,
    rating: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .clickable(onClick = onClick)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(DesignSystemR.drawable.nurse_image),
                contentDescription = stringResource(R.string.account_profile_photo),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primaryContainer)
                    .padding(5.dp)
                    .clip(CircleShape),
            )
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Theme.colors.primary)
                    .padding(horizontal = Theme.spacing.small, vertical = Theme.spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Star,
                    contentDescription = null,
                    tint = Theme.colors.onPrimary,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(2.dp))
                BasicText(
                    text = rating,
                    style = Theme.typography.hint.large.copy(
                        color = Theme.colors.onPrimary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
        Spacer(Modifier.height(Theme.spacing.medium))
        BasicText(
            text = name,
            style = Theme.typography.title.copy(
                color = Theme.colors.primaryFont,
                textAlign = TextAlign.Center,
            ),
        )
        Spacer(Modifier.height(Theme.spacing.small))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(DesignSystemR.drawable.ic_services),
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(Theme.spacing.small))
            BasicText(
                text = specialty,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.tint,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Composable
fun ProfileMenuCard(
    title: String,
    subtitle: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showVerifiedDot: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = Theme.spacing.medium, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(23.dp),
            )
        }
        Spacer(Modifier.width(Theme.spacing.medium))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicText(
                    text = title,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                if (showVerifiedDot) {
                    Spacer(Modifier.width(Theme.spacing.small))
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Theme.colors.success),
                    )
                }
            }
            BasicText(
                text = subtitle,
                style = Theme.typography.hint.large.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal,
                ),
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = stringResource(R.string.account_navigate_next, title),
            tint = Theme.colors.onDisable,
            modifier = Modifier.size(24.dp),
        )
    }
}
