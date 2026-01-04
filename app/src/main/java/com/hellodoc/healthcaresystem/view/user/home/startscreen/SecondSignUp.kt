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
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.view.user.home.startscreen.state.SignUpStep
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondSignUp : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val email = intent.getStringExtra("email") ?: ""

        setContent {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by viewModel.signUpState.collectAsState()

            // Initialize email from intent
            LaunchedEffect(email) {
                if (email.isNotEmpty()) {
                    viewModel.onSignUpEmailChange(email)
                }
            }

            // Handle navigation
            LaunchedEffect(uiState.currentStep) {
                if (uiState.currentStep == SignUpStep.ROLE_SELECTION) {
                    val intent = Intent(this@SecondSignUp, ThirdSignUp::class.java).apply {
                        putExtra("email", uiState.email)
                        putExtra("username", uiState.username)
                        putExtra("phone", uiState.phone)
                        putExtra("password", uiState.password)
                    }
                    startActivity(intent)
                }
            }

            // Show error messages
            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { message ->
                    Toast.makeText(this@SecondSignUp, message, Toast.LENGTH_SHORT).show()
                }
            }

            HealthCareSystemTheme {
                SecondSignUpScreen(
                    username = uiState.username,
                    onUsernameChange = viewModel::onSignUpUsernameChange,
                    phoneNumber = uiState.phone,
                    onPhoneChange = viewModel::onSignUpPhoneChange,
                    password = uiState.password,
                    onPasswordChange = viewModel::onSignUpPasswordChange,
                    repassword = uiState.confirmPassword,
                    onRepasswordChange = viewModel::onSignUpConfirmPasswordChange,
                    onNext = viewModel::moveToRoleSelection,
                    onBack = { finish() }
                )
            }
        }
    }

    @Composable
    fun SecondSignUpScreen(
        username: String,
        onUsernameChange: (String) -> Unit,
        phoneNumber: String,
        onPhoneChange: (String) -> Unit,
        password: String,
        onPasswordChange: (String) -> Unit,
        repassword: String,
        onRepasswordChange: (String) -> Unit,
        onNext: () -> Unit,
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
                    text = "Thông tin tài khoản",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Hãy hoàn tất thông tin cá nhân của bạn",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                AuthTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = "Họ và tên",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = phoneNumber,
                    onValueChange = onPhoneChange,
                    label = "Số điện thoại",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Mật khẩu",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = repassword,
                    onValueChange = onRepasswordChange,
                    label = "Nhập lại mật khẩu",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(40.dp))

                PrimaryButton(text = "Tiếp theo", onClick = onNext)

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
