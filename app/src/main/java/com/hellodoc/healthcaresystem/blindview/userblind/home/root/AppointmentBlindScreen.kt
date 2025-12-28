package com.hellodoc.healthcaresystem.blindview.userblind.home.root

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentBlindScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val appointmentViewModel: AppointmentViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    
    val appointments by appointmentViewModel.appointmentsUser.collectAsState()
    
    // Status cycle: 0: Pending (Đã đặt), 1: Done (Hoàn thành), 2: Cancelled (Đã hủy)
    var selectedStatusIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedAppointmentIndex by rememberSaveable { mutableIntStateOf(0) }
    
    val statusList = listOf("pending", "done", "cancelled")
    val statusLabels = listOf("đã đặt", "đã hoàn thành", "đã hủy")
    
    val filteredAppointments = remember(appointments, selectedStatusIndex) {
        appointments.filter { it.status == statusList[selectedStatusIndex] }
    }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var hasSwipedInCurrentGesture by remember { mutableStateOf(false) }
    val dragThreshold = 100f

    LaunchedEffect(Unit) {
        val userId = userViewModel.getUserAttribute("userId", context)
        if (userId != null && userId != "unknown") {
            appointmentViewModel.getAppointmentUser(userId)
        }
        
        FocusTTS.speakAndWait("Đây là trang Xem lịch khám, đang hiển thị các lịch khám đã đặt, chạm vào màn hình để tôi đọc thông tin lịch khám đang hiển thị, vuốt lên hoặc xuống để chuyển lịch khám, chạm hai lần để xem lịch khám đã hoàn thành")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(filteredAppointments.size, selectedStatusIndex, selectedAppointmentIndex) {
                detectTapGestures(
                    onDoubleTap = {
                        SoundManager.playTap()
                        vibrate(context)
                        
                        // Cycle status: 0 -> 1 -> 2 -> 0
                        selectedStatusIndex = (selectedStatusIndex + 1) % 3
                        selectedAppointmentIndex = 0
                        
                        val prompt = when(selectedStatusIndex) {
                            1 -> "Đang hiển thị các lịch khám đã hoàn thành, chạm vào màn hình để tôi đọc thông tin lịch khám đang hiển thị, chạm hai lần để xem lịch khám đã hủy"
                            2 -> "Đang hiển thị các lịch khám đã hủy, chạm vào màn hình để tôi đọc thông tin lịch khám đang hiển thị, chạm hai lần để xem lịch khám đã đặt"
                            else -> "Đang hiển thị các lịch khám đã đặt, chạm vào màn hình để tôi đọc thông tin lịch khám đang hiển thị, chạm hai lần để xem lịch khám đã hoàn thành"
                        }
                        
                        coroutineScope.launch {
                            FocusTTS.speak(prompt)
                        }
                    },
                    onTap = {
                        SoundManager.playTap()
                        vibrate(context)
                        
                        coroutineScope.launch {
                            if (filteredAppointments.isEmpty()) {
                                FocusTTS.speak("Danh sách này đang trống.")
                            } else {
                                val appt = filteredAppointments.getOrNull(selectedAppointmentIndex)
                                    appt?.let {
                                        println("appointment: $it")
                                        val zonedDateTime = try {
                                            ZonedDateTime.parse(it.date).withZoneSameInstant(ZoneId.systemDefault())
                                        } catch (e: Exception) {
                                            try {
                                                // Try parsing as simple LocalDate if ZonedDateTime fails
                                                java.time.LocalDate.parse(it.date).atStartOfDay(ZoneId.systemDefault())
                                            } catch (e2: Exception) {
                                                null
                                            }
                                        }

                                        val (day, month, year) = if (zonedDateTime != null) {
                                            Triple(zonedDateTime.dayOfMonth.toString(), zonedDateTime.monthValue.toString(), zonedDateTime.year.toString())
                                        } else {
                                            Triple("?", "?", "?")
                                        }

                                        val timeStr = BlindNavigationHelpers.formatTimeStringForTTS(it.time)
                                        val specialtyName = it.doctor.specialty ?: "Chưa xác định"
                                        val doctorName = it.doctor.name ?: "Bác sĩ"
                                        
                                        val countdownStr = if (zonedDateTime != null) {
                                            try {
                                                val parts = it.time.split(":")
                                                val h = parts[0].trim().toInt()
                                                val m = if (parts.size >= 2) parts[1].trim().toInt() else 0
                                                val targetFull = zonedDateTime.withHour(h).withMinute(m)
                                                val now = ZonedDateTime.now(ZoneId.systemDefault())
                                                BlindNavigationHelpers.getRemainingTimeText(targetFull, now)
                                            } catch (e: Exception) {
                                                ""
                                            }
                                        } else ""

                                        val info = "Đây là lịch khám với bác sĩ $doctorName chuyên ngành $specialtyName vào $timeStr ngày $day tháng $month năm $year. " +
                                                if (countdownStr.isNotEmpty()) "$countdownStr nữa." else ""
                                        FocusTTS.speak(info)
                                    }
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        hasSwipedInCurrentGesture = false
                    },
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                        hasSwipedInCurrentGesture = false
                    }
                ) { change, dragAmount ->
                    if (hasSwipedInCurrentGesture) return@detectDragGestures
                    
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                    
                    // Vertical Swipe (Up/Down) to change selected appointment
                    if (kotlin.math.abs(offsetY) > dragThreshold && kotlin.math.abs(offsetY) > kotlin.math.abs(offsetX)) {
                        if (filteredAppointments.isNotEmpty()) {
                            if (offsetY < 0) { // Swipe Up -> Next
                                if (selectedAppointmentIndex < filteredAppointments.size - 1) {
                                    selectedAppointmentIndex++
                                    SoundManager.playSwipe()
                                    vibrate(context)
                                    coroutineScope.launch {
                                        FocusTTS.speak("Đã chuyển lịch khám, chạm vào màn hình để tôi đọc thông tin lịch khám đang hiển thị")
                                    }
                                } else {
                                    coroutineScope.launch {
                                        FocusTTS.speak("Đã tới lịch khám cuối cùng.")
                                    }
                                }
                            } else { // Swipe Down -> Previous
                                if (selectedAppointmentIndex > 0) {
                                    selectedAppointmentIndex--
                                    SoundManager.playSwipe()
                                    vibrate(context)
                                    coroutineScope.launch {
                                        FocusTTS.speak("Đã chuyển lịch khám, chạm vào màn hình để tôi đọc thông tin lịch khám đang hiển thị")
                                    }
                                } else {
                                    coroutineScope.launch {
                                        FocusTTS.speak("Đang ở lịch khám đầu tiên.")
                                    }
                                }
                            }
                        }
                        hasSwipedInCurrentGesture = true
                        offsetY = 0f
                    }
                    
                    // Horizontal Swipe Right to go back to Booking
                    if (offsetX > dragThreshold && kotlin.math.abs(offsetX) > kotlin.math.abs(offsetY)) {
                        SoundManager.playSwipe()
                        vibrate(context)
                        navHostController.popBackStack()
                        hasSwipedInCurrentGesture = true
                        offsetX = 0f
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "XEM LỊCH KHÁM",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Trạng thái: ${statusLabels[selectedStatusIndex].uppercase()}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (filteredAppointments.isEmpty()) {
                Text(
                    text = "Không có lịch khám nào",
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            } else {
                val appt = filteredAppointments.getOrNull(selectedAppointmentIndex)
                Text(
                    text = "Lịch ${selectedAppointmentIndex + 1} / ${filteredAppointments.size}",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = appt?.doctor?.name ?: "Bác sĩ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Vuốt lên/xuống: Chuyển lịch\nChạm 1 lần: Nghe thông tin\nChạm 2 lần: Đổi trạng thái\nVuốt phải: Quay lại",
                fontSize = 16.sp,
                color = Color.Gray,
                lineHeight = 24.sp
            )
        }
    }
}
