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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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

    val pulseAnim by floatTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlow"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.size(256.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(256.dp)
                    .alpha(pulseAnim * 0.15f)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Theme.colors.primary.copy(alpha = 0.3f),
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
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(40.dp)
                    )
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
                        .size(140.dp)
                        .scale(scaleAnim.value)
                        .background(Theme.colors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = RD.drawable.ic_white_check_mark),
                        contentDescription = null,
                        tint = Theme.colors.onPrimary,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp + floatBadge1.dp)
                    .background(
                        color = Theme.colors.primaryContainer,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
                    .offset(x = (-8).dp, y = 8.dp + floatBadge2.dp)
                    .background(
                        color = Theme.colors.primaryVariant,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.congratulations),
                style = Theme.typography.title,
                fontWeight = FontWeight.Bold,
                color = Theme.colors.primaryFont,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.account_verified),
                style = Theme.typography.body.medium,
                fontWeight = FontWeight.SemiBold,
                color = Theme.colors.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.network_welcome_message),
                style = Theme.typography.body.small,
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton(
                caption = stringResource(R.string.start_your_journey),
                onClick = onHomeClick,
                iconPainter = painterResource(id = RD.drawable.ic_next_arrow),
                iconPosition = ButtonIconPosition.End,
                modifier = Modifier.fillMaxWidth().padding(start=4.dp)
            )

            Text(
                text = stringResource(R.string.review_community_guidelines),
                style = Theme.typography.hint.large,
                fontWeight = FontWeight.SemiBold,
                color = Theme.colors.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCommunityGuidelinesClick() }
                    .padding(vertical = 12.dp)
            )
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Theme.colors.infoContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Theme.colors.divider,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_bag_success),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = stringResource(R.string.available_jobs),
                    style = Theme.typography.hint.large,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.colors.primaryFont
                )
                Text(
                    text = stringResource(R.string.jobs_near_you),
                    style = Theme.typography.body.medium,
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Theme.colors.infoContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Theme.colors.divider,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_start_primary),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = stringResource(R.string.network_perks),
                    style = Theme.typography.hint.large,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.colors.primaryFont
                )
                Text(
                    text = stringResource(R.string.network_rewards),
                    style = Theme.typography.body.medium,
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont
                )
            }
        }
    }
}
