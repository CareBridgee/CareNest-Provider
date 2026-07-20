package com.carenest.provider.feature.onboarding.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.feature.onboarding.R
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.feature.onboarding.presentation.components.OnboardingTokens
import com.carenest.provider.designsystem.R as DesignSystemR

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToAuthentication: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            SplashEffect.NavigateToOnboarding -> onNavigateToOnboarding()
            SplashEffect.NavigateToAuthentication -> onNavigateToAuthentication()
        }
    }

    SplashContent(state = state)
}

@Composable
fun SplashContent(
    state: SplashState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.primaryVariant),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(OnboardingTokens.patternSize)
                .clip(RoundedCornerShape(OnboardingTokens.patternSize))
                .background(
                    Theme.colors.onPrimaryVariant.copy(
                        alpha = OnboardingTokens.decorativeSurfaceAlpha,
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(OnboardingTokens.splashLogoSize)
                    .clip(RoundedCornerShape(OnboardingTokens.splashLogoCornerRadius))
                    .background(Theme.colors.surface),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_work),
                    contentDescription = stringResource(R.string.splash_logo_content_description),
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(OnboardingTokens.splashLogoIconSize),
                )
            }

            Spacer(Modifier.height(Theme.spacing.large))
            BasicText(
                text = stringResource(R.string.splash_brand_name),
                style = Theme.typography.displayMedium.copy(
                    color = Theme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )
            BasicText(
                text = stringResource(R.string.splash_provider_label),
                style = Theme.typography.hint.large.copy(
                    color = Theme.colors.onPrimaryVariant,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                ),
            )

            Spacer(Modifier.height(Theme.spacing.extraLarge))
            Image(
                painter = painterResource(R.drawable.splash_provider_tools),
                contentDescription = stringResource(R.string.splash_image_content_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(Theme.size.onboardingImage)
                    .height(OnboardingTokens.splashIllustrationHeight)
                    .clip(RoundedCornerShape(OnboardingTokens.illustrationCornerRadius)),
            )
            Spacer(Modifier.height(Theme.spacing.extraLarge))
            BasicText(
                text = stringResource(R.string.splash_tagline),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.onPrimaryVariant,
                    textAlign = TextAlign.Center,
                ),
            )
        }

        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .safeDrawingPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Theme.size.small),
                    color = Theme.colors.onPrimary,
                    trackColor = Theme.colors.onPrimaryVariant.copy(
                        alpha = OnboardingTokens.decorativeSurfaceAlpha,
                    ),
                )
                Spacer(Modifier.height(Theme.spacing.small))
                BasicText(
                    text = stringResource(R.string.splash_initializing),
                    style = Theme.typography.hint.small.copy(
                        color = Theme.colors.onPrimaryVariant,
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(Modifier.height(Theme.spacing.extraLarge))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SplashPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        SplashContent(state = SplashState())
    }
}
