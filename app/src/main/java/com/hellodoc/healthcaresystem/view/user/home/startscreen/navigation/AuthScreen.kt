package com.hellodoc.healthcaresystem.view.user.home.startscreen.navigation

sealed class AuthScreen(val route: String) {
    object Start : AuthScreen("start_screen")
    object SignIn : AuthScreen("sign_in_screen")
    object SignUp : AuthScreen("sign_up_screen")
    object VerifyOtpSignUp : AuthScreen("verify_otp_sign_up_screen")
    object SecondSignUp : AuthScreen("second_sign_up_screen")
    object ThirdSignUp : AuthScreen("third_sign_up_screen")
    object SignUpSuccess : AuthScreen("sign_up_success_screen")
    object ForgotPassword : AuthScreen("forgot_password_screen")
    object VerifyOtpForgot : AuthScreen("verify_otp_forgot_screen/{email}") {
        fun createRoute(email: String) = "verify_otp_forgot_screen/$email"
    }
    object ResetPassword : AuthScreen("reset_password_screen/{email}") {
        fun createRoute(email: String) = "reset_password_screen/$email"
    }
    object ResetPasswordSuccess : AuthScreen("reset_password_success_screen")
}
