package com.carenest.provider.profile.presentation.completeprofile.under_review_screen.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.button.ButtonIconPosition
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.designsystem.R as RD

@Composable
fun SuccessScreenContent(
    onHomeClick: () -> Unit,
    onCommunityGuidelinesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
    }

    val floatTransition = rememberInfiniteTransition(label = "FloatTransition")

    val floatFrame by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatFrame"
    )

    val floatBadge1 by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatBadge1"
    )

    val floatBadge2 by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatBadge2"
    )

    val pulseAlpha by floatTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlow"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Theme.spacing.extraLarge,
                vertical = Theme.spacing.large
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.large)
    ) {
        Spacer(modifier = Modifier.height(Theme.spacing.small))

        Box(
            modifier = Modifier.size(256.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(256.dp)
                    .alpha(pulseAlpha)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Theme.colors.primary.copy(alpha = 0.4f),
                                Theme.colors.primary.copy(alpha = 0f)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(192.dp)
                    .offset(y = floatFrame.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Theme.colors.surface)
                    .border(
                        width = 1.dp,
                        color = Theme.colors.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scaleAnim.value)
                        .background(Theme.colors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(Theme.size.large),
                        tint = Theme.colors.onPrimary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(
                        x = Theme.spacing.small,
                        y = (-Theme.spacing.small.value).dp + floatBadge1.dp
                    )
                    .background(
                        color = Theme.colors.primaryContainer,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(
                        horizontal = Theme.spacing.medium,
                        vertical = Theme.spacing.small
                    )
            ) {
                Text(
                    text = stringResource(R.string.verified),
                    style = Theme.typography.hint.large,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.colors.onPrimaryContainer
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(
                        x = (-Theme.spacing.small.value).dp,
                        y = Theme.spacing.small + floatBadge2.dp
                    )
                    .background(
                        color = Theme.colors.primaryVariant,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(
                        horizontal = Theme.spacing.medium,
                        vertical = Theme.spacing.small
                    )
            ) {
                Text(
                    text = stringResource(R.string.approved),
                    style = Theme.typography.hint.large,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.colors.onPrimaryVariant
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
        ) {
            Text(
                text = stringResource(R.string.congratulations),
                style = Theme.typography.display,
                fontWeight = FontWeight.Bold,
                color = Theme.colors.primaryFont,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.account_verified),
                style = Theme.typography.title,
                fontWeight = FontWeight.SemiBold,
                color = Theme.colors.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))

            Text(
                text = stringResource(R.string.network_welcome_message),
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton(
                caption = stringResource(R.string.start_your_journey),
                onClick = onHomeClick,
                iconPainter = painterResource(id = RD.drawable.ic_next_arrow),
                iconPosition = ButtonIconPosition.End,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.review_community_guidelines),
                style = Theme.typography.body.small,
                fontWeight = FontWeight.SemiBold,
                color = Theme.colors.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCommunityGuidelinesClick() }
                    .padding(vertical = Theme.spacing.small)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
        ) {
            StatBentoCard(
                iconRes = RD.drawable.ic_bag_success,
                iconTint = Theme.colors.primary,
                labelRes = R.string.available_jobs,
                valueRes = R.string.jobs_near_you,
                modifier = Modifier.weight(1f)
            )

            StatBentoCard(
                iconRes = RD.drawable.ic_start_primary,
                iconTint = Theme.colors.primary,
                labelRes = R.string.network_perks,
                valueRes = R.string.network_rewards,
                useTertiaryIcon = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.medium))
    }
}

@Composable
private fun StatBentoCard(
    iconRes: Int,
    iconTint: Color,
    labelRes: Int,
    valueRes: Int,
    modifier: Modifier = Modifier,
    useTertiaryIcon: Boolean = false
) {

    Column(
        modifier = modifier
            .background(
                color = Theme.colors.infoContainer,
                shape = Theme.shapes.extraLarge
            )
            .border(
                width = Theme.spacing.extraSmall / 4,
                color = Theme.colors.divider,
                shape = Theme.shapes.extraLarge
            )
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(Theme.size.iconMedium + Theme.spacing.extraSmall)
        )
        Text(
            text = stringResource(labelRes),
            style = Theme.typography.body.small,
            fontWeight = FontWeight.SemiBold,
            color = Theme.colors.primaryFont
        )
        Text(
            text = stringResource(valueRes),
            style = Theme.typography.title,
            fontWeight = FontWeight.Bold,
            color = Theme.colors.primaryFont
        )
    }
}
