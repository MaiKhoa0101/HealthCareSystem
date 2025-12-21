package com.hellodoc.healthcaresystem.view.user.home.report

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ComplaintData
import com.hellodoc.healthcaresystem.view.user.personal.TopBar
import com.hellodoc.healthcaresystem.viewmodel.ReportViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun reportManager(
    context: Context,
    navHostController: NavHostController,
) {
    val viewModel: ReportViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    val userId = userViewModel.getUserAttribute("userId", context)
    println("USERID trong report "+ userId)

    val reportList by viewModel.userReportList.collectAsState()

    LaunchedEffect(userId) {
        userId?.let {
            userViewModel.getUser(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getReportByUserId(userId!!)
    }

    if (userId == null) {
        Text("Token không hợp lệ hoặc userId không tồn tại.")
        return
    }

    Scaffold(
        topBar = {
            //Back icon

            TopBar(
                title = "Lịch sử khiếu nại",
                onClick = { navHostController.popBackStack() }
            )
        }
    ) { paddingValues ->
        if (reportList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Bạn chưa có khiếu nại nào.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reportList) { report ->
                    ReportItem(report)
                }
            }
        }
    }
}

@Composable
fun ReportItem(report: ComplaintData) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${report.reportId.takeLast(6)}", // Showing last 6 chars for brevity
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                StatusChip(status = report.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = report.content,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Loại: ${report.targetType}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
                Text(
                    text = report.createdDate, // Ensure format is readable or format it here
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor, text) = when (status.lowercase()) {
        "pending", "opened" -> Triple(Color(0xFFFFF3E0), Color(0xFFEF6C00), "Đang xử lý")
        "resolved", "closed" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Đã giải quyết")
        "rejected" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Đã từ chối")
        else -> Triple(Color.LightGray, Color.Black, status)
    }

    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
