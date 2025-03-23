package com.example.healthcaresystem.Admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.example.healthcaresystem.viewmodel.UserViewModel
import com.example.healthcaresystem.model.GetUser
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.healthcaresystem.model.UpdateUser

class AdminUser : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            HealthCareSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserListScreen(
                        modifier = Modifier.padding(innerPadding),
                        sharedPreferences = sharedPreferences
                    )
                }
            }
        }
    }
}

@Composable
fun UserListScreen(modifier: Modifier = Modifier, sharedPreferences: SharedPreferences) {
    val context = LocalContext.current
    val viewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val users by viewModel.users.collectAsState()
    var userName by remember { mutableStateOf("Người dùng") }
    var role by remember { mutableStateOf("Người dùng") }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
        userName = viewModel.getUserNameFromToken()
        role = viewModel.getUserRole()
    }

    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Xin chào, $role $userName", style = MaterialTheme.typography.headlineMedium)
            Button(onClick = { viewModel.logout(context) }) {
                Text(text = "Đăng xuất")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Danh sách người dùng", style = MaterialTheme.typography.headlineSmall)

        if (users.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Không có người dùng", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 10.dp)) {
                items(users) { user ->
                    UserItem(user = user, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun UserItem(user: GetUser, viewModel: UserViewModel) {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var role by remember { mutableStateOf(user.role) }
    var phone by remember { mutableStateOf(user.phone) }
    var password by remember { mutableStateOf("") } // Không điền sẵn để tránh rủi ro bảo mật

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "ID: ${user.id}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Tên: $name", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Email: $email", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Role: $role", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Số điện thoại: $phone", style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = { isEditing = true }) {
                Text(text = "Chỉnh sửa")
            }
        }
    }

    if (isEditing) {
        AlertDialog(
            onDismissRequest = { isEditing = false },
            title = { Text("Chỉnh sửa thông tin người dùng") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu (để trống nếu không đổi)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val updatedUser = UpdateUser(
                        name = name,
                        email = email,
                        phone = phone,
                        password = if (password.isNotEmpty()) password else null // Chỉ gửi nếu có thay đổi
                    )

                    Log.d("UserItem", "User ID cần cập nhật: ${user.id}")
                    Log.d("UserItem", "Dữ liệu gửi lên API: $updatedUser")

                    viewModel.updateUser(user.id, updatedUser)
                    isEditing = false
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                Button(onClick = { isEditing = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewUserListScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    HealthCareSystemTheme {
        UserListScreen(sharedPreferences = sharedPreferences)
    }
}
