package com.hellodoc.healthcaresystem.view.user.home.startscreen.state

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOtpSent: Boolean = false
)
