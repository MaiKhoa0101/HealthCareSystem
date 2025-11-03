package com.hellodoc.healthcaresystem.user.home.fasttalk

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationSections(yourSentence:String, onInput:(String)->Unit){
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Anh đã ăn gì chưa")
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = yourSentence,
            onValueChange = { newValue ->
                onInput(newValue) // cập nhật khi gõ
            },
            label = { Text("") },
            modifier = Modifier
                .border(
                    width = 1.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
                .clip(
                    shape = RoundedCornerShape(16.dp)
                )
            ,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
        )
    }
}