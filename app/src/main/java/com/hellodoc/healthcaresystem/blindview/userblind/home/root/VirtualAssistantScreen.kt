package com.hellodoc.healthcaresystem.blindview.userblind.home.root

import android.Manifest
import android.content.pm.PackageManager
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.core.content.ContextCompat
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.view.user.home.fasttalk.startSpeechToTextRealtime
import com.hellodoc.healthcaresystem.view.user.supportfunction.FocusTTS
import com.hellodoc.healthcaresystem.view.user.supportfunction.SoundManager
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VirtualAssistantScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    
    var isListening by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        FocusTTS.waitUntilReady()
        delay(500)
        FocusTTS.speakAndWait("Đây là trang trợ lý ảo, hiện tại ứng dụng có 3 tính năng là xem bài viết, đặt lịch khám, xem lịch khám. Để tiếp tục hãy nhấn giữ màn hình và đọc to tính năng bạn muốn sử dụng")
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
                // Permission granted, do nothing here, user will long press again
            } else {
                coroutineScope.launch {
                    FocusTTS.speak("Ứng dụng cần quyền truy cập micro để sử dụng tính năng này")
                }
            }
        }
    )

    var wasLongPress by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (!wasLongPress) {
                            vibrate(context)
                            SoundManager.playTap()
                            coroutineScope.launch {
                                FocusTTS.speak("Đây là trang trợ lý ảo, hiện tại ứng dụng có 3 tính năng là xem bài viết, đặt lịch khám, xem lịch khám. Để tiếp tục hãy nhấn giữ màn hình và đọc to tính năng bạn muốn sử dụng")
                            }
                        }
                    },
                    onDoubleTap = {
                        vibrate(context)
                        SoundManager.playTap()
                        FocusTTS.stop()
                        navHostController.popBackStack()
                    },
                    onPress = {
                        wasLongPress = false
                        val job = coroutineScope.launch {
                            delay(500)
                            wasLongPress = true
                            FocusTTS.stop()
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                vibrate(context)
                                SoundManager.playHold()
                                isListening = true
                                startSpeechToTextRealtime(
                                    context = context,
                                    speechRecognizer = speechRecognizer,
                                    onPartial = { resultText = it },
                                    onFinal = { text ->
                                        isListening = false
                                        resultText = text
                                        handleVoiceCommand(text, navHostController) {
                                            coroutineScope.launch {
                                                FocusTTS.speakAndWait("Tính năng không hợp lệ, hãy nhấn giữ và đọc to lại tính năng bạn muốn chọn. Xem bài viết, đặt lịch khám hay xem lịch khám")
                                            }
                                        }
                                    },
                                    onEnd = {
                                        isListening = false
                                    }
                                )
                            }
                        }

                        tryAwaitRelease()
                        job.cancel()
                        if (isListening) {
                            speechRecognizer.stopListening()
                            isListening = false
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (dragAmount.x < -50) { // Swipe left
                            vibrate(context)
                            SoundManager.playTap()
                            FocusTTS.stop()
                            navHostController.navigate("home")
                        }
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
                "Trợ lý ảo",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (isListening) "Đang lắng nghe: $resultText" else if (resultText.isNotEmpty()) "Lệnh: $resultText" else "Nhấn giữ màn hình để nói",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

private fun handleVoiceCommand(text: String, navHostController: NavHostController, onError: () -> Unit) {
    val cleanText = text.lowercase()
    when {
        cleanText.contains("xem bài viết") -> navHostController.navigate("home")
        cleanText.contains("đặt lịch khám") -> navHostController.navigate("smart_booking_blind")
        cleanText.contains("xem lịch khám") -> navHostController.navigate("appointment_blind")
        else -> onError()
    }
}
