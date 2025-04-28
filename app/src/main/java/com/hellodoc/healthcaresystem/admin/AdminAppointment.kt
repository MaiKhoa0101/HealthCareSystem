package com.hellodoc.healthcaresystem.admin

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.appointment.model.AppointmentRow
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel



//@Preview(showBackground = true)
//@Composable
//fun AppointmentManagerScreenPreview() {
//    AppointmentManagerScreen()
//}

@Composable
fun AppointmentManagerScreen(
    sharedPreferences: SharedPreferences
) {
    val appointViewModel: AppointmentViewModel = viewModel(factory = viewModelFactory {
        initializer { AppointmentViewModel(sharedPreferences) }
    })

    LaunchedEffect(Unit) {
        appointViewModel.fetchAppointments()
    }

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

            TableDesign(sharedPreferences)
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
fun TableDesign(sharedPreferences: SharedPreferences) {
    val appointViewModel: AppointmentViewModel = viewModel(factory = viewModelFactory {
        initializer { AppointmentViewModel(sharedPreferences) }
    })

    val appointmentList = appointViewModel.appointmentsUser.collectAsState()
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
                    TableHeaderCell("Mã BS", 80)
                    TableHeaderCell("Tên BS", 100)
                    TableHeaderCell("Mã BN", 80)
                    TableHeaderCell("Tên BN", 100)
                    TableHeaderCell("Chuyên khoa", 120)
                    TableHeaderCell("Ghi chú", 150)
                    TableHeaderCell("Giờ", 80)
                    TableHeaderCell("Ngày", 100)
                    TableHeaderCell("Nơi khám", 200)
                    TableHeaderCell("Ngày tạo", 120)
                    TableHeaderCell("Trạng thái", 120)
                }

                // Check if list is empty
                if (appointmentList.value.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Không có dữ liệu", color = Color.Gray)
                    }
                } else {
                    appointmentList.value.forEachIndexed { index, row ->
                        val bgColor = if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bgColor)
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            // Đảm bảo các trường này khớp với cấu trúc AppointmentRow
                            TableCell(row.id ?: "trống", 60)
                            TableCell(row.doctor?.id ?: "trống", 80)
                            TableCell(row.doctor?.name ?: "trống", 100)
                            TableCell(row.patient?.id ?: "trống", 80)
                            TableCell(row.patient?.name ?: "trống", 100)
                            TableCell(row.doctor?.specialty?.name ?: "trống", 120)
                            TableCell(row.notes ?: "trống", 150)
                            TableCell(row.time ?: "trống", 80)
                            TableCell(row.date ?: "trống", 100)
                            TableCell(row.location ?: "trống", 200)
                            TableCell(row.createdAt ?: "trống", 120)
                            TableCell(
                                text = row.status ?: "trống",
                                width = 120,
                                color = when (row.status) {
                                    "Xác Nhận", "Done" -> Color(0xFF27AE60)
                                    "Hủy", "Canceled" -> Color.Red
                                    else -> Color.Gray
                                }
                            )
                        }
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
