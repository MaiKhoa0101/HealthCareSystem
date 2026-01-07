package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.view.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val email = intent.getStringExtra("email") ?: ""

        setContent {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by viewModel.resetPasswordState.collectAsState()

            // Navigate when password is reset
            LaunchedEffect(uiState.isPasswordReset) {
                if (uiState.isPasswordReset) {
                    val intent = Intent(this@ResetPasswordActivity, ResetPasswordSuccessActivity::class.java)
                    startActivity(intent)
                    viewModel.resetResetPasswordState()
                }
            }

            // Show error messages
            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { message ->
                    Toast.makeText(this@ResetPasswordActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

            HealthCareSystemTheme {
                ResetPasswordScreen(
                    newPassword = uiState.newPassword,
                    onNewPasswordChange = viewModel::onResetPasswordChange,
                    confirmPassword = uiState.confirmPassword,
                    onConfirmPasswordChange = viewModel::onResetConfirmPasswordChange,
                    isLoading = uiState.isLoading,
                    onReset = { viewModel.resetPassword(email) },
                    onBack = { finish() }
                )
            }
        }
    }

    @Composable
    fun ResetPasswordScreen(
        newPassword: String,
        onNewPasswordChange: (String) -> Unit,
        confirmPassword: String,
        onConfirmPasswordChange: (String) -> Unit,
        isLoading: Boolean,
        onReset: () -> Unit,
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
                HelloDocLogo(modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Đặt lại mật khẩu",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Hãy nhập mật khẩu mới của bạn",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                AuthTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    label = "Mật khẩu mới",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = "Xác nhận mật khẩu",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(40.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    PrimaryButton(text = "Đổi mật khẩu", onClick = onReset)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
