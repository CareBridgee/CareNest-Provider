package com.carenest.provider.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.account.presentation.model.ReviewUiModel
import com.carenest.provider.account.R
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun RatingStars(
    rating: Int,
    modifier: Modifier = Modifier,
    iconSize: Int = 20,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = null,
                tint = Theme.colors.warning,
                modifier = Modifier.size(iconSize.dp),
            )
        }
    }
}

@Composable
fun RatingDistributionRow(
    star: Int,
    progress: Float,
    percentage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = star.toString(),
            style = Theme.typography.hint.large.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier.width(24.dp),
        )
        androidx.compose.material3.LinearProgressIndicator(
            progress = { progress },
            color = Theme.colors.tint,
            trackColor = Theme.colors.track,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Theme.spacing.small)
                .clip(CircleShape),
        )
        BasicText(
            text = stringResource(R.string.reviews_percentage, percentage),
            style = Theme.typography.hint.large.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Normal,
            ),
            modifier = Modifier.width(38.dp),
        )
    }
}

@Composable
fun ReviewFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicText(
        text = label,
        style = Theme.typography.body.small.copy(
            color = if (selected) Theme.colors.onPrimary else Theme.colors.secondaryFont,
            fontWeight = FontWeight.Normal,
        ),
        modifier = modifier
            .clip(CircleShape)
            .background(if (selected) Theme.colors.tint else Theme.colors.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    )
}

@Composable
fun ReviewCard(
    review: ReviewUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, Theme.shapes.extraLarge)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            InitialsAvatar(review.initials)
            Spacer(Modifier.width(Theme.spacing.medium))
            Column(Modifier.weight(1f)) {
                BasicText(
                    text = stringResource(review.authorRes),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                RatingStars(rating = review.rating, iconSize = 18)
            }
            BasicText(
                text = stringResource(review.dateRes),
                    style = Theme.typography.hint.large.copy(
                    color = Theme.colors.hint,
                    fontWeight = FontWeight.Normal,
                ),
            )
        }
        BasicText(
            text = stringResource(review.bodyRes),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Normal,
            ),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.MedicalServices,
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(Theme.spacing.small))
            BasicText(
                text = stringResource(review.serviceRes),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.tint,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}
