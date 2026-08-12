package com.carenest.home.domain.usecase

import com.carenest.provider.core.location.LocationData
import com.carenest.provider.core.location.LocationTracker
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(
    private val locationTracker: LocationTracker,
) {
    suspend operator fun invoke(): LocationData? {
        return locationTracker.getCurrentLocation()
    }
}
