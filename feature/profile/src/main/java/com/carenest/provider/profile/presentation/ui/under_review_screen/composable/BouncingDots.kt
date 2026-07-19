package com.carenest.provider.profile.presentation.ui.under_review_screen.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun BouncingDot(delayMillis: Int, color: Color) {
    val transition = rememberInfiniteTransition(label = "DotBounce")
    val offsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 400,
                easing = LinearEasing,
                delayMillis = delayMillis
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotOffsetY"
    )
    Box(
        modifier = Modifier
            .size(Theme.spacing.small + Theme.spacing.extraSmall / 2)
            .offset(y = offsetY.dp)
            .clip(CircleShape)
            .background(color)
    )
}
