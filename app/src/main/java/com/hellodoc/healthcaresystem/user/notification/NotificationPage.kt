package com.hellodoc.healthcaresystem.user.notification
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.ui.theme.*

@Composable
fun NotificationPage(navHostController: NavHostController){
    val textNote: String ="adu"
    Column (modifier = Modifier.fillMaxWidth()){
        NotificationSectionFrame(textNote)

    }

}

@Composable
fun NotificationSectionFrame(textNote: String){
    Text(text = "Gần đây", fontWeight = FontWeight.Bold, fontSize = 20.sp)

    NotificationSection(textNote,false)
    Spacer(modifier = Modifier.height(8.dp))
    NotificationSection(textNote,false)

    Text(text = "Cũ hơn", fontWeight = FontWeight.Bold, fontSize = 20.sp)
    NotificationSection(textNote, true)
    Spacer(modifier = Modifier.height(8.dp))
    NotificationSection(textNote,true)
    Spacer(modifier = Modifier.height(8.dp))
    NotificationSection(textNote, true)
}




@Composable
fun NotificationSection(textnote: String, isRead: Boolean){
    Column (
        modifier = Modifier
            .background(if (isRead) Color.White else CyanNot)
            .fillMaxWidth()
            .clickable {  }
        ,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
        )
    {
        Row(modifier = Modifier.padding(10.dp))
        {
            Image(
                painter = painterResource(R.drawable.baseline_person_24),
                contentDescription = ""
            )
            Column {
                Text("Mai Khoa thích bài viết của bạn", fontWeight = FontWeight.Bold)
                Text("Nhấn vào đây để xem chi tiết")
            }
        }
    }
}