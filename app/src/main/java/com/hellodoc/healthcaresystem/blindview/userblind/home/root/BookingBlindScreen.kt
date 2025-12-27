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
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class BookingStep {
    INITIAL,
    SELECT_SPECIALTY,
    SELECT_DOCTOR,
    COMPLETE
}

@Composable
fun BookingBlindScreen(
    navHostController: NavHostController,
    specialtyViewModel: SpecialtyViewModel = hiltViewModel(),
    doctorViewModel: DoctorViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var currentStep by remember { mutableStateOf(BookingStep.INITIAL) }
    
    val specialties by specialtyViewModel.specialties.collectAsState()
    val doctors by specialtyViewModel.doctors.collectAsState() // Doctors of selected specialty
    
    var specialtyIndex by remember { mutableIntStateOf(0) }
    var doctorIndex by remember { mutableIntStateOf(0) }
    
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val horizontalThreshold = 150f
    val verticalThreshold = 100f

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
                                BookingStep.COMPLETE -> {
                                    currentStep = BookingStep.SELECT_DOCTOR
                                    FocusTTS.speak("Quay lại chọn bác sĩ, đang hiển thị bác sĩ ${doctors[doctorIndex].name}")
                                }
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
                                    FocusTTS.speakAndWait("Để tiến hành đặt lịch hãy chạm vào màn hình để chọn chuyên ngành đang hiển thị, vuốt lên hoặc xuống để chuyển chuyên ngành.")
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
                                            FocusTTS.speakAndWait("Bạn đã chọn chuyên ngành ${selectedSpecialty.name}, tiếp theo hãy chạm vào màn hình để chọn bác sĩ đang hiển thị, vuốt lên hoặc xuống để chuyển bác sĩ.")
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
                                        // Potential navigation to final booking confirm or similar
                                        // navHostController.navigate("booking_confirm")
                                        currentStep = BookingStep.COMPLETE
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                )
            }
            .pointerInput(currentStep, specialties, doctors, specialtyIndex, doctorIndex) {
                detectDragGestures(
                    onDragEnd = {
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) { change, dragAmount ->
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y

                    // Vertical Swipe to Cycle Items
                    if (kotlin.math.abs(offsetY) > verticalThreshold) {
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
                                else -> {}
                            }
                        }
                        offsetY = 0f
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
                    BookingStep.COMPLETE -> "Hoàn tất chọn"
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
                    BookingStep.COMPLETE -> "Bạn đã chọn xong"
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
