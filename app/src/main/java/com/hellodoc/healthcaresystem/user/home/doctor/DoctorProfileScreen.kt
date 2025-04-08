package com.hellodoc.healthcaresystem.user.home.doctor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.home.doctor.ui.theme.HealthCareSystemTheme

class DoctorProfileScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen()
        }
    }
}

@Composable
fun ProfileScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00BCD4)) // Màu nền xanh lam giống trong hình
    ) {
        item {
            // Header với hình ảnh và thông tin tổng quan
            headerSection()
            // Phần giới thiệu
            IntroSection()
            // Phần đánh giá từ người dùng
            ReviewSection()
            // Phần bài viết từ dịch vụ
            PostSection()
        }
    }
}

@Composable
fun headerSection(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.doctor),
            contentDescription = "doctor avatar",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.Yellow)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Khoa đẹp trai",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Đẹp trai lắm nha",
            fontSize = 14.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Thông tin tổng quan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "10 năm", value = "Kinh nghiệm")
            StatItem(label = "€969", value = "Bệnh nhân")
            StatItem(label = "37K", value = "Đánh giá")
        }
    }
}


@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color.White
        )
    }
}

@Composable
fun IntroSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Giới thiệu",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Được sĩ với 10 năm kinh nghiệm trong các vấn đề sức khỏe sinh sản và sinh lý. Chuyện điều trị rối loạn sinh lý, thoát dương sớm và các vấn đề sức khỏe của bạn.",
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ReviewSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar người đánh giá
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_info_details), // Thay bằng ảnh thực tế
                contentDescription = "Reviewer Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Nguyễn Thị Cà",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Đánh giá sao
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFC107), // Màu vàng cho sao
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "6 giờ trước",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bác sĩ biết lắng nghe và giải thích rõ ràng. Sẽ giới thiệu cho bạn bè!",
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun PostSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Bài viết đã đăng",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hình ảnh bài viết
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_info_details), // Thay bằng ảnh thực tế
                contentDescription = "Post Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Khoa đẹp trai",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Em bé đẹp trai lâu năm, đi đâu ai cũng nhìn, xin hỏi có cách chữa không ạ!",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            // Nút "Xem thêm"
            Text(
                text = "...",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

data class Review(
    val reviewerName: String,
    val rating: Int,
    val timeAgo: String,
    val comment: String
)

data class Post(
    val title: String,
    val content: String
)