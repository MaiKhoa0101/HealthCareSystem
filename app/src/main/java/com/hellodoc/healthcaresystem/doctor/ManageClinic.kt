package com.hellodoc.healthcaresystem.doctor

import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.PhotoCamera
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
import com.hellodoc.healthcaresystem.requestmodel.ModifyClinicRequest
import com.hellodoc.healthcaresystem.responsemodel.GetSpecialtyResponse
import com.hellodoc.healthcaresystem.responsemodel.ServiceOutput
import com.hellodoc.healthcaresystem.responsemodel.ServiceInput
import com.hellodoc.healthcaresystem.responsemodel.WorkHour
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel
import java.nio.file.WatchEvent


@Composable
fun EditClinicServiceScreen(sharedPreferences: SharedPreferences, navHostController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeadbarEditClinic(navHostController)
        }
    ) { innerPadding ->
        BodyEditClinicServiceScreen(modifier = Modifier.padding(innerPadding), sharedPreferences,navHostController)
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
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { navHostController.navigate("personal") }
        )
        Text(
            text = "Chỉnh sửa phòng khám",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BodyEditClinicServiceScreen(modifier:Modifier, sharedPreferences: SharedPreferences, navHostController:NavHostController) {

    // Khởi tạo ViewModel bằng custom factory để truyền SharedPreferences
    val doctorViewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })

    val specialtyViewModel: SpecialtyViewModel = viewModel(factory = viewModelFactory {
        initializer { SpecialtyViewModel(sharedPreferences) }
    })

    val specialty by specialtyViewModel.specialties.collectAsState()

    LaunchedEffect(Unit) {
        specialtyViewModel.fetchSpecialties()
    }

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
    var oldSchedule by remember { mutableStateOf(listOf<WorkHour>())}
    var servicesInput by remember { mutableStateOf<List<ServiceInput>>(emptyList()) }
    var servicesCreated by remember { mutableStateOf(listOf<ServiceOutput>()) }

    var newSchedule by remember { mutableStateOf<List<WorkHour>>(emptyList()) }


    // Gọi API để fetch user từ server
    LaunchedEffect(doctorId) {
        doctorId?.let {
            doctorViewModel.fetchDoctorById(it)
        }

    }
    // Lấy dữ liệu doctor từ StateFlow
    val doctor by doctorViewModel.doctor.collectAsState()


    var selectedSpecializationId by remember { mutableStateOf("") }
    var selectedSpecializationName by remember { mutableStateOf("") }
    var imageService by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var minprice by remember { mutableStateOf("") }
    var maxprice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var hasHomeService by remember { mutableStateOf(false) }
    var isClinicPaused by remember { mutableStateOf(false) }

    println("Dữ liệu đã sửa schedule: "+newSchedule)
    println("Dữ liệu đã sửa service: "+servicesInput)
    println("Dữ liệu đã sửa address: "+address)

//    if (doctor == null) return
//    else {
//        servicesCreated = doctor!!.services
//        oldSchedule = doctor!!.workHour
//        address = doctor?.address ?: ""
//        hasHomeService = doctor?.hasHomeService ?: false
//        isClinicPaused = doctor?.isClinicPaused ?: false
//    }
    LaunchedEffect(doctor) {
        if (doctor == null) return@LaunchedEffect
        else {
            servicesCreated = doctor?.services ?: emptyList()
            oldSchedule = doctor?.workHour ?: emptyList()
            address = doctor?.address ?: ""
            hasHomeService = doctor?.hasHomeService ?: false
            isClinicPaused = doctor?.isClinicPaused ?: false
        }

    }

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
                SpecializationSection(
                    allSpecialties = specialty,
                    selectedSpecializationId = selectedSpecializationId,
                    selectedSpecialization = selectedSpecializationName,
                    onSpecializationSelected = { id, name ->
                        selectedSpecializationId = id
                        selectedSpecializationName = name
                    }
                )
                Spacer(Modifier.height(16.dp))
                ServiceImagePicker(imageService) { pickedUri ->
                    imageService = pickedUri
                }

                Spacer(Modifier.height(16.dp))

                PriceRangeInput(
                    priceFrom = minprice,
                    priceTo = maxprice,
                    onFromChanged = { minprice = it },
                    onToChanged = { maxprice = it }
                )

                Spacer(Modifier.height(16.dp))

                DescriptionInput(description) { description = it }

                Spacer(Modifier.height(8.dp))

                AddServiceButton {
                    if (selectedSpecializationId.isNotBlank()
                        && selectedSpecializationName.isNotBlank()
                        && minprice.isNotBlank()
                        && maxprice.isNotBlank()
                    ) {
                        val newInputs = addService(
                            selectedSpecializationId,
                            selectedSpecializationName,
                            imageService,
                            minprice,
                            maxprice,
                            description,
                            servicesInput,
                        )

                        servicesInput = newInputs

                        println("So service da them: $servicesInput")

                        // Reset form
                        selectedSpecializationId = ""
                        selectedSpecializationName = ""
                        imageService = emptyList()
                        minprice = ""
                        maxprice = ""
                        description = ""
                    }
                }


                Spacer(Modifier.height(12.dp))
                ServiceTags(servicesCreated) { removed ->
                    val (newInputs, newOutputs) = removeService(removed, servicesInput, servicesCreated)
                    servicesInput = newInputs
                    servicesCreated = newOutputs
                    println("service input: "+newInputs)
                    println("service input: "+servicesInput)
                    println("service output: "+newOutputs)
                    println("service output: "+servicesCreated)
                }
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(thickness = 2.dp, color = Color.Gray)
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                AddressSection(
                    address = address,
                    onAddressChange = { address = it },
                    hasHomeService = hasHomeService,
                    onHomeServiceChange = { hasHomeService = it }
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(thickness = 2.dp, color = Color.Gray)

                WorkScheduleSection(
                    schedule = oldSchedule,
                    onDelete = { time ->
                        oldSchedule = removeTime(time, oldSchedule)
                    }
                )
                Spacer(Modifier.height(8.dp))
                TimePickerSection(
                    tempSchedule = newSchedule,
                    onAddTime = { newHour ->
                        newSchedule = newSchedule + newHour
                    }
                )
                Spacer(Modifier.height(8.dp))
                ClinicPauseSwitch(
                    isPaused = isClinicPaused,
                    onTogglePause = { isClinicPaused = it }
                )
                HorizontalDivider(thickness = 2.dp, color = Color.Gray)

                Box(modifier = Modifier.fillMaxSize()) {
                    SaveFloatingButton (
                        imageService,
                        newSchedule,
                        oldSchedule,
                        servicesInput,
                        servicesCreated,
                        address,
                        description,
                        hasHomeService,
                        isClinicPaused,
                        doctorViewModel,
                        doctorId!!,
                        navHostController
                    )
                }
            }
        }
    }
}

fun addService(
    id: String,
    name: String,
    imageUris: List<Uri>,
    min: String,
    max: String,
    desc: String,
    servicesInput: List<ServiceInput>
): List<ServiceInput> {
    val newServiceInput = ServiceInput(
        specialtyId = id,
        specialtyName = name,
        imageService = imageUris,
        minprice = min,
        maxprice = max,
        description = desc
    )
    return servicesInput + newServiceInput
}

fun removeService(
    toRemove: ServiceOutput,
    servicesInput: List<ServiceInput>,
    servicesCreated: List<ServiceOutput>
): Pair<List<ServiceInput>, List<ServiceOutput>> {
    val updatedOutputs = servicesCreated.filterNot {
        it.specialtyId == toRemove.specialtyId &&
                it.specialtyName == toRemove.specialtyName &&
                it.description == toRemove.description
    }

    val updatedInputs = servicesInput.filterNot {
        it.specialtyId == toRemove.specialtyId &&
                it.specialtyName == toRemove.specialtyName &&
                it.description == toRemove.description
    }

    return Pair(updatedInputs, updatedOutputs)
}


@Composable
fun SaveFloatingButton(
    imageUris: List<Uri>,
    schedule: List<WorkHour>,
    oldSchedule: List<WorkHour>,
    servicesInput: List<ServiceInput>,
    servicesCreated: List<ServiceOutput>,
    address: String,
    description: String,
    hasHomeService: Boolean,
    isClinicPaused: Boolean,
    doctorViewModel: DoctorViewModel,
    doctorID: String,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    println("Service sau khi xoa: $servicesCreated")
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Button(
            onClick = {
                val clinicUpdateData = ModifyClinicRequest(
                    address = address,
                    description = description,
                    workingHours = schedule,
                    oldWorkingHours = oldSchedule,
                    services = servicesInput,
                    oldServices = servicesCreated,
                    images = imageUris,
                    hasHomeService = hasHomeService,
                    isClinicPaused = isClinicPaused
                )
                println (address)
                doctorViewModel.updateClinic(clinicUpdateData, doctorID, context)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
//                .align(Alignment.Center)
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00C5CB),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),

        ) {
            Text(
                text = "Lưu thông tin phòng khám",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        val updateSuccess = doctorViewModel.updateSuccess
        LaunchedEffect(updateSuccess) {
            if (updateSuccess == true) {
                navHostController.navigate("personal")
                doctorViewModel.resetUpdateStatus()
            }
        }
    }
}


@Composable
fun SpecializationSection(
    allSpecialties: List<GetSpecialtyResponse>,
    selectedSpecializationId: String,
    selectedSpecialization: String,
    onSpecializationSelected: (String, String) -> Unit // ✅ sửa callback
) {
    AutoCompleteSpecialization(
        allSpecializations = allSpecialties,
        selectedName = selectedSpecialization,
        onSpecializationSelected = onSpecializationSelected // ✅ truyền đủ id, name
    )
}



@Composable
fun ServiceImagePicker(imageUris: List<Uri>, onImagesPicked: (List<Uri>) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        onImagesPicked(uris)
    }

    Column {
        Button(onClick = { launcher.launch("image/*") }) {
            Icon(Icons.Default.PhotoCamera, contentDescription = null)
            Text("Chọn ảnh")
        }

        LazyRow {
            items(imageUris) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(80.dp),
                    contentScale = ContentScale.Crop
                )
            }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .clickable(onClick = onAdd),
        horizontalArrangement = Arrangement.Center
    ){
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text("Thêm dịch vụ")
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}
@Composable
fun ServiceTags(
    services: List<ServiceOutput>,
    onRemove: (ServiceOutput) -> Unit,
) {
    Column {
        Text("Các dịch vụ đã thêm", fontWeight = FontWeight.Bold)

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
}


@Composable
fun AddressSection(
    address: String,
    onAddressChange: (String) -> Unit,
    hasHomeService: Boolean,
    onHomeServiceChange: (Boolean) -> Unit,
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
                checked = hasHomeService,
                onCheckedChange = onHomeServiceChange
            )
            Text("Có dịch vụ khám tại nhà")
        }
    }
}

@Composable
fun WorkScheduleSection(
    schedule: List<WorkHour>,
    onDelete: (WorkHour) -> Unit // thêm callback
) {
    val weekdays = listOf("TH 2", "TH 3", "TH 4", "TH 5", "TH 6", "TH 7", "CN")
    val scheduleByDay = schedule.groupBy { it.dayOfWeek }
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
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable(enabled = time != null) {
                                    time?.let { onDelete(it) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = time?.let {
                                    "${it.hour}:${it.minute.toString().padStart(2, '0')}"
                                } ?: "",
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
}


fun removeTime(timeDelete: WorkHour, schedule: List<WorkHour>): List<WorkHour> {
    return schedule.filterNot {
        it.dayOfWeek == timeDelete.dayOfWeek &&
                it.hour == timeDelete.hour &&
                it.minute == timeDelete.minute
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
fun ClinicPauseSwitch(
    isPaused: Boolean,
    onTogglePause: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Switch(
            checked = isPaused,
            onCheckedChange = onTogglePause,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Red,
                checkedTrackColor = Color.Gray,
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Tạm ngưng phòng khám")
    }
}

@Composable
fun AutoCompleteSpecialization(
    allSpecializations: List<GetSpecialtyResponse>,
    selectedName: String,
    onSpecializationSelected: (id: String, name: String) -> Unit
) {
    var query by remember { mutableStateOf(selectedName) }
    var expanded by remember { mutableStateOf(false) }

    // Lọc danh sách theo tên chuyên khoa
    val filtered = remember(query, allSpecializations) {
        if (query.isBlank()) emptyList()
        else allSpecializations.filter {
            it.name.contains(query, ignoreCase = true)
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
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            filtered.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        query = item.name
                        onSpecializationSelected(item.id, item.name) // ✅ Trả về cả id và name
                        expanded = false
                    }
                )
            }
        }
    }
}
