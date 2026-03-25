package com.hellodoc.healthcaresystem.blindview.userblind.home.doctor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Doctor
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun DoctorListBlindScreen(
    context: Context,
    navHostController: NavHostController
) {
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    var specialtyId by remember { mutableStateOf("") }
    var specialtyName by remember { mutableStateOf("") }
    var specialtyDesc by remember { mutableStateOf("") }
    val specialtyViewModel: SpecialtyViewModel = hiltViewModel()

    var isDataLoaded by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }
    var recognizedText by remember { mutableStateOf("") }

    // Text-to-Speech
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    // Speech Recognizer
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    // Khởi tạo TTS
    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("vi", "VN")
                // Phát âm thanh chào mừng
                tts?.speak(
                    "Đọc triệu chứng của bạn, tôi sẽ giúp bạn tìm bác sĩ",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
        }


        savedStateHandle?.get<String>("specialtyId")?.let {
            specialtyId = it
        }
        savedStateHandle?.get<String>("specialtyName")?.let {
            specialtyName = it
        }
        savedStateHandle?.get<String>("specialtyDesc")?.let {
            specialtyDesc = it
        }
        isDataLoaded = true
    }

    // Khởi tạo Speech Recognizer
    LaunchedEffect(Unit) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
            speechRecognizer?.destroy()
        }
    }

    LaunchedEffect(specialtyId) {
        if (specialtyId.isNotEmpty()) {
            specialtyViewModel.fetchSpecialtyDoctor(specialtyId)
        }
    }

    val doctors by specialtyViewModel.filteredDoctors.collectAsState()
    var isExpanded by remember { mutableStateOf(false) }

    // Hàm bắt đầu nghe
    fun startListening() {
        isListening = true
        recognizedText = ""

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Hãy nói triệu chứng của bạn...")
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
            }
            override fun onError(error: Int) {
                isListening = false
            }
            override fun onResults(results: Bundle?) {
                val matches =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (!matches.isNullOrEmpty()) {
                    recognizedText = matches[0]

                    analyzeSymptoms(recognizedText, specialtyViewModel) { foundId, foundName, foundDesc ->

                        if (foundId.isNotEmpty()) {
                            specialtyId = foundId
                            specialtyName = foundName
                            specialtyDesc = foundDesc

                            specialtyViewModel.fetchSpecialtyDoctor(foundId)

                            tts?.speak(
                                "Bạn cần khám chuyên khoa $foundName",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                null
                            )
                        } else {
                            tts?.speak(
                                "Tôi chưa xác định được chuyên khoa phù hợp",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                null
                            )
                        }
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    if (isDataLoaded) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopBar(
                onClick = { navHostController.popBackStack() },
                specialtyViewModel = specialtyViewModel,
                onVoiceSearch = { startListening() },
                isListening = isListening
            )

            // Hiển thị text nhận dạng được
            if (recognizedText.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Triệu chứng:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = recognizedText,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (specialtyName.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = specialtyName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = specialtyDesc,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (specialtyDesc.length > 100) {
                        Text(
                            text = if (isExpanded) "Thu gọn" else "Xem thêm",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable { isExpanded = !isExpanded }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (doctors.isEmpty() && specialtyId.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Không tìm thấy bác sĩ trong chuyên khoa này",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                } else if (specialtyId.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Nhấn vào biểu tượng mic để mô tả triệu chứng",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                } else {
                    items(doctors) { doctor ->
                        DoctorItem(
                            navHostController = navHostController,
                            doctor = doctor,
                            specialtyName = specialtyName,
                            specialtyId = specialtyId,
                            specialtyDesc = specialtyDesc,
                            viewModel = specialtyViewModel
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

fun analyzeSymptoms(
    symptoms: String,
    viewModel: SpecialtyViewModel,
    onResult: (String, String, String) -> Unit
) {
    val symptomsLower = symptoms.lowercase()

    val specialtyKeywords = mapOf(
        "Tim mạch" to listOf("tim", "huyết áp", "đau ngực"),
        "Da liễu" to listOf("ngứa", "mụn", "nấm"),
        "Tai mũi họng" to listOf("viêm họng", "ngạt mũi"),
    )

    var matchedName = ""
    var scoreMax = 0

    specialtyKeywords.forEach { (name, keywords) ->
        val score = keywords.count { symptomsLower.contains(it) }
        if (score > scoreMax) {
            scoreMax = score
            matchedName = name
        }
    }

    if (matchedName.isNotEmpty()) {
//        viewModel.getSpecialtyByName(matchedName) { specialty ->
//            onResult(
//                specialty.id,
//                specialty.name,
//                specialty.description
//            )
//        }
    } else {
        onResult("", "", "")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onClick: () -> Unit,
    specialtyViewModel: SpecialtyViewModel,
    onVoiceSearch: () -> Unit,
    isListening: Boolean
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back Button",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onClick() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Tìm bác sĩ",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Nút mic để kích hoạt voice search
            IconButton(
                onClick = onVoiceSearch,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Search",
                    tint = if (isListening) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                specialtyViewModel.filterDoctorsByLocation(searchQuery)
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            },
            placeholder = { Text("Nhập địa chỉ, ví dụ: HCM") },
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = MaterialTheme.colorScheme.background,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.background,
                disabledIndicatorColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}

@Composable
fun DoctorItem(
    navHostController: NavHostController,
    doctor: Doctor,
    specialtyName: String,
    specialtyId: String,
    specialtyDesc: String,
    viewModel: SpecialtyViewModel
) {
    val isClinicPaused = doctor.isClinicPaused ?: false
    val userViewModel: UserViewModel = hiltViewModel()
    val you by userViewModel.you.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        userViewModel.getYou(context)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(12.dp)
            )
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!doctor.avatarURL.isNullOrBlank()) {
                AsyncImage(
                    model = doctor.avatarURL,
                    contentDescription = doctor.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.doctor),
                    contentDescription = doctor.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Bác sĩ",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    if (isClinicPaused) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.errorContainer,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "Tạm ngưng nhận lịch",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
                Text(doctor.name, fontSize = 26.sp, fontWeight = FontWeight.Medium)
                Text(
                    specialtyName,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = doctor.address ?: "Chưa cập nhật địa chỉ",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if ( you?.id != doctor.id) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("doctorId", doctor.id)
                        }
                        navHostController.navigate("other_user_profile")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Đặt lịch khám",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}