package com.hellodoc.healthcaresystem.admin

import androidx.compose.foundation.ScrollState
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.user.home.model.ComplaintData

@Preview(showBackground = true)
@Composable
fun PreviewReportListScreen() {
    ReportManagerScreen()
}

@Composable
fun ReportManagerScreen() {
    val backgroundColor = Color(0xFFF4F5F7)
    val sampleComplaints = remember {
        mutableStateListOf(
            ComplaintData("1", "Phuong", "Support for theme", "Ứng dụng", "Open", "2025-01-19"),
            ComplaintData("2", "Anh", "Payment issue", "Bác sĩ", "Closed", "2025-01-18"),
            ComplaintData("3", "Mai", "App crash", "Ứng dụng", "Open", "2025-01-17"),
            ComplaintData("4", "Nam", "Wrong diagnosis", "Bác sĩ", "Pending", "2025-01-16"),
            ComplaintData("5", "Lan", "Slow response", "Ứng dụng", "Open", "2025-01-15"),
            ComplaintData("6", "Hùng", "Billing error", "Bác sĩ", "Closed", "2025-01-14"),
            ComplaintData("7", "Trang", "Feature request", "Ứng dụng", "Pending", "2025-01-13"),
            ComplaintData("8", "Vũ", "Login issue", "Ứng dụng", "Open", "2025-01-12")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        // Tiêu đề
            Text(
                text = "Danh sách khiếu nại",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

        // Phần thống kê

            ComplaintStatsScreen()
            Spacer(modifier = Modifier.height(16.dp))


        // Tiêu đề bảng
            Text(
                text = "Quản lí khiếu nại",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            )

        Spacer(modifier = Modifier.height(16.dp))

        ComplaintTable(sampleComplaints)
    }
}

@Composable
fun ComplaintTable(complaints: List<ComplaintData>) {
    // Cho phép cuộn ngang
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .background(Color(0xFF2B544F))
                    .padding(vertical = 8.dp)
            ) {
                TableCell(text = "ID", isHeader = true, width = 60.dp)
                TableCell(text = "Người dùng", isHeader = true, width = 100.dp)
                TableCell(text = "Nội dung", isHeader = true, width = 150.dp)
                TableCell(text = "Bác sĩ/Ứng dụng", isHeader = true, width = 100.dp)
                TableCell(text = "Trạng thái", isHeader = true, width = 80.dp)
                TableCell(text = "Ngày tạo", isHeader = true, width = 100.dp)
                TableCell(text = "Chức năng", isHeader = true, width = 80.dp)
            }

            // Content
            LazyColumn {
                itemsIndexed(complaints) { index, complaint ->
                    ComplaintRow(index + 1, complaint)
                }
            }
        }
    }
}

@Composable
fun ComplaintRow(id: Int, complaint: ComplaintData) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .background(if (id % 2 == 0) Color(0xFFF0F0F0) else Color.White)
            .padding(vertical = 8.dp)
    ) {
        TableCell(id.toString(), width = 60.dp)
        TableCell(complaint.user, width = 100.dp)
        TableCell(complaint.content, width = 150.dp)
        TableCell(complaint.targetType, width = 100.dp)
        TableCell(complaint.status, width = 80.dp)
        TableCell(complaint.createdDate, width = 100.dp)
        Box(
            modifier = Modifier
                .width(80.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Response")
                        }
                    },
                    onClick = {
                        expanded = false
                        // Thêm logic xử lý xác minh
                    }
                )
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Close")
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
fun ComplaintStatsScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ComplaintCard(
            icon = Icons.Default.LocalOffer,
            iconColor = Color(0xFF6A5ACD),
            number = "3947",
            label = "Tổng khiếu nại"
        )

        ComplaintCard(
            icon = Icons.Default.AccessTime,
            iconColor = Color(0xFFFFC107),
            number = "624",
            label = "Khiếu nại chờ duyệt"
        )

        ComplaintCard(
            icon = Icons.Default.CheckCircle,
            iconColor = Color(0xFF009688),
            number = "3195",
            label = "Khiếu nại đóng"
        )
    }
}

@Composable
fun ComplaintCard(
    icon: ImageVector,
    iconColor: Color,
    number: String,
    label: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = number,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = label,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}


