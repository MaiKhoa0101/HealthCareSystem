package com.hellodoc.healthcaresystem.presentation.view.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.requestmodel.AdminResponseRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ComplaintData
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportResponseScreen(
    complaint: ComplaintData,
    onBack: () -> Unit
) {
    var responseContent by remember { mutableStateOf("") }
    val date = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { onBack() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Gửi phản hồi khiếu nại",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Thông tin người gửi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Admin: Quản trị viên", fontSize = 17.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Người nhận", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(complaint.user, fontSize = 17.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Ngày gửi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(date, fontSize = 17.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Nội dung phản hồi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(5.dp))
        TextField(
            value = responseContent,
            onValueChange = { responseContent = it },
            placeholder = { Text("Nhập nội dung...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    if (responseContent.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập nội dung phản hồi", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    val result = RetrofitInstance.reportService.sendAdminResponse(
                        id = complaint.reportId, // ID report từ backend
                        response = AdminResponseRequest(
                            responseContent = responseContent,
                            responseTime = date
                        )
                    )
                    if (result.isSuccessful) {
                        complaint.status = "closed"
                        Toast.makeText(context, "Phản hồi đã được gửi", Toast.LENGTH_SHORT).show()
                        onBack()
                    } else {
                        Toast.makeText(context, "Gửi phản hồi thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text("Gửi phản hồi", fontSize = 16.sp)
        }
    }
}

