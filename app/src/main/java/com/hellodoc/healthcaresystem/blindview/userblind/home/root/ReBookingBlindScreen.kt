package com.hellodoc.healthcaresystem.blindview.userblind.home.root

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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.view.user.supportfunction.speakQueue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ReBookingStep {
    SELECT_DATE,
    SELECT_TIME,
    CONFIRMATION,
    COMPLETE
}

@Composable
fun ReBookingBlindScreen(
    navHostController: NavHostController,
    doctorId: String,
    specialtyName: String,
    doctorViewModel: DoctorViewModel = hiltViewModel(),
    appointmentViewModel: AppointmentViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var currentStep by remember { mutableStateOf(ReBookingStep.SELECT_DATE) }
    var hasGreeted by remember { mutableStateOf(false) }
    var instructionsCompletedByStep by remember { mutableStateOf<ReBookingStep?>(null) }
    
    val doctorDetail by doctorViewModel.doctor.collectAsState()
    val workingHoursResponse by doctorViewModel.availableWorkingHours.collectAsState()
    val appointmentSuccess by appointmentViewModel.appointmentSuccess.collectAsState()
    val appointmentError by appointmentViewModel.appointmentError.collectAsState()
    
    var selectedDateIndex by remember { mutableIntStateOf(0) }
    var selectedTimeIndex by remember { mutableIntStateOf(0) }
    
    var hasSwipedInCurrentGesture by remember { mutableStateOf(false) }
    
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val dragThreshold = 100f

    // Derived filtering logic
    val doctorWorkingDays = remember(doctorDetail) {
        doctorDetail?.workingHours?.map { it.dayOfWeek }?.distinct() ?: emptyList()
    }

    val filteredAvailableSlots = remember(workingHoursResponse, doctorWorkingDays) {
        workingHoursResponse?.availableSlots?.filter { slot ->
            val mappedDay = if (slot.dayOfWeek == 0) 8 else slot.dayOfWeek + 1
            val isWorkingDay = doctorWorkingDays.contains(mappedDay)
            
            val slotDate = try { java.time.LocalDate.parse(slot.date) } catch (e: Exception) { null }
            val isFutureOrToday = slotDate?.let { it.isAfter(java.time.LocalDate.now()) || it.isEqual(java.time.LocalDate.now()) } ?: true
            
            if (isWorkingDay && isFutureOrToday) {
                if (slotDate?.isEqual(java.time.LocalDate.now()) == true) {
                    slot.slots.any { timeSlot ->
                        try {
                            val slotTime = java.time.LocalTime.of(timeSlot.hour, timeSlot.minute)
                            slotTime.isAfter(java.time.LocalTime.now())
                        } catch (e: Exception) { true }
                    }
                } else {
                    true
                }
            } else {
                false
            }
        } ?: emptyList()
    }

    val filteredTimeSlots = remember(selectedDateIndex, filteredAvailableSlots) {
        filteredAvailableSlots.getOrNull(selectedDateIndex)?.let { slot ->
            val slotDate = try { java.time.LocalDate.parse(slot.date) } catch (e: Exception) { null }
            slot.slots.filter { timeSlot ->
                if (slotDate?.isEqual(java.time.LocalDate.now()) == true) {
                    try {
                        val slotTime = java.time.LocalTime.of(timeSlot.hour, timeSlot.minute)
                        slotTime.isAfter(java.time.LocalTime.now())
                    } catch (e: Exception) { true }
                } else {
                    true
                }
            }
        } ?: emptyList()
    }

    // Initial Greeting & Data Loading
    LaunchedEffect(Unit) {
        delay(700) // Slightly longer delay for stability
        doctorViewModel.fetchDoctorById(doctorId)
        doctorViewModel.fetchAvailableSlots(doctorId)
        
        val greeting = "Đang tiến hành đặt lại lịch khám. " +
                "Tiếp theo hãy tiến hành chọn ngày khám. " +
                "Hãy nhấn giữ vào màn hình để chọn ngày đang hiển thị, vuốt lên hoặc xuống để thay đổi ngày."
        
        FocusTTS.speakAndWait(greeting)
        hasGreeted = true
    }

    // Updated: Handle speech for working hours only AFTER instructions are done
    LaunchedEffect(currentStep, workingHoursResponse, filteredAvailableSlots, hasGreeted, instructionsCompletedByStep) {
        if (!hasGreeted) return@LaunchedEffect
        
        if (currentStep == ReBookingStep.SELECT_DATE && instructionsCompletedByStep == ReBookingStep.SELECT_DATE) {
            doctorDetail?.let { detail ->
                if (detail.isClinicPaused == true) {
                    FocusTTS.speak("Bác sĩ này đã tạm ngưng, hiện không thể đặt lịch, hãy chạm hai lần để quay lại.")
                } else if (detail.workingHours.isNullOrEmpty()) {
                    FocusTTS.speak("Bác sĩ này chưa có thời gian làm việc hợp lệ, hãy chạm hai lần để quay lại.")
                } else {
                    if (filteredAvailableSlots.isNotEmpty()) {
                        val slot = filteredAvailableSlots[selectedDateIndex]
                        FocusTTS.speak("Đang hiển thị thứ ${slot.dayOfWeek} ngày ${slot.date}, nhấn giữ vào màn hình để chọn ngày đang hiển thị")
                    }
                }
            }
        }
    }

    // Observe appointment status
    LaunchedEffect(appointmentSuccess, appointmentError) {
        if (appointmentSuccess) {
            FocusTTS.speakAndWait("Đặt lại lịch khám thành công!")
            navHostController.popBackStack()
            appointmentViewModel.resetAppointmentSuccess()
        } else if (appointmentError != null) {
            FocusTTS.speakAndWait("Đặt lại lịch khám thất bại: $appointmentError")
            appointmentViewModel.resetAppointmentError()
        }
    }

    // Updated: Handle speech for step transitions (supports forward and backward)
    LaunchedEffect(currentStep) {
        instructionsCompletedByStep = null
        when (currentStep) {
            ReBookingStep.SELECT_DATE -> {
                if (hasGreeted) { // Only speak if initial greeting is done
                    FocusTTS.speakAndWait("Hãy nhấn giữ vào màn hình để chọn ngày đang hiển thị, vuốt lên hoặc xuống để thay đổi ngày, chạm hai lần để quay lại.")
                    filteredAvailableSlots.getOrNull(selectedDateIndex)?.let { slot ->
                        FocusTTS.speak("Đang hiển thị thứ ${slot.dayOfWeek} ngày ${slot.date}")
                    }
                }
            }
            ReBookingStep.SELECT_TIME -> {
                FocusTTS.speakAndWait("Hãy nhấn giữ vào màn hình để chọn giờ đang hiển thị, vuốt lên hoặc xuống để chuyển giờ, chạm hai lần để quay lại.")
                filteredTimeSlots.getOrNull(selectedTimeIndex)?.let { timeSlot ->
                    val timeText = BlindNavigationHelpers.formatTimeForTTS(timeSlot.hour, timeSlot.minute)
                    FocusTTS.speak("Đang hiển thị $timeText")
                }
            }
            ReBookingStep.CONFIRMATION -> {
                 FocusTTS.speakAndWait("Đã hoàn thành lựa chọn, chạm vào màn hình để tôi đọc lại tóm tắt, nhấn giữ vào màn hình để xác nhận đặt lịch, chạm hai lần để quay lại.")
            }
            else -> {}
        }
        instructionsCompletedByStep = currentStep
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(currentStep, filteredAvailableSlots, filteredTimeSlots, selectedDateIndex, selectedTimeIndex) {
                detectTapGestures(
                    onDoubleTap = {
                        SoundManager.playTap()
                        vibrate(context)
                        coroutineScope.launch {
                            when (currentStep) {
                                ReBookingStep.SELECT_DATE -> {
                                    navHostController.popBackStack()
                                }
                                ReBookingStep.SELECT_TIME -> {
                                    currentStep = ReBookingStep.SELECT_DATE
                                }
                                ReBookingStep.CONFIRMATION -> {
                                    currentStep = ReBookingStep.SELECT_TIME
                                }
                                ReBookingStep.COMPLETE -> {
                                    navHostController.popBackStack()
                                }
                            }
                        }
                    },
                    onTap = {
                        SoundManager.playTap()
                        vibrate(context)
                        coroutineScope.launch {
                            when (currentStep) {
                                ReBookingStep.SELECT_DATE -> {
                                    filteredAvailableSlots.getOrNull(selectedDateIndex)?.let { slot ->
                                        FocusTTS.speak("Đang hiển thị thứ ${slot.dayOfWeek} ngày ${slot.date}, nhấn giữ vào màn hình để chọn ngày đang hiển thị")
                                    }
                                }
                                ReBookingStep.SELECT_TIME -> {
                                    filteredTimeSlots.getOrNull(selectedTimeIndex)?.let { timeSlot ->
                                        val timeText = BlindNavigationHelpers.formatTimeForTTS(timeSlot.hour, timeSlot.minute)
                                        FocusTTS.speak("Đang hiển thị $timeText, nhấn giữ vào màn hình để chọn giờ đang hiển thị")
                                    }
                                }
                                ReBookingStep.CONFIRMATION -> {
                                    val selectedDoctorName = doctorDetail?.name ?: "Bác sĩ"
                                    val selectedSlot = filteredAvailableSlots.getOrNull(selectedDateIndex)
                                    val selectedTime = filteredTimeSlots.getOrNull(selectedTimeIndex)
                                    val timeText = if (selectedTime != null) {
                                        BlindNavigationHelpers.formatTimeForTTS(selectedTime.hour, selectedTime.minute)
                                    } else ""
                                    val summary = "Tóm tắt đặt lại lịch khám. Chuyên ngành $specialtyName. Bác sĩ $selectedDoctorName. Thứ ${selectedSlot?.dayOfWeek} ngày ${selectedSlot?.date}. Giờ khám là $timeText. Nhấn giữ vào màn hình để xác nhận."
                                    FocusTTS.speak(summary)
                                }
                                else -> {}
                            }
                        }
                    },
                    onLongPress = {
                        SoundManager.playTap()
                        vibrate(context)
                        coroutineScope.launch {
                            when (currentStep) {
                                ReBookingStep.SELECT_DATE -> {
                                    if (filteredAvailableSlots.isNotEmpty() && selectedDateIndex < filteredAvailableSlots.size) {
                                        val selectedSlot = filteredAvailableSlots[selectedDateIndex]
                                        if (filteredTimeSlots.isEmpty()) {
                                            FocusTTS.speak("Ngày này không còn giờ khám khả dụng hôm nay, hãy chọn ngày khác.")
                                        } else {
                                            FocusTTS.speakAndWait("Bạn đã chọn thứ ${selectedSlot.dayOfWeek} ngày ${selectedSlot.date}")
                                            currentStep = ReBookingStep.SELECT_TIME
                                            selectedTimeIndex = 0
                                        }
                                    }
                                }
                                ReBookingStep.SELECT_TIME -> {
                                    filteredTimeSlots.getOrNull(selectedTimeIndex)?.let { timeSlot ->
                                        val timeText = BlindNavigationHelpers.formatTimeForTTS(timeSlot.hour, timeSlot.minute)
                                        FocusTTS.speakAndWait("Bạn đã chọn $timeText")
                                        currentStep = ReBookingStep.CONFIRMATION
                                    }
                                }
                                ReBookingStep.CONFIRMATION -> {
                                    val token = userViewModel.getUserAttribute("access_token", context)
                                    val patientID = userViewModel.getUserAttribute("userId", context)
                                    val patientModel = userViewModel.getUserAttribute("role", context)
                                    
                                    val selectedSlot = filteredAvailableSlots.getOrNull(selectedDateIndex)
                                    val selectedTime = filteredTimeSlots.getOrNull(selectedTimeIndex)
                                    
                                    if (selectedSlot != null && selectedTime != null) {
                                        appointmentViewModel.createAppointment(
                                            token,
                                            CreateAppointmentRequest(
                                                doctorID = doctorId,
                                                patientID = patientID,
                                                patientModel = patientModel,
                                                date = selectedSlot.date,
                                                time = selectedTime.time,
                                                examinationMethod = "at_clinic",
                                                notes = "Đặt lại lịch qua trợ lý giọng nói",
                                                reason = "Đặt lại lịch qua trợ lý giọng nói",
                                                totalCost = "0",
                                                location = doctorDetail?.address ?: ""
                                            )
                                        )
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                )
            }
             .pointerInput(currentStep, filteredAvailableSlots, filteredTimeSlots, selectedDateIndex, selectedTimeIndex) {
                detectDragGestures(
                    onDragStart = { hasSwipedInCurrentGesture = false },
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                        hasSwipedInCurrentGesture = false
                    }
                ) { change, dragAmount ->
                    if (hasSwipedInCurrentGesture) return@detectDragGestures

                    val dragX = dragAmount.x
                    val dragY = dragAmount.y

                    if (kotlin.math.abs(dragX) > kotlin.math.abs(dragY)) {
                        offsetX += dragX
                        if (kotlin.math.abs(offsetX) > dragThreshold) {
                            if (offsetX > 0) { // Swipe Right -> Home/Back
                                SoundManager.playSwipe()
                                vibrate(context)
                                navHostController.popBackStack()
                            }
                            hasSwipedInCurrentGesture = true
                            offsetX = 0f
                        }
                    } else {
                        offsetY += dragY
                        if (kotlin.math.abs(offsetY) > dragThreshold) {
                            if (offsetY < 0) { // Swipe Up -> Next
                                when (currentStep) {
                                    ReBookingStep.SELECT_DATE -> {
                                        if (filteredAvailableSlots.isNotEmpty()) {
                                            selectedDateIndex = (selectedDateIndex + 1) % filteredAvailableSlots.size
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            val slot = filteredAvailableSlots[selectedDateIndex]
                                            FocusTTS.speak("Đang hiển thị thứ ${slot.dayOfWeek} ngày ${slot.date}, nhấn giữ vào màn hình để chọn ngày đang hiển thị")
                                        }
                                    }
                                    ReBookingStep.SELECT_TIME -> {
                                        if (filteredTimeSlots.isNotEmpty()) {
                                            selectedTimeIndex = (selectedTimeIndex + 1) % filteredTimeSlots.size
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            val timeSlot = filteredTimeSlots[selectedTimeIndex]
                                            val timeText = BlindNavigationHelpers.formatTimeForTTS(timeSlot.hour, timeSlot.minute)
                                            FocusTTS.speak("Đang hiển thị $timeText, nhấn giữ vào màn hình để chọn giờ đang hiển thị")
                                        }
                                    }
                                    else -> {}
                                }
                            } else { // Swipe Down -> Previous
                                when (currentStep) {
                                    ReBookingStep.SELECT_DATE -> {
                                        if (filteredAvailableSlots.isNotEmpty()) {
                                            selectedDateIndex = if (selectedDateIndex > 0) selectedDateIndex - 1 else filteredAvailableSlots.size - 1
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            val slot = filteredAvailableSlots[selectedDateIndex]
                                            FocusTTS.speak("Đang hiển thị thứ ${slot.dayOfWeek} ngày ${slot.date}, nhấn giữ vào màn hình để chọn ngày đang hiển thị")
                                        }
                                    }
                                    ReBookingStep.SELECT_TIME -> {
                                        if (filteredTimeSlots.isNotEmpty()) {
                                            selectedTimeIndex = if (selectedTimeIndex > 0) selectedTimeIndex - 1 else filteredTimeSlots.size - 1
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            val timeSlot = filteredTimeSlots[selectedTimeIndex]
                                            val timeText = BlindNavigationHelpers.formatTimeForTTS(timeSlot.hour, timeSlot.minute)
                                            FocusTTS.speak("Đang hiển thị $timeText, nhấn giữ vào màn hình để chọn giờ đang hiển thị")
                                        }
                                    }
                                    else -> {}
                                }
                            }
                            hasSwipedInCurrentGesture = true
                            offsetY = 0f
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = when (currentStep) {
                    ReBookingStep.SELECT_DATE -> "Đặt lại - Chọn ngày"
                    ReBookingStep.SELECT_TIME -> "Đặt lại - Chọn giờ"
                    ReBookingStep.CONFIRMATION -> "Xác nhận đặt lại"
                    ReBookingStep.COMPLETE -> "Hoàn tất"
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = when (currentStep) {
                    ReBookingStep.SELECT_DATE -> filteredAvailableSlots.getOrNull(selectedDateIndex)?.let { "Thứ ${it.dayOfWeek} ${it.date}" } ?: "Đang tải ngày..."
                    ReBookingStep.SELECT_TIME -> filteredTimeSlots.getOrNull(selectedTimeIndex)?.displayTime ?: "Đang tải giờ..."
                    ReBookingStep.CONFIRMATION -> "Nhấn giữ để nghe lại\nChạm để xác nhận"
                    ReBookingStep.COMPLETE -> "Đặt lại thành công"
                },
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Bác sĩ: ${doctorDetail?.name ?: "Đang tải..."}\nChuyên ngành: $specialtyName",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
