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
    val address: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val role: String? = null,
)