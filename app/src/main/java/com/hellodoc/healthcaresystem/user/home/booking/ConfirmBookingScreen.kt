package com.hellodoc.healthcaresystem.user.home.booking

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.user.home.HomeActivity
import com.hellodoc.healthcaresystem.user.home.model.TopBar
import com.hellodoc.healthcaresystem.user.home.model.consultationMethod
import com.hellodoc.healthcaresystem.user.home.model.date
import com.hellodoc.healthcaresystem.user.home.model.doctorId
import com.hellodoc.healthcaresystem.user.home.model.doctorName
import com.hellodoc.healthcaresystem.user.home.model.patientID
import com.hellodoc.healthcaresystem.user.home.model.reason
import com.hellodoc.healthcaresystem.user.home.model.time
import com.hellodoc.healthcaresystem.user.home.model.totalCost
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel

@SuppressLint("UnrememberedMutableState")
@Composable
fun ConfirmBookingScreen(context: Context, navHostController: NavHostController) {
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val appointmentViewModel: AppointmentViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                AppointmentViewModel(sharedPreferences)
            }
        }
    )
    var notes by mutableStateOf("")
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.get<String>("notes")?.let {
            notes = it
        }
    }

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
                InfoText(label = "Bác sĩ đặt khám:", value = doctorName)
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Ngày đặt khám:", value = date)
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Giờ đặt khám:", value = time)
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
                Text("$totalCost đ", fontWeight = FontWeight.Bold)
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
                    onClick = {
                        appointmentViewModel.createAppointment(
                            CreateAppointmentRequest(
                                doctorID = doctorId,
                                patientID = patientID,
                                date = date,
                                time = time,
                                //status = status,
                                consultationMethod = consultationMethod,
                                notes = notes,
                                reason = reason
                                //totalCost = totalCost
                            )
                        )
                        showDialog = true },
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
    val context = LocalContext.current
    val fakeNavController = rememberNavController()
    ConfirmBookingScreen(context, fakeNavController)
}

