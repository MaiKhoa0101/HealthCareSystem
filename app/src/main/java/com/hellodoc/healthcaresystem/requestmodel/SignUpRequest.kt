package com.hellodoc.healthcaresystem.requestmodel

data class SignUpRequest (
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

data class GoogleLoginRequest (
    val idToken: String,
    val phone: String? = null
)