package com.carenest.request.presentation.ui.visit_summary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R
import com.carenest.provider.designsystem.R as RD

@Composable
fun VisitRatingDialogContent(
    selectedRating: Int,
    isSubmitting: Boolean,
    onStarSelected: (Int) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Theme.colors.surface, RoundedCornerShape(20.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(Theme.colors.primaryContainer, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Theme.colors.onPrimaryContainer,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.Center),
                )
            }
            Row(
                modifier = Modifier
                    .size(25.dp)
                    .background(Theme.colors.primary, CircleShape),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_verfied_white),
                    tint = Theme.colors.onPrimary,
                    contentDescription = null,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.visit_rating_title),
            style = Theme.typography.title,
            color = Theme.colors.primaryFont,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.visit_rating_subtitle),
            style = Theme.typography.body.medium,
            color = Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            for (star in 1..5) {
                IconButton(onClick = { onStarSelected(star) }) {
                    Icon(
                        imageVector = if (star <= selectedRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = stringResource(
                            R.string.visit_rating_star_content_description,
                            star
                        ),
                        tint = if (star <= selectedRating) Theme.colors.amber else Theme.colors.hint,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onSubmit, enabled = selectedRating > 0 && !isSubmitting) {
            Text(
                text = stringResource(R.string.visit_rating_submit_button),
                style = Theme.typography.body.medium,
                color = Theme.colors.primary,
            )
        }
        TextButton(onClick = onDismiss) {
            Text(
                text = stringResource(R.string.visit_rating_skip_button),
                style = Theme.typography.body.small,
                color = Theme.colors.secondaryFont,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SpTheme {
        VisitRatingDialogContent(
            selectedRating = 3,
            isSubmitting = false,
            onStarSelected = {},
            onSubmit = {},
            onDismiss = {},
        )
    }
}