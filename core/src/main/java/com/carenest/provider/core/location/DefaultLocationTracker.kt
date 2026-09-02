package com.carenest.provider.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class DefaultLocationTracker @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : LocationTracker {

    private val locationManager: LocationManager? by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    override suspend fun getCurrentLocation(): LocationResult {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            return LocationResult.Failure(LocationResult.Reason.PERMISSION_DENIED)
        }

        val lm = locationManager ?: return LocationResult.Failure(LocationResult.Reason.NO_FIX)

        val isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val lastKnown = getLastKnownLocation(lm)

        if (!isGpsEnabled && !isNetworkEnabled) {
            return lastKnown
                ?.let { LocationResult.Success(it) }
                ?: LocationResult.Failure(LocationResult.Reason.PROVIDERS_DISABLED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val providersToTry = mutableListOf<String>()
            if (isGpsEnabled) providersToTry.add(LocationManager.GPS_PROVIDER)
            if (isNetworkEnabled) providersToTry.add(LocationManager.NETWORK_PROVIDER)

            for (provider in providersToTry) {
                val location = withTimeoutOrNull(CURRENT_LOCATION_TIMEOUT_MS) {
                    fetchCurrentLocationForProvider(lm, provider)
                }
                if (location != null) {
                    return LocationResult.Success(
                        LocationData(location.latitude, location.longitude, location.time)
                    )
                }
            }
        }

        val singleUpdateLocation = withTimeoutOrNull(SINGLE_UPDATE_TIMEOUT_MS) {
            requestSingleLocationUpdate(lm, isNetworkEnabled, isGpsEnabled)
        }
        if (singleUpdateLocation != null) {
            return LocationResult.Success(singleUpdateLocation)
        }

        return lastKnown
            ?.let { LocationResult.Success(it) }
            ?: LocationResult.Failure(LocationResult.Reason.NO_FIX)
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.R)
    private suspend fun fetchCurrentLocationForProvider(
        lm: LocationManager,
        provider: String,
    ): Location? = suspendCancellableCoroutine { continuation ->
        val cancellationSignal = CancellationSignal()
        continuation.invokeOnCancellation {
            cancellationSignal.cancel()
        }
        try {
            lm.getCurrentLocation(
                provider,
                cancellationSignal,
                context.mainExecutor
            ) { loc ->
                if (continuation.isActive) {
                    continuation.resume(loc)
                }
            }
        } catch (_: Exception) {
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }
    }

    private fun getLastKnownLocation(lm: LocationManager): LocationData? {
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )
        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                val loc = lm.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || loc.time > bestLocation.time) {
                    bestLocation = loc
                }
            } catch (_: SecurityException) { }
        }
        return bestLocation?.let {
            LocationData(it.latitude, it.longitude, it.time)
        }
    }

    private suspend fun requestSingleLocationUpdate(
        lm: LocationManager,
        isNetworkEnabled: Boolean,
        isGpsEnabled: Boolean,
    ): LocationData? {
        val providers = mutableListOf<String>()
        if (isNetworkEnabled) providers.add(LocationManager.NETWORK_PROVIDER)
        if (isGpsEnabled) providers.add(LocationManager.GPS_PROVIDER)
        if (providers.isEmpty()) return null

        for (provider in providers) {
            val result = suspendCancellableCoroutine<LocationData?> { continuation ->
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        try {
                            lm.removeUpdates(this)
                        } catch (_: SecurityException) { }
                        if (continuation.isActive) {
                            continuation.resume(
                                LocationData(location.latitude, location.longitude, location.time)
                            )
                        }
                    }

                    @Deprecated("Deprecated in API 29")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {
                        try {
                            lm.removeUpdates(this)
                        } catch (_: SecurityException) { }
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                }

                continuation.invokeOnCancellation {
                    try {
                        lm.removeUpdates(listener)
                    } catch (_: SecurityException) { }
                }

                try {
                    lm.requestSingleUpdate(provider, listener, null)
                } catch (_: Exception) {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
            if (result != null) return result
        }
        return null
    }

    private companion object {
        const val CURRENT_LOCATION_TIMEOUT_MS = 10_000L
        const val SINGLE_UPDATE_TIMEOUT_MS = 15_000L
    }
}
