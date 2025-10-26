package com.parkingSystem.parkingSystem.requestmodel

data class LoginRequest (
    val email: String,
    val password: String
)

data class FirebaseLoginRequest(
    val idToken: String
)

