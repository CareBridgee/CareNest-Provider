package com.carenest.provider.profile.presentation.completeprofile.reupload_document_screen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.profile.R
import com.carenest.provider.designsystem.R as RD

private data class CaptureTip(
    val iconRes: Int,
    val titleRes: Int,
    val descriptionRes: Int
)

@Composable
fun CaptureTipsSection(
    modifier: Modifier = Modifier
) {
    val tips = listOf(
        CaptureTip(
            iconRes = RD.drawable.ic_lighting,
            titleRes = R.string.good_lighting,
            descriptionRes = R.string.good_lighting_tip
        ),
        CaptureTip(
            iconRes = RD.drawable.ic_crop_free,
            titleRes = R.string.show_edges,
            descriptionRes = R.string.show_edges_tip
        ),
        CaptureTip(
            iconRes = RD.drawable.ic_focus,
            titleRes = R.string.stay_focused,
            descriptionRes = R.string.stay_focused_tip
        ),
        CaptureTip(
            iconRes = RD.drawable.ic_flatware,
            titleRes = R.string.flat_surface,
            descriptionRes = R.string.flat_surface_tip
        )
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        Text(
            text = stringResource(R.string.capture_tips),
            style = Theme.typography.title,
            fontWeight = FontWeight.SemiBold,
            color = Theme.colors.primary
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                TipCard(
                    tip = tips[0],
                    modifier = Modifier.weight(1f)
                )
                TipCard(
                    tip = tips[1],
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                TipCard(
                    tip = tips[2],
                    modifier = Modifier.weight(1f)
                )
                TipCard(
                    tip = tips[3],
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TipCard(
    tip: CaptureTip,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = Theme.colors.surface,
                shape = Theme.shapes.extraLarge
            )
            .border(
                width = Theme.spacing.extraSmall / 8,
                color = Theme.colors.surfaceVariant.copy(alpha = 0.5f),
                shape = Theme.shapes.extraLarge
            )
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        Icon(
            painter = painterResource(id = tip.iconRes),
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(Theme.size.iconMedium)
        )

        Text(
            text = stringResource(tip.titleRes),
            style = Theme.typography.body.small,
            fontWeight = FontWeight.SemiBold,
            color = Theme.colors.primaryFont
        )

        Text(
            text = stringResource(tip.descriptionRes),
            style = Theme.typography.hint.large,
            color = Theme.colors.secondaryFont
        )
    }
}
