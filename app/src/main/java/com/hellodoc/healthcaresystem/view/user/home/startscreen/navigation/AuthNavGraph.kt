package com.hellodoc.healthcaresystem.view.user.home.startscreen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hellodoc.healthcaresystem.view.user.home.startscreen.ResetPasswordScreen
import com.hellodoc.healthcaresystem.view.user.home.startscreen.SignInRoute
import com.hellodoc.healthcaresystem.view.user.home.startscreen.SignUpRoute
import com.hellodoc.healthcaresystem.view.user.home.startscreen.VerifyOtpScreen
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel

@Composable
fun AuthNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
    onStartSignIn: () -> Unit,
    onStartSignUp: () -> Unit,
    onBackToStart: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = AuthScreen.Start.route,
        modifier = modifier
    ) {
        // Start Screen (We need to extract content from StartScreen.kt first, or just inline it here temporarily)
        composable(AuthScreen.Start.route) {
            // Placeholder: The actual StartScreen UI content needs to be called here.
            // I'll assume StartScreenContent is available from StartScreen.kt
             com.hellodoc.healthcaresystem.view.user.home.startscreen.StartScreenContent(
                 onSignIn = onStartSignIn,
                 onSignUp = onStartSignUp,
                 onBack = onBackToStart
             )
        }

        composable(AuthScreen.SignIn.route) {
            SignInRoute(navController = navController, viewModel = authViewModel)
        }

        composable(AuthScreen.SignUp.route) {
            SignUpRoute(navController = navController, viewModel = authViewModel)
        }

        composable(AuthScreen.ForgotPassword.route) {
            com.hellodoc.healthcaresystem.view.user.home.startscreen.ForgotPasswordRoute(
                navController = navController,
                viewModel = authViewModel
            )
        }

        composable(
            route = AuthScreen.VerifyOtpForgot.route,
            arguments = listOf(androidx.navigation.navArgument("email") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val uiState by authViewModel.otpState.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(email) {
                if (email.isNotEmpty()) {
                    authViewModel.setOtpEmail(email)
                }
            }

            // Navigate when verified
            LaunchedEffect(uiState.isVerified) {
                if (uiState.isVerified) {
                    navController.navigate(AuthScreen.ResetPassword.createRoute(email))
                    authViewModel.resetOtpState()
                }
            }
            
            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { message ->
                    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                }
            }

            // Timer logic
            LaunchedEffect(uiState.canResend) {
                if (!uiState.canResend) {
                    var remaining = 15
                    authViewModel.updateOtpTimer(remaining, false)
                    while (remaining > 0) {
                        kotlinx.coroutines.delay(1000)
                        remaining--
                        authViewModel.updateOtpTimer(remaining, false)
                    }
                    authViewModel.updateOtpTimer(0, true)
                }
            }

            VerifyOtpScreen(
                email = email,
                navHostController = navController
            )
        }
        
        composable(
             route = AuthScreen.ResetPassword.route,
             arguments = listOf(androidx.navigation.navArgument("email") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val uiState by authViewModel.resetPasswordState.collectAsState()
            val context = LocalContext.current

            // Navigate when password is reset
            LaunchedEffect(uiState.isPasswordReset) {
                if (uiState.isPasswordReset) {
                    // Navigate to SignIn or Home. Let's send to SignIn for re-login or auto login.
                    // Usually reset password requires re-login
                    android.widget.Toast.makeText(context, "Đặt lại mật khẩu thành công! Vui lòng đăng nhập lại.", android.widget.Toast.LENGTH_LONG).show()
                    navController.navigate(AuthScreen.SignIn.route) {
                        popUpTo(AuthScreen.Start.route) {
                            inclusive = false
                        }
                    }
                    authViewModel.resetResetPasswordState()
                }
            }

            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { message ->
                    android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                }
            }

             ResetPasswordScreen(
                    newPassword = uiState.newPassword,
                    onNewPasswordChange = authViewModel::onResetPasswordChange,
                    confirmPassword = uiState.confirmPassword,
                    onConfirmPasswordChange = authViewModel::onResetConfirmPasswordChange,
                    isLoading = uiState.isLoading,
                    onReset = { authViewModel.resetPassword(email) },
                    onBack = { navController.popBackStack() }
             )
        }

        composable(AuthScreen.VerifyOtpSignUp.route) {
            VerifyOtpScreen(
                email = "",
                navHostController = navController
            )
        }

    }
}
