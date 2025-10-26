package com.parkingSystem.parkingSystem.responsemodel

data class LoginResponse(
    val accessToken: String,
    val firebaseUid: String
)

