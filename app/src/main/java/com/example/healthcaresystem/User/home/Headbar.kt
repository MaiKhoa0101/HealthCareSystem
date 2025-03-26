package com.example.healthcaresystem.user.home

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
import com.example.healthcaresystem.R

@Composable
fun Headbar() {
    Box(
        modifier = Modifier
            .height(60.dp)
            .background(color = Color.Yellow)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.menu_icon),
            contentDescription = "Logo Icon",
            modifier = Modifier
                .size(50.dp)
                .padding(start = 10.dp, top = 7.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.doctor),
            contentDescription = "Logo Icon",
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.Center)
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
                .clickable {  },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.time_icon),
                contentDescription = "Time Icon",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = "Lịch hẹn",
                fontSize = 12.sp,
                color = Color.Cyan
            )
        }
    }
}