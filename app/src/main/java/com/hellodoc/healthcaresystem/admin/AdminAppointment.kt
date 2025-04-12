package com.hellodoc.healthcaresystem.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.appointment.model.AppointmentRow
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel

@Composable
fun AppointmentManagerScreen(viewModel: AppointmentViewModel, modifier: Modifier = Modifier) {
    AppointmentManagerScreen(appointmentList)
}

@Preview(showBackground = true)
@Composable
fun AppointmentManagerScreenPreview() {
    AppointmentManagerScreen(appointmentList)
}

@Composable
fun AppointmentManagerScreen(appointments: List<AppointmentRow>) {
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        item {
            Text("Danh sách lịch hẹn", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            CardAppointment(R.drawable.checkedbigicon, 3195, "Lịch hẹn mới được đặt")
            Spacer(Modifier.height(16.dp))
            CardAppointment(R.drawable.calendarbigicon, 3995, "Tổng lịch hẹn")

            Spacer(Modifier.height(24.dp))
            Text("Quản lí lịch hẹn khám", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(16.dp))
            SearchBar()
            Spacer(Modifier.height(16.dp))

            TableDesign(appointments)
        }
    }
}

@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") }
    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        placeholder = { Text("Tìm kiếm") },
        trailingIcon = {
            Image(
                painter = painterResource(id = R.drawable.searchicon),
                contentDescription = null
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun TableDesign(appointmentList: List<AppointmentRow>) {
    LazyRow {
        item {
            Column {
                // Header
                Row(
                    Modifier
                        .background(Color(0xFF2B544F))
                        .padding(vertical = 8.dp)
                ) {
                    TableHeaderCell("ID", 60)
                    TableHeaderCell("Bệnh nhân", 120)
                    TableHeaderCell("Chẩn đoán", 150)
                    TableHeaderCell("Bác sĩ", 100)
                    TableHeaderCell("Thời gian", 100)
                    TableHeaderCell("Nơi khám", 200)
                    TableHeaderCell("Ngày tạo", 120)
                    TableHeaderCell("Trạng thái", 120)
                }

                // Rows
                appointmentList.forEachIndexed { index, row ->
                    val bgColor = if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bgColor)
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        TableCell(row.id, 60)
                        TableCell(row.patientName, 120)
                        TableCell(row.diagnosis, 150)
                        TableCell(row.doctor, 100)
                        TableCell(row.time, 100)
                        TableCell(row.location, 200)
                        TableCell(row.createdAt, 120)
                        TableCell(
                            text = row.status,
                            width = 120,
                            color = if (row.status == "Xác Nhận") Color(0xFF27AE60) else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableCell(text: String, width: Int, color: Color = Color.Black) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun TableHeaderCell(text: String, width: Int) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CardAppointment(icon: Int, number: Int, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(number.toString(), style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(20.dp))
                Text(description, fontWeight = FontWeight.Light, fontSize = 10.sp)
            }
        }
    }
}

// Giả lập dữ liệu
val appointmentList = listOf(
    AppointmentRow("1", "Phuong", "Support for theme", "Khoa", "10 am", "Số 1, Đường Tô Ký, Q12", "2025-01-19", "Xác Nhận"),
    AppointmentRow("2", "Phuong", "Fake doctor", "Khoa", "10 am", "Số 1, Đường Tô Ký, Q12", "2023-08-28", "Xác Nhận"),
    AppointmentRow("3", "Phuong", "Support for theme", "Khoa", "10 am", "Số 1, Đường Tô Ký, Q12", "2025-01-19", "Xác Nhận"),
    AppointmentRow("4", "Phuong", "Support for theme", "Khoa", "10 am", "Trực tuyến", "2023-08-28", "Chờ xác Nhận"),
    AppointmentRow("5", "Phuong", "Support for theme", "Khoa", "10 am", "Số 1, Đường Tô Ký, Q12", "2025-01-19", "Xác Nhận"),
    AppointmentRow("6", "Phuong", "Fake doctor", "Khoa", "10 am", "Số 1, Đường Tô Ký, Q12", "2023-08-28", "Xác Nhận"),
    AppointmentRow("7", "Phuong", "Support for theme", "Khoa", "10 am", "Số 1, Đường Tô Ký, Q12", "2025-01-19", "Xác Nhận"),
    AppointmentRow("8", "Phuong", "Support for theme", "Khoa", "10 am", "Trực tuyến", "2023-08-28", "Chờ xác Nhận"),
    AppointmentRow("9", "Phuong", "Support for theme", "Khoa", "10 am", "Số 1, Đường Tô Ký, Q12", "2025-01-19", "Xác Nhận"),
    AppointmentRow("10", "Phuong", "Fake doctor", "Khoa", "10 am", "Số 1, Đường Tô Ký, Q12", "2023-08-28", "Xác Nhận"),
    AppointmentRow("11", "Phuong", "Support for theme", "Khoa", "10 am", "Số 1, Đường Tô Ký, Q12", "2025-01-19", "Xác Nhận"),
    AppointmentRow("12                                                             ", "Phuong", "Support for theme", "Khoa", "10 am", "Trực tuyến", "2023-08-28", "Chờ xác Nhận")

)
