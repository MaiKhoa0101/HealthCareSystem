package com.parkingSystem.parkingSystem.user.home.parking

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalParking
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
import com.parkingSystem.parkingSystem.viewmodel.ParkingViewModel
import com.parkingSystem.parkingSystem.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun ParkingBookingDetailScreen(
    context: Context,
    navHostController: NavHostController
) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val parkingViewModel: ParkingViewModel = viewModel(factory = viewModelFactory {
        initializer { ParkingViewModel(sharedPreferences) }
    })

    val scope = rememberCoroutineScope()

    // Trạng thái loading và thông báo
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    // Biến trạng thái kiểm tra đã load xong data chưa
    var isDataLoaded by remember { mutableStateOf(false) }

    // Thông tin bãi đậu xe và slot
    var park_id by remember { mutableStateOf("") }
    var park_name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var price by remember { mutableStateOf(0.0) }
    var type_vehicle by remember { mutableStateOf("") }

    var slotName by remember { mutableStateOf("") }
    var slotPosX by remember { mutableStateOf(0) }
    var slotPosY by remember { mutableStateOf(0) }

    // Thông tin người dùng
    var userId by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf("") }
    var userAddress by remember { mutableStateOf("") }
    var vehicleNumber by remember { mutableStateOf("") }

    // Ghi chú
    var notes by remember { mutableStateOf("") }

    // Lấy thông tin user
    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
        userName = userViewModel.getUserAttributeString("name")
        userPhone = userViewModel.getUserAttributeString("phone")
        userAddress = userViewModel.getUserAttributeString("address")
    }

    // Lấy thông tin từ backstack
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle

    LaunchedEffect(Unit) {
        // Lấy thông tin park
        savedStateHandle?.get<String>("park_id")?.let { park_id = it }
        savedStateHandle?.get<String>("park_name")?.let { park_name = it }
        savedStateHandle?.get<String>("address")?.let { address = it }
        savedStateHandle?.get<Double>("price")?.let { price = it }
        savedStateHandle?.get<String>("type_vehicle")?.let { type_vehicle = it }

        // Lấy thông tin slot
        savedStateHandle?.get<String>("slotName")?.let { slotName = it }
        savedStateHandle?.get<Int>("slotPosX")?.let { slotPosX = it }
        savedStateHandle?.get<Int>("slotPosY")?.let { slotPosY = it }

        isDataLoaded = true
        println("Park: $park_id - $park_name")
        println("Slot: $slotName ($slotPosX, $slotPosY)")
    }

    // Dialog thông báo
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                if (isSuccess) {
                    navHostController.popBackStack()
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    if (isSuccess) {
                        navHostController.popBackStack()
                    }
                }) {
                    Text("OK")
                }
            },
            title = { Text(if (isSuccess) "Thành công" else "Thông báo") },
            text = { Text(dialogMessage) }
        )
    }
    println("isDataLoaded: $isDataLoaded \nuserId: $userId \nparkId: $park_id")
    if (isDataLoaded && userId.isNotBlank() && park_id.isNotBlank()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopBar(
                    title = "Chi tiết đặt chỗ",
                    onClick = { navHostController.popBackStack() }
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Thông tin bãi đậu xe
                    item {
                        ParkingInfoSection(
                            parkName = park_name,
                            parkAddress = address,
                            parkTypeVehicle = type_vehicle
                        )
                    }

                    // Thông tin vị trí đậu xe
                    item {
                        SlotInfoSection(
                            slotName = slotName,
                            slotPosX = slotPosX,
                            slotPosY = slotPosY
                        )
                    }

                    // Thông tin người đặt
                    item {
                        UserInfoSection(
                            userName = userName,
                            userPhone = userPhone,
                            vehicleNumber = vehicleNumber,
                            onVehicleNumberChange = { vehicleNumber = it }
                        )
                    }

                    // Ghi chú
                    item {
                        NoteSection(
                            notes = notes,
                            onNoteChange = { notes = it }
                        )
                    }

                    // Tổng chi phí
                    item {
                        FeeSummarySection(
                            parkPrice = price,
                            parkTypeVehicle = type_vehicle
                        )
                    }

                    // Nút đặt chỗ
                    item {
                        BookParkingButton(
                            vehicleNumber = vehicleNumber,
                            isLoading = isLoading,
                            onBookClick = {
                                if (vehicleNumber.isBlank()) {
                                    dialogMessage = "Vui lòng nhập biển số xe"
                                    isSuccess = false
                                    showDialog = true
                                } else {
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            // Gọi API updateSlot
                                            parkingViewModel.bookSlot(
                                                parkId = park_id,
                                                slot_name = slotName,
                                            )

                                            isLoading = false
                                            isSuccess = true
                                            dialogMessage = "Đặt chỗ thành công!"
                                            showDialog = true
                                        } catch (e: Exception) {
                                            isLoading = false
                                            isSuccess = false
                                            dialogMessage = "Đặt chỗ thất bại: ${e.message}"
                                            showDialog = true
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(title: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .height(56.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable { onClick() }
        )

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ParkingInfoSection(
    parkName: String,
    parkAddress: String,
    parkTypeVehicle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocalParking,
                contentDescription = "Parking Icon",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 12.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Column {
                Text("Bãi đậu xe", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(parkName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(parkAddress, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Loại xe: $parkTypeVehicle", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun SlotInfoSection(
    slotName: String,
    slotPosX: Int,
    slotPosY: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Vị trí đậu xe", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Số vị trí:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                    Text(slotName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Tọa độ:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                    Text("($slotPosX, $slotPosY)", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun UserInfoSection(
    userName: String,
    userPhone: String,
    vehicleNumber: String,
    onVehicleNumberChange: (String) -> Unit
) {
    var showDetailDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
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
                    modifier = Modifier.clickable { showDetailDialog = true }
                )
            }
        }

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
                Text("Thông tin người đặt:", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow("Họ và tên:", userName)
                InfoRow("Điện thoại:", userPhone)

                Spacer(modifier = Modifier.height(12.dp))
                Text("Biển số xe:", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = vehicleNumber,
                    onValueChange = onVehicleNumberChange,
                    placeholder = { Text("Nhập biển số xe") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }
    }

    if (showDetailDialog) {
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            title = { Text("Chi tiết thông tin") },
            text = {
                Column {
                    InfoRow("Họ và tên:", userName)
                    InfoRow("Điện thoại:", userPhone)
                    InfoRow("Biển số xe:", vehicleNumber.ifEmpty { "Chưa nhập" })
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSection(notes: String, onNoteChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(text = "Ghi chú:", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = { onNoteChange(it) },
            placeholder = { Text("Nhập ghi chú (nếu có)...") },
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
fun FeeSummarySection(
    parkPrice: Double,
    parkTypeVehicle: String
) {
    CardSection(title = "Chi phí đậu xe") {
        InfoRow("Giá/giờ", "${String.format("%,.0f", parkPrice)}đ")
        InfoRow("Loại xe", parkTypeVehicle)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        InfoRow(
            "Tổng tiền",
            "${String.format("%,.0f", parkPrice)}đ",
            valueColor = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BookParkingButton(
    vehicleNumber: String,
    isLoading: Boolean,
    onBookClick: () -> Unit
) {
    Button(
        onClick = onBookClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Text(
                text = "Đặt chỗ ngay",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
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