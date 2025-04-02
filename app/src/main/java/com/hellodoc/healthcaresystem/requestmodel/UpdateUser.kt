package com.example.healthcaresystem.requestmodel

data class UpdateUser(
    val name: String,
    val email: String,
    val phone: String,
    val password: String? = null, // Có thể null để tránh gửi mật khẩu khi không đổi
    val role: String
)

