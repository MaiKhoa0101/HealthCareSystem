package com.parkingSystem.parkingSystem.admin

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.rememberAsyncImagePainter
import com.parkingSystem.parkingSystem.requestmodel.UpdateUserInput
import com.parkingSystem.parkingSystem.responsemodel.User
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel

@Composable
fun UserListScreen() {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }

    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val userList by userViewModel.allUser.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("User") }
    val users = userList?.users ?: emptyList()

    LaunchedEffect(Unit) {
        userViewModel.getAllUsers()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "User Management",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${users.size} accounts",
            color = Color.White,
            modifier = Modifier
                .background(Color(0xFF2E7D32), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search by name or email...") },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
            )

            DropdownMenuRoleSelector(
                selected = selectedRole,
                onSelected = { selectedRole = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filteredUsers = users.filter {
            (it.email?.contains(searchText, ignoreCase = true) == true || searchText.isEmpty()) &&
                    (it.role?.equals(selectedRole, ignoreCase = true) == true)
        }

        AccountTable2(filteredUsers, sharedPreferences)
    }
}

@Composable
fun DropdownMenuRoleSelector(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(text = selected)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("User", "Admin").forEach { role ->
                DropdownMenuItem(
                    text = { Text(role) },
                    onClick = {
                        onSelected(role)
                        expanded = false
                    }
                )
            }
        }
    }
}

object TableWidths {
    val ID = 60.dp
    val NAME = 150.dp
    val EMAIL = 200.dp
    val PHONE = 150.dp
    val ADDRESS = 180.dp
    val CREATED = 180.dp
    val UPDATED = 180.dp
    val ACTIONS = 120.dp

    val TOTAL = ID + NAME + EMAIL + PHONE + ADDRESS + CREATED + UPDATED + ACTIONS
}

@Composable
fun AccountTable2(accounts: List<User>, sharedPreferences: SharedPreferences) {
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .width(TableWidths.TOTAL)
                    .height(48.dp)
            ) {
                TableCell("ID", isHeader = true, width = TableWidths.ID)
                TableCell("Name", isHeader = true, width = TableWidths.NAME)
                TableCell("Email", isHeader = true, width = TableWidths.EMAIL)
                TableCell("Phone", isHeader = true, width = TableWidths.PHONE)
                TableCell("Address", isHeader = true, width = TableWidths.ADDRESS)
                TableCell("Created At", isHeader = true, width = TableWidths.CREATED)
                TableCell("Updated At", isHeader = true, width = TableWidths.UPDATED)
                TableCell("Actions", isHeader = true, width = TableWidths.ACTIONS)
            }

            // Content
            LazyColumn {
                itemsIndexed(accounts) { index, account ->
                    AccountRow2(index + 1, account, sharedPreferences)
                }
            }
        }
    }
}

@Composable
fun AccountRow2(uid: Int, account: User, sharedPreferences: SharedPreferences) {
    var expanded by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    Row(
        modifier = Modifier
            .width(TableWidths.TOTAL)
            .height(56.dp)
            .background(if (uid % 2 == 0) Color(0xFFF5F5F5) else Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(uid.toString(), width = TableWidths.ID, height = 56.dp)
        TableCell(account.name ?: "N/A", width = TableWidths.NAME, height = 56.dp)
        TableCell(account.email ?: "N/A", width = TableWidths.EMAIL, height = 56.dp)
        TableCell(account.phone ?: "N/A", width = TableWidths.PHONE, height = 56.dp)
        TableCell(account.address ?: "N/A", width = TableWidths.ADDRESS, height = 56.dp)
        TableCell(account.createdAt ?: "N/A", width = TableWidths.CREATED, height = 56.dp)
        TableCell(account.updatedAt ?: "N/A", width = TableWidths.UPDATED, height = 56.dp)

        // Actions Cell
        Box(
            modifier = Modifier
                .width(TableWidths.ACTIONS)
                .height(56.dp)
                .background(Color.White)
                .border(1.dp, Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    expanded = true
                    println("Menu clicked for user: ${account.email}")
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF5F5F5), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = Color(0xFF2B544F),
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier
                    .widthIn(min = 150.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Edit",
                                fontSize = 15.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        selectedUser = account
                        showEditDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )

                HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFD32F2F),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Delete",
                                fontSize = 15.sp,
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    onClick = {
                        expanded = false
                        showDeleteDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }

    // Edit Dialog
    if (showEditDialog && selectedUser != null) {
        EditUserDialog(
            user = selectedUser!!,
            onDismiss = {
                showEditDialog = false
                selectedUser = null
            },
            onSave = { id, updatedInput, context ->
                userViewModel.updateUser(id, updatedInput, context)
                showEditDialog = false
                selectedUser = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Confirm deletion?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Are you sure you want to delete this user?",
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Email: ${account.email ?: "N/A"}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2B544F),
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠️ This action cannot be undone!",
                        color = Color(0xFFD32F2F),
                        fontSize = 13.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userViewModel.deleteUser(account.uid)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xóa", fontSize = 15.sp)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancel", fontSize = 15.sp)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun EditUserDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (String, UpdateUserInput, Context) -> Unit,
    context: Context = LocalContext.current
) {
    var name by remember { mutableStateOf(user.name ?: "") }
    var email by remember { mutableStateOf(user.email ?: "") }
    var phone by remember { mutableStateOf(user.phone ?: "") }
    var address by remember { mutableStateOf(user.address ?: "") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val updatedInput = UpdateUserInput(
                    address = address,
                    name = name,
                    email = email,
                    phone = phone,
                    password = password.takeIf { it.isNotBlank() } ?: "",
                    role = user.role ?: "User"
                )
                onSave(user.uid, updatedInput, context)
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Edit account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full name") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") })
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("New password (leave blank to keep)") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun TableCell(
    text: String,
    isHeader: Boolean = false,
    width: Dp,
    height: Dp = if (isHeader) 48.dp else 56.dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(
                if (isHeader) Color(0xFF2B544F)
                else Color.Transparent
            )
            .border(0.5.dp, Color.LightGray)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            color = if (isHeader) Color.White else Color.Black,
            fontSize = if (isHeader) 14.sp else 13.sp,
            lineHeight = 16.sp
        )
    }
}
