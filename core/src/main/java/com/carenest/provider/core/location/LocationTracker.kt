package com.carenest.provider.core.location

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val timestampMillis: Long = 0L,
) {
    fun ageMillis(nowMillis: Long = System.currentTimeMillis()): Long =
        if (timestampMillis <= 0L) Long.MAX_VALUE else nowMillis - timestampMillis
}

sealed interface LocationResult {
    data class Success(val data: LocationData) : LocationResult
    data class Failure(val reason: Reason) : LocationResult

    enum class Reason {
        PERMISSION_DENIED,
        PROVIDERS_DISABLED,
        NO_FIX,
    }
}

interface LocationTracker {
    suspend fun getCurrentLocation(): LocationResult
}
