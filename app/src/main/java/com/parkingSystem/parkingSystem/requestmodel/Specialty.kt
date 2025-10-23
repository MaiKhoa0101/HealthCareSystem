package com.parkingSystem.parkingSystem.requestmodel

import android.net.Uri

data class SpecialtyRequest(
    val name: String,
    val icon: Uri?,
    val description: String
)

