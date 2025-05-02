package com.hellodoc.healthcaresystem.requestmodel

import android.net.Uri

data class ApplyDoctorRequest(
    val license: String,
    val specialty: String,
    val CCCD: String,
    val licenseUrl: Uri?,
    val faceUrl: Uri?,
    val avatarURL: Uri?,
    val frontCccdUrl: Uri?,
    val backCccdUrl: Uri?
)

data class DoctorUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
