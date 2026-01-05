package com.hellodoc.healthcaresystem.view.user.home.startscreen.state

data class VerifyOtpUiState(
    val otp: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val secondsLeft: Int = 15,
    val canResend: Boolean = false,
    val isVerified: Boolean = false
)
