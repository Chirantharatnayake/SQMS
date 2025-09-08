package com.example.queuemanagmentsystem.API

import com.example.queuemanagmentsystem.API.BranchPlace
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import kotlinx.coroutines.tasks.await

private fun inSriLanka(lat: Double, lng: Double): Boolean =
    lat in 5.7..9.9 && lng in 79.4..82.1

class PlacesRepository(private val client: PlacesClient) {

    /**
     * Searches for nearby People's Bank branches around the provided center.
     * Uses a geo‑hint inside the text query ("near lat,lng") instead of API location bias
     * features that were producing unresolved reference errors in the current SDK version.
     */
    suspend fun searchPeoplesBankNearby(center: LatLng): List<BranchPlace> {
        return try {
            val baseQuery = "People's Bank near ${center.latitude},${center.longitude}"
            val request = SearchByTextRequest.builder(
                baseQuery,
                listOf(
                    Place.Field.ID,
                    Place.Field.DISPLAY_NAME,
                    Place.Field.NAME,
                    Place.Field.LOCATION,
                    Place.Field.GOOGLE_MAPS_URI,
                    Place.Field.FORMATTED_ADDRESS
                )
            )
                .setMaxResultCount(30)
                .build()

            val response = client.searchByText(request).await()

            val branches = response.places.mapNotNull { place ->
                val name = place.displayName?.toString() ?: place.name ?: return@mapNotNull null
                val loc = place.location ?: return@mapNotNull null
                if (!inSriLanka(loc.latitude, loc.longitude)) return@mapNotNull null
                if (!name.contains("People", true) || !name.contains("Bank", true)) return@mapNotNull null
                BranchPlace(
                    name = name,
                    latLng = loc,
                    mapsUri = place.googleMapsUri
                )
            }

            // Sort by distance to center and keep closest 10
            branches.sortedBy { distanceBetween(center, it.latLng) }.take(10)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun distanceBetween(a: LatLng, b: LatLng): Double {
        val r = 6371.0
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLng = Math.toRadians(b.longitude - a.longitude)
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val h = Math.sin(dLat / 2).let { it * it } + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLng / 2).let { it * it }
        val c = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h))
        return r * c
    }
}
