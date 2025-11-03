package com.hellodoc.healthcaresystem.user.home.fasttalk

import android.inputmethodservice.Keyboard.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationSections(yourSentence:String, onInput:(String)->Unit, onDelete:(String)->Unit){
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = yourSentence,
                onValueChange = { newValue -> onInput(newValue) },
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .heightIn(min = 56.dp, max = 120.dp) // Giới hạn cao tối đa ~4 dòng
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp),
                maxLines = Int.MAX_VALUE,
                singleLine = false
            )
            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.Backspace,
                contentDescription = "Xóa từ cuối",
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .size(46.dp)
                    .clickable { onDelete(yourSentence) }
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun InputConversation(onMicToggle: () -> Unit,onDelete:()->Unit,inputText:String,isRecording:Boolean){
    Row (
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(inputText,
            modifier = Modifier.fillMaxWidth(0.7f),
            fontWeight = FontWeight.Bold
        )
        if (inputText=="" || isRecording) {
            Image(
                imageVector = if (isRecording) Icons.Default.Hearing else Icons.Default.Mic,
                contentDescription = if (isRecording) "Đang ghi âm" else "Bắt đầu ghi âm",
                modifier = Modifier
                    .size(40.dp)
                    .padding(10.dp)
                    .clip(CircleShape)
                    .clickable { onMicToggle() }
            )
        }
        else{
            Icon(
                imageVector = Icons.Default.Backspace,
                contentDescription = "Xóa cả câu",
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .size(46.dp)
                    .clickable { onDelete() }
                    .padding(10.dp)
            )
        }
    }
}