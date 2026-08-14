package com.carenest.provider.designsystem.components.shimmer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.Theme

/** A reusable decorative block for screen-specific loading skeletons. */
@Composable
fun ShimmerPlaceholder(
    modifier: Modifier,
    shape: Shape = Theme.shapes.small,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .shimmerEffect(),
    )
}

/** A text-shaped shimmer placeholder. Width is supplied by the caller. */
@Composable
fun ShimmerLine(
    modifier: Modifier,
    height: Dp = 14.dp,
) {
    ShimmerPlaceholder(
        modifier = modifier.height(height),
        shape = RoundedCornerShape(percent = 50),
    )
}
