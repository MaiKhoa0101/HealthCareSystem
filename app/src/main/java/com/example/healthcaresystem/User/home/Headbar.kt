package com.example.healthcaresystem.User.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthcaresystem.User.home.model.HeadbarIcon

//@Composable
//fun HeadbarScreen() {
//    Column() {
//        Headbar(
//            icon1 = R.drawable.menu_icon,
//            icon2 = R.drawable.doctor,
//            icon3 = R.drawable.time_icon,
//        )
//    }
//}
@Composable
fun Headbar(
    icon1: HeadbarIcon,
    icon2: HeadbarIcon,
    icon3: HeadbarIcon,
) {
    Box(
        modifier = Modifier
            .height(60.dp)
            .background(color = Color.Yellow)
            .fillMaxWidth()
    ) {
        // Icon bên trái
        Image(
            painter = painterResource(id = icon1.iconRes),
            contentDescription = "Logo Icon",
            modifier = Modifier
                .size(50.dp)
                .padding(start = 10.dp, top = 7.dp)
        )

        // Icon ở giữa
        Image(
            painter = painterResource(id = icon2.iconRes),
            contentDescription = "Center Icon",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center)
        )

        // Icon bên phải với văn bản "Lịch hẹn"
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
                .clickable { },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = icon3.iconRes),
                contentDescription = "Time Icon",
                modifier = Modifier.padding(top = 8.dp).size(30.dp)
            )
            Text(
                text = "Lịch hẹn",
                fontSize = 12.sp,
                color = Color.Cyan
            )
        }
    }
}
