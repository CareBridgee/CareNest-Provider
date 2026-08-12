package com.carenest.provider.core.location

data class LocationData(
    val latitude: Double,
    val longitude: Double,
)

interface LocationTracker {
    suspend fun getCurrentLocation(): LocationData?
}
