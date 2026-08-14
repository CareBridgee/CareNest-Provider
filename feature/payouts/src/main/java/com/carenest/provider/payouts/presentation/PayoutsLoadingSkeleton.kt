package com.carenest.provider.payouts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.progressSemantics
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.shimmer.ShimmerLine
import com.carenest.provider.designsystem.components.shimmer.ShimmerPlaceholder
import com.carenest.provider.designsystem.theme.Theme

@Composable
internal fun PayoutsLoadingSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .progressSemantics()
            .padding(horizontal = Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        userScrollEnabled = false,
    ) {
        item {
            ShimmerPlaceholder(
                Modifier.fillMaxWidth().height(176.dp),
                Theme.shapes.extraLarge,
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)) {
                repeat(2) {
                    ShimmerPlaceholder(
                        modifier = Modifier.weight(1f).height(82.dp),
                        shape = Theme.shapes.large,
                    )
                }
            }
        }
        item { ShimmerLine(Modifier.fillMaxWidth(.58f), height = 15.dp) }
        item { ShimmerLine(Modifier.fillMaxWidth(.42f), height = 22.dp) }
        items(4) { PayoutHistoryItemSkeleton() }
    }
}

@Composable
private fun PayoutHistoryItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .padding(horizontal = Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        ShimmerPlaceholder(Modifier.size(48.dp), CircleShape)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            ShimmerLine(Modifier.fillMaxWidth(.7f), height = 16.dp)
            ShimmerLine(Modifier.fillMaxWidth(.9f), height = 12.dp)
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ShimmerLine(Modifier.width(64.dp), height = 18.dp)
            ShimmerPlaceholder(Modifier.width(70.dp).height(20.dp), CircleShape)
        }
    }
}
