package com.carenest.provider.feature.onboarding.presentation.onboarding

import com.carenest.provider.feature.onboarding.R

val providerOnboardingPages: List<OnboardingPage> = listOf(
    OnboardingPage(
        id = 1,
        illustrationRes = R.drawable.onboarding_provide_healthcare,
        titleRes = R.string.onboarding_network_title,
        descriptionRes = R.string.onboarding_network_description,
        illustrationContentDescriptionRes = R.string.onboarding_network_image_description,
        style = OnboardingPageStyle.Network,
    ),
    OnboardingPage(
        id = 2,
        illustrationRes = R.drawable.onboarding_manage_visits,
        titleRes = R.string.onboarding_visits_title,
        descriptionRes = R.string.onboarding_visits_description,
        illustrationContentDescriptionRes = R.string.onboarding_visits_image_description,
        style = OnboardingPageStyle.Visits,
    ),
    OnboardingPage(
        id = 3,
        illustrationRes = R.drawable.onboarding_grow_career,
        titleRes = R.string.onboarding_career_title,
        descriptionRes = R.string.onboarding_career_description,
        illustrationContentDescriptionRes = R.string.onboarding_career_image_description,
        style = OnboardingPageStyle.Career,
    ),
)
