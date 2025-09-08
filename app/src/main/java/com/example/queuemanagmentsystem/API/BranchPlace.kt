package com.example.queuemanagmentsystem.API

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class BranchPlace(
    val name: String,
    val latLng: LatLng,
    val mapsUri: Uri? = null
)
