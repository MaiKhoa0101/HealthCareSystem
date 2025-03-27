package com.example.healthcaresystem.model.request

data class SignUpRequest (
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)