package com.hellodoc.healthcaresystem.blindview.userblind.home.booking

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.CreateAppointmentRequest
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
//import com.hellodoc.healthcaresystem.user.home.doctor.doctorName
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.viewmodel.NotificationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@Composable
fun ConfirmBookingScreen(context: Context, navHostController: NavHostController) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val appointmentViewModel: AppointmentViewModel = hiltViewModel()

    val notificationViewModel: NotificationViewModel = hiltViewModel()

    var notes by remember { mutableStateOf("") }
    var examinationMethod by remember { mutableStateOf("") }
    var doctorId by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("") }
    var doctorAddress by remember { mutableStateOf("") }
    var specialtyName by remember { mutableStateOf("") }
    var patientID by remember { mutableStateOf("") }
    var patientName by remember { mutableStateOf("") }
    var patientPhone by remember { mutableStateOf("") }
    var patientAddress by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var totalCost by remember { mutableStateOf("0") }
    var reason by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var patientModel by remember { mutableStateOf("") }
    var appointmentId by remember { mutableStateOf("") }
    var hasHomeService by remember { mutableStateOf(false) }

    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.get<String>("notes")?.let {
            reason = it
        }

        savedStateHandle?.get<String>("examinationMethod")?.let {
            examinationMethod = it
        }

        savedStateHandle?.get<String>("date")?.let {
            date = it
        }

        savedStateHandle?.get<String>("time")?.let {
            time = it
        }

        savedStateHandle?.get<String>("doctorId")?.let {
            doctorId = it
        }

        savedStateHandle?.get<String>("doctorName")?.let {
            doctorName = it
        }

        savedStateHandle?.get<String>("doctorAddress")?.let {
            doctorAddress = it
        }

        savedStateHandle?.get<String>("specialtyName")?.let {
            specialtyName = it
        }

        savedStateHandle?.get<String>("patientID")?.let {
            patientID = it
        }

        savedStateHandle?.get<String>("patientName")?.let {
            patientName = it
        }

        savedStateHandle?.get<String>("patientPhone")?.let {
            patientPhone = it
        }

        savedStateHandle?.get<String>("patientAddress")?.let {
            patientAddress = it
        }

        savedStateHandle?.get<String>("patientModel")?.let {
            patientModel = it
        }

        savedStateHandle?.get<String>("totalCost")?.let {
            totalCost = it
        }

        savedStateHandle?.get<String>("location")?.let {
            location = it
        }

        savedStateHandle?.get<Boolean>("hasHomeService")?.let {
            hasHomeService = it
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    val appointmentSuccess by appointmentViewModel.appointmentSuccess.collectAsState()

    LaunchedEffect(appointmentSuccess) {
        if (appointmentSuccess) {
            showDialog = true
            notificationViewModel.createNotification(userId = patientID, userModel = patientModel, type = "ForAppointment", content = "Bạn đã đặt lịch khám thành công với bác sĩ $doctorName", navigatePath = "appointment")
            notificationViewModel.createNotification(userId = doctorId, userModel = "Doctor", type = "ForAppointment", content = "Bạn có lịch khám mới với bệnh nhân $patientName", navigatePath = "appointment")
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
                InfoText(label = "Lời nhắn cho bác sĩ:", value = reason)
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Huỷ")
                }

                Spacer(modifier = Modifier.width(16.dp))

                val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                val formattedDate = if (date.isNotBlank()) {
                    val parsedDate = LocalDate.parse(date, inputFormatter)
                    parsedDate.format(outputFormatter)
                } else {
                    ""
                }

                Button(
                    onClick = {
                        val token = sharedPreferences.getString("access_token", null)
                        appointmentViewModel.createAppointment(
                            token,
                            CreateAppointmentRequest(
                                doctorID = doctorId,
                                patientID = patientID,
                                patientModel = patientModel,
                                date = formattedDate,
                                time = time,
                                examinationMethod = examinationMethod,
                                notes = reason,
                                reason = reason,
                                totalCost = totalCost,
                                location = location
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Xác nhận", color = MaterialTheme.colorScheme.onBackground)
                }
            }
                // Hiển thị Dialog khi bấm nút
            if (showDialog) {
                Dialog(onDismissRequest = {
                    showDialog = false
                    navigateToAppointment(context, navHostController)
                }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
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
                                    navigateToAppointment(context, navHostController)
                                          },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("OK", color = MaterialTheme.colorScheme.background)
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
private fun navigateToAppointment(context: Context, navHostController: NavHostController) {
    navHostController.navigate("appointment") {
        // Xóa tất cả các entry trong backstack cho đến startDestination
        popUpTo(navHostController.graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true // tránh tạo lại nếu đang ở đó
        restoreState = false   // không khôi phục trạng thái cũ
    }
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

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview(showBackground = true)
//@Composable
//fun ConfirmBookingScreenPreview() {
//    val context = LocalContext.current
//    val fakeNavController = rememberNavController()
//    ConfirmBookingScreen(context, fakeNavController)
//}

