package com.hellodoc.healthcaresystem.blindview.userblind.home.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R

@Composable
fun ExamplePost(){
    val sizeButton = 28.dp
    var isFavorited by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Chào mừng đến với hello doc, đây là bài viết mẫu để giới thiệu về ứng dụng hello doc.")
        Spacer(modifier=Modifier.height(10.dp))
        Image(
            painter = painterResource(id = R.drawable.post_example),
            contentDescription = "Đây là ảnh minh họa, ảnh có nội dung là 1 bác sĩ đang nói chuyện trước màn hình.",
            modifier = Modifier
        )
    }

}

@Composable
fun ExamplePost1(){
    val sizeButton = 28.dp
    var isFavorited by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Tổng thống Donal Trump vừa tái đắc cử")
        Spacer(modifier=Modifier.height(10.dp))
        Image(
            painter = painterResource(id = R.drawable.donald_trump),
            contentDescription = "Đây là ảnh về Donald Trump vừa tái đắc cử",
            modifier = Modifier
        )
    }

}