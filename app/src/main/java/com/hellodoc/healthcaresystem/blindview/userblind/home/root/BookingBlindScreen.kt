package com.hellodoc.healthcaresystem.blindview.userblind.home.root

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

enum class BookingStep {
    INITIAL,
    SELECT_SPECIALTY,
    SELECT_DOCTOR,
    SELECT_DATE,
    SELECT_TIME,
    CONFIRMATION,
    COMPLETE
}

@Composable
fun BookingBlindScreen(
    navHostController: NavHostController,
    specialtyViewModel: SpecialtyViewModel = hiltViewModel(),
    doctorViewModel: DoctorViewModel = hiltViewModel(),
    appointmentViewModel: AppointmentViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var currentStep by remember { mutableStateOf(BookingStep.INITIAL) }
    
    val specialties by specialtyViewModel.specialties.collectAsState()
    val doctors by specialtyViewModel.doctors.collectAsState()
    
    val doctorDetail by doctorViewModel.doctor.collectAsState()
    val workingHoursResponse by doctorViewModel.availableWorkingHours.collectAsState()
    val appointmentSuccess by appointmentViewModel.appointmentSuccess.collectAsState()
    val appointmentError by appointmentViewModel.appointmentError.collectAsState()
    
    var specialtyIndex by remember { mutableIntStateOf(0) }
    var doctorIndex by remember { mutableIntStateOf(0) }
    var selectedDateIndex by remember { mutableIntStateOf(0) }
    var selectedTimeIndex by remember { mutableIntStateOf(0) }
    
    var hasSwipedInCurrentGesture by remember { mutableStateOf(false) }
    
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val horizontalThreshold = 150f
    val verticalThreshold = 100f
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

    // Initial Greeting
    LaunchedEffect(Unit) {
        delay(1000)
        FocusTTS.speakAndWait("Đây là trang Đặt lịch khám, bấm vào màn hình để tiếp tục, bấm hai lần vào màn hình để quay lại")
    }

    // Handle data fetching
    LaunchedEffect(currentStep) {
        if (currentStep == BookingStep.SELECT_SPECIALTY) {
            specialtyViewModel.fetchSpecialties(forceRefresh = true)
        }
    }

    // Observe doctor details for clinic status
    LaunchedEffect(doctorDetail) {
        doctorDetail?.let { detail ->
            if (currentStep == BookingStep.SELECT_DOCTOR) {
                if (detail.isClinicPaused == true) {
                    FocusTTS.speak("Bác sĩ này đã tạm ngưng hãy chọn bác sĩ khác, hãy chạm 2 lần để trở lại")
                } else if (detail.workingHours.isNullOrEmpty()) {
                    FocusTTS.speak("Bác sĩ này chưa có thời gian hợp lệ, hãy lướt lên hoặc xuống để chuyển bác sĩ")
                } else {
                    currentStep = BookingStep.SELECT_DATE
                    selectedDateIndex = 0
                    FocusTTS.speakAndWait("Tiếp theo hãy tiến hành chọn ngày, chạm vào màn hình để chọn ngày đang hiển thị, vuốt lên hoặc xuống để chuyển ngày, chạm hai lần để quay lại.")
                    workingHoursResponse?.availableSlots?.let { slots ->
                        if (slots.isNotEmpty()) {
                            val slot = slots[0]
                            FocusTTS.speak("Đang hiển thị thứ ${slot.dayOfWeek} ngày ${slot.date}")
                        }
                    }
                }
            }
        }
    }

    // Observe appointment status
    LaunchedEffect(appointmentSuccess, appointmentError) {
        if (appointmentSuccess) {
            FocusTTS.speak("Đặt lịch thành công!")
            delay(1000)
            navHostController.popBackStack()
            appointmentViewModel.resetAppointmentSuccess()
        } else if (appointmentError != null) {
            FocusTTS.speak("Đặt lịch thất bại: $appointmentError")
            appointmentViewModel.resetAppointmentError()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(currentStep, specialties, doctors, specialtyIndex, doctorIndex) {
                detectTapGestures(
                    onDoubleTap = {
                        SoundManager.playTap()
                        vibrate(context)
                        coroutineScope.launch {
                            when (currentStep) {
                                BookingStep.INITIAL -> {
                                    navHostController.popBackStack()
                                }
                                BookingStep.SELECT_SPECIALTY -> {
                                    currentStep = BookingStep.INITIAL
                                    FocusTTS.speak("Quay lại trang Đặt lịch khám, bấm vào màn hình để bắt đầu")
                                }
                                BookingStep.SELECT_DOCTOR -> {
                                    currentStep = BookingStep.SELECT_SPECIALTY
                                    FocusTTS.speak("Quay lại chọn chuyên ngành, đang hiển thị chuyên ngành ${specialties[specialtyIndex].name}")
                                }
                                BookingStep.SELECT_DATE -> {
                                    currentStep = BookingStep.SELECT_DOCTOR
                                    FocusTTS.speak("Quay lại chọn bác sĩ, đang hiển thị bác sĩ ${doctors[doctorIndex].name}")
                                }
                                BookingStep.SELECT_TIME -> {
                                    currentStep = BookingStep.SELECT_DATE
                                    val slot = filteredAvailableSlots.getOrNull(selectedDateIndex)
                                    FocusTTS.speak("Quay lại chọn ngày, đang hiển thị thứ ${slot?.dayOfWeek} ngày ${slot?.date}")
                                }
                                BookingStep.CONFIRMATION -> {
                                    currentStep = BookingStep.SELECT_TIME
                                    val timeSlot = filteredTimeSlots.getOrNull(selectedTimeIndex)
                                    FocusTTS.speak("Quay lại chọn giờ, đang hiển thị ${timeSlot?.displayTime}")
                                }
                                BookingStep.COMPLETE -> {
                                    currentStep = BookingStep.SELECT_DOCTOR
                                    FocusTTS.speak("Quay lại chọn bác sĩ, đang hiển thị bác sĩ ${doctors[doctorIndex].name}")
                                }
                            }
                        }
                    },
                    onLongPress = {
                         if (currentStep == BookingStep.CONFIRMATION) {
                            val selectedDoctor = doctors[doctorIndex]
                            val selectedSlot = filteredAvailableSlots.getOrNull(selectedDateIndex)
                            val selectedTime = filteredTimeSlots.getOrNull(selectedTimeIndex)
                            val summary = "Tóm tắt thông tin đặt lịch của bạn. Chuyên ngành ${specialties[specialtyIndex].name}. Bác sĩ ${selectedDoctor.name}. Thứ ${selectedSlot?.dayOfWeek} ngày ${selectedSlot?.date}. Giờ khám là ${selectedTime?.displayTime}. Chạm vào màn hình một lần để xác nhận đặt lịch khám."
                            coroutineScope.launch {
                                FocusTTS.speak(summary)
                            }
                        }
                    },
                    onTap = {
                        SoundManager.playTap()
                        vibrate(context)
                        coroutineScope.launch {
                            when (currentStep) {
                                BookingStep.INITIAL -> {
                                    currentStep = BookingStep.SELECT_SPECIALTY
                                    FocusTTS.speakAndWait("Để tiến hành đặt lịch hãy chạm vào màn hình để chọn chuyên ngành đang hiển thị, vuốt lên hoặc xuống để chuyển chuyên ngành, lướt sang phải để quay lại trang chủ, lướt sang trái để xem lịch khám.")
                                    if (specialties.isNotEmpty()) {
                                        FocusTTS.speak("Đang hiển thị chuyên ngành ${specialties[specialtyIndex].name}")
                                    } else {
                                        FocusTTS.speak("Đang tải danh sách chuyên ngành, vui lòng đợi.")
                                    }
                                }
                                BookingStep.SELECT_SPECIALTY -> {
                                    if (specialties.isNotEmpty()) {
                                        val selectedSpecialty = specialties[specialtyIndex]
                                        val fetchedDoctors = specialtyViewModel.getSpecialtyDoctorAsync(selectedSpecialty.id)
                                        if (fetchedDoctors.isNullOrEmpty()) {
                                            FocusTTS.speak("Chuyên ngành này không có bác sĩ nào")
                                        } else {
                                            FocusTTS.speakAndWait("Bạn đã chọn chuyên ngành ${selectedSpecialty.name}, tiếp theo hãy chạm vào màn hình để chọn bác sĩ đang hiển thị, vuốt lên hoặc xuống để chuyển bác sĩ, chạm hai lần để quay lại.")
                                            FocusTTS.speak("Đang hiển thị bác sĩ ${fetchedDoctors[0].name}")
                                            currentStep = BookingStep.SELECT_DOCTOR
                                            doctorIndex = 0 // Reset doctor index
                                        }
                                    }
                                }
                                BookingStep.SELECT_DOCTOR -> {
                                    if (doctors.isNotEmpty()) {
                                        val selectedDoctor = doctors[doctorIndex]
                                        FocusTTS.speakAndWait("Bạn đã chọn bác sĩ ${selectedDoctor.name}")
                                        
                                        // Fetch doctor detail to check clinic status
                                        doctorViewModel.fetchDoctorById(selectedDoctor.id)
                                        doctorViewModel.fetchAvailableSlots(selectedDoctor.id)
                                        
                                        // Transition to date selection handled in a LaunchedEffect
                                        // But for immediate feedback, we wait for data
                                    }
                                }
                                 BookingStep.SELECT_DATE -> {
                                    if (filteredAvailableSlots.isNotEmpty() && selectedDateIndex < filteredAvailableSlots.size) {
                                        val selectedSlot = filteredAvailableSlots[selectedDateIndex]
                                        
                                        // We already filtered time slots in the derived state
                                        if (filteredTimeSlots.isEmpty()) {
                                            FocusTTS.speak("Ngày này không còn giờ khám khả dụng hôm nay, hãy chọn ngày khác.")
                                        } else {
                                            FocusTTS.speakAndWait("Bạn đã chọn thứ ${selectedSlot.dayOfWeek} ngày ${selectedSlot.date}, tiếp theo hãy chạm vào màn hình để chọn giờ đang hiển thị, vuốt lên hoặc xuống để chuyển giờ, chạm hai lần để quay lại.")
                                            currentStep = BookingStep.SELECT_TIME
                                            selectedTimeIndex = 0
                                            FocusTTS.speak("Đang hiển thị ${filteredTimeSlots[0].displayTime}")
                                        }
                                    }
                                }
                                BookingStep.SELECT_TIME -> {
                                    filteredTimeSlots.getOrNull(selectedTimeIndex)?.let { timeSlot ->
                                        FocusTTS.speakAndWait("Bạn đã chọn ${timeSlot.displayTime}")
                                        currentStep = BookingStep.CONFIRMATION
                                        FocusTTS.speakAndWait("Đã hoàn thành quá trình đặt lịch, nhấn giữ nếu muốn tôi đọc lại thông tin đặt lịch, chạm vào màn hình để xác nhận đặt lịch, chạm hai lần để quay lại.")
                                    }
                                }
                                BookingStep.CONFIRMATION -> {
                                    // Call createAppointment
                                    val token = userViewModel.getUserAttribute("access_token", context)
                                    val patientID = userViewModel.getUserAttribute("userId", context)
                                    val patientModel = userViewModel.getUserAttribute("role", context)
                                    
                                    val selectedDoctor = doctors[doctorIndex]
                                    val selectedSlot = filteredAvailableSlots.getOrNull(selectedDateIndex)
                                    val selectedTime = filteredTimeSlots.getOrNull(selectedTimeIndex)
                                    
                                    if (selectedSlot != null && selectedTime != null) {
                                        appointmentViewModel.createAppointment(
                                            token,
                                            CreateAppointmentRequest(
                                                doctorID = selectedDoctor.id,
                                                patientID = patientID,
                                                patientModel = patientModel,
                                                date = selectedSlot.date,
                                                time = selectedTime.time,
                                                examinationMethod = "at_clinic",
                                                notes = "Đặt lịch qua trợ lý giọng nói",
                                                reason = "Đặt lịch qua trợ lý giọng nói",
                                                totalCost = "0",
                                                location = selectedDoctor.address ?: ""
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
             .pointerInput(currentStep, specialties, doctors, specialtyIndex, doctorIndex, filteredAvailableSlots, filteredTimeSlots) {
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

                    val dragX = dragAmount.x
                    val dragY = dragAmount.y

                    // Horizontal Swipe (Screen Navigation)
                    if (kotlin.math.abs(dragX) > kotlin.math.abs(dragY)) {
                        offsetX += dragX
                        if (kotlin.math.abs(offsetX) > dragThreshold) {
                            if (offsetX < 0) { // Swipe Left -> Appointments
                                SoundManager.playSwipe()
                                vibrate(context)
                                navHostController.navigate("appointment_blind")
                            } else { // Swipe Right -> Home
                                SoundManager.playSwipe()
                                vibrate(context)
                                navHostController.popBackStack()
                            }
                            hasSwipedInCurrentGesture = true
                            offsetX = 0f
                        }
                    } else {
                        // Vertical Swipe (Item Navigation)
                        offsetY += dragY
                        if (kotlin.math.abs(offsetY) > dragThreshold) {
                            if (offsetY < 0) { // Swipe Up -> Next
                                when (currentStep) {
                                    BookingStep.SELECT_SPECIALTY -> {
                                        if (specialties.isNotEmpty()) {
                                            specialtyIndex = (specialtyIndex + 1) % specialties.size
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            FocusTTS.speak("Đang hiển thị chuyên ngành ${specialties[specialtyIndex].name}")
                                        }
                                    }
                                    BookingStep.SELECT_DOCTOR -> {
                                        if (doctors.isNotEmpty()) {
                                            doctorIndex = (doctorIndex + 1) % doctors.size
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            FocusTTS.speak("Đang hiển thị bác sĩ ${doctors[doctorIndex].name}")
                                        }
                                    }
                                    BookingStep.SELECT_DATE -> {
                                        if (filteredAvailableSlots.isNotEmpty()) {
                                            selectedDateIndex = (selectedDateIndex + 1) % filteredAvailableSlots.size
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            val slot = filteredAvailableSlots[selectedDateIndex]
                                            FocusTTS.speak("Đang hiển thị thứ ${slot.dayOfWeek} ngày ${slot.date}")
                                        }
                                    }
                                    BookingStep.SELECT_TIME -> {
                                        if (filteredTimeSlots.isNotEmpty()) {
                                            selectedTimeIndex = (selectedTimeIndex + 1) % filteredTimeSlots.size
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            FocusTTS.speak("Đang hiển thị ${filteredTimeSlots[selectedTimeIndex].displayTime}")
                                        }
                                    }
                                    else -> {}
                                }
                            } else { // Swipe Down -> Previous
                                when (currentStep) {
                                    BookingStep.SELECT_SPECIALTY -> {
                                        if (specialties.isNotEmpty()) {
                                            specialtyIndex = if (specialtyIndex > 0) specialtyIndex - 1 else specialties.size - 1
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            FocusTTS.speak("Đang hiển thị chuyên ngành ${specialties[specialtyIndex].name}")
                                        }
                                    }
                                    BookingStep.SELECT_DOCTOR -> {
                                        if (doctors.isNotEmpty()) {
                                            doctorIndex = if (doctorIndex > 0) doctorIndex - 1 else doctors.size - 1
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            FocusTTS.speak("Đang hiển thị bác sĩ ${doctors[doctorIndex].name}")
                                        }
                                    }
                                    BookingStep.SELECT_DATE -> {
                                        if (filteredAvailableSlots.isNotEmpty()) {
                                            selectedDateIndex = if (selectedDateIndex > 0) selectedDateIndex - 1 else filteredAvailableSlots.size - 1
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            val slot = filteredAvailableSlots[selectedDateIndex]
                                            FocusTTS.speak("Đang hiển thị thứ ${slot.dayOfWeek} ngày ${slot.date}")
                                        }
                                    }
                                    BookingStep.SELECT_TIME -> {
                                        if (filteredTimeSlots.isNotEmpty()) {
                                            selectedTimeIndex = if (selectedTimeIndex > 0) selectedTimeIndex - 1 else filteredTimeSlots.size - 1
                                            SoundManager.playSwipe()
                                            vibrate(context)
                                            FocusTTS.speak("Đang hiển thị ${filteredTimeSlots[selectedTimeIndex].displayTime}")
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
                    BookingStep.INITIAL -> "Đặt lịch khám"
                    BookingStep.SELECT_SPECIALTY -> "Chọn chuyên ngành"
                    BookingStep.SELECT_DOCTOR -> "Chọn bác sĩ"
                    BookingStep.SELECT_DATE -> "Chọn ngày"
                    BookingStep.SELECT_TIME -> "Chọn giờ"
                    BookingStep.CONFIRMATION -> "Xác nhận đặt lịch"
                    BookingStep.COMPLETE -> "Hoàn tất"
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = when (currentStep) {
                    BookingStep.INITIAL -> "Chạm để bắt đầu"
                    BookingStep.SELECT_SPECIALTY -> specialties.getOrNull(specialtyIndex)?.name ?: "Đang tải..."
                    BookingStep.SELECT_DOCTOR -> doctors.getOrNull(doctorIndex)?.name ?: "Đang tải..."
                    BookingStep.SELECT_DATE -> filteredAvailableSlots.getOrNull(selectedDateIndex)?.let { "Thứ ${it.dayOfWeek} ${it.date}" } ?: "Đang tải ngày..."
                    BookingStep.SELECT_TIME -> filteredTimeSlots.getOrNull(selectedTimeIndex)?.displayTime ?: "Đang tải giờ..."
                    BookingStep.CONFIRMATION -> "Nhấn giữ để nghe lại\nChạm để xác nhận"
                    BookingStep.COMPLETE -> "Đặt lịch thành công"
                },
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Vuốt lên/xuống để thay đổi\nChạm một lần để xác nhận\nChạm hai lần để quay lại",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
