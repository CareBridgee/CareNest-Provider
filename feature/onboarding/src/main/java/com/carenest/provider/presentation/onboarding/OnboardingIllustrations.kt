package com.carenest.provider.feature.onboarding.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.carenest.provider.feature.onboarding.R
import com.carenest.provider.designsystem.R as DesignSystemR
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.feature.onboarding.presentation.components.OnboardingTokens

@Composable
internal fun OnboardingIllustration(page: OnboardingPage) {
    Box(modifier = Modifier.fillMaxSize()) {
        OnboardingImageCard(page = page)

        when (page.style) {
            OnboardingPageStyle.Network -> Unit
            OnboardingPageStyle.Visits -> ManageVisitsOverlays()
            OnboardingPageStyle.Career -> GrowCareerOverlays()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun OnboardingCareerBenefits(modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        OnboardingBenefitChip(
            iconRes = DesignSystemR.drawable.ic_wallet,
            labelRes = R.string.onboarding_daily_payouts,
        )
        OnboardingBenefitChip(
            iconRes = DesignSystemR.drawable.ic_badge,
            labelRes = R.string.onboarding_expert_badge,
        )
        OnboardingBenefitChip(
            iconRes = DesignSystemR.drawable.ic_time,
            labelRes = R.string.onboarding_custom_shifts,
        )
    }
}

@Composable
private fun OnboardingImageCard(page: OnboardingPage) {
    val imageAlignment = when (page.style) {
        OnboardingPageStyle.Career -> Alignment.CenterEnd
        OnboardingPageStyle.Network,
        OnboardingPageStyle.Visits,
        -> Alignment.Center
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                elevation = OnboardingTokens.illustrationElevation,
                shape = RoundedCornerShape(OnboardingTokens.illustrationCornerRadius),
                clip = false,
            )
            .clip(RoundedCornerShape(OnboardingTokens.illustrationCornerRadius))
            .background(Theme.colors.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(page.illustrationRes),
            contentDescription = stringResource(page.illustrationContentDescriptionRes),
            contentScale = ContentScale.FillWidth,
            alignment = imageAlignment,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(OnboardingTokens.illustrationAspectRatio),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Theme.colors.backGround.copy(
                                alpha = OnboardingTokens.illustrationGradientAlpha,
                            ),
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun BoxScope.ManageVisitsOverlays() {
    OnboardingOverlayCard(
        iconRes = DesignSystemR.drawable.ic_heart_beat,
        labelRes = R.string.onboarding_vitals,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(OnboardingTokens.overlayPlacementInset),
    )
    OnboardingOverlayCard(
        iconRes = DesignSystemR.drawable.ic_location,
        labelRes = R.string.onboarding_live_map,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(OnboardingTokens.overlayPlacementInset),
    )
}

@Composable
private fun BoxScope.GrowCareerOverlays() {
    EarningsOverlay(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(OnboardingTokens.overlayPlacementInset),
    )
    ReviewOverlay(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(OnboardingTokens.overlayPlacementInset),
    )
    FlexibleHoursChip(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(
                x = -OnboardingTokens.flexibleHoursOffset,
                y = OnboardingTokens.flexibleHoursOffset,
            )
            .rotate(OnboardingTokens.flexibleHoursRotationDegrees),
    )
}

@Composable
private fun OnboardingOverlayCard(
    @DrawableRes iconRes: Int,
    @StringRes
    labelRes: Int,
    modifier: Modifier = Modifier,
) {
    OverlaySurface(modifier = modifier) {
        Row(
            modifier = Modifier.padding(
                horizontal = OnboardingTokens.overlayHorizontalPadding,
                vertical = OnboardingTokens.overlayVerticalPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OverlayIcon(iconRes = iconRes)
            BasicText(
                text = stringResource(labelRes),
                style = Theme.typography.hint.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

@Composable
private fun EarningsOverlay(modifier: Modifier = Modifier) {
    OverlaySurface(modifier = modifier.width(OnboardingTokens.earningsOverlayWidth)) {
        Column(
            modifier = Modifier.padding(OnboardingTokens.overlayContentPadding),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
        ) {
            BasicText(
                text = stringResource(R.string.onboarding_weekly_earnings),
                style = Theme.typography.hint.large.copy(color = Theme.colors.hint),
            )
            BasicText(
                text = stringResource(R.string.onboarding_earnings_amount),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OverlayIcon(
                    iconRes = DesignSystemR.drawable.ic_trending_up,
                    size = OnboardingTokens.overlaySmallIconSize,
                    tint = Theme.colors.success,
                )
                BasicText(
                    text = stringResource(R.string.onboarding_earnings_trend),
                    style = Theme.typography.hint.small.copy(color = Theme.colors.primary),
                )
            }
        }
    }
}

@Composable
private fun ReviewOverlay(modifier: Modifier = Modifier) {
    OverlaySurface(modifier = modifier.width(OnboardingTokens.reviewOverlayWidth)) {
        Row(
            modifier = Modifier.padding(OnboardingTokens.overlayContentPadding),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)) {
                Row(horizontalArrangement = Arrangement.spacedBy(OnboardingTokens.ratingIconSpacing)) {
                    repeat(OnboardingTokens.ratingStarCount) {
                        OverlayIcon(
                            iconRes = DesignSystemR.drawable.ic_star,
                            size = OnboardingTokens.ratingIconSize,
                            tint = Theme.colors.warning,
                        )
                    }
                }
                BasicText(
                    text = stringResource(R.string.onboarding_review_summary),
                    style = Theme.typography.hint.large.copy(color = Theme.colors.primaryFont),
                )
            }
            Box(
                modifier = Modifier
                    .size(OnboardingTokens.verificationIconContainerSize)
                    .clip(RoundedCornerShape(OnboardingTokens.verificationIconCornerRadius))
                    .background(Theme.colors.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                OverlayIcon(
                    iconRes = DesignSystemR.drawable.ic_badge,
                    tint = Theme.colors.primary,
                )
            }
        }
    }
}

@Composable
private fun FlexibleHoursChip(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(OnboardingTokens.overlayCornerRadius),
        color = Theme.colors.primary,
        shadowElevation = OnboardingTokens.overlayElevation,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = OnboardingTokens.overlayHorizontalPadding,
                vertical = OnboardingTokens.overlayVerticalPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OverlayIcon(
                iconRes = DesignSystemR.drawable.ic_booking,
                tint = Theme.colors.onPrimary,
            )
            BasicText(
                text = stringResource(R.string.onboarding_flexible_hours),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}

@Composable
private fun OnboardingBenefitChip(
    @DrawableRes iconRes: Int,
    @StringRes labelRes: Int,
) {
    Surface(
        shape = RoundedCornerShape(OnboardingTokens.benefitChipCornerRadius),
        color = Theme.colors.primaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = OnboardingTokens.benefitChipHorizontalPadding,
                vertical = OnboardingTokens.benefitChipVerticalPadding,
            ),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OverlayIcon(
                iconRes = iconRes,
                size = OnboardingTokens.benefitChipIconSize,
            )
            BasicText(
                text = stringResource(labelRes),
                style = Theme.typography.hint.large.copy(
                    color = Theme.colors.onPrimaryContainer,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }
    }
}

@Composable
private fun OverlaySurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(OnboardingTokens.overlayCornerRadius),
        color = Theme.colors.surface.copy(alpha = OnboardingTokens.overlaySurfaceAlpha),
        shadowElevation = OnboardingTokens.overlayElevation,
        border = BorderStroke(
            width = OnboardingTokens.overlayBorderWidth,
            color = Theme.colors.surface.copy(alpha = OnboardingTokens.overlayBorderAlpha),
        ),
        content = content,
    )
}

@Composable
private fun OverlayIcon(
    @DrawableRes iconRes: Int,
    size: Dp = OnboardingTokens.overlayIconSize,
    tint: Color = Theme.colors.primary,
) {
    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tint),
        modifier = Modifier.size(size),
    )
}
