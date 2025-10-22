package com.parkingSystem.parkingSystem.requestmodel

import android.net.Uri

data class SpecialtyRequest(
    val park_name: String,
    val icon: Uri?,
    val type_vehicle: String,
    val price: Double
)

