package com.carenest.provider.profile.presentation.completeprofile.under_review_screen.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.R as RD
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun IllustrationSection(modifier: Modifier = Modifier) {
    val floatTransition = rememberInfiniteTransition(label = "FloatTransition")

    val floatCard by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatCard"
    )

    val floatBadge1 by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatBadge1"
    )

    val floatBadge2 by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatBadge2"
    )

    val pulseTransition = rememberInfiniteTransition(label = "PulseTransition")

    val ringScale1 by pulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RingScale1"
    )
    val ringAlpha1 by pulseTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RingAlpha1"
    )

    val ringScale2 by pulseTransition.animateFloat(
        initialValue = 1.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RingScale2"
    )
    val ringAlpha2 by pulseTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RingAlpha2"
    )

    Box(
        modifier = modifier.size(256.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(256.dp)
                .scale(ringScale1)
                .alpha(ringAlpha1)
                .border(Theme.spacing.extraSmall / 4, Theme.colors.primaryVariant.copy(alpha = 0.2f), CircleShape)
        )

        Box(
            modifier = Modifier
                .size(224.dp)
                .scale(ringScale2)
                .alpha(ringAlpha2)
                .border(Theme.spacing.extraSmall / 4, Theme.colors.primaryVariant.copy(alpha = 0.4f), CircleShape)
        )

        Box(
            modifier = Modifier
                .size(192.dp)
                .offset(y = floatCard.dp)
                .clip(Theme.shapes.extraLarge)
                .background(Theme.colors.surface)
                .border(Theme.spacing.extraSmall / 4, Theme.colors.divider, Theme.shapes.extraLarge)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Theme.spacing.small)
                    .background(Theme.colors.primary)
                    .align(Alignment.TopCenter)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_check_mark),
                    contentDescription = null,
                    modifier = Modifier.size(Theme.size.large),
                    tint = Theme.colors.primary
                )
                Spacer(modifier = Modifier.height(Theme.spacing.medium))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BouncingDot(delayMillis = 0, color = Theme.colors.primary)
                    BouncingDot(delayMillis = 100, color = Theme.colors.primary)
                    BouncingDot(delayMillis = 200, color = Theme.colors.primary)
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = Theme.spacing.medium, y = (-Theme.spacing.medium.value).dp + floatBadge1.dp)
                .size(Theme.size.large)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.surface)
                .border(Theme.spacing.extraSmall / 4, Theme.colors.divider, Theme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_id_card),
                contentDescription = null,
                modifier = Modifier.size(Theme.size.iconMedium),
                tint = Theme.colors.secondary
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-Theme.spacing.medium.value).dp, y = Theme.spacing.medium.value.dp + floatBadge2.dp)
                .size(56.dp)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.surface)
                .border(Theme.spacing.extraSmall / 4, Theme.colors.divider, Theme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_person),
                contentDescription = null,
                modifier = Modifier.size(Theme.size.iconMedium),
                tint = Theme.colors.primary
            )
        }
    }
}