package com.carenest.provider.designsystem.components.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import kotlin.math.roundToInt

data class RatingSummaryState(
    val averageRating: Float,
    val totalReviews: Int,
    val ratingDistribution: Map<Int, Float>
)

@Composable
fun RatingSummary(
    state: RatingSummaryState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Theme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RatingHeader(
            averageRating = state.averageRating,
            totalReviews = state.totalReviews
        )

        Spacer(modifier = Modifier.height(Theme.spacing.large))

        RatingDistribution(
            distribution = state.ratingDistribution
        )
    }
}

@Composable
private fun RatingHeader(
    averageRating: Float,
    totalReviews: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surfaceVariant)
            .padding(Theme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "%.1f".format(averageRating),
            style = Theme.typography.display,
            color = Theme.colors.tint,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.padding(bottom = Theme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            var iconTint = Theme.colors.amber
            repeat(5) { index ->
                val starLevel = index + 1
                val icon = if (averageRating >= starLevel) {
                    iconTint = Theme.colors.amber
                    Icons.Filled.Star
                } else if (averageRating > index && averageRating < starLevel) {
                    iconTint = Theme.colors.amber
                    Icons.Filled.Star
                } else {
                    iconTint = Theme.colors.onDisable
                    Icons.Outlined.Star
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(Theme.size.iconMedium)
                )
            }
        }


        Text(
            text = stringResource(id = R.string.rating_reviews_suffix, totalReviews),
            style = Theme.typography.body.medium,
            color = Theme.colors.secondaryFont,
            fontSize = 16.sp
        )
    }
}


@Composable
private fun RatingDistribution(
    distribution: Map<Int, Float>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        // Star levels from 5 down to 1
        for (star in 5 downTo 1) {
            val percentage = distribution[star] ?: 0f
            RatingBarRow(
                starLevel = star,
                percentage = percentage
            )
        }
    }
}

@Composable
private fun RatingBarRow(
    starLevel: Int,
    percentage: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = starLevel.toString(),
            style = Theme.typography.body.medium,
            color = Theme.colors.secondaryFont,
            modifier = Modifier.width(24.dp)
        )

        Spacer(modifier = Modifier.width(Theme.spacing.small))

        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
            color = Theme.colors.tint,
            trackColor = Theme.colors.track,
            strokeCap = StrokeCap.Round,
        )

        Spacer(modifier = Modifier.width(Theme.spacing.medium))

        Text(
            text = "${(percentage * 100).roundToInt()}%",
            style = Theme.typography.body.medium,
            color = Theme.colors.secondaryFont,
            modifier = Modifier.width(44.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}


@Preview(showBackground = true)
@Composable
fun RatingSummaryPreview() {
    val state = RatingSummaryState(
        averageRating = 2.3f,
        totalReviews = 128,
        ratingDistribution = mapOf(
            5 to 0.72f,
            4 to 0.06f,
            3 to 0.01f,
            2 to 0.01f,
            1 to 0.03f
        )
    )
    SpTheme {
        Surface(color = Theme.colors.backGround) {
            RatingSummary(state = state)
        }
    }
}
