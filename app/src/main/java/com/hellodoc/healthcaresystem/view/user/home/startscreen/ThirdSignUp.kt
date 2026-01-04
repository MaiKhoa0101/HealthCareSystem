package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.view.user.home.startscreen.HelloDocLogo
import com.hellodoc.healthcaresystem.view.user.home.startscreen.PrimaryButton
import com.hellodoc.healthcaresystem.view.user.home.startscreen.SignUpSuccess
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThirdSignUp : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val email = intent.getStringExtra("email") ?: ""
        val username = intent.getStringExtra("username") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val password = intent.getStringExtra("password") ?: ""

        setContent {
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by viewModel.signUpState.collectAsState()

            // Initialize data from intent
            LaunchedEffect(Unit) {
                viewModel.onSignUpEmailChange(email)
                viewModel.onSignUpUsernameChange(username)
                viewModel.onSignUpPhoneChange(phone)
                viewModel.onSignUpPasswordChange(password)
                viewModel.onSignUpConfirmPasswordChange(password)
            }

            // Handle successful sign up
            LaunchedEffect(uiState.isCompleted) {
                if (uiState.isCompleted) {
                    Toast.makeText(this@ThirdSignUp, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ThirdSignUp, SignUpSuccess::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    viewModel.resetSignUpState()
                }
            }

            // Show error messages
            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { message ->
                    Toast.makeText(this@ThirdSignUp, message, Toast.LENGTH_SHORT).show()
                }
            }

            HealthCareSystemTheme {
                ThirdSignUpScreen(
                    selectedRole = uiState.selectedRole,
                    onRoleSelect = viewModel::onSignUpRoleChange,
                    isLoading = uiState.isLoading,
                    onSignUp = viewModel::completeSignUp,
                    onBack = { finish() }
                )
            }
        }
    }

    @Composable
    fun ThirdSignUpScreen(
        selectedRole: String,
        onRoleSelect: (String) -> Unit,
        isLoading: Boolean,
        onSignUp: () -> Unit,
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
                    text = "Bạn là ai?",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Chọn vai trò phù hợp nhất với nhu cầu của bạn",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                RoleOption(
                    title = "Người dùng thông thường",
                    role = "User",
                    selected = selectedRole == "User",
                    onClick = { onRoleSelect("User") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                RoleOption(
                    title = "Người khiếm thị",
                    role = "Blind",
                    selected = selectedRole == "Blind",
                    onClick = { onRoleSelect("Blind") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                RoleOption(
                    title = "Người khiếm thính",
                    role = "Deaf",
                    selected = selectedRole == "Deaf",
                    onClick = { onRoleSelect("Deaf") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                RoleOption(
                    title = "Người khiếm thanh",
                    role = "Mute",
                    selected = selectedRole == "Mute",
                    onClick = { onRoleSelect("Mute") }
                )

                Spacer(modifier = Modifier.height(48.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else {
                    PrimaryButton(text = "Đăng ký ngay", onClick = onSignUp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    @Composable
    fun RoleOption(
        title: String,
        role: String,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray
            ),
            color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Black
                )
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
