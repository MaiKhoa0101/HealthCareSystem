package com.hellodoc.healthcaresystem.view.user.home.booking

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AvailableSlot
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingCalendarScreen(
    context: Context,
    navHostController: NavHostController
) {
    val doctorViewModel: DoctorViewModel = hiltViewModel()

    // Lấy thông tin chỉnh sửa từ màn hình trước đó
    val doctorId = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("doctorId") ?: ""
    val isEditing = navHostController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("isEditing") ?: false
    val appointmentId = navHostController.previousBackStackEntry?.savedStateHandle?.get<String>("appointmentId") ?: ""

    var availableTimes by remember { mutableStateOf<List<String>>(emptyList()) }

    // Thêm state để lưu trữ available slots data
    var availableSlots: List<AvailableSlot> by remember { mutableStateOf<List<AvailableSlot>>(emptyList()) }
    var availableDates by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }

    LaunchedEffect(doctorId) {
        doctorId.let {
            doctorViewModel.fetchDoctorById(it)
            // Gọi API để lấy available slots
            doctorViewModel.fetchAvailableSlots(it)
        }
    }

    val doctor by doctorViewModel.doctor.collectAsState()
    val availableSlotsData by doctorViewModel.availableWorkingHours.collectAsState()

    // Cập nhật available dates từ API response
    LaunchedEffect(availableSlotsData) {
        availableSlotsData?.let { response ->
            val slots = response.availableSlots
            availableSlots = slots
            availableDates = slots.mapNotNull { slot ->
                try {
                    LocalDate.parse(slot.date) // date dạng "2025-06-28"
                } catch (e: Exception) {
                    null
                }
            }.toSet()
        }
    }

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
                .background(MaterialTheme.colorScheme.background)
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
                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.secondaryContainer),
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
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
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
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onBackground
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
                                week.forEachIndexed { dayIndex, day ->
                                    val isPast = day != null && day.isBefore(LocalDate.now())
                                    val isSelected = day == selectedDate
                                    // Kiểm tra xem ngày có available slots hay không
                                    val isAvailable = day != null && availableDates.contains(day)
                                    val isClickable = day != null && !isPast && isAvailable

                                    if (isSelected && isAvailable) {
                                        // Tìm available times cho ngày được chọn
                                        val selectedSlot = availableSlots.find { slot ->
                                            LocalDate.parse(slot.date) == selectedDate
                                        }
                                        availableTimes = selectedSlot?.slots?.map { slot ->
                                            slot.displayTime
                                        } ?: emptyList()
                                    }

                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .weight(1f, fill = day != null)
                                            .padding(4.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isSelected -> MaterialTheme.colorScheme.tertiaryContainer
                                                    isAvailable && !isPast -> MaterialTheme.colorScheme.tertiary
                                                    !isAvailable && day != null && !isPast -> MaterialTheme.colorScheme.secondaryContainer
                                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                                }
                                            )
                                            .clickable(enabled = isClickable) {
                                                day?.let {
                                                    selectedDate = it
                                                    selectedTime = ""
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day?.dayOfMonth?.toString() ?: "",
                                            color = when {
                                                isPast -> MaterialTheme.colorScheme.onBackground
                                                isSelected -> MaterialTheme.colorScheme.onBackground
                                                isAvailable && !isPast -> MaterialTheme.colorScheme.onBackground
                                                else -> MaterialTheme.colorScheme.onBackground
                                            },
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
                        Text(
                            text = if (availableDates.contains(selectedDate))
                                "Không có giờ phù hợp cho ngày này"
                            else
                                "Vui lòng chọn ngày có lịch khám",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.error
                        )
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
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
                                    contentColor = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiaryContainer),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text(time, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
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
                    enabled = selectedTime.isNotEmpty() && availableDates.contains(selectedDate),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.background,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(
                        text = "Xác nhận",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
