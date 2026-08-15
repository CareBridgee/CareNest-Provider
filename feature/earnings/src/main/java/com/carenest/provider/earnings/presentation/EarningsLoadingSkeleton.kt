package com.carenest.provider.earnings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
internal fun EarningsLoadingSkeleton(modifier: Modifier = Modifier) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(164.dp),
                shape = Theme.shapes.extraLarge,
            )
        }
        item { ShimmerLine(Modifier.fillMaxWidth(.45f), height = 22.dp) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                repeat(3) { index ->
                    ShimmerPlaceholder(
                        modifier = Modifier
                            .width(if (index == 0) 132.dp else 112.dp)
                            .height(40.dp),
                        shape = CircleShape,
                    )
                }
            }
        }
        items(4) { EarningsItemSkeleton() }
    }
}

@Composable
private fun EarningsItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .padding(horizontal = Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        ShimmerPlaceholder(Modifier.size(48.dp), CircleShape)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ShimmerLine(Modifier.fillMaxWidth(.72f), height = 16.dp)
            ShimmerLine(Modifier.fillMaxWidth(.9f), height = 12.dp)
            ShimmerLine(Modifier.fillMaxWidth(.58f), height = 11.dp)
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ShimmerLine(Modifier.width(58.dp), height = 17.dp)
            ShimmerPlaceholder(Modifier.width(72.dp).height(20.dp), CircleShape)
        }
    }
}
