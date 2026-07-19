package com.carenest.provider.feature.onboarding.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.components.button.ButtonIconPosition
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.swipingcards.SwipeDirection
import com.carenest.provider.designsystem.components.swipingcards.SwipingCardStack
import com.carenest.provider.designsystem.components.swipingcards.SwipingCardStackState
import com.carenest.provider.designsystem.components.swipingcards.rememberSwipingCardStackState
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.feature.onboarding.R
import com.carenest.provider.feature.onboarding.presentation.components.OnboardingPageIndicator
import com.carenest.provider.feature.onboarding.presentation.components.OnboardingTokens

@Composable
fun OnboardingScreen(
    onNavigateToAuthentication: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cardStackState = rememberSwipingCardStackState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is OnboardingEffect.MoveToPage -> cardStackState.swipe(SwipeDirection.Left)
            OnboardingEffect.NavigateToAuthentication -> onNavigateToAuthentication()
        }
    }

    OnboardingContent(
        state = state,
        cardStackState = cardStackState,
        onIntent = viewModel::onIntent,
    )
}

@Composable
fun OnboardingContent(
    state: OnboardingState,
    cardStackState: SwipingCardStackState,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
    pages: List<OnboardingPage> = providerOnboardingPages,
) {
    val currentPageIndex = state.currentPageIndex.coerceIn(0, providerOnboardingPages.lastIndex)
    val currentPage = providerOnboardingPages[currentPageIndex]

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .safeDrawingPadding(),
        containerColor = Theme.colors.backGround,
        topBar = {
            OnboardingTopBar(
                onSkip = { onIntent(OnboardingIntent.SkipClicked) },
                isDisabled = state.isCompleting || cardStackState.isAnimating,
                showSkip = !state.isLastPage,
                modifier = Modifier.padding(horizontal = OnboardingTokens.horizontalMargin),
            )
        },
    ) { contentPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            val isCompact = maxHeight < OnboardingTokens.compactScreenHeight
            val illustrationAreaHeight = if (isCompact) {
                OnboardingTokens.compactIllustrationAreaHeight
            } else {
                OnboardingTokens.illustrationAreaHeight
            }
            val textContentHeight = if (isCompact) {
                OnboardingTokens.compactTextContentMinHeight
            } else {
                OnboardingTokens.textContentMinHeight
            }
            val sectionSpacing = if (isCompact) Theme.spacing.small else Theme.spacing.medium

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = maxHeight)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = OnboardingTokens.horizontalMargin)
                    .padding(vertical = Theme.spacing.small),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                SwipingCardStack(
                    cards = pages,
                    key = OnboardingPage::id,
                    state = cardStackState,
                    maxVisibleCards = OnboardingTokens.visibleCardCount,
                    modifier = Modifier
                        .fillMaxWidth(OnboardingTokens.deckWidthFraction)
                        .height(illustrationAreaHeight),
                    onSwipe = { result ->
                        val frontPageId = result.resultingOrder.firstOrNull()?.id
                        val newPageIndex = providerOnboardingPages.indexOfFirst { it.id == frontPageId }
                        if (newPageIndex >= 0) {
                            onIntent(OnboardingIntent.PageChanged(newPageIndex))
                        }
                    },
                ) { page ->
                    OnboardingIllustration(page = page)
                }

                Spacer(Modifier.height(sectionSpacing))

                OnboardingTextContent(
                    page = currentPage,
                    minimumHeight = textContentHeight,
                )

                Spacer(Modifier.height(Theme.spacing.small))

                OnboardingPageIndicator(
                    pageCount = state.totalPageCount,
                    currentPage = currentPageIndex,
                )

                Spacer(Modifier.height(sectionSpacing))

                OnboardingBottomActions(
                    state = state,
                    isAnimating = cardStackState.isAnimating,
                    onIntent = onIntent,
                )
            }
        }
    }
}

@Composable
private fun OnboardingTopBar(
    onSkip: () -> Unit,
    isDisabled: Boolean,
    showSkip: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Theme.size.componentsNormalHeight),
    ) {
        BasicText(
            text = stringResource(R.string.onboarding_provider_brand),
            modifier = Modifier.align(Alignment.CenterStart),
            style = Theme.typography.title.copy(
                color = Theme.colors.primary,
                fontWeight = FontWeight.Bold,
            ),
        )
        if (showSkip) {
            TextButton(
                onClick = onSkip,
                enabled = !isDisabled,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .heightIn(min = Theme.size.componentsNormalHeight),
            ) {
                BasicText(
                    text = stringResource(R.string.onboarding_skip),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
        }
    }
}

@Composable
private fun OnboardingTextContent(
    page: OnboardingPage,
    minimumHeight: androidx.compose.ui.unit.Dp,
) {
    val isCareerPage = page.style == OnboardingPageStyle.Career
    val horizontalAlignment = if (isCareerPage) {
        Alignment.Start
    } else {
        Alignment.CenterHorizontally
    }
    val textAlignment = if (isCareerPage) TextAlign.Start else TextAlign.Center

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = minimumHeight),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = page,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            contentKey = OnboardingPage::id,
            label = "onboardingText",
        ) { targetPage ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                BasicText(
                    text = stringResource(targetPage.titleRes),
                    modifier = Modifier.fillMaxWidth(),
                    style = Theme.typography.displayMedium.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                        textAlign = textAlignment,
                    ),
                )
                BasicText(
                    text = stringResource(targetPage.descriptionRes),
                    modifier = Modifier.fillMaxWidth(),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        textAlign = textAlignment,
                    ),
                )
                if (isCareerPage) {
                    OnboardingCareerBenefits()
                }
            }
        }
    }
}

@Composable
private fun OnboardingBottomActions(
    state: OnboardingState,
    isAnimating: Boolean,
    onIntent: (OnboardingIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val actionLabel = if (state.isLastPage) {
        R.string.onboarding_get_started
    } else {
        R.string.onboarding_next
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .padding(horizontal = OnboardingTokens.horizontalMargin)
            .padding(top = Theme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PrimaryButton(
            caption = stringResource(actionLabel),
            iconPainter = painterResource(R.drawable.onboarding_arrow_forward),
            iconPosition = ButtonIconPosition.End,
            isDisabled = state.isCompleting || isAnimating,
            isLoading = state.isCompleting,
            onClick = {
                onIntent(
                    if (state.isLastPage) {
                        OnboardingIntent.FinalActionClicked
                    } else {
                        OnboardingIntent.NextClicked
                    },
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun OnboardingPreview(pageIndex: Int) {
    SpTheme(isDarkTheme = false, languageCode = "en") {
        val cardStackState = rememberSwipingCardStackState()
        val previewPages = providerOnboardingPages.drop(pageIndex) +
            providerOnboardingPages.take(pageIndex)

        OnboardingContent(
            state = OnboardingState(currentPageIndex = pageIndex),
            cardStackState = cardStackState,
            onIntent = {},
            pages = previewPages,
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "First page")
@Composable
private fun FirstPagePreview() = OnboardingPreview(pageIndex = 0)

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "Manage visits")
@Composable
private fun MiddlePagePreview() = OnboardingPreview(pageIndex = 1)

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "Grow career")
@Composable
private fun FinalPagePreview() = OnboardingPreview(pageIndex = 2)

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Compact phone")
@Composable
private fun CompactPhonePreview() = OnboardingPreview(pageIndex = 0)
