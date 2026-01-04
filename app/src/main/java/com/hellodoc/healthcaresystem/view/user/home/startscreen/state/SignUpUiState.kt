package com.hellodoc.healthcaresystem.view.user.home.startscreen.state

data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.EMAIL,
    val email: String = "",
    val username: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val selectedRole: String = "User",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCompleted: Boolean = false
)

enum class SignUpStep {
    EMAIL,
    OTP_VERIFICATION,
    PERSONAL_INFO,
    ROLE_SELECTION
}
