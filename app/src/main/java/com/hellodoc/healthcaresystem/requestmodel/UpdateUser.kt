package com.hellodoc.healthcaresystem.requestmodel

import android.net.Uri

data class UpdateUser(
    val avatarURL: String,
    val address: String,
    val name: String,
    val email: String,
    val phone: String,
    val password: String? = null, // Có thể null để tránh gửi mật khẩu khi không đổi
    val role: String,
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