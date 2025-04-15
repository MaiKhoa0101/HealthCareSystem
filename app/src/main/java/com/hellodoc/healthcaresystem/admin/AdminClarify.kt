package com.hellodoc.healthcaresystem.admin

import android.content.Context
import androidx.compose.foundation.ScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.user.home.model.Account
import org.checkerframework.checker.units.qual.A

@Preview(showBackground = true)
@Composable
fun PreviewClarifyListScreen() {
    ClarifyManagerScreen()
}

@Composable
fun ClarifyManagerScreen() {
    val accountList = remember {
        mutableStateListOf(
            Account("phuong@gmail.com", "0783203982", "GP-123", false),
            Account("anh@gmail.com", "0901234567", "GP-456", false),
            Account("mai@gmail.com", "0912345678", "GP-789", false),
            Account("nam@gmail.com", "0923456789", "GP-012", false),
            Account("lan@gmail.com", "0934567890", "GP-345", false),
            Account("hung@gmail.com", "0945678901", "GP-678", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("vu@gmail.com", "0967890123", "GP-234", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
            Account("trang@gmail.com", "0956789012", "GP-901", false),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tiêu đề

            Text(
                text = "Xác thực tài khoản",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))


        // Số lượng tài khoản

            Text(
                text = "${accountList.size} tài khoản",
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xFF2E7D32), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            ClarifyTable(accountList)

    }
}

@Composable
fun ClarifyTable(clarifies: List<Account>) {
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Column {
            Row(
                modifier = Modifier
                    .background(Color(0xFF2B544F))
                    .padding(vertical = 8.dp)
            ) {
                TableCell("ID", isHeader = true, width = 60.dp)
                TableCell("Email", isHeader = true, width = 200.dp)
                TableCell("Số điện thoại", isHeader = true, width = 150.dp)
                TableCell("CCHN", isHeader = true, width = 120.dp)
                TableCell("Chức năng", isHeader = true, width = 100.dp)
            }

            LazyColumn {
                itemsIndexed(clarifies) { index, clarify ->
                    ClarifyRow(index + 1, clarify)
                }
            }
        }
    }
}

@Composable
fun ClarifyRow(id: Int, account: Account) {
    var expanded by remember { mutableStateOf(false)}

    Row(
        modifier = Modifier
            .background(if (id % 2 == 0) Color(0xFFF0F0F0) else Color.White)
            .padding(vertical = 8.dp)
    ) {
        TableCell(id.toString(), width = 60.dp)
        TableCell(account.email, width = 200.dp)
        TableCell(account.phone, width = 150.dp)
        TableCell(account.licenseId, width = 120.dp)
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
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verified account")
                        }
                    },
                    onClick = {
                        expanded = false
                        // Thêm logic xử lý xác minh
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
                        // Thêm logic xử lý xóa
                    }
                )
            }
        }

    }
}

@Composable
fun TableCell(text: String, isHeader: Boolean = false, width: Dp) {
    Text(
        text = text,
        color = if (isHeader) Color.White else Color.Black,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp),
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        fontSize = 14.sp
    )
}