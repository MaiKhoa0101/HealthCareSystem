package com.hellodoc.healthcaresystem.view.user.home.startscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay

@Composable
fun HelloDocLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo_hellodoc),
        contentDescription = "HelloDoc Logo",
        modifier = modifier.size(150.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.Black
        ),
        enabled = enabled
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            }
        },
        keyboardOptions = keyboardOptions,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun SocialSignInButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun AuthScreenContainer(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}
@Composable
fun OrDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
        Text(
            text = " Hoặc ",
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
    }
}

@Composable
fun GoogleButton(onClick: () -> Unit) {
    SocialSignInButton(
        text = "Tiếp tục với Google",
        iconRes = R.drawable.google_logo, // Ensure this drawable exists, otherwise use a placeholder
        onClick = onClick
    )
}

@Composable
fun VerifyOtpScreen(
    email: String,
    navHostController: NavHostController
) {
    var otp by remember { mutableStateOf("") }
    var isCanResend by remember { mutableStateOf(true) }
    var secondsLeft by remember { mutableIntStateOf(0) }
    val authViewModel: AuthViewModel = hiltViewModel()

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        } else {
            isCanResend = true
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = navHostController::popBackStack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HelloDocLogo(modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Xác minh OTP",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Nhập mã OTP đã gửi đến email\n$email",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            AuthTextField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it },
                label = "Mã OTP",
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Xác nhận",
                onClick = {
                    authViewModel.verifyOtp(
                        email = email,
                        otp = otp
                    )
                },
                enabled = otp.length == 6
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Chưa nhận được mã? ", color = Color.Gray)
                if (isCanResend) {
                    Text(
                        text = "Gửi lại",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            println("EHEHEHEHEHe")
                        }
                    )
                } else {
                    Text(
                        text = "Gửi lại trong 00:${String.format("%02d", secondsLeft)}",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Xác nhận mật khẩu",
                isPassword = true,
                keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password)
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
