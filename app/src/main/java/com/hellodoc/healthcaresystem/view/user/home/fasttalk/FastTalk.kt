package com.hellodoc.healthcaresystem.view.user.home.fasttalk

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
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
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import com.hellodoc.healthcaresystem.R
import android.Manifest
import android.speech.SpeechRecognizer
import androidx.compose.foundation.layout.Box
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun FastTalk(
    navHostController: NavHostController,
    userViewModel: UserViewModel = hiltViewModel(),
    context: Context
) {
    var yourSentence by remember { mutableStateOf("") }
    var theirsSentence by remember { mutableStateOf("") }
    var tempSpeech by remember { mutableStateOf("") }
    var tempTheirSpeech by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }

    // ✅ Hướng đang mở rộng
    var extendedAlignment by remember { mutableStateOf<String?>(null) }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    Box( // dùng Box để overlay được các layer
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
                },
                onDelete={
                    theirsSentence=""
                },
                theirsSentence +
                        if (tempTheirSpeech.isNotEmpty())
                            " $tempTheirSpeech"
                        else "",
                isRecording
            )
            ConversationsLine { content -> yourSentence = " $content" }

            ConversationSections(
                yourSentence +
                        if (tempSpeech.isNotEmpty())
                            " $tempSpeech"
                        else "",
                onInput = { newText -> yourSentence = newText }, onDelete = { yourSentence = if (yourSentence.lastIndexOf(" ") != -1) yourSentence.substring(0, yourSentence.lastIndexOf(" ")) else "" } )

            CircleWordMenu(
                onChoice = { yourSentence += " $it" },
                onExtend = { alignment ->
                    extendedAlignment = alignment
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
                                    yourSentence += " $result"
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
                    if (yourSentence.isNotBlank()) {
                        speakText(context, yourSentence)
                    } else {
                        Toast.makeText(context, "Chưa có nội dung để đọc", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // ✅ Overlay menu mở rộng — đè lên mọi thứ
        if (extendedAlignment != null) {
            val groupWord = when (extendedAlignment) {
                "Top" -> listWordUp
                "Left" -> listWordsLeft
                "Bottom" -> listWordDown
                "Right" -> listWordsRight
                else -> emptyList()
            }

            // Màn che + menu mở rộng
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f)) // mờ nền
                    .clickable { extendedAlignment = null }, // click ra ngoài để tắt
                contentAlignment = Alignment.Center
            ) {
                ExtendingChoice(
                    groupWord = groupWord,
                    onChoice = {
                        yourSentence += " $it"
                        extendedAlignment = null // tắt overlay sau khi chọn
                    }
                )
            }
        }
    }
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