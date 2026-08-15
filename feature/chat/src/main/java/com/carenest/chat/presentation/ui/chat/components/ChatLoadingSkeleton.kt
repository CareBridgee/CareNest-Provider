package com.carenest.chat.presentation.ui.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.progressSemantics
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.shimmer.ShimmerPlaceholder
import com.carenest.provider.designsystem.theme.Theme

@Composable
internal fun ChatLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .progressSemantics()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            ShimmerPlaceholder(Modifier.width(84.dp).height(26.dp), CircleShape)
        }
        MessageSkeleton(widthFraction = .68f, height = 66.dp, alignEnd = false)
        MessageSkeleton(widthFraction = .52f, height = 48.dp, alignEnd = true)
        MessageSkeleton(widthFraction = .78f, height = 82.dp, alignEnd = false)
        MessageSkeleton(widthFraction = .6f, height = 58.dp, alignEnd = true)
        Spacer(Modifier.weight(1f))
        MessageSkeleton(widthFraction = .72f, height = 64.dp, alignEnd = false)
    }
}

@Composable
private fun MessageSkeleton(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp,
    alignEnd: Boolean,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (alignEnd) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        ShimmerPlaceholder(
            modifier = Modifier.fillMaxWidth(widthFraction).height(height),
            shape = Theme.shapes.large,
        )
    }
}
