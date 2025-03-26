package com.example.healthcaresystem.admin

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
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.healthcaresystem.R
import com.example.healthcaresystem.model.UpdateUser

//class AdminUser : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//            HealthCareSystemTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    UserListScreen(
//                        modifier = Modifier.padding(innerPadding),
//                        sharedPreferences = sharedPreferences
//                    )
//                }
//            }
//        }
//    }
//}


@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    sharedPreferences: SharedPreferences
) {
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

    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Danh sách người dùng",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        if (users.isEmpty()) {
            EmptyUserList()
        } else {
            UserList(users = users, viewModel = viewModel)
        }
    }
}


@Composable
fun EmptyUserList() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Không có người dùng",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun UserList(users: List<GetUser>, viewModel: UserViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {
        items(users) { user ->
            UserItem(user = user, viewModel = viewModel)
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
    var password by remember { mutableStateOf("") } // Empty to avoid security risks
    val currentPassword = user.password ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp), // Space between the two cards
        verticalAlignment = Alignment.CenterVertically
    ) {
        // First Card: User Details
        Card(
            modifier = Modifier
                .weight(1f) // Occupy available space proportionally
                .height(150.dp) // Set height for the card
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        bottomStart = 16.dp,
                        topEnd = 0.dp, // Inner corner (near the gap) is sharp
                        bottomEnd = 0.dp // Inner corner (near the gap) is sharp
                    )
                ),
            elevation = CardDefaults.cardElevation(200.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp) // Space between text items
            ) {
                Text(
                    text = "ID: ${user.id}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Tên: $name",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Email: $email",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Role: $role",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Số điện thoại: $phone",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Second Card: Action Buttons
        Card(
            modifier = Modifier
                .height(150.dp) // Match the height of the first card
                .width(100.dp) // Fixed width for the buttons card
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp, // Inner corner (near the gap) is sharp
                        bottomStart = 0.dp, // Inner corner (near the gap) is sharp
                        topEnd = 16.dp,
                        bottomEnd = 16.dp
                    )
                ),
            elevation = CardDefaults.cardElevation(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Cyan // Set the background color of the card
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center, // Center the buttons
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { isEditing = true },
                    modifier = Modifier.size(40.dp) // Adjust button size
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)
                    )
                }
                IconButton(
                    onClick = { /* Handle delete action */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.minus),
                        contentDescription = "Remove",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }


    if (isEditing) {
        EditUserDialog(
            user = user,
            name = name,
            email = email,
            role = role,
            phone = phone,
            password = password,
            onNameChange = { name = it },
            onEmailChange = { email = it },
            onRoleChange = { role = it },
            onPhoneChange = { phone = it },
            onPasswordChange = { password = it },
            onConfirm = {
                val updatedUser = UpdateUser(
                    name = name,
                    email = email,
                    phone = phone,
                    password = if (password.isNotEmpty()) password else currentPassword,
                    role = role
                )
                Log.d("UserItem", "User ID to update: ${user.id}")
                Log.d("UserItem", "Data sent to API: $updatedUser")
                viewModel.updateUser(user.id, updatedUser)
                isEditing = false
            },
            onDismiss = { isEditing = false }
        )
    }
}

@Composable
fun EditUserDialog(
    user: GetUser,
    name: String,
    email: String,
    role: String,
    phone: String,
    password: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Chỉnh sửa thông tin người dùng") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Tên") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Mật khẩu (để trống nếu không đổi)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = onRoleChange,
                    label = { Text("Role") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Lưu")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
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
