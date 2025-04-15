package com.hellodoc.healthcaresystem.user.personal

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.R

@Composable
fun EditUserProfile(navHostController: NavHostController) {
    Scaffold(
        topBar = { HeadbarEditUserProfile(navHostController) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues).padding(horizontal = 10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item { ChangeAvatar() }
            item { Spacer(modifier = Modifier.height(10.dp)) }
            item { ContentEditUser() }
            item { AcceptEditButton() }
        }
    }
}

@Composable
fun AcceptEditButton(){
    Button (
        modifier = Modifier.fillMaxWidth(),
        onClick = {}
    ){
        Text("Lưu thay đổi")
    }
}


@Composable
fun HeadbarEditUserProfile(navHostController: NavHostController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF00BCD4)) // A pleasant cyan
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_back),
            contentDescription = "Back button",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(28.dp)
                .clickable { navHostController.navigate("personal") }
        )
        Text(
            text = "Chỉnh sửa hồ sơ",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ChangeAvatar() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)

    ) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.clickable {
                // TODO: Open image picker or dialog to change avatar
            }
        ) {
            Image(
                painter = painterResource(R.drawable.avarta),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape)
            )
            Icon(
                painter = painterResource(R.drawable.camera),
                contentDescription = "Change Avatar",
                tint = Color.White,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .padding(6.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun ContentEditUser() {
    var nameText by remember { mutableStateOf("") }
    var usernameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var phoneText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var repasswordText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InputEditField("Họ và tên", nameText, { nameText = it }, "Nguyễn Văn A")
        InputEditField("Tên đăng nhập", usernameText, { usernameText = it }, "nguyenvana")
        InputEditField("Email", emailText, { emailText = it }, "example@gmail.com")
        InputEditField("Số điện thoại", phoneText, { phoneText = it }, "0123456789")
        InputEditField("Mật khẩu", passwordText, { passwordText = it }, "Mật khẩu", isPassword = true)
        InputEditField("Nhập lại mật khẩu", repasswordText, { repasswordText = it }, "Nhập lại mật khẩu", isPassword = true)
    }
}

@Composable
fun InputEditField(
    nameField: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = nameField,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditUserProfilePreview() {
    val navHostController = rememberNavController()
    EditUserProfile(navHostController = navHostController)
}
