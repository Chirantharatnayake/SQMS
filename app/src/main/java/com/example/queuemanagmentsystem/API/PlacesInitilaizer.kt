package com.example.queuemanagmentsystem.API

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

object PlacesInitializer {
    fun client(context: Context): PlacesClient {
        if (!Places.isInitialized()) {
            // Initialize Places SDK with your API key
            Places.initialize(context, "AIzaSyDixkE6Z6eoaB6dEwZtJvLdMRq2AchlLmA")
        }
        return Places.createClient(context)
    }
}
