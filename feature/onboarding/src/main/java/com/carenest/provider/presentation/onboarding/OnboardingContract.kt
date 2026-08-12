package com.carenest.provider.presentation.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingPage(
    val id: Int,
    @param:DrawableRes val illustrationRes: Int,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    @param:StringRes val illustrationContentDescriptionRes: Int,
    val style: OnboardingPageStyle,
)

enum class OnboardingPageStyle {
    Network,
    Visits,
    Career,
}

data class OnboardingState(
    val currentPageIndex: Int = 0,
    val totalPageCount: Int = ONBOARDING_PAGE_COUNT,
    val isCompleting: Boolean = false,
) {
    val isLastPage: Boolean
        get() = currentPageIndex == totalPageCount - 1
}

sealed interface OnboardingIntent {
    data class PageChanged(val pageIndex: Int) : OnboardingIntent
    data object NextClicked : OnboardingIntent
    data object BackToStartClicked : OnboardingIntent
    data object SkipClicked : OnboardingIntent
    data object FinalActionClicked : OnboardingIntent
}

sealed interface OnboardingEffect {
    data class MoveToPage(val pageIndex: Int) : OnboardingEffect
    data object NavigateToAuthentication : OnboardingEffect
}

const val ONBOARDING_PAGE_COUNT = 3
