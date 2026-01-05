
package com.hellodoc.healthcaresystem.view.user.home.startscreen

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.blindview.userblind.home.root.HomeBlindActivity
import com.hellodoc.healthcaresystem.view.admin.AdminRoot
import com.hellodoc.healthcaresystem.view.user.home.root.HomeActivity
import com.hellodoc.healthcaresystem.view.user.home.startscreen.navigation.AuthScreen
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel

@Composable
fun SignInRoute(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.signInState.collectAsState()
    val auth = Firebase.auth

    // Google Sign In Setup
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            account.idToken?.let { idToken ->
                                viewModel.loginWithGoogle(idToken)
                            }
                        } else {
                            Toast.makeText(context, "Đăng nhập Google thất bại.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle Authentication Success
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            val intent = when (uiState.userRole) {
                "Admin" -> Intent(context, AdminRoot::class.java)
                "User", "Doctor" -> Intent(context, HomeActivity::class.java)
                "Blind" -> Intent(context, HomeBlindActivity::class.java)
                else -> {
                    Toast.makeText(context, "Vai trò không hợp lệ: ${uiState.userRole}", Toast.LENGTH_SHORT).show()
                    viewModel.resetSignInState()
                    null
                }
            }
            
            if (intent != null) {
                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                viewModel.resetSignInState()
                context.startActivity(intent)
                // If we are in an Activity hosting this NavHost, we usually want to finish it.
                // We can check if context is Activity
                // (context as? Activity)?.finish() // Removed to avoid conflict with CLEAR_TASK
            }
        }
    }

    // Show error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearSignInErrorMessage()
        }
    }

    SignInScreen(
        email = uiState.email,
        onEmailChange = viewModel::onSignInEmailChange,
        password = uiState.password,
        onPasswordChange = viewModel::onSignInPasswordChange,
        isLoading = uiState.isLoading,
        onSignIn = viewModel::signIn,
        onGoogleSignIn = {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        },
        onSignUp = {
            navController.navigate(AuthScreen.SignUp.route)
        },
        onForgotPassword = {
            navController.navigate(AuthScreen.ForgotPassword.route)
        },
        onBack = {
            navController.popBackStack()
        }
    )
}

@Composable
fun SignInScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onSignIn: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
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
                text = "Đăng nhập",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Chào mừng trở lại!",
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

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Mật khẩu",
                isPassword = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Quên mật khẩu?",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onForgotPassword() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                PrimaryButton(text = "Đăng nhập", onClick = onSignIn)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OrDivider()

            Spacer(modifier = Modifier.height(24.dp))

            GoogleButton(onClick = onGoogleSignIn)

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Chưa có tài khoản? ", color = Color.Gray)
                Text(
                    text = "Đăng ký",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUp() }
                )
            }
        }
    }
}
