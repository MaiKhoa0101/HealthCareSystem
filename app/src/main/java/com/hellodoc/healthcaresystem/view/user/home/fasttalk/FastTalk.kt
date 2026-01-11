package com.hellodoc.healthcaresystem.view.user.home.fasttalk

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R
import android.Manifest
import android.speech.SpeechRecognizer
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.QA
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.FastTalkViewModel
import com.hellodoc.healthcaresystem.viewmodel.StateViewModel
import kotlinx.coroutines.launch

@Composable
fun FastTalk(
    navHostController: NavHostController,
    context: Context
) {
    val fastTalkViewModel: FastTalkViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()

    val stateViewModel: StateViewModel = hiltViewModel()
    // ✅ Chỉ dùng TextFieldValue
    var yourSentenceValue by remember { mutableStateOf(TextFieldValue(text = "")) }
    var theirsSentence by remember { mutableStateOf("") }
    var tempTheirSpeech by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var extendedAlignment by remember { mutableStateOf<String?>(null) }
    val verb by fastTalkViewModel.wordVerbSimilar.collectAsState()
    val noun by fastTalkViewModel.wordNounSimilar.collectAsState()
    val adj  by fastTalkViewModel.wordSupportSimilar.collectAsState()
    val pro  by fastTalkViewModel.wordPronounSimilar.collectAsState()
    // ✅ GỌI API KHI TỪ ĐỔI
    LaunchedEffect(Unit,yourSentenceValue.text) {
        if (yourSentenceValue.text.isNotBlank()) {
            println("Gọi API với từ: " + getLastWord(yourSentenceValue.text))
            fastTalkViewModel.getWordSimilar(getLastWord(yourSentenceValue.text))
        }
    }
    LaunchedEffect(Unit,theirsSentence) {
        if (theirsSentence != "") {
            println("Phân tích câu hỏi từ máy khách: $theirsSentence")
            fastTalkViewModel.analyzeSentence(theirsSentence)
            try{
                fastTalkViewModel.findQuickResponse(theirsSentence)
            } catch (e: Exception) {
                println("lỗi database ${e.message}")
                e.printStackTrace()
            }

        }
    }
    // ===== STATE QUẢN LÝ DIALOG =====
    var showDataLoadDialog by remember { mutableStateOf(false) }
    val isDataDownloaded by stateViewModel.isDataDownloaded.collectAsState(initial = false)
    val isLoading by fastTalkViewModel.isLoading.collectAsState()

    // ===== KIỂM TRA DỮ LIỆU KHI VÀO MÀN HÌNH =====
    LaunchedEffect(Unit,isDataDownloaded) {
        stateViewModel.getDownloadStatus()
        // Kiểm tra xem đã tải dữ liệu chưa
        if (!isDataDownloaded) {
            showDataLoadDialog = true
        }
    }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderFastTalk(navHostController, "Hỗ trợ nói chuyện")
            InputConversation(
                onMicToggle = {
                    if (!isRecording) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                        } else {
                            isRecording = true
                            startSpeechToTextRealtime(
                                context,
                                speechRecognizer,
                                onFinal = { result ->
                                    theirsSentence += " $result"
                                    tempTheirSpeech = ""
                                    isRecording = false
                                },
                                onEnd = { isRecording = false }
                            )
                        }
                    } else {
                        isRecording = false
                        speechRecognizer.stopListening()
                    }
                    vibrate(context)
                },
                onDelete = {
                    theirsSentence = ""
                },
                theirsSentence +
                        if (tempTheirSpeech.isNotEmpty())
                            " $tempTheirSpeech"
                        else "",
                isRecording
            )

            ConversationSections(
                yourSentence = yourSentenceValue,
                onInput = { newValue ->
                    yourSentenceValue = newValue.copy(
                        selection = TextRange(newValue.text.length)
                    )
                    vibrate(context)
                },
                onDelete = {
                    val currentText = yourSentenceValue.text
                    val newText = if (currentText.lastIndexOf(" ") != -1)
                        currentText.take(currentText.lastIndexOf(" "))
                    else ""
                    yourSentenceValue = TextFieldValue(
                        text = newText,
                        selection = TextRange(newText.length)
                    )
                    vibrate(context)
                }
            )

            val currentWordFromSentence = remember(yourSentenceValue.text, theirsSentence) {
                getLastWord(
                    when {
                        yourSentenceValue.text.isNotBlank() -> yourSentenceValue.text
                        theirsSentence.isNotBlank() -> theirsSentence
                        else -> "tôi"
                    }
                )
            }
            SuggestionsRow(fastTalkViewModel) { content ->
                val newText = yourSentenceValue.text + " $content"
                yourSentenceValue = TextFieldValue(
                    text = newText,
                    selection = TextRange(newText.length)
                )
                vibrate(context)
            }

            CircleWordMenu(
                currentWord = currentWordFromSentence,
                onChoice = { word ->
                    val newText = yourSentenceValue.text + " $word"
                    yourSentenceValue = TextFieldValue(
                        text = newText,
                        selection = TextRange(newText.length)
                    )
                    vibrate(context)
                },
                onExtend = {
                    alignment -> extendedAlignment = alignment
                    vibrate(context)
                }
            )

            BottomSectionFastTalk(
                isRecording = isRecording,
                onMicToggle = {
                    if (!isRecording) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
                        } else {
                            isRecording = true
                            startSpeechToTextRealtime(
                                context,
                                speechRecognizer,
                                onFinal = { result ->
                                    val newText = yourSentenceValue.text + " $result"
                                    yourSentenceValue = TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length)
                                    )
                                    isRecording = false
                                },
                                onEnd = { isRecording = false }
                            )
                        }
                    } else {
                        isRecording = false
                        speechRecognizer.stopListening()
                    }
                },
                onPronounce = {
                    if (yourSentenceValue.text.isNotBlank()) {
                        speakText(context, yourSentenceValue.text)
                        coroutineScope.launch {
                            fastTalkViewModel.analyzeSentence(yourSentenceValue.text)

                            println("bắt đầu lưu trong roomDB")
                            fastTalkViewModel.insertQuickResponse(theirsSentence, yourSentenceValue.text)
                            println("đã lưu vào roomDB")
                            fastTalkViewModel.updateQA(QA(theirsSentence, yourSentenceValue.text))
                            fastTalkViewModel.findQuickResponse(theirsSentence)

                        }
                    } else {
                        Toast.makeText(context, "Chưa có nội dung để đọc", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        // ===== DIALOG TẢI DỮ LIỆU =====
        if (showDataLoadDialog) {
            DataLoadingDialog(
                isLoading = isLoading,
                onConfirm = {
                    fastTalkViewModel.readFromLocalFile()
                    stateViewModel.setDownloadStatus(true)
                    showDataLoadDialog=false
                },
                onDismiss = {
                    showDataLoadDialog = false
                }
            )
        }
        // ✅ Overlay menu mở rộng
        if (extendedAlignment != null) {
            val groupWord = when (extendedAlignment) {
                "Top" -> noun
                "Left" -> verb
                "Bottom" -> pro
                "Right" -> adj
                else -> emptyList()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
                    .clickable { extendedAlignment = null },
                contentAlignment = Alignment.Center
            ) {
                ExtendingChoice(
                    groupWord = groupWord,
                    onChoice = { word ->
                        val newText = yourSentenceValue.text + " $word"
                        yourSentenceValue = TextFieldValue(
                            text = newText,
                            selection = TextRange(newText.length)
                        )
                        extendedAlignment = null
                    }
                )
            }
        }
    }
}

fun parseTokenJson(json: String?): List<String> {
    return try {
        val type = object : TypeToken<List<String>>() {}.type
        Gson().fromJson(json, type)
    } catch (e: Exception) {
        emptyList()
    }
}
// ===== DIALOG COMPONENT =====
@Composable
fun DataLoadingDialog(
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        confirmButton = {
            if (!isLoading) {
                TextButton(onClick = {
                    onConfirm()
                }) {
                    Text("Tải dữ liệu", color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        dismissButton = {
            if (!isLoading) {
                TextButton(onClick = onDismiss) {
                    Text("Để sau", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isLoading) "Đang tải dữ liệu..." else "Tải dữ liệu Fast Talk",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isLoading) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Đang tải dữ liệu từ file local...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Vui lòng đợi trong giây lát",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = "Chức năng Fast Talk cần tải dữ liệu từ kho dữ liệu cục bộ để hoạt động.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.Green,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Không cần kết nối Internet",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Tải nhanh, chỉ mất vài giây",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        properties = DialogProperties(
            dismissOnBackPress = !isLoading,
            dismissOnClickOutside = !isLoading
        )
    )
}

fun getLastWord(text: String): String {
    return text.trim().split("\\s+".toRegex()).lastOrNull() ?: ""
}


@Composable
fun HeaderFastTalk(navHostController: NavHostController, name: String) {


    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navHostController.popBackStack() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            Image(
                painter = painterResource(id = R.drawable.speak),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}