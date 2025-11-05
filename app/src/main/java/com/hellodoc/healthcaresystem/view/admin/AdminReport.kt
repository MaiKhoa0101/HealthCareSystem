package com.hellodoc.healthcaresystem.view.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ComplaintData
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
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.reportService.getAllReports()
                reportList.clear()
                response.forEachIndexed { index, report ->
                    reportList.add(
                        ComplaintData(
                            id = (index + 1).toString(),
                            reportId = report._id,
                            user = report.reporter?.name ?: "Không rõ",
                            content = report.content ?: "Không có nội dung",
                            targetType = report.type ?: "Không xác định",
                            status = report.status ?: "opened",
                            createdDate = report.createdAt?.substring(0, 10) ?: "Không rõ",
                            reportedId = report.reportedId ?: "Không rõ",
                            postId = report.postId
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    NavHost(navController = navController, startDestination = "ReportMain") {
        composable("ReportMain") {
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

                        ComplaintStatsScreen(reportList = reportList)

                        Text(
                            text = "Quản lí khiếu nại",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(16.dp)
                        )
                        TableReport(
                            reportList = reportList,
                            onDetailClick = { selectedComplaint = it },
                            navController = navController
                        )
                    }
                }
            }
        }
        composable("RespondScreen/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val complaint = reportList.find { it.reportId == id }
            if (complaint != null) {
                ReportResponseScreen(
                    complaint = complaint,
                    onBack = { navController.popBackStack() }
                )
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
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Người báo cáo: ")
                            }
                            append(selectedComplaint!!.user)
                        },
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Nội dung: ")
                            }
                            append(selectedComplaint!!.content)
                        },
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Loại: ")
                            }
                            append(selectedComplaint!!.targetType)
                        },
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Ngày tạo: ")
                            }
                            append(selectedComplaint!!.createdDate)
                        },
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("ID người bị báo cáo: ")
                            }
                            append(selectedComplaint?.reportedId ?: "Không rõ")
                        },
                        fontSize = 18.sp
                    )
                    if (selectedComplaint?.targetType == "Bài viết") {
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("ID bài viết: ")
                                }
                                append(selectedComplaint?.postId ?: "Không rõ")
                            },
                            fontSize = 18.sp
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun TableReport(
    reportList: List<ComplaintData>,
    onDetailClick: (ComplaintData) -> Unit,
    navController: NavController
){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
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
                                                navController.navigate("RespondScreen/${complaint.reportId}")
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
                                                    Text("Delete")
                                                }
                                            },
                                            onClick = {
                                                expanded = false
                                                coroutineScope.launch {
                                                    if (complaint.status == "opened") {
                                                        Toast.makeText(context, "Chưa thể xóa khiếu nại đang chờ xử lý", Toast.LENGTH_SHORT).show()
                                                        return@launch
                                                    }

                                                    val result = RetrofitInstance.reportService.deleteReport(complaint.reportId)
                                                    if (result.isSuccessful) {
                                                        (reportList as MutableList).remove(complaint)
                                                        Toast.makeText(context, "Đã xóa khiếu nại", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
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
fun ComplaintStatsScreen(reportList: List<ComplaintData>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ComplaintCard(
            icon = Icons.Default.LocalOffer,
            iconColor = Color(0xFF6A5ACD),
            number = reportList.size.toString(),
            label = "Tổng khiếu nại"
        )

        ComplaintCard(
            icon = Icons.Default.AccessTime,
            iconColor = Color(0xFFFFC107),
            number = reportList.count {it.status == "pending"}.toString(),
            label = "Khiếu nại chờ duyệt"
        )

        ComplaintCard(
            icon = Icons.Default.CheckCircle,
            iconColor = Color(0xFF009688),
            number = reportList.count {it.status == "closed"}.toString(),
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