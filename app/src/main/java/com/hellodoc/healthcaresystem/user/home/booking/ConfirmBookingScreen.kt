package com.hellodoc.healthcaresystem.user.home.booking

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.home.HomeActivity

@Composable
fun ConfirmBookingScreen(context: Context, navHostController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TopBar(title = "Xác nhận lịch hẹn khám", onClick = { navHostController.popBackStack() })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Xác nhận lại thông tin:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.padding(start = 8.dp)) {
                InfoText(label = "Họ và tên Bệnh nhân:", value = "Nguyễn Văn Tèo")
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Bác sĩ đặt khám:", value = "Dương Văn Lực")
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Ngày đặt khám:", value = "31/03/2025")
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Giờ đặt khám:", value = "08:00")
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Địa chỉ khám:", value = "Khám tại phòng khám")
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Mã dịch vụ:", value = "342h59wrt7")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tổng phí
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tổng phí phải trả:", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.weight(1f))
                Text("0đ", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút hủy và xác nhận
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { /* TODO: Hủy */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Huỷ")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Xác nhận", color = Color.White)
                }
            }
            // Hiển thị Dialog khi bấm nút
            if (showDialog) {
                Dialog(onDismissRequest = {
                    showDialog = false
                    navigateToAppointment(context)
                }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .background(Color.White, shape = RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color(0xFFB2DFDB), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_confirmed),
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Đặt lịch thành công!",
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    showDialog = false
                                    navigateToAppointment(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A0E21)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("OK", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Hàm điều hướng sang HomeActivity và chỉ định mở tab "appointment"
private fun navigateToAppointment(context: Context) {
    val intent = Intent(context, HomeActivity::class.java).apply {
        putExtra("navigate-to", "appointment")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    context.startActivity(intent)
}

@Composable
fun InfoText(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
//        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmBookingScreenPreview() {
    val fakeNavController = rememberNavController()
    val context = LocalContext.current
    ConfirmBookingScreen(context, fakeNavController)
}

