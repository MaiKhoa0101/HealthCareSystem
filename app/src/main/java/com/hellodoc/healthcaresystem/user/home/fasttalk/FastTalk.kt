package com.hellodoc.healthcaresystem.user.home.fasttalk

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

@Composable
fun FastTalk(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    userViewModel: UserViewModel,
    context: Context
) {
    var yourSentence by remember { mutableStateOf("") }
    var tempSpeech by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }

    // Giữ 1 reference để stop được
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderFastTalk(navHostController, "Hỗ trợ nói chuyện")
        InputConversation()

        ConversationSections(
            yourSentence + if (tempSpeech.isNotEmpty()) " $tempSpeech" else "",
            onInput = { newText -> yourSentence = newText },
            onDelete = {
                yourSentence = if (yourSentence.lastIndexOf(" ") != -1)
                    yourSentence.substring(0, yourSentence.lastIndexOf(" "))
                else ""
            }
        )

        CircleWordMenu(
            onChoice = { content -> yourSentence += " $content" }
        )

        BottomSectionFastTalk(
            isRecording = isRecording,
            onMicToggle = {
                if (!isRecording) {
                    // Chưa ghi âm → bắt đầu
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
                            onEnd = {
                                isRecording = false
                            }
                        )
                    }
                } else {
                    // Đang ghi âm → dừng lại
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