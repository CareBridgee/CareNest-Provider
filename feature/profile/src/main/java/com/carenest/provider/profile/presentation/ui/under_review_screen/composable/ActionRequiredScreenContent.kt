package com.carenest.provider.profile.presentation.ui.under_review_screen.composable

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.carenest.provider.designsystem.components.button.ButtonIconPosition
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.designsystem.R as RD

@Composable
fun ActionRequiredScreenContent(
    onUploadAgainClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PingTransition")

    val pingScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PingScale"
    )

    val pingAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PingAlpha"
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
        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        Box(
            modifier = Modifier.size(Theme.size.large * 2),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pingScale)
                    .alpha(pingAlpha)
                    .border(
                        width = Theme.spacing.extraSmall / 2,
                        color = Theme.colors.error.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize(0.85f)
                    .background(
                        color = Theme.colors.errorContainer.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(Theme.size.large),
                    tint = Theme.colors.error
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Theme.colors.surface,
                    shape = Theme.shapes.extraLarge
                )
                .border(
                    width = Theme.spacing.extraSmall / 8,
                    color = Theme.colors.surfaceVariant.copy(alpha = 0.5f),
                    shape = Theme.shapes.extraLarge
                )
                .padding(Theme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {

            Text(
                text = stringResource(R.string.action_required),
                style = Theme.typography.title,
                color = Theme.colors.error,
                fontWeight = FontWeight.Bold
            )

            val rejectedReason = stringResource(R.string.rejected_license_reason)
            val annotatedReason = buildAnnotatedString {
                val boldStart = "<b>"
                val boldEnd = "</b>"
                var cursor = 0
                while (true) {
                    val startIdx = rejectedReason.indexOf(boldStart, cursor)
                    if (startIdx == -1) { append(rejectedReason.substring(cursor)); break }
                    append(rejectedReason.substring(cursor, startIdx))
                    val endIdx = rejectedReason.indexOf(boldEnd, startIdx)
                    if (endIdx == -1) { append(rejectedReason.substring(startIdx)); break }
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Theme.colors.primaryFont)) {
                        append(rejectedReason.substring(startIdx + boldStart.length, endIdx))
                    }
                    cursor = endIdx + boldEnd.length
                }
            }
            Text(
                text = annotatedReason,
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                modifier = Modifier.fillMaxWidth()
            ) {
                InstructionRow(
                    iconRes = RD.drawable.ic_lighting,
                    titleRes = R.string.ensure_good_lighting,
                    descRes = R.string.good_lighting_desc
                )
                InstructionRow(
                    iconRes = RD.drawable.ic_focus,
                    titleRes = R.string.stay_in_focus,
                    descRes = R.string.in_focus_desc
                )
                InstructionRow(
                    iconRes = RD.drawable.ic_visible_fail,
                    titleRes = R.string.visible_edges,
                    descRes = R.string.visible_edges_desc
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                modifier = Modifier.fillMaxWidth()
            ) {
                PrimaryButton(
                    caption = stringResource(R.string.upload_again),
                    onClick = onUploadAgainClick,
                    iconPainter = painterResource(id = RD.drawable.ic_download),
                    iconPosition = ButtonIconPosition.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                PrimaryButton(
                    caption = stringResource(R.string.contact_support),
                    onClick = onContactSupportClick,
                    iconPainter = painterResource(id = RD.drawable.ic_contact),
                    iconPosition = ButtonIconPosition.Start,
                    containerColor = Theme.colors.primaryContainer,
                    contentColor = Theme.colors.onPrimaryContainer,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        val helpTip = stringResource(R.string.help_center_tip)
        val annotatedTip = buildAnnotatedString {
            val boldStart = "<b>"
            val boldEnd = "</b>"
            var cursor = 0
            while (true) {
                val startIdx = helpTip.indexOf(boldStart, cursor)
                if (startIdx == -1) { append(helpTip.substring(cursor)); break }
                append(helpTip.substring(cursor, startIdx))
                val endIdx = helpTip.indexOf(boldEnd, startIdx)
                if (endIdx == -1) { append(helpTip.substring(startIdx)); break }
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Theme.colors.primary)) {
                    append(helpTip.substring(startIdx + boldStart.length, endIdx))
                }
                cursor = endIdx + boldEnd.length
            }
        }
        Text(
            text = annotatedTip,
            style = Theme.typography.hint.large,
            color = Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.medium)
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))
    }
}

@Composable
private fun InstructionRow(
    iconRes: Int,
    titleRes: Int,
    descRes: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Theme.colors.backGround,
                shape = Theme.shapes.large
            )
            .border(
                width = Theme.spacing.extraSmall / 8,
                color = Theme.colors.divider,
                shape = Theme.shapes.large
            )
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(Theme.size.iconMedium)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall / 2)
        ) {
            Text(
                text = stringResource(titleRes),
                style = Theme.typography.body.small,
                color = Theme.colors.primaryFont
            )
            Text(
                text = stringResource(descRes),
                style = Theme.typography.hint.large,
                color = Theme.colors.secondaryFont
            )
        }
    }
}
