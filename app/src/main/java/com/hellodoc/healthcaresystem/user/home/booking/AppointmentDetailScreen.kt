package com.hellodoc.healthcaresystem.user.home.booking

import android.content.Context
import android.content.SharedPreferences

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

var doctorId: String = ""
var doctorName: String = ""
var doctorAddress: String = ""
var specialtyName: String = ""
var patientID: String = ""
var patientName: String = ""
var patientPhone: String = ""
var patientAddress: String = ""
var date: String = "" // Ví dụ: "20/04/2025"
var time: String = "" // Ví dụ: "14:30"
//var status: String = "pending" // pending/confirmed/cancelled
//var examinationMethod: String = "" // "at_clinic" hoặc "at_home"
var totalCost: String = "0"
var reason: String = "hello"
var location: String = ""

@Composable
fun AppointmentDetailScreen(context: Context, onBack: () -> Unit, navHostController: NavHostController) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    // Biến trạng thái kiểm tra đã load xong data chưa
    var isDataLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        patientName = userViewModel.getUserAttributeString("name")
        patientPhone = userViewModel.getUserAttributeString("phone")
        patientAddress = userViewModel.getUserAttributeString("address")
        patientID = userViewModel.getUserAttributeString("userId")
    }

    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
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

        isDataLoaded = true
    }

    if (isDataLoaded && patientID.isNotBlank() && doctorId.isNotBlank()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val examinationMethod = remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }
            TopBar(title = "Chi tiết lịch hẹn khám", onClick = { navHostController.popBackStack() })
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF4F5F7))
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    DoctorInfoSection()
                }

                item {
                    PatientInfoSection()
                }

                item {
                    VisitMethodSection(examinationMethod)
                }

                item {
                    AppointmentDateSection(navHostController)
                }

                item {
                    NoteToDoctorSection(notes, onNoteChange = { notes = it })
                }

                item {
                    FeeSummarySection()
                }

                item {
                    BookButton(navHostController, sharedPreferences, examinationMethod, notes)
                }
            }
        }
    }
}

@Composable
fun TopBar(title: String,onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF00BCD4))
            .statusBarsPadding()
            .height(56.dp)
    ) {
        // Nút quay lại
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable { onClick() }
        )

        // Tiêu đề ở giữa
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}



@Composable
fun DoctorInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.doctor), // thay bằng ảnh thực tế
                contentDescription = "Doctor",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Bác sĩ", fontWeight = FontWeight.Medium, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(doctorName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(specialtyName, color = Color.Gray)

//                Spacer(modifier = Modifier.height(20.dp))
//
//                Text("0 đ / giờ")
            }
        }
    }
}

@Composable
fun PatientInfoSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
        // Phần nền dưới: Xem chi tiết & Sửa hồ sơ
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0))
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
                    color = Color.Black,
                    fontSize = 13.sp
                )

                TextButton(
                    onClick = { /* TODO */ },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color(0xFFDCF5F9),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(6.dp),
                ) {
                    Text("Sửa hồ sơ", fontSize = 13.sp)
                }
            }
        }

        // Box trắng nằm đè lên trên
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
                .padding(vertical = 16.dp) // 👈 padding cho cả shadow + nội dung
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(12.dp))
                    .background(Color.White, RoundedCornerShape(12.dp))
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
}

@Composable
fun VisitMethodSection(examinationMethod:  MutableState<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
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
                    if (examinationMethod.value == "at_clinic") Color(0xFFD2D2D2) else Color(0xFFDDFDFF)
                )
                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                .clickable { examinationMethod.value = "at_clinic" }
                .padding(12.dp)
                .height(70.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Khám tại phòng khám", fontWeight = FontWeight.Bold)
            Row {
                Text(
                    "Địa chỉ:",
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    doctorAddress,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Khám tại nhà
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (examinationMethod.value == "at_home") Color(0xFFD2D2D2) else Color(0xFFDDFDFF)
                )
                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                .clickable { examinationMethod.value = "at_home" }
                .height(100.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier

                .weight(1f)) {
                Text("Khám tại nhà", fontWeight = FontWeight.Bold)
                Row {
                    Text(
                        "Địa chỉ:",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        patientAddress,
                        fontSize = 13.sp
                    )
                }
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}


@Composable
fun AppointmentDateSection(navHostController : NavHostController) {
    val savedStateHandle = navHostController.currentBackStackEntry?.savedStateHandle
//    var selectedDate by remember { mutableStateOf("31/3/2025") }
//    var selectedTime by remember { mutableStateOf("20:00") }

    // Cập nhật giá trị nếu có trong savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.get<String>("selected_date")?.let {
            date = it
        }
        savedStateHandle?.get<String>("selected_time")?.let {
            time = it
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = "Ngày khám", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0))
                .padding(horizontal = 12.dp, vertical = 14.dp)
                .clickable {navHostController.navigate("booking-calendar")},
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
                tint = Color.Black
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteToDoctorSection(notes: String, onNoteChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
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
                .background(Color(0xFFE0E0E0)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}


@Composable
fun FeeSummarySection() {
    CardSection(title = "Chi phí khám tại phòng khám") {
        InfoRow("Voucher dịch vụ", "0đ", Color.Red)
        InfoRow("Giá dịch vụ", "0đ")
        InfoRow("Tạm tính giá tiền", "0đ", fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookButton(navHostController: NavHostController, sharedPreferences: SharedPreferences, examinationMethod: MutableState<String>, notes: String) {
//    val appointmentViewModel: AppointmentViewModel = viewModel(
//        factory = viewModelFactory {
//            initializer {
//                AppointmentViewModel(sharedPreferences)
//            }
//        }
//    )

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



//    FeeSummarySection()

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
                        set("examinationMethod", examinationMethod.value)
                        set("notes", notes)
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
            containerColor = Color(0xFF00C5CB), // Màu nền
            contentColor = Color.White          // Màu chữ
        )
    ) {
        Text("Đặt dịch vụ")
    }
    Spacer(modifier = Modifier.height(60.dp))
}

@Composable
fun CardSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
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
    valueColor: Color = Color.Black,
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

@Preview(name = "Light Mode")
@Composable
fun PreviewAppointmentDetailScreen() {
    val context = LocalContext.current
    val fakeNavController = rememberNavController()
    AppointmentDetailScreen(
        context = context,
        onBack = {},
        navHostController = fakeNavController,
    )
}
