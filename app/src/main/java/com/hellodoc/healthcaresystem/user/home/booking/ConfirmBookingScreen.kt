package com.hellodoc.healthcaresystem.user.home.booking

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.hellodoc.healthcaresystem.user.post.userId
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.viewmodel.NotificationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@Composable
fun ConfirmBookingScreen(context: Context, navHostController: NavHostController) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val appointmentViewModel: AppointmentViewModel = viewModel( factory = viewModelFactory {
            initializer { AppointmentViewModel(sharedPreferences) }
        })

    val notificationViewModel: NotificationViewModel = viewModel(factory = viewModelFactory {
        initializer { NotificationViewModel(sharedPreferences) }
    })

    var notes by remember { mutableStateOf("") }
    var examinationMethod by remember { mutableStateOf("") }
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.get<String>("notes")?.let {
            notes = it
        }
        savedStateHandle?.get<String>("examinationMethod")?.let {
            examinationMethod = it
        }

    }

    var showDialog by remember { mutableStateOf(false) }
    val appointmentSuccess by appointmentViewModel.appointmentSuccess.collectAsState()

    LaunchedEffect(appointmentSuccess) {
        if (appointmentSuccess) {
            showDialog = true // ✅ khi thành công thì hiển thị dialog
            notificationViewModel.createNotification(userId = patientID, userModel = patientModel, content = "Bạn đã đặt lịch khám thành công với bác sĩ $doctorName")
            notificationViewModel.createNotification(userId = doctorId, userModel = "Doctor", content = "Bạn có lịch khám mới với bệnh nhân $patientName")
        }
    }

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
                InfoText(label = "Họ và tên Bệnh nhân:", value = patientName)
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Bác sĩ đặt khám:", value = doctorName)
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Ngày đặt khám:", value = date)
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Giờ đặt khám:", value = time)
                Spacer(modifier = Modifier.height(24.dp))
                var method = ""
                if (examinationMethod == "at_clinic") {
                    method = "Khám tại phòng khám"
                    location = doctorAddress
                } else {
                    method = "Khám tại nhà"
                    location = patientAddress
                }
                InfoText(label = "Phương thức khám:", value = method)
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Địa chỉ khám:", value = location)
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Mã dịch vụ:", value = "342h59wrt7")
                Spacer(modifier = Modifier.height(24.dp))
                InfoText(label = "Tổng phí phải trả:", value = "$totalCost đ")
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

                val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                val date = LocalDate.parse(date, inputFormatter)
                val formattedDate = date.format(outputFormatter)

                Button(
                    onClick = {
                        appointmentViewModel.createAppointment(
                            CreateAppointmentRequest(
                                doctorID = doctorId,
                                patientID = patientID,
                                patientModel = patientModel,
                                date = formattedDate,
                                time = time,
//                                status = status,
                                examinationMethod = examinationMethod,
                                notes = notes,
                                reason = reason,
                                totalCost = totalCost,
                                location = location
                            )
                        )
                              },
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
                                    appointmentViewModel.resetAppointmentSuccess()
                                    navigateToAppointment(context)
                                          },
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

            val appointmentError by appointmentViewModel.appointmentError.collectAsState()

            if (appointmentError != null) {
                AlertDialog(
                    onDismissRequest = {
                        appointmentViewModel.resetAppointmentError()
                    },
                    confirmButton = {
                        Button(onClick = {
                            appointmentViewModel.resetAppointmentError()
                            navHostController.popBackStack()
                        }) {
                            Text("OK")
                        }
                    },
                    title = { Text("Đặt lịch thất bại") },
                    text = { Text(appointmentError ?: "") }
                )
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
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically // Đảm bảo các phần tử căn chỉnh theo chiều dọc
    ) {
        // Label sẽ ở sát bên trái
        Text(text = label, modifier = Modifier.weight(1f))

        // Value sẽ ở sát bên phải và có thể xuống dòng nếu cần
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .weight(1f) // Đảm bảo phần tử này chiếm không gian còn lại
                .fillMaxWidth(), // Đảm bảo value sử dụng hết không gian có sẵn
            maxLines = Int.MAX_VALUE, // Cho phép value xuống dòng khi dài
            overflow = TextOverflow.Clip // Không dùng ba chấm mà chỉ cắt trực tiếp nếu không vừa
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ConfirmBookingScreenPreview() {
    val context = LocalContext.current
    val fakeNavController = rememberNavController()
    ConfirmBookingScreen(context, fakeNavController)
}

