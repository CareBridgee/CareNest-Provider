package com.carenest.provider.feature.onboarding.data.repository

import com.carenest.provider.data.local.OnboardingPreferences
import com.carenest.provider.feature.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class OnboardingRepositoryImpl @Inject constructor(
    private val preferences: OnboardingPreferences,
) : OnboardingRepository {
    override fun observeCompletionStatus(): Flow<Boolean> = preferences.isCompleted

    override suspend fun completeOnboarding() {
        preferences.setCompleted()
    }
}
