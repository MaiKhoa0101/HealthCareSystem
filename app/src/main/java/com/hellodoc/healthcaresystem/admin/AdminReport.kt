package com.hellodoc.healthcaresystem.admin

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.Search
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
import com.hellodoc.healthcaresystem.responsemodel.ComplaintData
import com.hellodoc.healthcaresystem.responsemodel.ReportResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
fun PreviewReportListScreen() {
    ReportManagerScreen()
}

@Composable
fun ReportManagerScreen() {
    val backgroundColor = Color(0xFFF4F5F7)
    val reportList = remember { mutableStateListOf<ComplaintData>() }
    val coroutineScope = rememberCoroutineScope()
    var selectedComplaint by remember { mutableStateOf<ComplaintData?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.reportService.getAllReports()
                reportList.clear()
                response.reversed().forEachIndexed { index, report ->
                    reportList.add(
                        ComplaintData(
                            id = (index + 1).toString(),
                            user = report.reporter?.name ?: "Không rõ",
                            content = report.content ?: "Không có nội dung",
                            targetType = report.type ?: "Không xác định",
                            status = report.status ?: "pending",
                            createdDate = report.createdAt?.substring(0, 10) ?: "Không rõ",
                            reportedId = report.reportedId ?: "Không rõ"
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            item {
                Text(
                    text = "Danh sách khiếu nại",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                ComplaintStatsScreen()

                Text(
                    text = "Quản lí khiếu nại",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                )
                TableReport(reportList) { selectedComplaint = it }
            }
        }
    }
    if (selectedComplaint != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .wrapContentHeight()
                    .padding(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Chi tiết khiếu nại", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            "Đóng",
                            color = Color.Red,
                            modifier = Modifier.clickable { selectedComplaint = null }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Người báo cáo: ${selectedComplaint!!.user}")
                    Text("Nội dung: ${selectedComplaint!!.content}")
                    Text("Loại: ${selectedComplaint!!.targetType}")
                    Text("Ngày tạo: ${selectedComplaint!!.createdDate}")
                    Text("ID người bị báo cáo: ${selectedComplaint?.reportedId ?: "Không rõ"}")
                }
            }
        }
    }
}

@Composable
fun TableReport(
    reportList: List<ComplaintData>,
    onDetailClick: (ComplaintData) -> Unit
){

    LazyRow {
        item {
            Column {
                // Header
                Row(
                    Modifier
                        .background(Color(0xFF2196F3))
                        .padding(vertical = 8.dp)
                ) {
                    ComplaintTableHeader()
                }

                // Rows
                reportList.forEachIndexed { index, complaint ->
                    val bgColor = if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
                    var expanded by remember { mutableStateOf(false) }
                    Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(bgColor)
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                TableCell(complaint.id, width = 60.dp)
                                TableCell(complaint.user, width = 100.dp)
                                TableCell(complaint.content, width = 150.dp)
                                TableCell(complaint.targetType, width = 140.dp)
                                TableCell(complaint.status, width = 120.dp)
                                TableCell(complaint.createdDate, width = 100.dp)
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
                                                        imageVector = Icons.Default.Search,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text("Detail")
                                                }
                                            },
                                            onClick = {
                                                expanded = false
                                                onDetailClick(complaint)
                                            }
                                        )
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
                }
            }
        }
    }
}


@Composable
fun ComplaintTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF2196F3))
            .padding(vertical = 8.dp)
    ) {
        TableCell(text = "ID", isHeader = true, width = 60.dp)
        TableCell(text = "Người dùng", isHeader = true, width = 100.dp)
        TableCell(text = "Nội dung", isHeader = true, width = 150.dp)
        TableCell(text = "Bác sĩ/Ứng dụng", isHeader = true, width = 140.dp)
        TableCell(text = "Trạng thái", isHeader = true, width = 120.dp)
        TableCell(text = "Ngày tạo", isHeader = true, width = 100.dp)
        TableCell(text = "Chức năng", isHeader = true, width = 100.dp)
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