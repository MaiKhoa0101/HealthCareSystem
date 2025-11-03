package com.hellodoc.healthcaresystem.user.home.fasttalk

import android.content.SharedPreferences
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
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import com.hellodoc.healthcaresystem.R

@Composable
fun FastTalk(navHostController: NavHostController, sharedPreferences: SharedPreferences, userViewModel: UserViewModel){
    var yourSentence by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        HeaderFastTalk(navHostController, "Hỗ trợ nói chuyện")
        ConversationSections(
            yourSentence,
            onInput = { newText ->
                yourSentence = newText // khi người dùng gõ thì cập nhật
            }
        )
        ConversationsLine(navHostController)
        CircleWordMenu(
            onChoice = { content ->
                yourSentence += " $content" // nối thêm vào câu hiện có
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