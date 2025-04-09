package com.hellodoc.healthcaresystem.user.home.startscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hellodoc.healthcaresystem.R

@Composable
@Preview(showBackground = true)
fun AppointmentListScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Chờ khám", "Khám xong", "Đã huỷ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00F1F8)) // Cyan header
    ) {
        // Title
        Text(
            text = "Danh sách lịch hẹn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 48.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Content background with rounded top
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Card sample (can add LazyColumn if needed for many)
            if (selectedTab == 0) {
                AppointmentCard()
            }
        }
    }
}

@Composable
fun AppointmentCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("31 tháng 3, 2025", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.doctor), // Place your doctor image in res/drawable
                    contentDescription = "Doctor Avatar",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Dương Văn Lực", fontWeight = FontWeight.Bold)
                    Text("Sức khoẻ sinh sản")
                    Text(
                        text = "🏥 Bệnh viện Đại học Y dược TP. HCM",
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { /* UI-only */ },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Huỷ")
                }
                Button(
                    onClick = { /* UI-only */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Chỉnh sửa", color = Color.White)
                }
            }
        }
    }
}
