package com.carenest.provider.feature.onboarding.domain.usecase

import com.carenest.provider.feature.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke() = repository.completeOnboarding()
}
