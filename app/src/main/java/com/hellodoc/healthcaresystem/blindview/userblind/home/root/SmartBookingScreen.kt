package com.hellodoc.healthcaresystem.blindview.userblind.home.root

import android.Manifest
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.core.content.ContextCompat
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
import com.hellodoc.healthcaresystem.view.user.home.fasttalk.startSpeechToTextRealtime
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.GeminiViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SmartBookingScreen(
    navHostController: NavHostController,
    geminiViewModel: GeminiViewModel = hiltViewModel(),
    specialtyViewModel: SpecialtyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    
    val specialties by specialtyViewModel.specialties.collectAsState()
    var resultText by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var recommendedSpecialty by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        specialtyViewModel.fetchSpecialties(forceRefresh = true)
        delay(500)
        FocusTTS.speakAndWait("Đây là trang Đặt lịch khám, để tiến hành đặt lịch, nhấn giữ vào màn hình và đọc to triệu chứng của bạn. Nhấn 2 lần để trở lại.")
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
                            FocusTTS.speak("Đây là trang Đặt lịch khám, để tiến hành đặt lịch, nhấn giữ vào màn hình và đọc to triệu chứng của bạn. Nhấn 2 lần để trở lại.")
                        }
                    },
                    onDoubleTap = {
                        vibrate(context)
                        SoundManager.playTap()
                        navHostController.popBackStack()
                    },
                    onLongPress = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            return@detectTapGestures
                        }

                        vibrate(context)
                        SoundManager.playHold()
                        startSpeechToTextRealtime(
                            context = context,
                            speechRecognizer = speechRecognizer,
                            onPartial = { resultText = it },
                            onFinal = { text ->
                                resultText = text
                                coroutineScope.launch {
                                    isAnalyzing = true
                                    FocusTTS.speakAndWait("Đang phân tích triệu chứng của bạn, vui lòng đợi giây lát")
                                    
                                    val specialtyNames = specialties.map { it.name }
                                    val matchedIndex = geminiViewModel.analyzeSymptomsForSpecialty(text, specialtyNames)
                                    
                                    if (matchedIndex != null) {
                                        val specialty = specialties[matchedIndex]
                                        recommendedSpecialty = specialty.name
                                        FocusTTS.speakAndWait("Dựa trên triệu chứng $text, tôi đề xuất bạn nên khám chuyên khoa ${specialty.name}.")
                                        // Here you could automatically navigate to DoctorList or stay to show more details
                                    } else {
                                        FocusTTS.speakAndWait("Tôi không tìm thấy chuyên khoa phù hợp với triệu chứng của bạn. Hãy thử mô tả cụ thể hơn")
                                    }
                                    isAnalyzing = false
                                }
                            },
                            onEnd = { }
                        )
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                "Đặt lịch thông minh",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (isAnalyzing) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Đang phân tích...", fontSize = 18.sp)
            } else {
                Text(
                    text = if (recommendedSpecialty != null) "Chuyên khoa đề xuất: $recommendedSpecialty" 
                           else if (resultText.isNotEmpty()) "Triệu chứng: $resultText"
                           else "Nhấn giữ và nói triệu chứng của bạn",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
