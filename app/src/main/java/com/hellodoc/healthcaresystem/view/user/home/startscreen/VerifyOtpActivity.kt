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
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class VerifyOtpActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val email = intent.getStringExtra("email") ?: ""

        setContent {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by viewModel.otpState.collectAsState()

            LaunchedEffect(email) {
                if (email.isNotEmpty()) {
                    viewModel.setOtpEmail(email)
                }
            }

            // Navigate when OTP is verified
            LaunchedEffect(uiState.isVerified) {
                if (uiState.isVerified) {
                    val intent = Intent(this@VerifyOtpActivity, ResetPasswordActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                    viewModel.resetOtpState()
                }
            }

            // Show error messages
            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { message ->
                    Toast.makeText(this@VerifyOtpActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

            // Timer logic
            LaunchedEffect(uiState.canResend) {
                if (!uiState.canResend) {
                    var remaining = 15
                    viewModel.updateOtpTimer(remaining, false)
                    while (remaining > 0) {
                        delay(1000)
                        remaining--
                        viewModel.updateOtpTimer(remaining, false)
                    }
                    viewModel.updateOtpTimer(0, true)
                }
            }

            HealthCareSystemTheme {
                VerifyOtpScreen(
                    email = email,
                    otp = uiState.otp,
                    onOtpChange = { if (it.length <= 6) viewModel.onOtpChange(it) },
                    secondsLeft = uiState.secondsLeft,
                    isCanResend = uiState.canResend,
                    isLoading = uiState.isLoading,
                    onVerify = viewModel::verifyOtp,
                    onResend = { viewModel.resendOtp(isSignUp = false) },
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
