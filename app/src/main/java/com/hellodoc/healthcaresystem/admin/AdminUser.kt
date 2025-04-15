package com.hellodoc.healthcaresystem.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.user.home.model.Account2

@Composable
fun UserListScreen() {
    val accountList2 = remember {
        mutableStateListOf(
            Account2("phuong","phuong@gmail.com", "0783203982", "2025-01-19"),
            Account2("phuong","phuong@gmail.com", "0783203982", "2025-01-19"),
            Account2("phuong","phuong@gmail.com", "0783203982", "2025-01-19"),
            Account2("phuong","minh@gmail.com", "0783203982", "2025-01-19"),
            )
    }

    var searchText by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("User") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Quản lí tài khoản",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${accountList2.size} tài khoản",
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
            // Tìm kiếm
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Tìm...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
                )
            }

            // Lọc
            Column(modifier = Modifier.width(100.dp)) {
                DropdownMenuRoleSelector(selectedRole) {
                    selectedRole = it
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AccountTable2(accountList2.filter {
            it.email.contains(searchText, ignoreCase = true)
        })
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
            listOf("User", "Doctor", "Admin").forEach { role ->
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

@Composable
fun AccountTable2(accounts: List<Account2>) {
    // Cho phép cuộn ngang
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .background(Color(0xFF2B544F))
                    .padding(vertical = 8.dp)
            ) {
                TableCell("ID", isHeader = true, width = 60.dp)
                TableCell("Name", isHeader = true, width = 100.dp)
                TableCell("Email", isHeader = true, width = 200.dp)
                TableCell("Số điện thoại", isHeader = true, width = 150.dp)
                TableCell("Ngày tạo", isHeader = true, width = 120.dp)
                TableCell("Chức năng", isHeader = true, width = 100.dp)
            }

            // Content
            LazyColumn {
                itemsIndexed(accounts) { index, account ->
                    AccountRow2(index + 1, account)
                }
            }
        }
    }
}
@Composable
fun AccountRow2(id: Int, account: Account2) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .background(if (id % 2 == 0) Color(0xFFF0F0F0) else Color.White)
            .padding(vertical = 8.dp)
    ) {
        TableCell(id.toString(), width = 60.dp)
        TableCell(account.name, width = 100.dp)
        TableCell(account.email, width = 200.dp)
        TableCell(account.phone, width = 150.dp)
        TableCell(account.createdDate, width = 120.dp)
        Box(
            modifier = Modifier
                .width(100.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .border(1.dp, Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .background(Color.White)

            ) {
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit, // hoặc icon tuỳ chọn
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit account")
                        }
                    },
                    onClick = {
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Remove")
                        }
                    },
                    onClick = {
                        expanded = false
                    }
                )
            }

        }
    }
}









