package com.carenest.request.presentation.ui.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.chip.StatusChip
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.request.R

@Composable
fun RequestsListHeader(pendingCount: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.primaryContainer, shape = RoundedCornerShape(24.dp))
            .padding(horizontal = Theme.spacing.medium , vertical = Theme.spacing.extraSmall),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing.small, bottom = Theme.spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                BasicText(
                    text = stringResource(R.string.requests_list_pending_label),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondary.copy(alpha = 0.40f),
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                BasicText(
                    text = stringResource(R.string.request_des),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.onInfoContainer.copy(alpha = 0.70f),
                    ),
                )
            }
            StatusChip(
                label = stringResource(R.string.requests_list_offers_count, pendingCount),
                containerColor = Theme.colors.onPrimary,
                contentColor = Theme.colors.onPrimaryContainer,
                modifier = Modifier.align(Alignment.CenterVertically).height(30.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        RequestsListHeader(pendingCount = 2)
    }
}
