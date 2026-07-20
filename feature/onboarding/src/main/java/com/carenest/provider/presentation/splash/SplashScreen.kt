package com.carenest.provider.feature.onboarding.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_BRAND_DELAY_MILLIS = 120L
private const val SPLASH_ICON_INITIAL_SCALE = 0.96f

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
    animateEntrance: Boolean = true,
) {
    val initialAlpha = if (animateEntrance) 0f else 1f
    val iconAlpha = remember(animateEntrance) { Animatable(initialAlpha) }
    val iconScale = remember(animateEntrance) {
        Animatable(if (animateEntrance) SPLASH_ICON_INITIAL_SCALE else 1f)
    }
    val brandAlpha = remember(animateEntrance) { Animatable(initialAlpha) }

    LaunchedEffect(animateEntrance) {
        if (!animateEntrance) return@LaunchedEffect

        coroutineScope {
            launch {
                iconAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = OnboardingTokens.splashProgressDurationMillis,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
            launch {
                iconScale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = OnboardingTokens.splashProgressDurationMillis,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
            launch {
                delay(SPLASH_BRAND_DELAY_MILLIS)
                brandAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = OnboardingTokens.splashProgressDurationMillis,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.primaryVariant)
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.splash_provider_icon),
            contentDescription = stringResource(R.string.splash_logo_content_description),
            colorFilter = ColorFilter.tint(Theme.colors.onPrimary),
            modifier = Modifier
                .size(Theme.size.logo - Theme.spacing.large)
                .graphicsLayer {
                    alpha = iconAlpha.value
                    scaleX = iconScale.value
                    scaleY = iconScale.value
                },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = brandAlpha.value },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
        }

        Spacer(Modifier.height(Theme.spacing.medium))
        BasicText(
            text = stringResource(R.string.splash_tagline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.large)
                .graphicsLayer { alpha = brandAlpha.value },
            style = Theme.typography.body.small.copy(
                color = Theme.colors.onPrimaryVariant,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            ),
        )

        if (state.isLoading) {
            Spacer(Modifier.height(Theme.spacing.large))
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
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SplashPreview() {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        SplashContent(
            state = SplashState(),
            animateEntrance = false,
        )
    }
}
