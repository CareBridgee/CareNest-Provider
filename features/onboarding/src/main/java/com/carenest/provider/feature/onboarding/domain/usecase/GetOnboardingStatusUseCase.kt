package com.carenest.provider.feature.onboarding.domain.usecase

import com.carenest.provider.feature.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetOnboardingStatusUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    operator fun invoke(): Flow<Boolean> = repository.observeCompletionStatus()
}
