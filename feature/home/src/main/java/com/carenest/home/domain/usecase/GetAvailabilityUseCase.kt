package com.carenest.home.domain.usecase

import com.carenest.provider.core.datastore.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAvailabilityUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    operator fun invoke(): Flow<Boolean> = appPreferences.state.map { it.isOnline }
}
