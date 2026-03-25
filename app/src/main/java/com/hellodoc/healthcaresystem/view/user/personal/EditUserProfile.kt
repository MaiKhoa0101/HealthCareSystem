package com.hellodoc.healthcaresystem.view.user.personal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.UpdateUserInput
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel


@Composable
fun EditUserProfile(navHostController: NavHostController) {
// Khởi tạo ViewModel bằng custom factory để truyền SharedPreferences
    val userViewModel: UserViewModel = hiltViewModel()

    val context = LocalContext.current

    val userId = userViewModel.getUserAttribute("userId", context)

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
    var avatarURL by remember { mutableStateOf<Uri?>(null) }
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
            item { ChangeAvatar(
                user = user!!,
                imageUri = avatarURL,
                onImageSelected = { avatarURL = it }) }
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
                    avatarURL = avatarURL,
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                        )
                    )
                )
                .height(64.dp)
        ) {
            IconButton(
                onClick = { navHostController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Chỉnh sửa hồ sơ",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ChangeAvatar(
    user: User,
    imageUri: Uri?,
    onImageSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showRationaleDialog by remember { mutableStateOf(false) }

    // Launcher để chọn ảnh
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    // Launcher để xin quyền
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Quyền được cấp, mở image picker
            imagePickerLauncher.launch("image/*")
        } else {
            // Quyền bị từ chối, hiển thị dialog thông báo
            showPermissionDialog = true
        }
    }

    // Function để kiểm tra và xin quyền
    fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                // Đã có quyền, mở image picker
                imagePickerLauncher.launch("image/*")
            }
            else -> {
                // Chưa có quyền, xin quyền
                permissionLauncher.launch(permission)
            }
        }
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clip(CircleShape).border(1.dp, MaterialTheme.colorScheme.primary, CircleShape).clickable {
                checkAndRequestPermission()
            }
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                )
            } else {
                AsyncImage(
                    model = user.avatarURL,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
                )
            }
            Icon(
                painter = painterResource(R.drawable.camera),
                contentDescription = "Change Avatar",
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), CircleShape)
                    .padding(6.dp)
                    .clip(CircleShape)
            )
        }
    }

    // Dialog thông báo khi quyền bị từ chối
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Cần quyền truy cập") },
            text = { Text("Ứng dụng cần quyền truy cập ảnh để thay đổi avatar. Vui lòng cấp quyền trong Cài đặt.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        checkAndRequestPermission()                    }
                ) {
                    Text("Đồng ý")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Hủy")
                }
            }
        )
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
    avatarURL: Uri?,
    role: String,
    viewModel: UserViewModel= hiltViewModel(),
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val updateSuccess by viewModel.updateSuccess.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()

    // Điều hướng khi update thành công
    LaunchedEffect(updateSuccess) {
        if (updateSuccess == true) {
            navHostController.popBackStack()
        }
    }

    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = !isUpdating, // khi đang update thì disable nút
        onClick = {
            if (password != repassword) {
                Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
            } else {
                val updateUser = UpdateUserInput(
                    name = name,
                    email = email,
                    phone = phone,
                    address = address,
                    avatarURL = avatarURL,
                    role = role,
                    password = password
                )
                viewModel.updateUser(userId, updateUser, context)
            }
        }
    ) {
        if (isUpdating) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(15.dp))
            Text("Đang lưu...")
        } else {
            Text("Lưu thay đổi")
        }
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
            placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onBackground) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}