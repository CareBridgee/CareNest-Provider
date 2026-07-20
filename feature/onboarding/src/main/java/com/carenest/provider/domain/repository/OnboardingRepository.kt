package com.carenest.provider.feature.onboarding.domain.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    fun observeCompletionStatus(): Flow<Boolean>
    suspend fun completeOnboarding()
}
