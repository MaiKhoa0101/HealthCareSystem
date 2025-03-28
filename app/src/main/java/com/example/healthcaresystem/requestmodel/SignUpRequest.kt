package com.example.healthcaresystem.requestmodel

data class SignUpRequest (
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)