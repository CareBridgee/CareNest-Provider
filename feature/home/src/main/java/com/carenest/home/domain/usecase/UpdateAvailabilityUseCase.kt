package com.carenest.home.domain.usecase

import com.carenest.provider.core.datastore.AppPreferences
import javax.inject.Inject

class UpdateAvailabilityUseCase @Inject constructor(
    private val appPreferences: AppPreferences,
) {
    suspend operator fun invoke(isOnline: Boolean) {
        appPreferences.setOnline(isOnline)
    }
}
