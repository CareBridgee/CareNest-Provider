package com.carenest.request.presentation.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.progressSemantics
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.shimmer.ShimmerLine
import com.carenest.provider.designsystem.components.shimmer.ShimmerPlaceholder
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun OfferDetailsLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = skeletonContainer(modifier),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        ShimmerPlaceholder(Modifier.fillMaxWidth().height(76.dp), Theme.shapes.medium)
        DetailsSectionSkeleton(height = 104.dp, showAvatar = true)
        DetailsSectionSkeleton(height = 118.dp)
        DetailsSectionSkeleton(height = 142.dp)
        DetailsSectionSkeleton(height = 88.dp)
    }
}

@Composable
fun OfferConfirmedLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = skeletonContainer(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        Spacer(Modifier.height(Theme.spacing.medium))
        ShimmerPlaceholder(Modifier.size(84.dp), CircleShape)
        ShimmerLine(Modifier.fillMaxWidth(.58f), height = 25.dp)
        ShimmerLine(Modifier.fillMaxWidth(.82f), height = 14.dp)
        ShimmerPlaceholder(Modifier.fillMaxWidth().height(72.dp), Theme.shapes.medium)
        DetailsSectionSkeleton(height = 112.dp, showAvatar = true)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            repeat(2) {
                ShimmerPlaceholder(
                    modifier = Modifier.weight(1f).height(116.dp),
                    shape = Theme.shapes.extraLarge,
                )
            }
        }
        ShimmerPlaceholder(Modifier.fillMaxWidth().height(52.dp), Theme.shapes.large)
        ShimmerPlaceholder(Modifier.fillMaxWidth().height(52.dp), Theme.shapes.large)
    }
}

@Composable
fun PatientSummaryLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = skeletonContainer(modifier),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        DetailsSectionSkeleton(height = 132.dp, showAvatar = true)
        DetailsSectionSkeleton(height = 132.dp)
        repeat(4) { index ->
            DetailsSectionSkeleton(height = if (index == 3) 150.dp else 104.dp)
        }
    }
}

@Composable
fun VisitCompletedLoadingSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = skeletonContainer(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        ShimmerPlaceholder(Modifier.size(90.dp), CircleShape)
        ShimmerLine(Modifier.fillMaxWidth(.58f), height = 28.dp)
        ShimmerLine(Modifier.fillMaxWidth(.78f), height = 14.dp)
        ShimmerPlaceholder(Modifier.fillMaxWidth().height(210.dp), Theme.shapes.large)
        ShimmerPlaceholder(Modifier.fillMaxWidth().height(88.dp), Theme.shapes.large)
        ShimmerPlaceholder(Modifier.fillMaxWidth().height(52.dp), Theme.shapes.large)
    }
}

@Composable
private fun DetailsSectionSkeleton(
    height: androidx.compose.ui.unit.Dp,
    showAvatar: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        if (showAvatar) {
            ShimmerPlaceholder(Modifier.size(56.dp), CircleShape)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ShimmerLine(Modifier.fillMaxWidth(.48f), height = 17.dp)
            ShimmerLine(Modifier.fillMaxWidth(), height = 13.dp)
            ShimmerLine(Modifier.fillMaxWidth(.72f), height = 13.dp)
        }
    }
}

@Composable
private fun skeletonContainer(modifier: Modifier): Modifier = modifier
    .fillMaxSize()
    .progressSemantics()
    .verticalScroll(rememberScrollState(), enabled = false)
    .padding(Theme.spacing.medium)
