package com.hellodoc.healthcaresystem.blindview.userblind.home.root

import android.Manifest
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.core.content.ContextCompat
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
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
import com.hellodoc.healthcaresystem.view.user.home.fasttalk.startSpeechToTextRealtime
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import com.hellodoc.healthcaresystem.requestmodel.SuggestedAppointmentRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.SuggestedAppointmentResponse
import com.hellodoc.healthcaresystem.view.user.supportfunction.formatTimeToVietnamese
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class SmartBookingStep {
    SYMPTOMS,
    DATE_RANGE,
    TIME_RANGE,
    RESULTS
}

@Composable
fun SmartBookingScreen(
    navHostController: NavHostController,
    geminiViewModel: GeminiViewModel = hiltViewModel(),
    specialtyViewModel: SpecialtyViewModel = hiltViewModel(),
    appointmentViewModel: AppointmentViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    
    val specialties by specialtyViewModel.specialties.collectAsState()
    var resultText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var recommendedSpecialty by remember { mutableStateOf<String?>(null) }
    var recommendedSpecialtyId by remember { mutableStateOf<String?>(null) }

    var currentStep by remember { mutableStateOf(SmartBookingStep.SYMPTOMS) }
    var suggestedAppointments by remember { mutableStateOf<List<SuggestedAppointmentResponse>>(emptyList()) }
    var currentResultIndex by remember { mutableIntStateOf(0) }

    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    var fromHour by remember { mutableStateOf("") }
    var toHour by remember { mutableStateOf("") }

    val currentDateTimeStr = remember {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("'ngày' d 'tháng' M 'năm' yyyy", Locale("vi", "VN"))
        val dayOfWeek = when (now.dayOfWeek.value) {
            1 -> "thứ 2"
            2 -> "thứ 3"
            3 -> "thứ 4"
            4 -> "thứ 5"
            5 -> "thứ 6"
            6 -> "thứ 7"
            7 -> "Chủ nhật"
            else -> ""
        }
        val timeStr = formatTimeToVietnamese(
            String.format("%02d:%02d", now.hour, now.minute)
        )
        "Hiện tại là $timeStr $dayOfWeek ${now.format(formatter)}"
    }

    LaunchedEffect(Unit) {
        specialtyViewModel.fetchSpecialties(forceRefresh = true)
        FocusTTS.waitUntilReady()
        delay(500)
        FocusTTS.speakAndWait("Đây là trang Đặt lịch khám, để tiến hành đặt lịch, nhấn giữ vào màn hình và đọc to triệu chứng của bạn. Nhấn 2 lần để trở lại.")
    }

    val appointmentSuccess by appointmentViewModel.appointmentSuccess.collectAsState()
    val appointmentError by appointmentViewModel.appointmentError.collectAsState()

    LaunchedEffect(appointmentSuccess, appointmentError) {
        if (appointmentSuccess) {
            FocusTTS.speakAndWait("Chúc mừng, bạn đã đặt lịch khám thành công!")
            delay(1000)
            navHostController.popBackStack()
            appointmentViewModel.resetAppointmentSuccess()
        } else if (appointmentError != null) {
            FocusTTS.speakAndWait("Có lỗi xảy ra khi đặt lịch: $appointmentError. Vui lòng thử lại sau.")
            appointmentViewModel.resetAppointmentError()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted
            } else {
                coroutineScope.launch {
                    FocusTTS.speak("Ứng dụng cần quyền truy cập micro để sử dụng tính năng này")
                }
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(specialties) {
                detectTapGestures(
                    onTap = {
                        vibrate(context)
                        SoundManager.playTap()
                        coroutineScope.launch {
                            when (currentStep) {
                                SmartBookingStep.SYMPTOMS -> {
                                    FocusTTS.speak("Đây là trang Đặt lịch khám, để tiến hành đặt lịch, nhấn giữ vào màn hình và đọc to triệu chứng của bạn. Nhấn 2 lần để trở lại.")
                                }
                                SmartBookingStep.DATE_RANGE -> {
                                    FocusTTS.speak("Bước 2: Chọn khoảng ngày rảnh. Nhấn giữ và đọc khoảng ngày bạn muốn tìm lịch. Ví dụ: từ thứ 2 đến thứ 6 tuần sau.")
                                }
                                SmartBookingStep.TIME_RANGE -> {
                                    FocusTTS.speak("Bước 3: Chọn khoảng giờ rảnh. Nhấn giữ và đọc khoảng giờ bạn rảnh. Ví dụ: từ 8 giờ sáng đến 2 giờ chiều.")
                                }
                                SmartBookingStep.RESULTS -> {
                                    if (suggestedAppointments.isNotEmpty()) {
                                        val item = suggestedAppointments[currentResultIndex] // Get current item for TTS
                                        val formattedDate = formatDateForTTS(item.date)
                                        val formattedTime = formatTimeToVietnamese(item.time)
                                        FocusTTS.speak("Đây là lịch khám lúc $formattedTime $formattedDate với bác sĩ ${item.doctorName} chuyên ngành ${item.specialtyName}. Nhấn giữ để tiến hành đặt lịch. Trượt lên hoặc xuống để xem lịch khác.")
                                    } else {
                                        FocusTTS.speak("Không tìm thấy lịch khám phù hợp. Nhấn 2 lần để quay lại.")
                                    }
                                }
                            }
                        }
                    },
                    onDoubleTap = {
                        vibrate(context)
                        SoundManager.playTap()
                        FocusTTS.stop()
                        navHostController.popBackStack()
                    },
                    onLongPress = {
                        FocusTTS.stop()
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            return@detectTapGestures
                        }

                        vibrate(context)
                        SoundManager.playHold()

                        if (currentStep == SmartBookingStep.RESULTS && suggestedAppointments.isNotEmpty()) {
                            // Tiến hành đặt lịch với item hiện tại
                            coroutineScope.launch {
                                val item = suggestedAppointments[currentResultIndex]
                                val formattedDateForSpeech = LocalDate.parse(item.date).format(DateTimeFormatter.ofPattern("EEEE, 'ngày' dd 'tháng' MM", Locale("vi", "VN")))
                                val formattedTimeForSpeech = formatTimeToVietnamese(item.time)
                                FocusTTS.speakAndWait("Bạn đã chọn đặt lịch với bác sĩ ${item.doctorName}, chuyên khoa ${item.specialtyName}. Thời gian: $formattedTimeForSpeech, $formattedDateForSpeech. Hệ thống đang xử lý.")
                                
                                val token = userViewModel.getUserAttribute("access_token", context)
                                val patientID = userViewModel.getUserAttribute("userId", context)
                                val patientModel = userViewModel.getUserAttribute("role", context)
                                val address = userViewModel.getUserAttribute("address", context)

                                appointmentViewModel.createAppointment(
                                    token = token,
                                    createAppointmentRequest = CreateAppointmentRequest(
                                        doctorID = item.doctorId,
                                        patientID = patientID,
                                        patientModel = patientModel,
                                        date = item.date,
                                        time = item.time,
                                        examinationMethod = "at_home",
                                        notes = "Đặt lịch thông minh qua trợ lý giọng nói",
                                        reason = "Đặt lịch thông minh",
                                        totalCost = "0",
                                        location = address
                                    )
                                )
                            }
                            return@detectTapGestures
                        }

                        isListening = true
                        startSpeechToTextRealtime(
                            context = context,
                            speechRecognizer = speechRecognizer,
                            onPartial = { resultText = it },
                            onFinal = { text ->
                                isListening = false
                                resultText = text
                                coroutineScope.launch {
                                    isAnalyzing = true
                                    when (currentStep) {
                                        SmartBookingStep.SYMPTOMS -> {
                                            FocusTTS.speakAndWait("Đang phân tích triệu chứng của bạn, vui lòng đợi giây lát")
                                            val specialtyNames = specialties.map { it.name }
                                            val matchedIndex = geminiViewModel.analyzeSymptomsForSpecialty(text, specialtyNames)
                                            
                                            if (matchedIndex != null) {
                                                val specialty = specialties[matchedIndex]
                                                recommendedSpecialty = specialty.name
                                                recommendedSpecialtyId = specialty.id
                                                FocusTTS.speakAndWait("Dựa trên triệu chứng $text, tôi đề xuất bạn nên khám chuyên khoa ${specialty.name}.")
                                                
                                                currentStep = SmartBookingStep.DATE_RANGE
                                                FocusTTS.speakAndWait("Tiếp theo tôi sẽ giúp bạn tìm lịch khám với chuyên ngành ${specialty.name}. $currentDateTimeStr. Hãy đọc to khoảng ngày bạn rảnh để tôi tìm lịch phù hợp. Ví dụ: từ thứ 2 ngày 5 tháng 1 năm 2026 đến thứ 6 ngày 10 tháng 1 năm 2026.")
                                            } else {
                                                FocusTTS.speakAndWait("Tôi không tìm thấy chuyên khoa phù hợp với triệu chứng của bạn. Hãy thử mô tả cụ thể hơn")
                                            }
                                        }
                                        SmartBookingStep.DATE_RANGE -> {
                                            FocusTTS.speakAndWait("Đang ghi nhận khoảng ngày của bạn")
                                            val request = geminiViewModel.analyzeDateTimeRange(text, currentDateTimeStr)
                                            if (request != null) {
                                                fromDate = request.fromDate
                                                toDate = request.toDate
                                                currentStep = SmartBookingStep.TIME_RANGE
                                                FocusTTS.speakAndWait("Tiếp theo hãy đọc to khoảng giờ bạn rảnh. Ví dụ: từ 8 giờ 30 phút sáng đến 2 giờ 30 phút chiều.")
                                            } else {
                                                FocusTTS.speakAndWait("Tôi không rõ khoảng ngày bạn nói. Vui lòng đọc lại rõ ràng hơn.")
                                            }
                                        }
                                        SmartBookingStep.TIME_RANGE -> {
                                            FocusTTS.speakAndWait("Đang tìm lịch khám phù hợp, vui lòng đợi")
                                            val timeRequest = geminiViewModel.analyzeDateTimeRange(text, currentDateTimeStr)
                                            if (timeRequest != null && recommendedSpecialtyId != null) {
                                                val finalRequest = SuggestedAppointmentRequest(
                                                    specialtyId = recommendedSpecialtyId!!,
                                                    fromDate = fromDate,
                                                    toDate = toDate,
                                                    fromHour = timeRequest.fromHour,
                                                    toHour = timeRequest.toHour
                                                )
                                                val response = geminiViewModel.getSuggestedAppointments(finalRequest)
                                                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                                                    suggestedAppointments = response.body()!!
                                                    currentStep = SmartBookingStep.RESULTS
                                                    currentResultIndex = 0
                                                    val item = suggestedAppointments[0]
                                                    val formattedDate = formatDateForTTS(item.date)
                                                    val formattedTime = formatTimeToVietnamese(item.time)
                                                    FocusTTS.speakAndWait("Tôi đã tìm được lịch phù hợp cho bạn. Đây là lịch khám lúc $formattedTime $formattedDate với bác sĩ ${item.doctorName} chuyên ngành ${item.specialtyName}. Nhấn giữ để tiến hành đặt lịch. Trượt lên hoặc xuống để xem lịch khác.")
                                                } else {
                                                    FocusTTS.speakAndWait("Rất tiếc, tôi không tìm thấy lịch khám nào phù hợp trong khoảng thời gian này. Vui lòng thử lại với khoảng thời gian khác.")
                                                    currentStep = SmartBookingStep.DATE_RANGE // Quay lại hỏi ngày
                                                }
                                            } else {
                                                FocusTTS.speakAndWait("Tôi không rõ khoảng giờ bạn nói. Vui lòng đọc lại rõ ràng hơn.")
                                            }
                                        }
                                        SmartBookingStep.RESULTS -> { /* Already handled above */ }
                                    }
                                    isAnalyzing = false
                                }
                            },
                            onEnd = { 
                                isListening = false
                            }
                        )
                    }
                )
            }
            .pointerInput(suggestedAppointments, currentResultIndex) {
                 // Removed detectTapGestures { } to prevent blocking inner gestures
            }
            .pointerInput(suggestedAppointments, currentStep) {
                if (currentStep == SmartBookingStep.RESULTS && suggestedAppointments.isNotEmpty()) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount.y > 50) { // Swipe down
                                if (currentResultIndex > 0) {
                                    currentResultIndex--
                                    vibrate(context)
                                    SoundManager.playTap()
                                    val item = suggestedAppointments[currentResultIndex]
                                    val formattedDate = formatDateForTTS(item.date)
                                    val formattedTime = formatTimeToVietnamese(item.time)
                                    coroutineScope.launch {
                                        FocusTTS.speak("Đây là lịch khám lúc $formattedTime $formattedDate với bác sĩ ${item.doctorName} chuyên ngành ${item.specialtyName}. Nhấn giữ để tiến hành đặt lịch. Trượt lên hoặc xuống để xem lịch khác.")
                                    }
                                }
                            } else if (dragAmount.y < -50) { // Swipe up
                                if (currentResultIndex < suggestedAppointments.size - 1) {
                                    currentResultIndex++
                                    vibrate(context)
                                    SoundManager.playTap()
                                    val item = suggestedAppointments[currentResultIndex]
                                    val formattedDate = formatDateForTTS(item.date)
                                    val formattedTime = formatTimeToVietnamese(item.time)
                                    coroutineScope.launch {
                                        FocusTTS.speak("Đây là lịch khám lúc $formattedTime $formattedDate với bác sĩ ${item.doctorName} chuyên ngành ${item.specialtyName}. Nhấn giữ để tiến hành đặt lịch. Trượt lên hoặc xuống để xem lịch khác.")
                                    }
                                }
                            } else if (dragAmount.x < -150) { // Swipe left
                                vibrate(context)
                                SoundManager.playTap()
                                FocusTTS.stop()
                                navHostController.navigate("home")
                            }
                        }
                    )
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
                when (currentStep) {
                    SmartBookingStep.SYMPTOMS -> "Đặt lịch thông minh"
                    SmartBookingStep.DATE_RANGE -> "Chọn ngày rảnh"
                    SmartBookingStep.TIME_RANGE -> "Chọn giờ rảnh"
                    SmartBookingStep.RESULTS -> "Kết quả gợi ý"
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (isListening) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Đang lắng nghe: $resultText", fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary)
            } else if (isAnalyzing) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Đang phân tích...", fontSize = 18.sp)
            } else {
                Text(
                    text = when (currentStep) {
                        SmartBookingStep.SYMPTOMS -> {
                            if (recommendedSpecialty != null) "Chuyên khoa đề xuất: $recommendedSpecialty"
                            else if (resultText.isNotEmpty()) "Triệu chứng: $resultText"
                            else "Nhấn giữ và nói triệu chứng của bạn"
                        }
                        SmartBookingStep.DATE_RANGE -> {
                            if (fromDate.isNotEmpty()) "Từ $fromDate đến $toDate"
                            else "Nhấn giữ và nói khoảng ngày bạn rảnh"
                        }
                        SmartBookingStep.TIME_RANGE -> {
                            "Nhấn giữ và nói khoảng giờ bạn rảnh"
                        }
                        SmartBookingStep.RESULTS -> {
                            if (suggestedAppointments.isNotEmpty()) {
                                val item = suggestedAppointments[currentResultIndex]
                                "Bác sĩ: ${item.doctorName}\nLúc: ${item.time} ngày ${item.date}"
                            } else "Không có kết quả"
                        }
                    },
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

fun formatDateForTTS(dateStr: String): String {
    return try {
        val date = LocalDate.parse(dateStr)
        val dayOfWeek = when (date.dayOfWeek.value) {
            1 -> "thứ 2"
            2 -> "thứ 3"
            3 -> "thứ 4"
            4 -> "thứ 5"
            5 -> "thứ 6"
            6 -> "thứ 7"
            7 -> "Chủ nhật"
            else -> ""
        }
        "$dayOfWeek ngày ${date.dayOfMonth} tháng ${date.monthValue} năm ${date.year}"
    } catch (e: Exception) {
        "ngày $dateStr"
    }
}

