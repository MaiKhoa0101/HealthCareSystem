package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hellodoc.healthcaresystem.view.user.home.startscreen.HelloDocLogo
import com.hellodoc.healthcaresystem.view.user.home.startscreen.PrimaryButton
import com.hellodoc.healthcaresystem.view.user.home.startscreen.AuthTextField
import com.hellodoc.healthcaresystem.view.user.home.startscreen.navigation.AuthScreen
import com.hellodoc.healthcaresystem.view.user.home.startscreen.state.SignUpStep
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel

@Composable
fun SignUpRoute(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.signUpState.collectAsState()

    // Handle navigation based on steps
    LaunchedEffect(uiState.currentStep) {
        when (uiState.currentStep) {
            SignUpStep.EMAIL -> { /* Stay here */ }
            SignUpStep.OTP_VERIFICATION -> {
                navController.navigate("verify_otp_forgot_screen/${uiState.email}")
            }
            else -> {}
        }
    }

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    SignUpScreen(
        email = uiState.email,
        onEmailChange = viewModel::onSignUpEmailChange,
        isLoading = uiState.isLoading,
        onContinue = {
            navController.navigate(AuthScreen.VerifyOtpSignUp.route)
        },
        onSignIn = {
            navController.navigate(AuthScreen.SignIn.route) {
                popUpTo(AuthScreen.Start.route)
            }
        },
        onBack = { navController.popBackStack() }
    )
}

@Composable
fun SignUpScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    onContinue: () -> Unit,
    onSignIn: () -> Unit,
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
                text = "Tạo tài khoản mới",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Hãy nhập email của bạn để bắt đầu",
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
                PrimaryButton(text = "Tiếp tục", onClick = onContinue)
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Đã có tài khoản? ", color = Color.Gray)
                Text(
                    text = "Đăng nhập",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignIn() }
                )
            }
        }
    }
}
