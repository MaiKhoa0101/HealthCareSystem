package com.hellodoc.healthcaresystem.user.personal

import android.content.SharedPreferences
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.UpdateUser
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

@Composable
fun EditUserProfile(sharedPreferences: SharedPreferences ,navHostController: NavHostController) {
// Khởi tạo ViewModel bằng custom factory để truyền SharedPreferences
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })


    val token = sharedPreferences.getString("access_token", null)

    val jwt = remember(token) {
        try {
            JWT(token ?: throw IllegalArgumentException("Token is null"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val userId = jwt?.getClaim("userId")?.asString()

    // Gọi API để fetch user từ server
    LaunchedEffect(userId) {
        userId?.let {
            userViewModel.getUser(it)
        }

    }
    // Lấy dữ liệu user từ StateFlow
    val user by userViewModel.user.collectAsState()
    if (user == null) return

    // 🔁 State lưu thông tin chỉnh sửa
    var nameText by remember { mutableStateOf(user!!.name) }
    var emailText by remember { mutableStateOf(user!!.email) }
    var phoneText by remember { mutableStateOf(user!!.phone) }
    var addressText by remember { mutableStateOf(user!!.address) }
    var passwordText by remember { mutableStateOf("") }
    var repasswordText by remember { mutableStateOf("") }

    Scaffold(
        topBar = { HeadbarEditUserProfile(navHostController) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues).padding(horizontal = 10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item { ChangeAvatar(user!!) }
            item {
                ContentEditUser(
                    nameText, { nameText = it },
                    emailText, { emailText = it },
                    phoneText, { phoneText = it },
                    addressText, { addressText = it },
                    passwordText, { passwordText = it },
                    repasswordText, { repasswordText = it },
                )
            }
            item {
                AcceptEditButton(
                    userId = user!!.id,
                    nameText,
                    emailText,
                    phoneText,
                    addressText,
                    passwordText,
                    repasswordText,
                    avatarURL = user!!.avatarURL,
                    role = user!!.role,
                    viewModel = userViewModel,
                    navHostController
                )
            }
        }
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
fun ChangeAvatar(user: User) {
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
            AsyncImage(
                model = user.avatarURL,
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
fun ContentEditUser(
    nameText: String, onNameChange: (String) -> Unit,
    emailText: String, onEmailChange: (String) -> Unit,
    phoneText: String, onPhoneChange: (String) -> Unit,
    addressText: String, onAddressChange: (String) -> Unit,
    passwordText: String, onPasswordChange: (String) -> Unit,
    repasswordText: String, onRepasswordChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InputEditField("Họ và tên", nameText, onNameChange, "")
        InputEditField("Email", emailText, onEmailChange, "")
        InputEditField("Số điện thoại", phoneText, onPhoneChange, "")
        InputEditField("Địa chỉ", addressText, onAddressChange, "")
        InputEditField("Mật khẩu", passwordText, onPasswordChange, "", isPassword = true)
        InputEditField("Nhập lại mật khẩu", repasswordText, onRepasswordChange, "", isPassword = true)
    }
}

@Composable
fun AcceptEditButton(
    userId: String,
    name: String,
    email: String,
    phone: String,
    address: String,
    password: String,
    repassword: String,
    avatarURL: String,
    role: String,
    viewModel: UserViewModel,
    navHostController: NavHostController
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (password != repassword) {
                println("Mật khẩu không khớp")
            }
            else {

                val updateUser = UpdateUser(
                    name = name,
                    email = email,
                    phone = phone,
                    address = address,
                    avatarURL = avatarURL,
                    role = role,
                    password = if (password.isBlank()) null else password
                )

                viewModel.updateUser(userId, updateUser)
                navHostController.navigate("personal")
            }
        }
    ) {
        Text("Lưu thay đổi")
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