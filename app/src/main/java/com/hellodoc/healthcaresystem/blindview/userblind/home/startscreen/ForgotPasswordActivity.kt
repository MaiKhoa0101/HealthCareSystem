package com.hellodoc.healthcaresystem.blindview.userblind.home.startscreen

import com.hellodoc.healthcaresystem.view.user.home.startscreen.AuthTextField
import com.hellodoc.healthcaresystem.view.user.home.startscreen.HelloDocLogo
import com.hellodoc.healthcaresystem.view.user.home.startscreen.PrimaryButton
import com.hellodoc.healthcaresystem.view.user.home.startscreen.VerifyOtpActivity

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HealthCareSystemTheme {
                var email by remember { mutableStateOf("") }
                var isLoading by remember { mutableStateOf(false) }
                val otpResult by userViewModel.otpResult.collectAsState()

                // Observe OTP result
                LaunchedEffect(otpResult) {
                    otpResult?.let { result ->
                        isLoading = false
                        result.onSuccess { response ->
                            Toast.makeText(this@ForgotPasswordActivity, response.message, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@ForgotPasswordActivity, VerifyOtpActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                        }.onFailure { error ->
                            Toast.makeText(this@ForgotPasswordActivity, "Lỗi: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                ForgotPasswordScreen(
                    email = email,
                    onEmailChange = { email = it },
                    isLoading = isLoading,
                    onSendOtp = {
                        if (email.isEmpty()) {
                            Toast.makeText(this@ForgotPasswordActivity, "Email không hợp lệ!", Toast.LENGTH_SHORT).show()
                        } else {
                            isLoading = true
                            userViewModel.requestOtp(email)
                        }
                    },
                    onBack = { finish() }
                )
            }
        }
    }

    @Composable
    fun ForgotPasswordScreen(
        email: String,
        onEmailChange: (String) -> Unit,
        isLoading: Boolean,
        onSendOtp: () -> Unit,
        onBack: () -> Unit
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
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
                    text = "Quên mật khẩu",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Nhập email của bạn để nhận mã xác minh khôi phục mật khẩu",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                AuthTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    PrimaryButton(text = "Gửi mã xác minh", onClick = onSendOtp)
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }


}
