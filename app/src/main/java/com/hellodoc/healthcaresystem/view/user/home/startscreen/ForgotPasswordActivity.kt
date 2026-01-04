package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hellodoc.healthcaresystem.view.user.home.startscreen.navigation.AuthScreen
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordRoute(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.forgotPasswordState.collectAsState()

    // Navigate when OTP is sent
    LaunchedEffect(uiState.isOtpSent) {
        if (uiState.isOtpSent) {
            navController.navigate(AuthScreen.VerifyOtpForgot.createRoute(uiState.email))
            viewModel.resetForgotPasswordState()
        }
    }

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    ForgotPasswordScreen(
        email = uiState.email,
        onEmailChange = viewModel::onForgotPasswordEmailChange,
        isLoading = uiState.isLoading,
        onSendOtp = viewModel::sendForgotPasswordOtp,
        onBack = { navController.popBackStack() }
    )
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
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
