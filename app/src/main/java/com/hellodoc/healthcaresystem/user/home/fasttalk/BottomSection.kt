package com.hellodoc.healthcaresystem.user.home.fasttalk

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BottomSectionFastTalk(
    isRecording: Boolean,
    onMicToggle: () -> Unit,
    onPronounce: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = if (isRecording) Icons.Default.Hearing else Icons.Default.Mic,
            contentDescription = if (isRecording) "Đang ghi âm" else "Bắt đầu ghi âm",
            modifier = Modifier
                .size(60.dp)
                .padding(10.dp)
                .clip(CircleShape)
                .clickable { onMicToggle() }
        )
        Image(
            imageVector = Icons.Default.RecordVoiceOver,
            contentDescription = "Đọc lại",
            modifier = Modifier
                .size(60.dp)
                .padding(10.dp)
                .clip(CircleShape)
                .clickable { onPronounce() }
        )
    }
}
