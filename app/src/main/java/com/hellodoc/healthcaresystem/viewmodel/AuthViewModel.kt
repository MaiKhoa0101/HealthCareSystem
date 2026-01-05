package com.hellodoc.healthcaresystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.model.repository.UserRepository
import com.hellodoc.healthcaresystem.view.user.home.startscreen.state.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    // ... State definitions (unchanged) ...
    // ============ Sign In State ============
    private val _signInState = MutableStateFlow(SignInUiState())
    val signInState: StateFlow<SignInUiState> = _signInState.asStateFlow()
    
    // ============ Sign Up State ============
    // ... (keep other states)
    private val _signUpState = MutableStateFlow(SignUpUiState())
    val signUpState: StateFlow<SignUpUiState> = _signUpState.asStateFlow()
    
    private val _forgotPasswordState = MutableStateFlow(ForgotPasswordUiState())
    val forgotPasswordState: StateFlow<ForgotPasswordUiState> = _forgotPasswordState.asStateFlow()
    
    private val _otpState = MutableStateFlow(VerifyOtpUiState())
    val otpState: StateFlow<VerifyOtpUiState> = _otpState.asStateFlow()
    
    private val _resetPasswordState = MutableStateFlow(ResetPasswordUiState())
    val resetPasswordState: StateFlow<ResetPasswordUiState> = _resetPasswordState.asStateFlow()
    
    // ============================================
    // UTILS
    // ============================================
    private fun saveToken(token: String) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("access_token", token).commit() // Use commit() for synchronous write
    }

    // ============================================
    // SIGN IN FUNCTIONS
    // ============================================
    
    fun onSignInEmailChange(email: String) {
        _signInState.update { it.copy(email = email, errorMessage = null) }
    }
    
    fun onSignInPasswordChange(password: String) {
        _signInState.update { it.copy(password = password, errorMessage = null) }
    }

    fun clearSignInErrorMessage() {
        _signInState.update { it.copy(errorMessage = null) }
    }

    fun signIn() {
        val state = _signInState.value
        
        if (state.email.isBlank() || state.password.isBlank()) {
            _signInState.update { it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin") }
            return
        }
        
        viewModelScope.launch {
            _signInState.update { it.copy(isLoading = true, errorMessage = null) }
            
            userRepository.login(state.email, state.password)
                .onSuccess { response ->
                    saveToken(response.accessToken) // SAVE TOKEN HERE
                    val role = extractRoleFromToken(response.accessToken)
                    _signInState.update { 
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            userRole = role
                        )
                    }
                }
                .onFailure { error ->
                    _signInState.update { 
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _signInState.update { it.copy(isLoading = true, errorMessage = null) }
            
            userRepository.loginGoogle(idToken)
                .onSuccess { response ->
                    saveToken(response.accessToken) // SAVE TOKEN HERE
                    val role = extractRoleFromToken(response.accessToken)
                    _signInState.update { 
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            userRole = role
                        )
                    }
                }
                .onFailure { error ->
                    _signInState.update { 
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }
    
    // ============================================
    // SIGN UP FUNCTIONS
    // ============================================
    
    fun onSignUpEmailChange(email: String) {
        _signUpState.update { it.copy(email = email, errorMessage = null) }
    }
    
    fun onSignUpUsernameChange(username: String) {
        _signUpState.update { it.copy(username = username, errorMessage = null) }
    }
    
    fun onSignUpPhoneChange(phone: String) {
        _signUpState.update { it.copy(phone = phone, errorMessage = null) }
    }
    
    fun onSignUpPasswordChange(password: String) {
        _signUpState.update { it.copy(password = password, errorMessage = null) }
    }
    
    fun onSignUpConfirmPasswordChange(confirmPassword: String) {
        _signUpState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }
    
    fun onSignUpRoleChange(role: String) {
        _signUpState.update { it.copy(selectedRole = role) }
    }
    
    fun requestSignUpOtp() {
        val email = _signUpState.value.email
        if (email.isBlank()) {
            _signUpState.update { it.copy(errorMessage = "Email không hợp lệ") }
            return
        }
        
        viewModelScope.launch {
            _signUpState.update { it.copy(isLoading = true, errorMessage = null) }
            
            userRepository.requestOtpSignUp(email)
                .onSuccess {
                    _signUpState.update { 
                        it.copy(
                            isLoading = false,
                            currentStep = SignUpStep.OTP_VERIFICATION
                        )
                    }
                }
                .onFailure { error ->
                    _signUpState.update { 
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }
    
    fun moveToPersonalInfo() {
        _signUpState.update { it.copy(currentStep = SignUpStep.PERSONAL_INFO) }
    }
    
    fun moveToRoleSelection() {
        val state = _signUpState.value
        
        if (state.username.isBlank() || state.phone.isBlank() || 
            state.password.isBlank() || state.confirmPassword.isBlank()) {
            _signUpState.update { it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin") }
            return
        }
        
        if (state.password != state.confirmPassword) {
            _signUpState.update { it.copy(errorMessage = "Mật khẩu không khớp") }
            return
        }
        
        _signUpState.update { it.copy(currentStep = SignUpStep.ROLE_SELECTION, errorMessage = null) }
    }
    
    fun completeSignUp() {
        val state = _signUpState.value
        
        viewModelScope.launch {
            _signUpState.update { it.copy(isLoading = true, errorMessage = null) }
            
            userRepository.signUp(
                username = state.username,
                email = state.email,
                phone = state.phone,
                password = state.password,
                role = state.selectedRole
            )
                .onSuccess {
                    _signUpState.update { it.copy(isLoading = false, isCompleted = true) }
                }
                .onFailure { error ->
                    _signUpState.update { 
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }
    
    // ============================================
    // FORGOT PASSWORD FUNCTIONS
    // ============================================
    
    fun onForgotPasswordEmailChange(email: String) {
        _forgotPasswordState.update { it.copy(email = email, errorMessage = null) }
    }
    
    fun sendForgotPasswordOtp() {
        val email = _forgotPasswordState.value.email
        if (email.isBlank()) {
            _forgotPasswordState.update { it.copy(errorMessage = "Email không hợp lệ") }
            return
        }
        
        viewModelScope.launch {
            _forgotPasswordState.update { it.copy(isLoading = true, errorMessage = null) }
            
            userRepository.requestOtp(email)
                .onSuccess { response ->
                    _forgotPasswordState.update { 
                        it.copy(isLoading = false, isOtpSent = true)
                    }
                }
                .onFailure { error ->
                    _forgotPasswordState.update { 
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }
    
    // ============================================
    // OTP VERIFICATION FUNCTIONS
    // ============================================
    
    fun onOtpChange(otp: String) {
        _otpState.update { it.copy(otp = otp, errorMessage = null) }
    }
    
    fun setOtpEmail(email: String) {
        _otpState.update { it.copy(email = email) }
    }
    
    fun updateOtpTimer(secondsLeft: Int, canResend: Boolean) {
        _otpState.update { it.copy(secondsLeft = secondsLeft, canResend = canResend) }
    }
    
    fun verifyOtp() {
        val state = _otpState.value
        
        if (state.otp.isBlank()) {
            _otpState.update { it.copy(errorMessage = "Vui lòng nhập mã OTP") }
            return
        }
        
        viewModelScope.launch {
            _otpState.update { it.copy(isLoading = true, errorMessage = null) }
            
            userRepository.verifyOtp(state.email, state.otp)
                .onSuccess {
                    _otpState.update { it.copy(isLoading = false, isVerified = true) }
                }
                .onFailure { error ->
                    _otpState.update { 
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }
    
    fun resendOtp(isSignUp: Boolean = false) {
        val email = _otpState.value.email
        
        viewModelScope.launch {
            _otpState.update { it.copy(canResend = false, secondsLeft = 15, errorMessage = null) }
            
            val result = if (isSignUp) {
                userRepository.requestOtpSignUp(email)
            } else {
                userRepository.requestOtp(email)
            }
            
            result.onFailure { error ->
                _otpState.update { it.copy(errorMessage = error.message) }
            }
        }
    }
    
    // ============================================
    // RESET PASSWORD FUNCTIONS
    // ============================================
    
    fun onResetPasswordChange(password: String) {
        _resetPasswordState.update { it.copy(newPassword = password, errorMessage = null) }
    }
    
    fun onResetConfirmPasswordChange(confirmPassword: String) {
        _resetPasswordState.update { 
            it.copy(confirmPassword = confirmPassword, errorMessage = null)
        }
    }
    
    fun resetPassword(email: String) {
        val state = _resetPasswordState.value
        
        if (state.newPassword.length < 6) {
            _resetPasswordState.update { it.copy(errorMessage = "Mật khẩu phải có ít nhất 6 ký tự") }
            return
        }
        
        if (state.newPassword != state.confirmPassword) {
            _resetPasswordState.update { it.copy(errorMessage = "Mật khẩu không khớp") }
            return
        }
        
        viewModelScope.launch {
            _resetPasswordState.update { it.copy(isLoading = true, errorMessage = null) }
            
            userRepository.resetPassword(email, state.newPassword)
                .onSuccess {
                    _resetPasswordState.update { 
                        it.copy(isLoading = false, isPasswordReset = true)
                    }
                }
                .onFailure { error ->
                    _resetPasswordState.update { 
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }
    
    // ============================================
    // UTILITY FUNCTIONS
    // ============================================
    
    private fun extractRoleFromToken(token: String): String {
        return try {
            val jwt = JWT(token)
            jwt.getClaim("role").asString() ?: "User"
        } catch (e: Exception) {
            "User"
        }
    }
    
    fun resetSignInState() {
        _signInState.value = SignInUiState()
    }
    
    fun resetSignUpState() {
        _signUpState.value = SignUpUiState()
    }
    
    fun resetForgotPasswordState() {
        _forgotPasswordState.value = ForgotPasswordUiState()
    }
    
    fun resetOtpState() {
        _otpState.value = VerifyOtpUiState()
    }
    
    fun resetResetPasswordState() {
        _resetPasswordState.value = ResetPasswordUiState()
    }
}
