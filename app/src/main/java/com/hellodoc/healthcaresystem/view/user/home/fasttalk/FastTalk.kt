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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R
import android.Manifest
import android.speech.SpeechRecognizer
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.FastTalkViewModel

@Composable
fun FastTalk(
    navHostController: NavHostController,
    context: Context
) {
    val viewModel: FastTalkViewModel = hiltViewModel()

    // ✅ Chỉ dùng TextFieldValue
    var yourSentenceValue by remember { mutableStateOf(TextFieldValue(text = "")) }
    var theirsSentence by remember { mutableStateOf("") }
    var tempSpeech by remember { mutableStateOf("") }
    var tempTheirSpeech by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var extendedAlignment by remember { mutableStateOf<String?>(null) }
    val verb by viewModel.wordVerbSimilar.collectAsState()
    val noun by viewModel.wordNounSimilar.collectAsState()
    val adj  by viewModel.wordSupportSimilar.collectAsState()
    val pro  by viewModel.wordPronounSimilar.collectAsState()
    // ✅ GỌI API KHI TỪ ĐỔI
    LaunchedEffect(Unit,yourSentenceValue.text) {
        if (yourSentenceValue.text.isNotBlank()) {
            println("Gọi API với từ: " + getLastWord(yourSentenceValue.text))
            viewModel.getWordSimilar(getLastWord(yourSentenceValue.text))
        }
    }
    LaunchedEffect(Unit,theirsSentence) {
        if (theirsSentence != "") {
            println("Phân tích câu hỏi từ máy khách: $theirsSentence")
            viewModel.analyzeSentence(theirsSentence)
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
                                onPartial = { tempTheirSpeech = it },
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
                        currentText.substring(0, currentText.lastIndexOf(" "))
                    else ""
                    yourSentenceValue = TextFieldValue(
                        text = newText,
                        selection = TextRange(newText.length)
                    )
                    vibrate(context)
                }
            )

            // ✅ Sử dụng yourSentenceValue.text
            val currentWordFromSentence = remember(yourSentenceValue.text, theirsSentence) {
                getLastWord(
                    when {
                        yourSentenceValue.text.isNotBlank() -> yourSentenceValue.text
                        theirsSentence.isNotBlank() -> theirsSentence
                        else -> "tôi"
                    }
                )
            }
            SuggestionsRow { content ->
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
                                onPartial = { tempSpeech = it },
                                onFinal = { result ->
                                    val newText = yourSentenceValue.text + " $result"
                                    yourSentenceValue = TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length)
                                    )
                                    tempSpeech = ""
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
                    } else {
                        Toast.makeText(context, "Chưa có nội dung để đọc", Toast.LENGTH_SHORT).show()
                    }
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


fun getLastWord(text: String): String {
    return text.trim().split("\\s+".toRegex()).lastOrNull() ?: ""
}



@Composable
fun HeaderFastTalk(navHostController: NavHostController, name: String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = Modifier.clickable{
                navHostController.popBackStack()
            }
        )
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Image(
            painter = painterResource(id = R.drawable.speak),
            contentDescription = null
        )
    }
}