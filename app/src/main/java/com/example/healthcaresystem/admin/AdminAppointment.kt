package com.example.healthcaresystem.admin

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
import com.example.healthcaresystem.responsemodel.AppointmentResponse
import com.example.healthcaresystem.viewmodel.AppointmentViewModel

@Composable
fun LichKhamScreen(viewModel: AppointmentViewModel, modifier: Modifier = Modifier) {
    val appointments by viewModel.appoinments.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAppointments()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lịch Hẹn Khám",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (appointments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Không có lịch hẹn", color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(appointments) { appointment ->
                    AppointmentCard(appointment)
                }
            }
        }
    }
}


@Composable
fun AppointmentCard(appointment: AppointmentResponse) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🧑 Bệnh nhân: ${appointment.patient.name}", fontWeight = FontWeight.Bold)
            Text("⚕️ Chuẩn đoán: ${appointment.reason}")
            Text("👨‍⚕️ Bác sĩ: ${appointment.doctor.name}")
            Text("📅 Ngày: ${appointment.date}")
            Text("⏰ Giờ: ${appointment.time}")
            Text("Trạng thái: ${appointment.status}")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa", color = Color.White)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xác nhận xóa", color = Color.White)
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
