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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.button.ButtonIconPosition
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.designsystem.R as RD

@Composable
fun SuccessScreenContent(
    onDashboardClick: () -> Unit,
    onCommunityGuidelinesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val floatTransition = rememberInfiniteTransition(label = "FloatTransition")

    val floatBadge1 by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatBadge1"
    )

    val floatBadge2 by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FloatBadge2"
    )

    val checkmarkScale = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        checkmarkScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.size(256.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(192.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Theme.colors.primary.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(192.dp)
                    .scale(checkmarkScale.value)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(40.dp),
                        clip = false
                    )
                    .clip(RoundedCornerShape(40.dp))
                    .background(Theme.colors.surface)
                    .border(
                        width = 1.dp,
                        color = Theme.colors.primary.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Theme.colors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = RD.drawable.ic_check_mark_white),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Theme.colors.onPrimary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-8).dp + floatBadge1.dp)
                    .background(
                        color = Theme.colors.primaryContainer,
                        shape = RoundedCornerShape(9999.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.verified),
                    style = Theme.typography.body.small,
                    color = Theme.colors.onPrimaryContainer
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-8).dp, y = 8.dp + floatBadge2.dp)
                    .background(
                        color = Theme.colors.successContainer,
                        shape = RoundedCornerShape(9999.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.approved),
                    style = Theme.typography.body.small,
                    color = Theme.colors.onSuccessContainer
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.congratulations),
                style = Theme.typography.display,
                color = Theme.colors.primaryFont,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.account_verified),
                style = Theme.typography.title,
                color = Theme.colors.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.network_welcome_message),
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryButton(
                caption = stringResource(R.string.start_your_journey),
                onClick = onDashboardClick,
                iconPainter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowForward),
                iconPosition = ButtonIconPosition.End,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.review_community_guidelines),
                style = Theme.typography.body.small.copy(fontWeight = FontWeight.Bold),
                color = Theme.colors.primary,
                modifier = Modifier
                    .clip(Theme.shapes.small)
                    .clickable(onClick = onCommunityGuidelinesClick)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_bag),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = stringResource(R.string.available_jobs),
                    style = Theme.typography.body.small,
                    color = Theme.colors.primaryFont
                )

                Text(
                    text = stringResource(R.string.jobs_near_you),
                    style = Theme.typography.title,
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_primary_star),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Theme.colors.primary
                )

                Text(
                    text = stringResource(R.string.network_perks),
                    style = Theme.typography.body.small,
                    color = Theme.colors.primaryFont
                )

                Text(
                    text = stringResource(R.string.network_rewards),
                    style = Theme.typography.title,
                    color = Theme.colors.primaryFont
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
            SuccessScreenContent(
                onDashboardClick = {},
                onCommunityGuidelinesClick = {}
            )
    }
}