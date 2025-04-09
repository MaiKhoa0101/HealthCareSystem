package com.hellodoc.healthcaresystem.user.home
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R

@Preview (showBackground = true)
@Composable
fun ProfileUserPage(){
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        ProfileSection()
        PostUser()
    }
}


@Composable
fun ProfileSection(){
    Column(
        modifier = Modifier
        .background(Color.Cyan)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            UserIntroSection()
            Spacer(modifier = Modifier.height(26.dp))
            UserProfileModifierSection()
        }
    }
}

@Composable
fun PostUser(){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                color = Color.Gray,
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "Bài viết đã đăng",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Post()
    }
}



@Composable
fun UserIntroSection(){
    Column (
        modifier = Modifier
        .fillMaxWidth().height(160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ){
        Image(painter = painterResource(R.drawable.doctor), contentDescription = "avt user")
        Text("Mai Nguyễn Đăng Khoa", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("@mndk2015")
    }
}

@Composable
fun UserProfileModifierSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(40.dp).width(150.dp)
        ) {
            Text(
                text = "Chỉnh sửa hồ sơ",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(40.dp).width(150.dp)
        ) {
            Text(
                text = "Quản lý phòng khám",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Composable
fun Post(){
    Text("Bai viet")
}