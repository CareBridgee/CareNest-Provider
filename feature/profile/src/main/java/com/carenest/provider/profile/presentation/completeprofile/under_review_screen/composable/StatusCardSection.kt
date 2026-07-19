package com.carenest.provider.profile.presentation.completeprofile.under_review_screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.designsystem.R as RD

@Composable
fun StatusCardSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, Theme.shapes.extraLarge)
            .padding(Theme.spacing.large)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
                ) {
                    Icon(
                        painter = painterResource(id = RD.drawable.ic_pen),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Theme.colors.primary
                    )
                    Text(
                        text = stringResource(R.string.application_received),
                        style = Theme.typography.body.small,
                        color = Theme.colors.primaryFont
                    )
                }
                Icon(
                    painter = painterResource(id = RD.drawable.ic_green_check),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Theme.colors.success
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Theme.colors.divider)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
                ) {
                    Icon(
                        painter = painterResource(id = RD.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Theme.colors.primary
                    )
                    Text(
                        text = stringResource(R.string.background_check),
                        style = Theme.typography.body.small,
                        color = Theme.colors.primaryFont
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
                ) {
                    Text(
                        text = stringResource(R.string.in_progress),
                        style = Theme.typography.hint.large,
                        color = Theme.colors.primary
                    )
                    PulsingDot(color = Theme.colors.primary)
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Theme.colors.divider)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
                ) {
                    Icon(
                        painter = painterResource(id = RD.drawable.ic_correct),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Theme.colors.hint
                    )
                    Text(
                        text = stringResource(R.string.final_approval),
                        style = Theme.typography.body.small,
                        color = Theme.colors.primaryFont
                    )
                }
                Text(
                    text = stringResource(R.string.pending),
                    style = Theme.typography.hint.large,
                    color = Theme.colors.hint
                )
            }
        }
    }
}