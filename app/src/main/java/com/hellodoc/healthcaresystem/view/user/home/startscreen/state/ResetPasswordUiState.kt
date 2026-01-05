package com.hellodoc.healthcaresystem.view.user.home.startscreen.state

data class ResetPasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordReset: Boolean = false
)
