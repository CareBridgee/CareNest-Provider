package com.carenest.provider.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.feature.onboarding.R
import com.carenest.provider.presentation.components.OnboardingTokens
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import com.carenest.provider.designsystem.R as RD

private const val LOGO_ENTRANCE_DURATION_MILLIS = 800
private const val CARENEST_FADE_DELAY_MILLIS = 280L
private const val CARENEST_FADE_DURATION_MILLIS = 420
private const val PROVIDER_FADE_DELAY_MILLIS = 500L
private const val PROVIDER_FADE_DURATION_MILLIS = 380
private const val SUBTITLE_FADE_DELAY_MILLIS = 820L
private const val SUBTITLE_FADE_DURATION_MILLIS = 420
private const val LIGHT_SWEEP_DELAY_MILLIS = 880L
private const val LIGHT_SWEEP_DURATION_MILLIS = 500
private const val LOADING_FADE_DELAY_MILLIS = 1_220L
private const val LOADING_FADE_DURATION_MILLIS = 360
private const val GLOW_FADE_DURATION_MILLIS = 1_400
private const val LOGO_INITIAL_SCALE = 0.92f
private const val LOGO_INITIAL_ALPHA = 0.08f
private const val LIGHT_SWEEP_MAX_ALPHA = 0.28f
private const val LIGHT_SWEEP_BAND_WIDTH_FACTOR = 0.22f
private const val LOGO_GLOW_INITIAL_ALPHA = 0.18f

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
    val logoAlpha = remember(animateEntrance) {
        Animatable(if (animateEntrance) LOGO_INITIAL_ALPHA else 1f)
    }
    val logoScale = remember(animateEntrance) {
        Animatable(if (animateEntrance) LOGO_INITIAL_SCALE else 1f)
    }
    val careNestAlpha = remember(animateEntrance) {
        Animatable(if (animateEntrance) 0f else 1f)
    }
    val careNestOffset = remember(animateEntrance) {
        Animatable(if (animateEntrance) 1f else 0f)
    }
    val providerAlpha = remember(animateEntrance) {
        Animatable(if (animateEntrance) 0f else 1f)
    }
    val subtitleAlpha = remember(animateEntrance) {
        Animatable(if (animateEntrance) 0f else 1f)
    }
    val subtitleOffset = remember(animateEntrance) {
        Animatable(if (animateEntrance) 1f else 0f)
    }
    val lightSweep = remember(animateEntrance) {
        Animatable(if (animateEntrance) 0f else 1f)
    }
    val loadingAlpha = remember(animateEntrance) {
        Animatable(if (animateEntrance) 0f else 1f)
    }
    val glowAlpha = remember(animateEntrance) {
        Animatable(if (animateEntrance) LOGO_GLOW_INITIAL_ALPHA else 0f)
    }

    LaunchedEffect(animateEntrance) {
        if (!animateEntrance) return@LaunchedEffect

        coroutineScope {
            launch {
                logoAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = LOGO_ENTRANCE_DURATION_MILLIS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
            launch {
                logoScale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = LOGO_ENTRANCE_DURATION_MILLIS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
            launch {
                delay(CARENEST_FADE_DELAY_MILLIS)
                coroutineScope {
                    launch {
                        careNestAlpha.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = CARENEST_FADE_DURATION_MILLIS,
                                easing = FastOutSlowInEasing,
                            ),
                        )
                    }
                    launch {
                        careNestOffset.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = CARENEST_FADE_DURATION_MILLIS,
                                easing = FastOutSlowInEasing,
                            ),
                        )
                    }
                }
            }
            launch {
                delay(PROVIDER_FADE_DELAY_MILLIS)
                providerAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = PROVIDER_FADE_DURATION_MILLIS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
            launch {
                delay(SUBTITLE_FADE_DELAY_MILLIS)
                coroutineScope {
                    launch {
                        subtitleAlpha.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = SUBTITLE_FADE_DURATION_MILLIS,
                                easing = FastOutSlowInEasing,
                            ),
                        )
                    }
                    launch {
                        subtitleOffset.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = SUBTITLE_FADE_DURATION_MILLIS,
                                easing = FastOutSlowInEasing,
                            ),
                        )
                    }
                }
            }
            launch {
                delay(LIGHT_SWEEP_DELAY_MILLIS)
                lightSweep.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = LIGHT_SWEEP_DURATION_MILLIS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
            launch {
                delay(LOADING_FADE_DELAY_MILLIS)
                loadingAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = LOADING_FADE_DURATION_MILLIS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
            launch {
                glowAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = GLOW_FADE_DURATION_MILLIS,
                        easing = FastOutSlowInEasing,
                    ),
                )
            }
        }
    }

    val upwardOffsetPx = with(LocalDensity.current) {
        Theme.spacing.small.toPx()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.primary)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Theme.colors.primary.copy(
                            alpha = OnboardingTokens.decorativeSurfaceAlpha,
                        ),
                    ),
                ),
            )
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        PremiumLogoAnimation(
            lightSweepProgress = lightSweep.value,
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        BasicText(
            text = stringResource(R.string.splash_brand_name),
            modifier = Modifier.graphicsLayer {
                alpha = careNestAlpha.value
                translationY = upwardOffsetPx * careNestOffset.value
            },
            style = Theme.typography.displayMedium.copy(
                color = Theme.colors.onPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            text = stringResource(R.string.splash_provider_label),
            modifier = Modifier.graphicsLayer { alpha = providerAlpha.value },
            style = Theme.typography.hint.large.copy(
                color = Theme.colors.onPrimaryVariant,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(Modifier.height(Theme.spacing.medium))

        BasicText(
            text = stringResource(R.string.splash_tagline),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Theme.spacing.large)
                .graphicsLayer {
                    alpha = subtitleAlpha.value
                    translationY = upwardOffsetPx * subtitleOffset.value
                },
            style = Theme.typography.body.small.copy(
                color = Theme.colors.onPrimaryVariant,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
            ),
        )

        if (state.isLoading) {
            Spacer(Modifier.height(Theme.spacing.large))
            Column(
                modifier = Modifier.graphicsLayer { alpha = loadingAlpha.value },
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
            }
        }
    }
}

@Composable
private fun PremiumLogoAnimation(
    lightSweepProgress: Float,
    modifier: Modifier = Modifier,
) {
    val logoPainter = painterResource(RD.drawable.logo)
    val lightSweepAlpha =
        (sin(lightSweepProgress * PI).toFloat() * LIGHT_SWEEP_MAX_ALPHA).coerceAtLeast(0f)

    Box(
        modifier = modifier
            .background(
                shape = Theme.shapes.veryExtraLarge, color = Theme.colors.onPrimary
            )
            .size(128.dp),
    ) {
        Image(
            painter = logoPainter,
            contentDescription = stringResource(R.string.splash_logo_content_description),
            modifier = Modifier.matchParentSize()
        )
        if (lightSweepAlpha > 0f) {
            Image(
                painter = logoPainter,
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .lightSweep(lightSweepProgress),
            )
        }
    }
}

private fun Modifier.lightSweep(progress: Float): Modifier = drawWithContent {
    val bandWidth = size.width * LIGHT_SWEEP_BAND_WIDTH_FACTOR
    val centerX = -bandWidth + ((size.width + (bandWidth * 2f)) * progress)
    val sweepPath = Path().apply {
        moveTo(centerX - bandWidth, size.height)
        lineTo(centerX, 0f)
        lineTo(centerX + bandWidth, 0f)
        lineTo(centerX, size.height)
        close()
    }

    clipPath(sweepPath) {
        this@drawWithContent.drawContent()
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
