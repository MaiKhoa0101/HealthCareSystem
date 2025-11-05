package com.hellodoc.healthcaresystem.requestmodel

data class GoogleSignInRequest(
    val idToken: String,
    val email: String,
    val name: String,
    val phone: String
)
