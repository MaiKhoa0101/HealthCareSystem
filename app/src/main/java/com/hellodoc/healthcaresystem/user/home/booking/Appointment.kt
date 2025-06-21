package com.hellodoc.healthcaresystem.user.home.booking

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.UpdateAppointmentRequest
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.animation.core.*
import com.hellodoc.core.common.skeletonloading.SkeletonBox

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentListScreen(sharedPreferences: SharedPreferences, navHostController: NavHostController) {
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
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        navBackStackEntry?.lifecycle?.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    appointmentViewModel.getAppointmentUser(userId!!)
                }
            }
        )
    }

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

    AppointmentScreenUI(
        appointmentsUser = appointmentsUser,
        userId,
        sharedPreferences = sharedPreferences,
        navHostController = navHostController,
        appointmentViewModel = appointmentViewModel
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentScreenUI(
    appointmentsUser: List<AppointmentResponse>,
    userID: String,
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    appointmentViewModel: AppointmentViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val appointmentsDoc by appointmentViewModel.appointmentsDoctor.collectAsState()

    val roles = listOf("Đã đặt", "Được đặt")
    val tabs = listOf("Chờ khám", "Khám xong", "Đã huỷ")
    val jwt = remember {
        try {
            JWT(sharedPreferences.getString("access_token", null) ?: "")
        } catch (e: Exception) {
            null
        }
    }
    val userRole = jwt?.getClaim("role")?.asString() ?: "user"
    val isPatient = userRole == "user" || userRole == "patient"
    val isDoctor = userRole == "doctor"
    var roleSelectedTab by remember { mutableStateOf(if (isDoctor) 1 else 0) }
    val appointmentUpdated by appointmentViewModel.appointmentUpdated.collectAsState()
    val isLoading by appointmentViewModel.isLoading.collectAsState()

    LaunchedEffect(appointmentUpdated, appointmentsDoc) {
        if (appointmentUpdated && appointmentsDoc.any { it.status == "done" }) {
            selectedTab = 1 //chuyển tab khi dữ liệu done đã cập nhật
            appointmentViewModel.resetAppointmentUpdated()
        }
    }

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
            //Tabs chọn Vai trò
            TabRow(
                selectedTabIndex = roleSelectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.Black,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                roles.forEachIndexed { index, title ->
                    val isTabEnabled = when {
                        isPatient -> index == 0
                        isDoctor -> true
                        else -> false
                    }

                    Tab(
                        selected = roleSelectedTab == index,
                        onClick = {
                            if (isTabEnabled) {
                                roleSelectedTab = index
                            }
                        },
                        enabled = isTabEnabled,
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (roleSelectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (isTabEnabled) Color.Black else Color.Gray
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


            if (isLoading) {
                // Hiển thị skeleton giả lập 5 item
                LazyColumn {
                    items(5) {
                        AppointmentSkeletonItem()
                    }
                }
            } else {
                // ✅ Hiển thị
                LazyColumn {
                    items(filteredAppointments) { appointment ->
                        AppointmentCard(
                            appointment,
                            userID,
                            selectedTab = selectedTab,
                            roleSelectedTab = roleSelectedTab,
                            sharedPreferences = sharedPreferences,
                            navHostController = navHostController,
                            appointmentViewModel = appointmentViewModel,
                            onDoneConfirmed = { selectedTab = 1 }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCard(
    appointment: AppointmentResponse,
    userID: String,
    selectedTab: Int,
    roleSelectedTab: Int,
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    appointmentViewModel: AppointmentViewModel,
    onDoneConfirmed: () -> Unit
) {
    val isPatient = roleSelectedTab == 0
    val isDoctor = roleSelectedTab == 1
    val avatarUrl = if (isDoctor) null else appointment.doctor.avatarURL
    val displayName = if (isDoctor) appointment.patient.name else appointment.doctor.name


    val formattedDate = try {
        ZonedDateTime.parse(appointment.date)
            .withZoneSameInstant(java.time.ZoneId.systemDefault())
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        try {
            LocalDate.parse(appointment.date)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            "Ngày không hợp lệ"
        }
    }

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
                    "$formattedDate | ${appointment.time}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.LightGray, CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.doctor),
                        contentDescription = displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.LightGray, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(displayName, fontWeight = FontWeight.Bold)
                    Text(appointment.notes ?: "Không có ghi chú")

                    Row {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Hospital Location",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(appointment.location ?: "Địa điểm không xác định")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isDoctor) {
                    if (selectedTab == 0) {
                        OutlinedButton(onClick = {
                            appointmentViewModel.cancelAppointment(appointment.id, userID)
                        }) {
                            Text("Hủy")
                        }
                        Button(onClick = {
                            appointmentViewModel.confirmAppointmentDone(appointment.id, userID)
                        }) {
                            Text("Hoàn thành", color = Color.White)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedButton(onClick = {
                                appointmentViewModel.deleteAppointment(appointment.id, userID)
                            }) {
                                Text("Xóa")
                            }
                        }
                    }
                }
                else if (isPatient) {
                    if (selectedTab == 0) { // Chờ khám
                        OutlinedButton(
                            onClick = { appointmentViewModel.cancelAppointment(appointment.id, userID) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                        ) {
                            Text("Huỷ")
                        }
                        Button(
                            onClick = {
                                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("isEditing", true)
                                    set("appointmentId", appointment.id)
                                    set("doctorId", appointment.doctor.id)
                                    set("doctorName", appointment.doctor.name)
                                    set("specialtyName", appointment.doctor.specialty ?: "")
                                    set("selected_date", formattedDate)
                                    set("selected_time", appointment.time)
                                    set("notes", appointment.notes ?: "")
                                    set("location", appointment.location ?: "")
                                }
                                navHostController.navigate("appointment-detail")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("Chỉnh sửa", color = Color.White)
                        }
                    }
                    else if (selectedTab == 1 || selectedTab == 2) { // Khám xong hoặc Đã huỷ
                        OutlinedButton(
                            onClick = { appointmentViewModel.deleteAppointment(appointment.id, userID) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                        ) {
                            Text("Xóa")
                        }
                        Button(
                            onClick = {
                                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("isEditing", false)
                                    set("doctorId", appointment.doctor.id)
                                    set("doctorName", appointment.doctor.name)
                                    set("specialtyName", appointment.doctor.specialty ?: "")
                                    set("location", appointment.location ?: "")
                                }
                                navHostController.navigate("appointment-detail")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("Đặt lại", color = Color.White)
                        }
                        Button(
                            onClick = {
                                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("doctorId", appointment.doctor.id)
                                    set("selectedTab", 1)
                                }
                                navHostController.navigate("other_user_profile")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242760))
                        ) {
                            Text("Đánh giá", color = Color.White)
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun AppointmentSkeletonItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // ngày giờ
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth(0.4f),
            height = 20.dp,
            shape = RoundedCornerShape(4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            // avatar tròn
            SkeletonBox(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                height = 90.dp,
                shape = CircleShape
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                // tên
                SkeletonBox(
                    modifier = Modifier
                        .width(150.dp),
                    height = 20.dp,
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // ghi chú
                SkeletonBox(
                    modifier = Modifier
                        .width(180.dp),
                    height = 16.dp,
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                // địa điểm
                SkeletonBox(
                    modifier = Modifier
                        .width(120.dp),
                    height = 16.dp,
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2 nút
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SkeletonBox(
                modifier = Modifier
                    .width(100.dp),
                height = 36.dp,
                shape = RoundedCornerShape(8.dp)
            )
            SkeletonBox(
                modifier = Modifier
                    .width(100.dp),
                height = 36.dp,
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}


