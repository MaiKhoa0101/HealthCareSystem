package com.hellodoc.healthcaresystem.requestmodel

import android.net.Uri

data class SpecialtyRequest(
    val name: String,
    val icon: Uri?,
    val description: String
)

data class GetDoctorBySpecialtyNameRequest(
    val name: String
)
