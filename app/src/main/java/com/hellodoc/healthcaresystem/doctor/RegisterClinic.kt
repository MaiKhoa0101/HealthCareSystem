package com.hellodoc.healthcaresystem.doctor

import android.media.audiofx.AudioEffect.Descriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.personal.ChangeAvatar
import com.hellodoc.healthcaresystem.user.personal.InputEditField

@Composable
fun RegisterClinic(navHostController: NavHostController){
    Scaffold(
        topBar = { HeadbarResClinic(navHostController) },
    ) { paddingValues -> // paddingValues được truyền vào content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(10.dp)
        ) {
            item {
                ContentEditResDoctor()
            }
        }
    }
}

@Composable
fun HeadbarResClinic(navHostController:NavHostController){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Cyan)
            .padding(20.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_back),
            contentDescription = "nút lùi về",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(30.dp)
                .clickable { navHostController.navigate("personal") }
        )
        Text(
            text = "Đăng kí phòng khám",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun ContentEditResDoctor(){
    var CCCDText by remember { mutableStateOf("") }
    var GPHNText by remember { mutableStateOf("") }
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxHeight()
    )
    {
        Text(
            "Chức năng này sẽ cho phép bạn mở và quảng bá phòng khám, dịch vụ của bạn đến các người dùng khác của hệ thống. \n" +
                    "Hãy điền các thông tin dưới đây, hãy đảm bảo hình ảnh bạn gửi rõ ràng và không qua chỉnh sửa"
        )

        Spacer(modifier = Modifier.height(10.dp))

        InputEditField(
            "Mã số căn cước công dân",
            CCCDText, { CCCDText = it }, "05xxxxxxxxx"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column {
            Text(
                "Ảnh chụp căn cước công dân",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
                )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                InputImage("Ảnh CCCD mặt trước")
                InputImage("Ảnh CCCD mặt sau")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column {
            Text(
                "Ảnh chụp mặt bạn",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
                )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                InputImage("Ảnh mặt bạn")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        InputEditField(
            "Mã giấy phép hành nghề do bộ y tế cấp",
            GPHNText,
            { CCCDText = it },
            "Nguyễn Văn A"
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column {
            Text(
                "Ảnh chụp mặt bạn",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                InputImage("Ảnh giấy phép hành nghề")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Tôi cam kết những thông tin tôi đăng là đúng và sẵn sàng chịu trách nhiệm trước pháp luật\n" +
                "\n" +
                "Việc bạn thực hiện đăng kí phòng khám trên ứng dụng đồng nghĩa với việc bạn phải tuân thủ theo chính sách về bác sĩ sử dụng dịch vụ trên hệ thống, chi tiết xem tại đây.\n")
    }
}

@Composable
fun InputImage(description: String) {
    Box(
        modifier = Modifier
            .padding(vertical = 16.dp)

    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier.clickable {
                    // TODO: Open image picker or dialog to change avatar
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.blankfield),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
                Icon(
                    painter = painterResource(R.drawable.camera),
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                )
            }
            Text(description)
        }

    }
}

