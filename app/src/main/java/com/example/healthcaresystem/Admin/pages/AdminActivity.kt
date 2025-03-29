package com.example.healthcaresystem.Admin.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.healthcaresystem.ui.theme.HealthCareSystemTheme

// Dữ liệu giả lập
data class Appointment(
    val id: String,
    val patientName: String,
    val patientDob: String, // Ngày sinh bệnh nhân
    val diagnosis: String, // Chuẩn đoán bệnh
    val doctorName: String,
    val doctorSpecialty: String, // Chuyên khoa bác sĩ
    val date: String,
    val time: String,
    val type: String // Thêm kiểu khám (Trực tuyến hoặc Tại phòng khám)
)

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LichKhamScreen()
            }
        }
    }
}

@Composable
fun LichKhamScreen() {
    val appointments = remember {
        mutableStateListOf(
            Appointment("1", "Nguyễn Văn Tbành", "12/03/1985", "Cảm cúm", "Bác sĩ B", "Nội tổng quát", "25/03/2025", "08:30", "Tại phòng khám"),
            Appointment("2", "Trần Thị Lưỡng", "22/06/1990", "Đau đầu", "Bác sĩ C", "Thần kinh", "26/03/2025", "10:00", "Khám trực tuyến"),
            Appointment("3", "Lê Văn Cuống", "15/09/1978", "Viêm họng", "Bác sĩ D", "Tai mũi họng", "27/03/2025", "14:15", "Tại phòng khám")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00BCD4)) // Màu nền xanh giống trang chủ
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lịch Hẹn Khám",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (appointments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Không có lịch hẹn", color = Color.White)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(appointments) { appointment ->
                    AppointmentCard(appointment, onDelete = {
                        appointments.remove(appointment)})
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment, onDelete: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("🧑 Bệnh nhân: ${appointment.patientName}", fontWeight = FontWeight.Bold, maxLines = 2)
                    Text("📅 Ngày sinh: ${appointment.patientDob}")
                    Text("⚕️ Chuẩn đoán: ${appointment.diagnosis}")
                    Text("👨‍⚕️ Bác sĩ: ${appointment.doctorName}")
                    Text("🏥 Chuyên khoa: ${appointment.doctorSpecialty}")
                    Text("📅 Ngày: ${appointment.date}")
                    Text("⏰ Giờ: ${appointment.time}")
                    Text("🏥 Loại khám: ${appointment.type}", fontWeight = FontWeight.Bold, color = Color.Blue)
                }
                Button(
                    onClick = { showDialog = true },
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa", maxLines = 1)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDialog = false
                    }
                ) {
                    Text("Xác nhận xóa")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Hủy")
                }
            },
            title = { Text("Xác nhận") },
            text = { Text("Ấn nút xóa để xóa lịch hẹn.") }
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    HealthCareSystemTheme {
        LichKhamScreen()
    }
}