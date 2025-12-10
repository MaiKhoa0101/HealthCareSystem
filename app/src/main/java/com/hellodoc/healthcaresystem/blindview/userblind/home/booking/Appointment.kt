package com.hellodoc.healthcaresystem.blindview.userblind.home.booking

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AppointmentResponse
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import com.hellodoc.core.common.skeletonloading.SkeletonBox
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentListScreen(navHostController: NavHostController) {

    val userViewModel: UserViewModel = hiltViewModel()
    val appointmentViewModel: AppointmentViewModel = hiltViewModel()
    val appointmentsUser by appointmentViewModel.appointmentsUser.collectAsState()
    val appointmentsDoc by appointmentViewModel.appointmentsDoctor.collectAsState()
    val context = LocalContext.current
    val userId = userViewModel.getUserAttribute("userId", context)
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
        navHostController = navHostController
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentScreenUI(
    appointmentsUser: List<AppointmentResponse>,
    userID: String,
    navHostController: NavHostController
) {
    val appointmentViewModel: AppointmentViewModel = hiltViewModel()
    var selectedTab by remember { mutableStateOf(0) }
    val appointmentsDoc by appointmentViewModel.appointmentsDoctor.collectAsState()
    val context = LocalContext.current
    val roles = listOf("Đã đặt", "Được đặt")
    val tabs = listOf("Chờ khám", "Khám xong", "Đã huỷ")
    val userViewModel: UserViewModel = hiltViewModel()
    val userRole = userViewModel.getUserAttribute("role", context)
    val isPatient = userRole == "User"
    val isDoctor = userRole == "Doctor"
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
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Text(
            text = "Danh sách lịch hẹn",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .padding(top = 48.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
        ) {
            //Tabs chọn Vai trò
            TabRow(
                selectedTabIndex = roleSelectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
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
                                color = if (isTabEnabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.secondaryContainer
                            )
                        }
                    )
                }
            }

            // Tabs chọn Trạng thái
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
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

            val appointmentsRole = when (roleSelectedTab) {
                0 -> appointmentsUser // Vai trò "Đã đặt" (User đặt lịch)
                1 -> appointmentsDoc  // Vai trò "Được đặt" (Doctor được đặt lịch)
                else -> appointmentsUser
            }

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
                LazyColumn {
                    items(filteredAppointments) { appointment ->
                        AppointmentCard(
                            appointment,
                            userID,
                            selectedTab = selectedTab,
                            roleSelectedTab = roleSelectedTab,
                            navHostController = navHostController,
                            appointmentViewModel = appointmentViewModel
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
    navHostController: NavHostController,
    appointmentViewModel: AppointmentViewModel,
) {
    val isPatient = roleSelectedTab == 0
    val isDoctor = roleSelectedTab == 1
    val avatarUrl = if (isDoctor) null else appointment.doctor.avatarURL
    val displayName = if (isDoctor) appointment.patient.name else appointment.doctor.name
    val noteForDoctor = appointment.notes


    val formattedDate = try {
        ZonedDateTime.parse(appointment.date)
            .withZoneSameInstant(ZoneId.systemDefault())
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
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
                            .border(1.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.doctor),
                        contentDescription = displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(displayName, fontWeight = FontWeight.Bold)
                    Text(noteForDoctor ?: "Không có ghi chú")

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
                            Text("Hoàn thành", color = MaterialTheme.colorScheme.background)
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
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
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
                                    set("doctorAddress", appointment.location )
                                    set("doctorAvatar", appointment.doctor.avatarURL)
                                    set("specialtyName", appointment.doctor.specialty ?: "")
                                    set("selected_date", formattedDate)
                                    set("selected_time", appointment.time)
                                    set("notes", appointment.notes ?: "")
                                    set("location", appointment.location)
                                }
                                navHostController.navigate("appointment-detail")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                        ) {
                            Text("Chỉnh sửa", color = MaterialTheme.colorScheme.background)
                        }
                    }
                    else if (selectedTab == 1 ) { // Khám xong hoặc Đã huỷ
                        OutlinedButton(
                            onClick = { appointmentViewModel.deleteAppointment(appointment.id, userID) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                        ) {
                            Text("Xóa")
                        }
                        Button(
                            onClick = {
                                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("isEditing", false)
                                    set("doctorId", appointment.doctor.id)
                                    set("doctorName", appointment.doctor.name)
                                    set("doctorAvatar", appointment.doctor.avatarURL)
                                    set("doctorAddress", appointment.location)
                                    set("notes", appointment.notes)
                                    set("specialtyName", appointment.doctor.specialty ?: "")
                                    set("location", appointment.location )
                                }
                                navHostController.navigate("appointment-detail")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                        ) {
                            Text("Đặt lại", color = MaterialTheme.colorScheme.background)
                        }
                        Button(
                            onClick = {
                                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("doctorId", appointment.doctor.id)
                                    set("isRating", true)
                                    set("selectedTab", 1)
                                }
                                navHostController.navigate("other_user_profile")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                        ) {
                            Text("Đánh giá", color = MaterialTheme.colorScheme.background)
                        }
                    }
                    else if ( selectedTab == 2){
                        OutlinedButton(
                            onClick = { appointmentViewModel.deleteAppointment(appointment.id, userID) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                        ) {
                            Text("Xóa")
                        }
                        Button(
                            onClick = {
                                navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                    set("isEditing", false)
                                    set("doctorId", appointment.doctor.id)
                                    set("doctorName", appointment.doctor.name)
                                    set("doctorAvatar", appointment.doctor.avatarURL)
                                    set("doctorAddress", appointment.location)
                                    set("specialtyName", appointment.doctor.specialty ?: "")
                                    set("location", appointment.location )
                                }
                                navHostController.navigate("appointment-detail")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
                        ) {
                            Text("Đặt lại", color = MaterialTheme.colorScheme.background)
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
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth(0.4f)
                .height(20.dp),
            shape = RoundedCornerShape(4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            SkeletonBox(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                shape = CircleShape
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                SkeletonBox(
                    modifier = Modifier
                        .width(150.dp)
                        .height(20.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonBox(
                    modifier = Modifier
                        .width(180.dp)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                SkeletonBox(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SkeletonBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp)
            )
            SkeletonBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}
