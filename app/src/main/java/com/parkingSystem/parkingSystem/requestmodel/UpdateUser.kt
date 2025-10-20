package com.parkingSystem.parkingSystem.requestmodel

import android.net.Uri

data class UpdateUser(
    val avatarURL: String? = null,
    val name: String,
    val email: String,
    val phone: String,
    val password: String? = null
)

data class UpdateUserInput(
    val avatarURL: Uri?,
    val address: String,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String,
)