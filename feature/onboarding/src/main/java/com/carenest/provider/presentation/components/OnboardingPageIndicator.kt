package com.carenest.provider.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun OnboardingPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val color by animateColorAsState(
                targetValue = if (isSelected) {
                    Theme.colors.primary
                } else {
                    Theme.colors.primaryContainer
                },
                label = "onboardingIndicatorColor",
            )

            Box(
                modifier = Modifier
                    .width(
                        if (isSelected) {
                            OnboardingTokens.activeIndicatorWidth
                        } else {
                            OnboardingTokens.inactiveIndicatorWidth
                        },
                    )
                    .height(OnboardingTokens.pageIndicatorHeight)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}
