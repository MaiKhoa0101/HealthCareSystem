package com.hellodoc.healthcaresystem.user.home.booking

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentListScreen(sharedPreferences: SharedPreferences) {
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val appointmentViewModel: AppointmentViewModel = viewModel(factory = viewModelFactory {
        initializer { AppointmentViewModel(sharedPreferences) }
    })

    val appointmentsUser by appointmentViewModel.appointmentsUser.collectAsState()
    val appointmentsDoc by appointmentViewModel.appointmentsDoctor.collectAsState()

    val token = sharedPreferences.getString("access_token", null)

    val jwt = remember(token) {
        try {
            JWT(token ?: throw IllegalArgumentException("Token is null"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val userId = jwt?.getClaim("userId")?.asString()

    LaunchedEffect(userId) {
        userId?.let {
            userViewModel.getUser(it)
            appointmentViewModel.getAppointmentUser(it)
            appointmentViewModel.getAppointmentDoctor(it)
        }
    }


    if (userId == null) {
        Text("Token không hợp lệ hoặc userId không tồn tại.")
        return
    }

    AppointmentScreenUI(appointmentsUser = appointmentsUser, appointmentsDoc = appointmentsDoc, userId, sharedPreferences = sharedPreferences )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentScreenUI(
    appointmentsUser: List<AppointmentResponse>,
    appointmentsDoc: List<AppointmentResponse>,
    userID: String,
    sharedPreferences: SharedPreferences
) {
    var roleSelectedTab by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(0) }

    val roles = listOf("Đã đặt", "Được đặt")
    val tabs = listOf("Chờ khám", "Khám xong", "Đã huỷ")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00F1F8))
    ) {
        Text(
            text = "Danh sách lịch hẹn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(top = 48.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
        ) {
            // Tabs chọn Vai trò
            TabRow(
                selectedTabIndex = roleSelectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                roles.forEachIndexed { index, title ->
                    Tab(
                        selected = roleSelectedTab == index,
                        onClick = { roleSelectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (roleSelectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tabs chọn Trạng thái
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

            // ✅ Chọn lịch theo VAI TRÒ
            val appointmentsRole = when (roleSelectedTab) {
                0 -> appointmentsUser // Vai trò "Đã đặt" (User đặt lịch)
                1 -> appointmentsDoc  // Vai trò "Được đặt" (Doctor được đặt lịch)
                else -> appointmentsUser
            }

            // ✅ Lọc theo TRẠNG THÁI
            val filteredAppointments = when (selectedTab) {
                0 -> appointmentsRole.filter { it.status == "pending" }
                1 -> appointmentsRole.filter { it.status == "done" }
                2 -> appointmentsRole.filter { it.status == "cancelled" }
                else -> appointmentsRole
            }

            // ✅ Hiển thị
            LazyColumn {
                items(filteredAppointments) { appointment ->
                    AppointmentCard(appointment, userID, sharedPreferences = sharedPreferences )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCard(appointment: AppointmentResponse, userID: String, sharedPreferences: SharedPreferences) {
    val appointmentViewModel: AppointmentViewModel = viewModel(factory = viewModelFactory {
        initializer { AppointmentViewModel(sharedPreferences) }
    })

    val formattedDate = ZonedDateTime.parse(appointment.date)
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    formattedDate,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    appointment.time,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.doctor),
                    contentDescription = "Doctor Avatar",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(appointment.doctor.name, fontWeight = FontWeight.Bold)
                    Text(appointment.notes ?: "Không có ghi chú")

                    Row {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Hospital Location",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(appointment.location ?: "Địa điểm không xác định" )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {appointmentViewModel.cancelAppointment(appointment.id, userID)},
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Huỷ")
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Chỉnh sửa", color = Color.White)
                }
            }
        }
    }
}