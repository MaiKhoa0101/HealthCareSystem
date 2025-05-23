package com.hellodoc.healthcaresystem.user.home.booking

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingCalendarScreen(
    context: Context,
    navHostController: NavHostController
) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val doctorViewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })

    // Lấy thông tin chỉnh sửa từ màn hình trước đó
    val isEditing = navHostController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isEditing") ?: false
    val appointmentId = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("appointmentId") ?: ""

//    val availableTimes = listOf(
//        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
//        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
//    )

    var availableTimes by remember { mutableStateOf<List<String>>(emptyList()) }


    LaunchedEffect(doctorId) {
        doctorId.let { doctorViewModel.fetchDoctorById(it) }
    }
    val doctor by doctorViewModel.doctor.collectAsState()
    val workHours = doctor?.workHour

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf("") }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val daysOfWeek = listOf("TH2", "TH3", "TH4", "TH5", "TH6", "TH7", "CN")

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(title = "Chọn lịch hẹn khám", onClick = { navHostController.popBackStack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Chọn ngày", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            item {
                // Calendar Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Month-Year Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            }
                            Text(
                                text = "Tháng ${currentMonth.monthValue} / ${currentMonth.year}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Days of week header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 4.dp),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Dates
                        val firstDay = currentMonth.atDay(1)
                        val daysInMonth = currentMonth.lengthOfMonth()
                        val firstDayOfWeek = (firstDay.dayOfWeek.value%8)
                        val dates = List(firstDayOfWeek-1) { null } + (1..daysInMonth).map { currentMonth.atDay(it) }

                        val dateRows = dates.chunked(7)

                        dateRows.forEachIndexed { index, week ->
                            val isLastRow = index == dateRows.lastIndex
                            val nonNullDays = week.count { it != null }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (!isLastRow || nonNullDays == 7) Arrangement.SpaceBetween else Arrangement.Start
                            ) {
                                week.forEachIndexed { index, day ->
                                    val isPast = day != null && day.isBefore(LocalDate.now())
                                    val isSelected = day == selectedDate

                                    if (isSelected) {
                                        val selectedDayOfWeek = (index + 2) // vì DayOfWeek.MONDAY = 1

                                        val times = workHours
                                            ?.filter { wh -> wh.dayOfWeek == selectedDayOfWeek }
                                            ?.map { wh -> "%02d:%02d".format(wh.hour, wh.minute) }

                                        if (times != null) {
                                            availableTimes = times
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .weight(1f, fill = day != null)
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isSelected -> Color(0xFF00BCD4)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable(enabled = day != null && !isPast) {
                                                day?.let {
                                                    selectedDate = it
//                                                    val selectedDayOfWeek = (index + 2) // vì DayOfWeek.MONDAY = 1
//
//                                                    val times = workHours
//                                                        ?.filter { wh -> wh.dayOfWeek == selectedDayOfWeek }
//                                                        ?.map { wh -> "%02d:%02d".format(wh.hour, wh.minute) }
//
//                                                    if (times != null) {
//                                                        availableTimes = times
//                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day?.dayOfMonth?.toString() ?: "",
                                            color = when {
                                                isPast -> Color.Gray
                                                isSelected -> Color.White
                                                else -> Color.Black
                                            }
                                        )
                                    }
                                }

                                if (isLastRow && week.size < 7) {
                                    repeat(7 - week.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text("Chọn giờ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            if (availableTimes.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Không có giờ phù hợp cho ngày này", fontSize = 13.sp)
                    }
                }
            } else {
                items(availableTimes.chunked(3)) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { time ->
                            val isSelected = selectedTime == time
                            Button(
                                onClick = { selectedTime = time },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF00BCD4) else Color.White,
                                    contentColor = if (isSelected) Color.White else Color.Black
                                ),
                                border = BorderStroke(1.dp, Color.Gray),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(time, fontSize = 14.sp)
                            }
                        }

                        repeat(3 - row.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        navHostController.previousBackStackEntry?.savedStateHandle?.apply {
                            set(
                                "selected_date",
                                selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            )
                            set("selected_time", selectedTime)
                        }
                        navHostController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00BCD4),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Xác nhận",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = remember(selectedDate) {
        val yearMonth = YearMonth.of(selectedDate.year, selectedDate.month)
        (1..yearMonth.lengthOfMonth()).toList()
    }

    val startDayOfWeek = remember(selectedDate) {
        LocalDate.of(selectedDate.year, selectedDate.month, 1).dayOfWeek.value
    }

    LazyVerticalGrid(columns = GridCells.Fixed(7)) {
        items(startDayOfWeek - 1) {
            Box(modifier = Modifier.height(40.dp))
        }
        items(daysInMonth) { day ->
            val date = LocalDate.of(selectedDate.year, selectedDate.month, day)
            val isSelected = date == selectedDate
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color(0xFF00BCD4) else Color.Transparent)
                    .clickable { onDateSelected(date) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = day.toString(), color = if (isSelected) Color.White else Color.Black)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, name = "Booking Calendar Preview")
@Composable
fun BookingCalendarScreenPreview() {
    val context = LocalContext.current
    val fakeNavController = rememberNavController()
    BookingCalendarScreen(
        context = context,
        navHostController = fakeNavController,
//        onDateTimeSelected = { date, time ->
//            // Cho preview thì mình không cần xử lý gì ở đây
//        }
    )
}
