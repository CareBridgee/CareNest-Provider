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

    override suspend fun getCurrentLocation(): LocationData? {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            return null
        }

        val lm = locationManager ?: return null

        val isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            return getLastKnownLocation(lm)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val provider = when {
                isGpsEnabled -> LocationManager.GPS_PROVIDER
                isNetworkEnabled -> LocationManager.NETWORK_PROVIDER
                else -> LocationManager.PASSIVE_PROVIDER
            }
            val location = suspendCancellableCoroutine<Location?> { continuation ->
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
            if (location != null) {
                return LocationData(location.latitude, location.longitude)
            }
        }

        val lastKnown = getLastKnownLocation(lm)
        if (lastKnown != null) {
            return lastKnown
        }

        return requestSingleLocationUpdate(lm, isGpsEnabled, isNetworkEnabled)
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
        return bestLocation?.let { LocationData(it.latitude, it.longitude) }
    }

    private suspend fun requestSingleLocationUpdate(
        lm: LocationManager,
        isGpsEnabled: Boolean,
        isNetworkEnabled: Boolean
    ): LocationData? {
        val provider = when {
            isGpsEnabled -> LocationManager.GPS_PROVIDER
            isNetworkEnabled -> LocationManager.NETWORK_PROVIDER
            else -> return null
        }

        return suspendCancellableCoroutine { continuation ->
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    try {
                        lm.removeUpdates(this)
                    } catch (_: SecurityException) { }
                    if (continuation.isActive) {
                        continuation.resume(LocationData(location.latitude, location.longitude))
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
    }
}
