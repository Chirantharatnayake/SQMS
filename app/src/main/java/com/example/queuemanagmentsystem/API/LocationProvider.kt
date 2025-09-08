package com.example.queuemanagmentsystem.API

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

private val SRI_LANKA_CENTER = LatLng(7.8731, 80.7718)
private fun isInSriLanka(lat: Double, lng: Double): Boolean =
    lat in 5.7..9.9 && lng in 79.4..82.1

class LocationProvider(context: Context) {
    private val fused = LocationServices.getFusedLocationProviderClient(context)

    /** Returns current location if available, else a safe fallback (Sri Lanka center). */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentOrFallback(): LatLng {
        return try {
            // First try to get the last known location
            val lastLocation = runCatching { fused.lastLocation.await() }.getOrNull()
            val usableLast = lastLocation?.takeIf { isInSriLanka(it.latitude, it.longitude) && (System.currentTimeMillis() - it.time) < 300_000 }

            val result = if (usableLast != null) {
                LatLng(usableLast.latitude, usableLast.longitude)
            } else {
                // Request a fresh location with timeout
                val cancellationTokenSource = CancellationTokenSource()
                val freshLocation = withTimeoutOrNull(10_000) {
                    fused.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.token
                    ).await()
                }
                freshLocation?.takeIf { isInSriLanka(it.latitude, it.longitude) }?.let { LatLng(it.latitude, it.longitude) }
            }
            result ?: SRI_LANKA_CENTER
        } catch (_: Exception) {
            // If anything fails, use Sri Lanka center as fallback
            SRI_LANKA_CENTER
        }
    }
}


