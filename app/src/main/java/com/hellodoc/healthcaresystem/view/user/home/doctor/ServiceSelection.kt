package com.hellodoc.healthcaresystem.view.user.home.doctor

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.hellodoc.healthcaresystem.viewmodel.AppointmentViewModel
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ServiceSelectionScreen(
    navHostController: NavHostController,
    appointmentId: String,
    patientName: String,
    doctorId: String
) {
    val appointmentViewModel: AppointmentViewModel = hiltViewModel()
    val doctorViewModel: DoctorViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    var selectedServices by remember { mutableStateOf(setOf<String>()) }
    var showPayment by remember { mutableStateOf(false) }
    var customPrice by remember { mutableStateOf("") }
    var customNote by remember { mutableStateOf("") }
    var useCustomPrice by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userId = userViewModel.getUserAttribute("userId", context)

    // Lấy thông tin bác sĩ
    val doctor by doctorViewModel.doctor.collectAsState()
    val isLoading by doctorViewModel.isLoading.collectAsState()

    // Load thông tin bác sĩ khi vào màn hình
    LaunchedEffect(doctorId) {
        doctorViewModel.fetchDoctorById(doctorId)
    }

    // Convert services từ doctor
    val doctorServices = remember(doctor) {
        doctor?.services?.map { serviceOutput ->
            // Lấy giá trung bình hoặc giá min
            val avgPrice = try {
                val min = serviceOutput.minprice.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
                val max = serviceOutput.maxprice.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
                if (max > 0) (min + max) / 2 else min
            } catch (e: Exception) {
                0
            }

            Service(
                id = serviceOutput.specialtyId,
                name = serviceOutput.specialtyName,
                price = avgPrice,
                description = serviceOutput.description
            )
        } ?: emptyList()
    }

    // Danh sách dịch vụ chung (fallback nếu bác sĩ không có dịch vụ)
    val commonServices = remember {
        listOf(
            Service("common_1", "Khám tổng quát", 200000, "Khám sức khỏe tổng quát"),
            Service("common_2", "Tái khám", 150000, "Tái khám theo dõi"),
            Service("common_3", "Tư vấn sức khỏe", 100000, "Tư vấn và giải đáp thắc mắc"),
            Service("common_4", "Xét nghiệm cơ bản", 150000, "Xét nghiệm máu, nước tiểu"),
            Service("common_5", "Đo huyết áp", 50000, "Kiểm tra huyết áp"),
            Service("common_6", "Tư vấn dinh dưỡng", 180000, "Tư vấn chế độ ăn uống")
        )
    }

    // Xác định dịch vụ để hiển thị
    val availableServices = remember(doctorServices) {
        if (doctorServices.isNotEmpty()) doctorServices else commonServices
    }

    val hasDoctorServices = doctorServices.isNotEmpty()

    val totalAmount = if (useCustomPrice) {
        customPrice.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0
    } else {
        availableServices
            .filter { selectedServices.contains(it.id) }
            .sumOf { it.price }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (showPayment) "Thanh toán" else "Chọn dịch vụ",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showPayment) {
                            showPayment = false
                        } else {
                            navHostController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Đang tải dịch vụ...",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            !showPayment -> {
                ServiceSelectionContent(
                    modifier = Modifier.padding(padding),
                    services = availableServices,
                    selectedServices = selectedServices,
                    hasDoctorServices = hasDoctorServices,
                    useCustomPrice = useCustomPrice,
                    customPrice = customPrice,
                    customNote = customNote,
                    onServiceToggle = { serviceId ->
                        selectedServices = if (selectedServices.contains(serviceId)) {
                            selectedServices - serviceId
                        } else {
                            selectedServices + serviceId
                        }
                        useCustomPrice = false
                    },
                    onCustomPriceChange = { price ->
                        customPrice = price
                        useCustomPrice = true
                        selectedServices = emptySet()
                    },
                    onCustomNoteChange = { note ->
                        customNote = note
                    },
                    onToggleCustomPrice = {
                        useCustomPrice = !useCustomPrice
                        if (useCustomPrice) {
                            selectedServices = emptySet()
                        } else {
                            customPrice = ""
                            customNote = ""
                        }
                    },
                    totalAmount = totalAmount,
                    onConfirm = {
                        val hasValidSelection = (selectedServices.isNotEmpty() && !useCustomPrice) ||
                                (useCustomPrice && customPrice.replace("[^0-9]".toRegex(), "").isNotEmpty())

                        if (hasValidSelection) {
                            showPayment = true
                        }
                    }
                )
            }
            else -> {
                PaymentScreen(
                    modifier = Modifier.padding(padding),
                    patientName = patientName,
                    services = if (useCustomPrice) {
                        listOf(
                            Service(
                                "custom",
                                customNote.ifEmpty { "Dịch vụ khám bệnh" },
                                customPrice.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0,
                                "Nhập giá tự do"
                            )
                        )
                    } else {
                        availableServices.filter { selectedServices.contains(it.id) }
                    },
                    totalAmount = totalAmount,
                    onComplete = {
                        appointmentViewModel.confirmAppointmentDone(
                            appointmentId = appointmentId,
                            userId = userId!!
                        )
                        navHostController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun ServiceSelectionContent(
    modifier: Modifier = Modifier,
    services: List<Service>,
    selectedServices: Set<String>,
    hasDoctorServices: Boolean,
    useCustomPrice: Boolean,
    customPrice: String,
    customNote: String,
    onServiceToggle: (String) -> Unit,
    onCustomPriceChange: (String) -> Unit,
    onCustomNoteChange: (String) -> Unit,
    onToggleCustomPrice: () -> Unit,
    totalAmount: Int,
    onConfirm: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Banner thông báo nếu không có dịch vụ riêng
            if (!hasDoctorServices) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3CD)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ℹ️",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Bạn chưa thiết lập dịch vụ riêng",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF856404)
                                )
                                Text(
                                    text = "Đang hiển thị dịch vụ chung của phòng khám",
                                    fontSize = 12.sp,
                                    color = Color(0xFF856404)
                                )
                            }
                        }
                    }
                }
            }

            // Toggle giữa chọn dịch vụ và nhập giá tự do
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !useCustomPrice,
                        onClick = { if (useCustomPrice) onToggleCustomPrice() },
                        label = { Text("Chọn từ danh sách") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = if (!useCustomPrice) {
                            {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                    FilterChip(
                        selected = useCustomPrice,
                        onClick = { if (!useCustomPrice) onToggleCustomPrice() },
                        label = { Text("Nhập giá tự do") },
                        modifier = Modifier.weight(1f),
                        leadingIcon = if (useCustomPrice) {
                            {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }
            }

            if (useCustomPrice) {
                // Form nhập giá tự do
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Nhập thông tin dịch vụ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            OutlinedTextField(
                                value = customNote,
                                onValueChange = onCustomNoteChange,
                                label = { Text("Tên dịch vụ") },
                                placeholder = { Text("VD: Khám tổng quát, Tư vấn dinh dưỡng...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                )
                            )

                            OutlinedTextField(
                                value = customPrice,
                                onValueChange = { value ->
                                    // Chỉ cho phép nhập số
                                    if (value.isEmpty() || value.all { it.isDigit() }) {
                                        onCustomPriceChange(value)
                                    }
                                },
                                label = { Text("Số tiền") },
                                placeholder = { Text("VD: 200000") },
                                suffix = { Text("₫") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                                )
                            )

                            if (customPrice.isNotEmpty()) {
                                val amount = customPrice.toIntOrNull() ?: 0
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Số tiền:",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = formatter.format(amount),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Danh sách dịch vụ
                if (services.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📋",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Chưa có dịch vụ nào",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Vui lòng sử dụng tùy chọn 'Nhập giá tự do'",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(services) { service ->
                        ServiceItem(
                            service = service,
                            isSelected = selectedServices.contains(service.id),
                            onToggle = { onServiceToggle(service.id) }
                        )
                    }
                }
            }
        }

        // Bottom bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tổng cộng",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = formatter.format(totalAmount),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = (selectedServices.isNotEmpty() && !useCustomPrice) ||
                                (useCustomPrice && customPrice.isNotEmpty() && customNote.isNotEmpty()),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B),
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "Tiếp tục",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceItem(
    service: Service,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!service.description.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = service.description,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatter.format(service.price),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    Color.Gray.copy(alpha = 0.3f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun PaymentScreen(
    modifier: Modifier = Modifier,
    patientName: String,
    services: List<Service>,
    totalAmount: Int,
    onComplete: () -> Unit
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val qrBitmap = remember(totalAmount) {
        generateQRCode("Thanh toán khám bệnh: ${formatter.format(totalAmount)}")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Bệnh nhân: $patientName",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Chi tiết dịch vụ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    services.forEach { service ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = service.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (!service.description.isNullOrEmpty()) {
                                    Text(
                                        text = service.description,
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        maxLines = 1
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatter.format(service.price),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        thickness = 1.dp,
                        color = Color.Gray.copy(alpha = 0.3f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tổng cộng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formatter.format(totalAmount),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Quét mã QR để thanh toán",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    qrBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .size(250.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Vui lòng quét mã để hoàn tất thanh toán",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        item {
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Hoàn thành",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        // Thêm khoảng trống ở cuối
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

fun generateQRCode(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK
                    else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Data class cho Service
data class Service(
    val id: String,
    val name: String,
    val price: Int,
    val description: String? = null
)