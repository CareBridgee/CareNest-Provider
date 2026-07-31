package com.carenest.provider.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.components.shimmer.shimmerEffect
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun ProfileMenuLoadingSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SkeletonSurface(horizontalAlignment = Alignment.CenterHorizontally) {
                ShimmerBlock(Modifier.size(100.dp), CircleShape)
                Spacer(Modifier.height(Theme.spacing.medium))
                ShimmerLine(.7f, 22.dp)
                Spacer(Modifier.height(Theme.spacing.small))
                ShimmerLine(.52f, 14.dp)
            }
        }
        repeat(6) {
            item { MenuRowSkeleton() }
        }
        item {
            Spacer(Modifier.height(Theme.spacing.small))
            ShimmerBlock(
                Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                Theme.shapes.extraLarge,
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = Theme.spacing.small,
                        bottom = Theme.spacing.medium,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                ShimmerBlock(Modifier.width(150.dp).height(12.dp))
            }
        }
    }
}

@Composable
fun PublicProfileLoadingSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        item {
            SkeletonSurface(horizontalAlignment = Alignment.CenterHorizontally) {
                ShimmerBlock(Modifier.size(116.dp), CircleShape)
                Spacer(Modifier.height(Theme.spacing.medium))
                ShimmerLine(.76f, 22.dp)
                Spacer(Modifier.height(Theme.spacing.small))
                ShimmerLine(.55f, 14.dp)
                Spacer(Modifier.height(Theme.spacing.medium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                ) {
                    repeat(3) {
                        ShimmerBlock(
                            Modifier
                                .weight(1f)
                                .height(64.dp),
                            Theme.shapes.large,
                        )
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                repeat(2) {
                    ShimmerBlock(
                        Modifier
                            .weight(1f)
                            .height(48.dp),
                        Theme.shapes.large,
                    )
                }
            }
        }
        item { AboutCardSkeleton() }
        item { ReviewCardSkeleton(lineCount = 3) }
    }
}

@Composable
fun ProfessionalDocumentsLoadingSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ShimmerLine(.7f, 24.dp)
            Spacer(Modifier.height(Theme.spacing.small))
            ShimmerLine(1f, 14.dp)
            Spacer(Modifier.height(6.dp))
            ShimmerLine(.78f, 14.dp)
            Spacer(Modifier.height(Theme.spacing.medium))
        }
        repeat(3) {
            item { DocumentCardSkeleton() }
        }
        item {
            SkeletonSurface(
                modifier = Modifier.height(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                ShimmerBlock(Modifier.size(64.dp), Theme.shapes.large)
                Spacer(Modifier.height(Theme.spacing.medium))
                ShimmerLine(.62f, 18.dp)
                Spacer(Modifier.height(Theme.spacing.small))
                ShimmerLine(.5f, 12.dp)
            }
        }
        item {
            SkeletonSurface {
                ShimmerLine(.7f, 16.dp)
                Spacer(Modifier.height(Theme.spacing.medium))
                repeat(3) { index ->
                    ShimmerLine(if (index == 2) .72f else 1f, 12.dp)
                    if (index != 2) Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SettingsLoadingSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            ShimmerLine(.38f, 24.dp)
            Spacer(Modifier.height(Theme.spacing.small))
            ShimmerLine(.78f, 14.dp)
        }
        item {
            ShimmerLine(.36f, 14.dp)
            Spacer(Modifier.height(Theme.spacing.small))
            SkeletonSurface(contentPadding = PaddingValues(0.dp)) {
                SettingsRowSkeleton()
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Theme.colors.divider),
                )
                SettingsRowSkeleton(showValue = false)
            }
        }
        item {
            ShimmerLine(.4f, 14.dp)
            Spacer(Modifier.height(Theme.spacing.small))
            SkeletonSurface(contentPadding = PaddingValues(0.dp)) {
                SettingsRowSkeleton(showValue = false)
            }
        }
    }
}

@Composable
fun RatingsAndReviewsLoadingSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        item {
            SkeletonSurface(horizontalAlignment = Alignment.CenterHorizontally) {
                ShimmerBlock(
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    Theme.shapes.large,
                )
                Spacer(Modifier.height(Theme.spacing.medium))
                repeat(5) {
                    RatingDistributionSkeleton()
                    if (it != 4) Spacer(Modifier.height(Theme.spacing.small))
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                repeat(4) {
                    ShimmerBlock(
                        Modifier
                            .weight(1f)
                            .height(40.dp),
                        CircleShape,
                    )
                }
            }
        }
        repeat(3) {
            item { ReviewCardSkeleton(lineCount = 3) }
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Theme.spacing.medium),
                contentAlignment = Alignment.Center,
            ) {
                ShimmerBlock(
                    Modifier
                        .width(208.dp)
                        .height(44.dp),
                    Theme.shapes.extraLarge,
                )
            }
        }
    }
}

@Composable
fun WalletLoadingSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    ShimmerLine(.56f, 24.dp)
                    Spacer(Modifier.height(Theme.spacing.small))
                    ShimmerLine(.8f, 14.dp)
                }
                ShimmerBlock(Modifier.width(92.dp).height(32.dp), CircleShape)
            }
        }
        item {
            ShimmerLine(.34f, 14.dp)
            Spacer(Modifier.height(Theme.spacing.small))
            PrimaryPayoutCardSkeleton()
        }
        item {
            Row(Modifier.fillMaxWidth()) {
                ShimmerLine(.45f, 14.dp, Modifier.weight(1f))
                ShimmerBlock(Modifier.width(58.dp).height(14.dp))
            }
        }
        repeat(2) {
            item { MenuRowSkeleton() }
        }
        item {
            SkeletonSurface {
                ShimmerLine(.46f, 16.dp)
                Spacer(Modifier.height(Theme.spacing.medium))
                ShimmerLine(1f, 12.dp)
                Spacer(Modifier.height(8.dp))
                ShimmerLine(.92f, 12.dp)
                Spacer(Modifier.height(8.dp))
                ShimmerLine(.7f, 12.dp)
            }
        }
    }
}

@Composable
private fun MenuRowSkeleton() {
    SkeletonSurface(
        contentPadding = PaddingValues(
            horizontal = Theme.spacing.medium,
            vertical = 12.dp,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerBlock(Modifier.size(44.dp), Theme.shapes.medium)
            Spacer(Modifier.width(Theme.spacing.medium))
            Column(Modifier.weight(1f)) {
                ShimmerLine(.58f, 16.dp)
                Spacer(Modifier.height(6.dp))
                ShimmerLine(.76f, 12.dp)
            }
            ShimmerBlock(Modifier.size(20.dp), CircleShape)
        }
    }
}

@Composable
private fun AboutCardSkeleton() {
    SkeletonSurface {
        ShimmerLine(.38f, 20.dp)
        Spacer(Modifier.height(Theme.spacing.medium))
        repeat(5) { index ->
            ShimmerLine(if (index == 4) .64f else 1f, 14.dp)
            if (index != 4) Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(Theme.spacing.medium))
        Row(horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
            ShimmerBlock(Modifier.width(110.dp).height(28.dp), CircleShape)
            ShimmerBlock(Modifier.width(124.dp).height(28.dp), CircleShape)
        }
        Spacer(Modifier.height(Theme.spacing.small))
        ShimmerBlock(Modifier.width(92.dp).height(28.dp), CircleShape)
    }
}

@Composable
private fun ReviewCardSkeleton(lineCount: Int) {
    SkeletonSurface {
        Row(verticalAlignment = Alignment.Top) {
            ShimmerBlock(Modifier.size(44.dp), CircleShape)
            Spacer(Modifier.width(Theme.spacing.medium))
            Column(Modifier.weight(1f)) {
                ShimmerLine(.48f, 14.dp)
                Spacer(Modifier.height(6.dp))
                ShimmerBlock(Modifier.width(88.dp).height(16.dp))
            }
            ShimmerBlock(Modifier.width(72.dp).height(12.dp))
        }
        Spacer(Modifier.height(Theme.spacing.medium))
        repeat(lineCount) { index ->
            ShimmerLine(if (index == lineCount - 1) .7f else 1f, 13.dp)
            if (index != lineCount - 1) Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(Theme.spacing.medium))
        ShimmerLine(.58f, 14.dp)
    }
}

@Composable
private fun DocumentCardSkeleton() {
    SkeletonSurface {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerBlock(Modifier.size(48.dp), Theme.shapes.medium)
            Spacer(Modifier.width(Theme.spacing.medium))
            Column(Modifier.weight(1f)) {
                ShimmerLine(.72f, 16.dp)
                Spacer(Modifier.height(6.dp))
                ShimmerLine(.82f, 12.dp)
            }
            Spacer(Modifier.width(Theme.spacing.small))
            Column(horizontalAlignment = Alignment.End) {
                ShimmerBlock(Modifier.width(72.dp).height(22.dp), CircleShape)
                Spacer(Modifier.height(Theme.spacing.small))
                ShimmerBlock(Modifier.width(42.dp).height(14.dp))
            }
        }
    }
}

@Composable
private fun SettingsRowSkeleton(showValue: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing.medium, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBlock(Modifier.size(24.dp), CircleShape)
        Spacer(Modifier.width(Theme.spacing.medium))
        ShimmerLine(.42f, 16.dp, Modifier.weight(1f))
        if (showValue) {
            ShimmerBlock(Modifier.width(64.dp).height(14.dp))
        } else {
            ShimmerBlock(Modifier.width(46.dp).height(26.dp), CircleShape)
        }
    }
}

@Composable
private fun RatingDistributionSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBlock(Modifier.width(20.dp).height(12.dp))
        Spacer(Modifier.width(Theme.spacing.small))
        ShimmerBlock(
            Modifier
                .weight(1f)
                .height(8.dp),
            CircleShape,
        )
        Spacer(Modifier.width(Theme.spacing.small))
        ShimmerBlock(Modifier.width(34.dp).height(12.dp))
    }
}

@Composable
private fun PrimaryPayoutCardSkeleton() {
    SkeletonSurface {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerBlock(Modifier.size(48.dp), Theme.shapes.medium)
            Spacer(Modifier.width(Theme.spacing.medium))
            Column(Modifier.weight(1f)) {
                ShimmerLine(.62f, 18.dp)
                Spacer(Modifier.height(6.dp))
                ShimmerLine(.78f, 13.dp)
            }
        }
        Spacer(Modifier.height(Theme.spacing.large))
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                ShimmerLine(.48f, 12.dp)
                Spacer(Modifier.height(6.dp))
                ShimmerLine(.7f, 18.dp)
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(.7f),
            ) {
                ShimmerLine(.5f, 12.dp)
                Spacer(Modifier.height(6.dp))
                ShimmerBlock(Modifier.width(76.dp).height(26.dp), CircleShape)
            }
        }
        Spacer(Modifier.height(Theme.spacing.large))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            repeat(2) {
                ShimmerBlock(
                    Modifier
                        .weight(1f)
                        .height(48.dp),
                    Theme.shapes.large,
                )
            }
        }
    }
}

@Composable
private fun SkeletonSurface(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Theme.spacing.medium),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .padding(contentPadding),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        content = content,
    )
}

@Composable
private fun ShimmerLine(
    widthFraction: Float,
    height: Dp,
    modifier: Modifier = Modifier,
) {
    ShimmerBlock(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height),
    )
}

@Composable
private fun ShimmerBlock(
    modifier: Modifier,
    shape: Shape = RoundedCornerShape(5.dp),
) {
    Spacer(
        modifier = modifier
            .clip(shape)
            .shimmerEffect(),
    )
}
