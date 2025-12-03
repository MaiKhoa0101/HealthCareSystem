package com.hellodoc.healthcaresystem.view.user.home.booking

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.requestmodel.UpdateAppointmentRequest
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.roomDb.data.dao.AppointmentDao
//import com.hellodoc.healthcaresystem.user.home.doctor.doctorAvatar
//import com.hellodoc.healthcaresystem.user.home.doctor.doctorName
//import com.hellodoc.healthcaresystem.user.home.doctor.specialtyName
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateForServer(input: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(input, inputFormatter).format(outputFormatter)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentDetailScreen(
    context: Context,
    navHostController: NavHostController
) {

    println(" appointment detail render duoc")
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userViewModel: UserViewModel = hiltViewModel()

    // Biến trạng thái kiểm tra đã load xong data chưa
    var isDataLoaded by remember { mutableStateOf(false) }
    // Biến kiểm tra xem đang ở chế độ chỉnh sửa hay tạo mới
    var isEditing by remember { mutableStateOf(false)}

    // All moved state variables
    var doctorId by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("") }
    var doctorAddress by remember { mutableStateOf("") }
    var doctorAvatar by remember { mutableStateOf("") }
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

    LaunchedEffect(Unit) {
        patientName = userViewModel.getUserAttribute("name",context)
        println("patientName" + patientName)
        patientPhone = userViewModel.getUserAttribute("phone",context)
        println("patientPhone" + patientPhone)
        patientAddress = userViewModel.getUserAttribute("address",context)
        println("patientAddress" + patientAddress)
        patientID = userViewModel.getUserAttribute("userId",context)
        println("patientId" + patientID)
        patientModel = userViewModel.getUserAttribute("role",context)
    }

//    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
//    val selectedDateLiveData = navHostController.currentBackStackEntry
//        ?.savedStateHandle
//        ?.getLiveData<String>("selected_date")
//
//    val selectedDateState = selectedDateLiveData?.observeAsState()
//    val selectedDate = selectedDateState?.value ?: remember { mutableStateOf("") }.value
//
//    val selectedTimeLiveData = navHostController.currentBackStackEntry
//        ?.savedStateHandle
//        ?.getLiveData<String>("selected_time")
//
//    val selectedTimeState = selectedTimeLiveData?.observeAsState()
//    val selectedTime = selectedTimeState?.value ?: ""
//
//    LaunchedEffect(selectedDate, selectedTime) {
//        if (selectedDate.isNotEmpty()) date = selectedDate
//        if (selectedTime.isNotEmpty()) time = selectedTime
//    }

    // Lấy ngày giờ từ backstack
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    val selectedDateState = navHostController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("selected_date")
        ?.observeAsState()
    val selectedTimeState = navHostController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("selected_time")
        ?.observeAsState()

    LaunchedEffect(selectedDateState?.value, selectedTimeState?.value) {
        selectedDateState?.value?.let { date = it }
        selectedTimeState?.value?.let { time = it }
    }

    LaunchedEffect(Unit) {
        // Kiểm tra xem có đang ở chế độ chỉnh sửa không
        savedStateHandle?.get<Boolean>("isEditing")?.let { isEditing = it }
        savedStateHandle?.get<String>("appointmentId")?.let { appointmentId = it }
        savedStateHandle?.get<String>("doctorId")?.let { doctorId = it }
        savedStateHandle?.get<String>("doctorName")?.let { doctorName = it }
        savedStateHandle?.get<String>("doctorAddress")?.let { doctorAddress = it }
        savedStateHandle?.get<String>("doctorAvatar")?.let { doctorAvatar = it }
        savedStateHandle?.get<String>("specialtyName")?.let { specialtyName = it }
        savedStateHandle?.get<String>("notes")?.let { reason = it }
        savedStateHandle?.get<String>("location")?.let { location = it }
        savedStateHandle?.get<Boolean>("hasHomeService")?.let { hasHomeService = it }

        savedStateHandle?.get<Boolean>("isEditing")?.let {
            isEditing = it
            if (isEditing) {
                // Nếu đang chỉnh sửa, lấy ID của lịch hẹn
                savedStateHandle.get<String>("appointmentId")?.let {
                    appointmentId = it
                }
            }
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
        savedStateHandle?.get<String>("doctorAvatar")?.let {
            doctorAvatar = it
        }
        savedStateHandle?.get<String>("specialtyName")?.let {
            specialtyName = it
        }
        savedStateHandle?.get<String>("notes")?.let {
            reason = it
        }
        savedStateHandle?.get<String>("location")?.let {
            location = it
        }
        savedStateHandle?.get<Boolean>("hasHomeService")?.let {
            hasHomeService = it
        }

        isDataLoaded = true
    }

    if (isDataLoaded && patientID.isNotBlank() && doctorId.isNotBlank()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val examinationMethod = remember { mutableStateOf("") }
//            var notes by remember {
//                mutableStateOf(savedStateHandle?.get<String>("notes") ?: "")
//            }
            var notes by remember { mutableStateOf(reason) }

            val title = if (isEditing) "Chỉnh sửa lịch hẹn khám" else "Chi tiết lịch hẹn khám"

            TopBar(title = title, onClick = { navHostController.popBackStack() })
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    DoctorInfoSection(
                        doctorName = doctorName,
                        doctorAvatar = doctorAvatar,
                        specialtyName = specialtyName
                    )
                }
                item {
                    PatientInfoSection(
                        patientName = patientName,
                        patientPhone = patientPhone
                    )
                }

                item {
                    VisitMethodSection(
                        examinationMethod = examinationMethod,
                        doctorAddress = doctorAddress,
                        patientAddress = patientAddress,
                        hasHomeService = hasHomeService
                    )
                }
                item {
                    AppointmentDateSection(
                        navHostController = navHostController,
                        doctorId = doctorId,
                        date = date,
                        time = time,
                        isEditing = isEditing,
                        appointmentId = appointmentId
                    )
                }


                item {
                    NoteToDoctorSection(notes, onNoteChange = { notes = it })
                }

                item {
                    FeeSummarySection()
                }

                item {
                    if (isEditing) {
                        UpdateButton(
                            navHostController = navHostController,
                            examinationMethod = examinationMethod,
                            notes = notes,
                            appointmentId = appointmentId,
                            patientID = patientID,
                            date = date,
                            time = time,
                        )
                    } else {
                        BookButton(
                            navHostController = navHostController,
                            sharedPreferences = sharedPreferences,
                            examinationMethod = examinationMethod,
                            notes = notes,
                            date = date,
                            time = time,
                            doctorId = doctorId,
                            doctorName = doctorName,
                            doctorAddress = doctorAddress,
                            specialtyName = specialtyName,
                            patientID = patientID,
                            patientName = patientName,
                            patientPhone = patientPhone,
                            patientAddress = patientAddress,
                            patientModel = patientModel,
                            totalCost = totalCost,
                            location = location,
                            hasHomeService = hasHomeService
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateButton(
    navHostController: NavHostController,
    examinationMethod: MutableState<String>,
    notes: String,
    appointmentId: String,
    patientID: String,
    date: String,
    time: String,
) {
    val appointmentViewModel: AppointmentViewModel = hiltViewModel()
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Thiếu thông tin") },
            text = { Text(dialogMessage) }
        )
    }

    Button(
        onClick = {
            when {
                examinationMethod.value.isBlank() -> {
                    dialogMessage = "Vui lòng chọn hình thức khám"
                    showDialog = true
                }
                date.isBlank() || time.isBlank() -> {
                    dialogMessage = "Vui lòng chọn ngày giờ khám"
                    showDialog = true
                }
                else -> {
                    val updateRequest = UpdateAppointmentRequest(
                        date = formatDateForServer(date),
                        time = time,
                        notes = notes
                    )

                    appointmentViewModel.updateAppointment(
                        appointmentId = appointmentId,
                        appointmentData = updateRequest,
                        patientID = patientID
                    )
                    navHostController.popBackStack("appointment", inclusive = false)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Text("Cập nhật lịch hẹn")
    }
    Spacer(modifier = Modifier.height(60.dp))
}

@Composable
fun TopBar(title: String,onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
//            .statusBarsPadding()
            .height(56.dp)
    ) {
        // Nút quay lại
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable { onClick() }
        )

        // Tiêu đề ở giữa
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DoctorInfoSection(
    doctorName: String,
    doctorAvatar: String,
    specialtyName: String
) {
    println("doctor info render duoc")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(doctorAvatar),
                contentDescription = "anh bac si",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Bác sĩ", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(doctorName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(specialtyName, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun PatientInfoSection(
    patientName: String,
    patientPhone: String
) {
    var showDetailDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
        // Phần nền dưới
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Xem chi tiết",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable {
                        showDetailDialog = true
                    }
                )
            }
        }

        // Box thông tin
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Đặt lịch khám này cho:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow("Họ và tên:", patientName)
                InfoRow("Giới tính:", "Nam")
                InfoRow("Ngày sinh:", "11/12/2000")
                InfoRow("Điện thoại:", patientPhone)
            }
        }
    }

    if (showDetailDialog) {
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            title = { Text("Chi tiết hồ sơ") },
            text = {
                Column {
                    InfoRow("Họ và tên:", patientName)
                    InfoRow("Giới tính:", "Nam")
                    InfoRow("Ngày sinh:", "11/12/2000")
                    InfoRow("Điện thoại:", patientPhone)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = false }) {
                    Text("Đóng")
                }
            }
        )
    }
}

@Composable
fun VisitMethodSection(
    examinationMethod: MutableState<String>,
    doctorAddress: String,
    patientAddress: String,
    hasHomeService: Boolean
) {
    println("visit method render duoc")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Phương thức khám", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Khám tại phòng khám
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (examinationMethod.value == "at_clinic") MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
                )
                .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(10.dp))
                .clickable { examinationMethod.value = "at_clinic" }
                .padding(12.dp)
                .height(70.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Khám tại phòng khám", fontWeight = FontWeight.Bold)
            Row {
                Text("Địa chỉ:", fontSize = 13.sp)
                Spacer(modifier = Modifier.width(5.dp))
                Text(doctorAddress, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (hasHomeService && patientAddress != "Chưa có địa chỉ") {
            // Khám tại nhà
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (examinationMethod.value == "at_home") MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
                    )
                    .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(10.dp))
                    .clickable { examinationMethod.value = "at_home" }
                    .height(100.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Khám tại nhà", fontWeight = FontWeight.Bold)
                    Row {
                        Text("Địa chỉ:", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(patientAddress, fontSize = 13.sp)
                    }
                }
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun AppointmentDateSection(
    navHostController: NavHostController,
    doctorId: String,
    date: String,
    time: String,
    isEditing: Boolean,
    appointmentId: String
) {
    println("appointment date render duoc")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = "Ngày khám", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 12.dp, vertical = 14.dp)
                .clickable {
                    navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("isEditing", isEditing)
                        if (isEditing) set("appointmentId", appointmentId)
                    }
                    navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                        set("doctorId", doctorId)
                    }
                    navHostController.navigate("booking-calendar") {
                        navHostController.previousBackStackEntry?.savedStateHandle?.apply {
                            set("isEditing", isEditing)
                            if (isEditing) set("appointmentId", appointmentId)
                        }
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(time)
                Spacer(modifier = Modifier.width(16.dp))
                Text(date)
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteToDoctorSection(notes: String, onNoteChange: (String) -> Unit) {
    println("note doctor render duoc")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = "Lời nhắn cho bác sĩ:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { onNoteChange(it) },
            placeholder = { Text("Nhập lời nhắn...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer
            )
        )
    }
}


@Composable
fun FeeSummarySection() {
    println(" fee sumary render duoc")
    CardSection(title = "Chi phí khám tại phòng khám") {
        InfoRow("Voucher dịch vụ", "0đ", MaterialTheme.colorScheme.error)
        InfoRow("Giá dịch vụ", "0đ")
        InfoRow("Tạm tính giá tiền", "0đ", fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookButton(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    examinationMethod: MutableState<String>,
    notes: String,
    date: String,
    time: String,
    doctorId: String,
    doctorName: String,
    doctorAddress: String,
    specialtyName: String,
    patientID: String,
    patientName: String,
    patientPhone: String,
    patientAddress: String,
    patientModel: String,
    totalCost: String,
    location: String,
    hasHomeService: Boolean
) {
    println("book btn render duoc")
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text("Thiếu thông tin") },
            text = { Text(dialogMessage) }
        )
    }

    Button(
        onClick = {
            when {
                examinationMethod.value.isBlank() -> {
                    dialogMessage = "Vui lòng chọn hình thức khám"
                    showDialog = true
                }
                date.isBlank() || time.isBlank() -> {
                    dialogMessage = "Vui lòng chọn ngày giờ khám"
                    showDialog = true
                }
                else -> {
                    navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                        // Thông tin lịch hẹn
                        set("examinationMethod", examinationMethod.value)
                        set("notes", notes)
                        set("date", date)
                        set("time", time)

                        // Thông tin bác sĩ
                        set("doctorId", doctorId)
                        set("doctorName", doctorName)
                        set("doctorAddress", doctorAddress)
                        set("specialtyName", specialtyName)

                        // Thông tin bệnh nhân
                        set("patientID", patientID)
                        set("patientName", patientName)
                        set("patientPhone", patientPhone)
                        set("patientAddress", patientAddress)
                        set("patientModel", patientModel)

                        // Thông tin khác
                        set("totalCost", totalCost)
                        set("location", location)
                        set("hasHomeService", hasHomeService)
                    }
                    navHostController.navigate("booking-confirm")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(
            text = "Đặt dịch vụ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
    Spacer(modifier = Modifier.height(60.dp))
}


@Composable
fun CardSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onBackground,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Text(
            text = value,
            color = valueColor,
            fontWeight = fontWeight,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            softWrap = true
        )
    }
}

