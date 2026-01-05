package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.hellodoc.core.common.activity.BaseActivity
import com.hellodoc.healthcaresystem.view.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.view.user.home.root.HomeActivity
import com.hellodoc.healthcaresystem.view.user.home.startscreen.navigation.AuthNavGraph
import com.hellodoc.healthcaresystem.view.user.home.startscreen.navigation.AuthScreen
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartScreen : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if user is already logged in (Double check safety)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("access_token", null)
        if (!token.isNullOrEmpty() && token != "unknown") {
            // Already logged in, redirect to Home
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContent {
            HealthCareSystemTheme {
                val navController = rememberNavController()
                // Use extension viewModels() or ViewModelProvider, consistent with Activity usage
                val authViewModel: AuthViewModel by viewModels() 

                AuthNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    onStartSignIn = { navController.navigate(AuthScreen.SignIn.route) },
                    onStartSignUp = { navController.navigate(AuthScreen.SignUp.route) },
                    onBackToStart = { 
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack() 
                        } else {
                            finish()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun StartScreenContent(onSignIn: () -> Unit, onSignUp: () -> Unit, onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(80.dp))
                HelloDocLogo(modifier = Modifier.size(200.dp))
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "Chào mừng đến với HelloDoc",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Hãy chọn phương thức để bắt đầu hành trình chăm sóc sức khỏe của bạn.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                PrimaryButton(
                    text = "Đăng nhập",
                    onClick = onSignIn
                )
                Spacer(modifier = Modifier.height(12.dp))
                SecondaryButton(
                    text = "Đăng ký tài khoản",
                    onClick = onSignUp
                )
            }
        }
    }
}
