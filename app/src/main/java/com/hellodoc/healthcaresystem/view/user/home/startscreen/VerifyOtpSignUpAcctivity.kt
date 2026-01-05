package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.view.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyOtpSignUpAcctivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val email = intent.getStringExtra("email") ?: ""

        setContent {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by viewModel.otpState.collectAsState()

            // Initialize email in ViewModel
            LaunchedEffect(email) {
                viewModel.setOtpEmail(email)
            }

            // Handle successful verification
            LaunchedEffect(uiState.isVerified) {
                if (uiState.isVerified) {
                    Toast.makeText(this@VerifyOtpSignUpAcctivity, "Xác minh thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@VerifyOtpSignUpAcctivity, SecondSignUp::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    viewModel.resetOtpState()
                    finish()
                }
            }

            // Show error messages
            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { message ->
                    Toast.makeText(this@VerifyOtpSignUpAcctivity, message, Toast.LENGTH_SHORT).show()
                }
            }
            
            // Timer logic in UI or ViewModel?
            // ViewModel has updateOtpTimer but the loop is better in UI or ViewModel with a loop.
            // Let's keep loop in UI for simplicity or move to ViewModel if strict MVVM.
            // The existing ViewModel has `updateOtpTimer` which just updates state.
            // So we need to run the timer here and update ViewModel, OR run timer in ViewModel.
            // Running timer in ViewModel is better to survive config changes, but `AuthViewModel` is not scoped to this activity if recreated.
            // However, hiltViewModel() is scoped to Activity.
            // Let's run timer in LaunchedEffect here for now, similar to previous implementation.
            
            var secondsLeft by remember { mutableStateOf(15) }
            var isCanResend by remember { mutableStateOf(false) }

            // Sync with ViewModel state if needed, or just keep local timer since ViewModel doesn't enforce timer logic strictly in `resendOtp`.
            // Wait, `resendOtp` in ViewModel resets secondsLeft to 15.
            // So we should observe ViewModel's timer logic if implemented, but I only added `updateOtpTimer`.
            // Let's stick to local timer for display, and call viewModel.resendOtp() which resets logic.
            
            LaunchedEffect(isCanResend) {
                if (!isCanResend) {
                    secondsLeft = 15
                    while (secondsLeft > 0) {
                        delay(1000)
                        secondsLeft--
                    }
                    isCanResend = true
                }
            }

            HealthCareSystemTheme {
                VerifyOtpScreen(
                    email = email,
                    otp = uiState.otp,
                    onOtpChange = viewModel::onOtpChange,
                    secondsLeft = secondsLeft,
                    isCanResend = isCanResend,
                    isLoading = uiState.isLoading,
                    onVerify = viewModel::verifyOtp,
                    onResend = {
                        viewModel.resendOtp(isSignUp = true)
                        isCanResend = false
                    },
                    onBack = { finish() }
                )
            }
        }
    }

    @Composable
    fun VerifyOtpScreen(
        email: String,
        otp: String,
        onOtpChange: (String) -> Unit,
        secondsLeft: Int,
        isCanResend: Boolean,
        isLoading: Boolean,
        onVerify: () -> Unit,
        onResend: () -> Unit,
        onBack: () -> Unit
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HelloDocLogo(modifier = Modifier.size(120.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Xác minh OTP",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Nhập mã OTP đã gửi đến email\n$email",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                AuthTextField(
                    value = otp,
                    onValueChange = onOtpChange,
                    label = "Mã OTP",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    PrimaryButton(
                        text = "Xác nhận",
                        onClick = onVerify,
                        enabled = otp.length == 6
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Chưa nhận được mã? ", color = Color.Gray)
                    if (isCanResend) {
                        Text(
                            text = "Gửi lại",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onResend() }
                        )
                    } else {
                        Text(
                            text = "Gửi lại trong 00:${String.format("%02d", secondsLeft)}",
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
