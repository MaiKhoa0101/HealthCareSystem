package com.hellodoc.healthcaresystem.doctor

import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.auth0.android.jwt.JWT

import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.requestmodel.ModifyClinic
import com.hellodoc.healthcaresystem.responsemodel.ServiceOutput
import com.hellodoc.healthcaresystem.responsemodel.ServiceInput
import com.hellodoc.healthcaresystem.responsemodel.WorkHour
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel


@Composable
fun EditClinicServiceScreen(sharedPreferences: SharedPreferences, navHostController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeadbarEditClinic(navHostController)
        }
    ) { innerPadding ->
        BodyEditClinicServiceScreen(modifier = Modifier.padding(innerPadding), sharedPreferences)
    }
}


@Composable
fun HeadbarEditClinic(navHostController: NavHostController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF00BCD4))
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.arrow_back),
            contentDescription = "Back button",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(28.dp)
                .clickable { navHostController.navigate("personal") }
        )
        Text(
            text = "Chỉnh sửa phòng khám",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BodyEditClinicServiceScreen(modifier:Modifier, sharedPreferences: SharedPreferences) {

    // Khởi tạo ViewModel bằng custom factory để truyền SharedPreferences
    val doctorViewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })


    val token = sharedPreferences.getString("access_token", null)

    val jwt = remember(token) {
        try {
            JWT(token ?: throw IllegalArgumentException("Token is null"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val doctorId = jwt?.getClaim("userId")?.asString()
    var servicesCreated by remember { mutableStateOf(listOf<ServiceOutput>()) }
    var scheduleCreated by remember { mutableStateOf(listOf<WorkHour>())}

    var schedule by remember { mutableStateOf<List<WorkHour>>(emptyList()) }
    var services by remember { mutableStateOf<List<ServiceInput>>(emptyList()) }

    // Gọi API để fetch user từ server
    LaunchedEffect(doctorId) {
        doctorId?.let {
            doctorViewModel.fetchDoctorById(it)
        }

    }
    // Lấy dữ liệu doctor từ StateFlow
    val doctor by doctorViewModel.doctor.collectAsState()
    if (doctor == null) return
    else {
        servicesCreated = doctor!!.services
        scheduleCreated = doctor!!.workHour
    }

    var selectedSpecialization by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var priceFrom by remember { mutableStateOf("") }
    var priceTo by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var service by remember { mutableStateOf<ServiceInput?>(null) }


    println("Dữ liệu đã sửa schedule: "+schedule)
    println("Dữ liệu đã sửa service: "+services)
    println("Dữ liệu đã sửa address: "+address)

    service = ServiceInput(
        specializationName = selectedSpecialization,
        imageUri = imageUri ?: Uri.EMPTY, // hoặc kiểm tra null phù hợp
        priceFrom = priceFrom,
        priceTo = priceTo,
        description = description
    )

    LazyColumn(
        modifier = modifier
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "Chức năng này sẽ cho phép bạn chỉnh sửa thông tin phòng khám, dịch vụ của bạn",
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(16.dp))
                SpecializationSection()
                Spacer(Modifier.height(16.dp))
                ServiceImagePicker(imageUri) { pickedUri ->
                    imageUri = pickedUri
                }

                Spacer(Modifier.height(16.dp))

                PriceRangeInput(
                    priceFrom = priceFrom,
                    priceTo = priceTo,
                    onFromChanged = { priceFrom = it },
                    onToChanged = { priceTo = it }
                )

                Spacer(Modifier.height(16.dp))

                DescriptionInput(description) { description = it }

                Spacer(Modifier.height(8.dp))

                AddServiceButton {
                    if (selectedSpecialization.isNotBlank() && priceFrom.isNotBlank() && priceTo.isNotBlank()) {
                        val newService = ServiceInput(
                            specializationName = selectedSpecialization,
                            imageUri = imageUri ?: Uri.EMPTY,
                            priceFrom = priceFrom,
                            priceTo = priceTo,
                            description = description
                        )

                        services = (services ?: emptyList()) + newService

                        println("So service da them: "+services)
                        // Xóa input sau khi thêm
                        selectedSpecialization = ""
                        imageUri = null
                        priceFrom = ""
                        priceTo = ""
                        description = ""
                    }
                }


                Spacer(Modifier.height(12.dp))

                ServiceTags(servicesCreated) { removed ->
                    servicesCreated = servicesCreated - removed
                }
            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                AddressSection(
                    address = address,
                    onAddressChange = { address = it },
                )
                Spacer(Modifier.height(8.dp))
                WorkScheduleSection(scheduleCreated)
                Spacer(Modifier.height(8.dp))
                TimePickerSection(
                    tempSchedule = schedule,
                    onAddTime = { newHour ->
                        schedule = schedule + newHour
                    }
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    SaveFloatingButton (
                        schedule,
                        services,
                        address,
                        doctorViewModel,
                        doctorId!!
                    )
                }

            }
        }
    }
}

@Composable
fun SaveFloatingButton(
    schedule: List<WorkHour>,
    services: List<ServiceInput>,
    address: String,
    doctorViewModel: DoctorViewModel,
    doctorID: String
) {
    val context = LocalContext.current
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Button(
            onClick = {
                val clinicUpdateData = ModifyClinic(
                    workingHours = schedule,
                    services = services,
                    address = address
                )
                println ("Clinic data: "+ clinicUpdateData)
                doctorViewModel.updateClinic(clinicUpdateData, doctorID,context)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .align(Alignment.Center)
            ) {
            Text(
                text = "Đặt khám",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}


@Composable
fun SpecializationSection() {
    val allSpecialties = listOf("Nội soi", "Nội tiết", "Nha khoa", "Da liễu", "Tim mạch")
    var selectedSpecialty by remember { mutableStateOf("") }

    AutoCompleteSpecialization(
        allSpecializations = allSpecialties,
        selected = selectedSpecialty,
        onSpecializationSelected = { selectedSpecialty = it }
    )
}

@Composable
fun ServiceImagePicker(imageUri: Uri?, onImagePicked: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onImagePicked(it) }
    }

    Row {
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(1.dp, Color.Gray)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null)
        }

        Spacer(Modifier.width(8.dp))

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun PriceRangeInput(
    priceFrom: String,
    priceTo: String,
    onFromChanged: (String) -> Unit,
    onToChanged: (String) -> Unit
) {
    Column {
        Text("Phí dịch vụ", fontWeight = FontWeight.Bold)
        Row {
            OutlinedTextField(
                value = priceFrom,
                onValueChange = onFromChanged,
                label = { Text("Từ") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = priceTo,
                onValueChange = onToChanged,
                label = { Text("Đến") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@Composable
fun DescriptionInput(description: String, onChange: (String) -> Unit) {
    Column {
        Text("Thông tin giới thiệu", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = description,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddServiceButton(onAdd: () -> Unit) {
    IconButton(onClick = onAdd) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}
@Composable
fun ServiceTags(
    services: List<ServiceOutput>,
    onRemove: (ServiceOutput) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        items(services) { service ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFE0E0E0))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = service.specialtyName,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Xoá",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onRemove(service) },
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}


@Composable
fun AddressSection(
    address: String,
    onAddressChange: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = address,
            onValueChange = { onAddressChange(it) },
            label = { Text("Địa chỉ phòng khám") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = false,
                onCheckedChange = { }
            )
            Text("Có dịch vụ khám tại nhà")
        }
    }
}


@Composable
fun WorkScheduleSection(schedule: List<WorkHour>) {
    val weekdays = listOf("TH 2", "TH 3", "TH 4", "TH 5", "TH 6", "TH 7", "CN")

    // Nhóm theo thứ trong tuần (2-8)
    val scheduleByDay = schedule.groupBy { it.dayOfWeek }

    // Tìm số khung giờ tối đa trong 1 ngày (để lặp theo hàng)
    val maxRows = scheduleByDay.values.maxOfOrNull { it.size } ?: 0

    Text("Lịch hiện tại", fontWeight = FontWeight.SemiBold)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Hàng tiêu đề: TH2 - CN
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekdays.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vẽ từng hàng thời gian (giống như table row)
            for (i in 0 until maxRows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (dayIndex in 2..8) {
                        val times = scheduleByDay[dayIndex] ?: emptyList()
                        val time = times.getOrNull(i)

                        Text(
                            text = time?.let { "${it.hour}:${it.minute.toString().padStart(2, '0')}" } ?: "",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            color = Color(0xFF444444)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun TimePickerSection(
    tempSchedule: List<WorkHour>,
    onAddTime: (WorkHour) -> Unit
) {
    var day by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = day,
            onValueChange = { day = it },
            label = { Text("Thứ") },
            modifier = Modifier.weight(1f),
            placeholder = { Text("2 - 8") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = hour,
            onValueChange = { hour = it },
            label = { Text("Giờ") },
            modifier = Modifier.weight(1f),
            placeholder = { Text("0 - 23") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = minute,
            onValueChange = { minute = it },
            label = { Text("Phút") },
            modifier = Modifier.weight(1f),
            placeholder = { Text("0 - 59") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        IconButton(onClick = {
            val d = day.toIntOrNull()
            val h = hour.toIntOrNull()
            val m = minute.toIntOrNull()

            if (d in 2..8 && h in 0..23 && m in 0..59) {
                val newWorkHour = WorkHour(d!!, h!!, m!!)
                if (newWorkHour !in tempSchedule) {
                    onAddTime(newWorkHour)
                }
                // Reset input
                day = ""
                hour = ""
                minute = ""
            }
        }) {
            Icon(Icons.Default.Add, contentDescription = "Add time")
        }
    }
}


@Composable
fun AutoCompleteSpecialization(
    allSpecializations: List<String>, // giả lập từ DB
    selected: String,
    onSpecializationSelected: (String) -> Unit
) {
    var query by remember { mutableStateOf(selected) }
    var expanded by remember { mutableStateOf(false) }

    val filtered = remember(query) {
        if (query.isBlank()) emptyList()
        else allSpecializations.filter {
            it.contains(query, ignoreCase = true)
        }
    }

    Column {
        Text("Chuyên khoa của bạn", fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Nhập chuyên khoa") },
            singleLine = true
        )

        DropdownMenu(
            expanded = expanded && filtered.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth().background(Color.White)
        ) {
            filtered.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        query = item
                        expanded = false
                        onSpecializationSelected(item)
                    }
                )
            }
        }
    }
}

