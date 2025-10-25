package com.parkingSystem.parkingSystem.user.home.booking

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.auth0.android.jwt.JWT
import com.parkingSystem.parkingSystem.responsemodel.BookingDto
import com.parkingSystem.parkingSystem.ui.theme.LocalGradientTheme
import com.parkingSystem.parkingSystem.viewmodel.BookingHistoryViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingHistoryScreen(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController
) {
    val vm: BookingHistoryViewModel = viewModel(factory = viewModelFactory {
        initializer { BookingHistoryViewModel(sharedPreferences) }
    })

    val bookings by vm.bookings.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    val token = sharedPreferences.getString("access_token", null)
    val jwt = remember(token) { token?.let { runCatching { JWT(it) }.getOrNull() } }
    val userId = jwt?.getClaim("userId")?.asString()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Chờ duyệt", "Hoàn tất", "Đã huỷ")

    LaunchedEffect(userId) {
        userId?.let { vm.fetchBookingHistory(it) }
    }

    val gradient = LocalGradientTheme.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient.primary)
    ) {
        Text(
            text = "Lịch sử đặt chỗ",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .padding(top = 48.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            shape = MaterialTheme.shapes.extraLarge.copy(
                topStart = CornerSize(30.dp),
                topEnd = CornerSize(30.dp)
            ),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp,
            shadowElevation = 8.dp
        ) {

            when {
                userId == null -> {
                    CenterMessage("Token không hợp lệ hoặc userId không tồn tại.")
                }

                isLoading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                error != null -> {
                    CenterMessage(
                        text = "Lỗi: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                bookings.isEmpty() -> {
                    CenterMessage(
                        text = "Chưa có lịch sử đặt chỗ.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            indicator = { tabPositions ->
                                TabRowDefaults.Indicator(
                                    modifier = Modifier
                                        .tabIndicatorOffset(tabPositions[selectedTab])
                                        .height(3.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                val isSelected = selectedTab == index
                                Tab(
                                    selected = isSelected,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected)
                                                    FontWeight.Bold
                                                else
                                                    FontWeight.Normal,
                                                color = if (isSelected)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                            )
                                        )
                                    }
                                )
                            }
                        }
                        val filteredBookings = remember(selectedTab, bookings) {
                            when (selectedTab) {
                                0 -> bookings.filter { b ->
                                    when (b.status?.lowercase()) {
                                        "pending" -> true
                                        else -> false
                                    }
                                }

                                1 -> bookings.filter { b ->
                                    when (b.status?.lowercase()) {
                                        "done" -> true
                                        else -> false
                                    }
                                }
                                2 -> bookings.filter { b ->
                                    when (b.status?.lowercase()) {
                                        "cancelled"-> true
                                        else -> false
                                    }
                                }

                                else -> bookings
                            }
                        }
                        if (filteredBookings.isEmpty()) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Không có dữ liệu trong mục này.",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Medium
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 24.dp,
                                    top = 12.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredBookings) { booking ->
                                    BookingCard(
                                        b = booking,
                                        onClick = { bookingId ->
                                            navHostController.navigate("booking_detail/$bookingId")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun CenterMessage(
    text: String,
    color: Color = MaterialTheme.colorScheme.onBackground
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = color,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun BookingCard(
    b: BookingDto,
    onClick: (String) -> Unit

) {
    val dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val startText = runCatching {
        OffsetDateTime.parse(b.startTime).format(dateFmt)
    }.getOrNull() ?: (b.startTime ?: "--")

    val endText = runCatching {
        OffsetDateTime.parse(b.endTime).format(dateFmt)
    }.getOrNull() ?: (b.endTime ?: "--")

    val createdText = runCatching {
        OffsetDateTime.parse(b.createdAt).format(dateFmt)
    }.getOrNull() ?: (b.createdAt ?: "--")

    val statusColor = when (b.status?.lowercase()) {
        "pending" -> MaterialTheme.colorScheme.primary
        "done" -> Color(0xFF2E7D32)
        "cancelled", "canceled" -> Color(0xFFC62828)
        else -> MaterialTheme.colorScheme.onBackground
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable {
                b.id?.let { onClick(it) }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    b.parkName ?: "Bãi xe",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    b.status?.uppercase() ?: "--",
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                b.address ?: "Địa chỉ: --",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "Vị trí: ${b.slotName ?: "--"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                "Biển số: ${b.numberPlate ?: "--"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(Modifier.height(10.dp))

            Text(
                "Bắt đầu: $startText",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            )
            Text(
                "Kết thúc: $endText",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            )
            Text(
                "Tạo lúc: $createdText",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Light
                )
            )

            if (b.price != null && b.type_vehicle != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "Giá: ${"%,.0f".format(b.price)}đ / ${b.type_vehicle}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }
}

