package com.parkingSystem.parkingSystem.user.home.booking

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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
import com.parkingSystem.parkingSystem.ui.theme.LocalGradientTheme
import com.parkingSystem.parkingSystem.viewmodel.BookingDetailViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingDetailScreen(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    bookingId: String
) {
    val vm: BookingDetailViewModel = viewModel(factory = viewModelFactory {
        initializer { BookingDetailViewModel(sharedPreferences) }
    })

    val booking by vm.booking.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(bookingId) {
        vm.fetchBookingDetail(bookingId)
    }

    val gradient = LocalGradientTheme.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Quay lại",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        navHostController.popBackStack()
                    }
            )

            Text(
                text = "Chi tiết đặt chỗ",
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.extraLarge.copy(
                topStart = CornerSize(30.dp),
                topEnd = CornerSize(30.dp)
            ),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp,
            shadowElevation = 8.dp
        ) {

            when {
                isLoading -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                error != null -> {
                    CenterDetailMessage(
                        text = error ?: "Lỗi không xác định",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                booking == null -> {
                    CenterDetailMessage("Không tìm thấy dữ liệu đặt chỗ.")
                }

                else -> {
                    val b = booking!!

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

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {

                                // header line
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        b.parkName ?: "Bãi xe",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        b.status?.uppercase() ?: "--",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = statusColor
                                        )
                                    )
                                }

                                Text(
                                    b.address ?: "Địa chỉ: --",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                    )
                                )

                                Divider()

                                InfoRow(label = "Biển số", value = b.numberPlate ?: "--")
                                InfoRow(label = "Vị trí đậu", value = b.slotName ?: "--")
                                InfoRow(label = "Loại xe", value = b.type_vehicle ?: "--")

                                Divider()

                                InfoRow(label = "Phương thức thanh toán", value = b.paymentMethod?: "--")
                                InfoRow(label = "Trạng thái thanh toán", value = b.statusPayment?: "--")
                                InfoRow(label = "Tạo lúc", value = createdText)

                                if (b.price != null && b.type_vehicle != null) {
                                    Divider()
                                    InfoRow(
                                        label = "Giá",
                                        value = "${"%,.0f".format(b.price)}đ / ${b.type_vehicle}",
                                        bold = true
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (b.status?.lowercase() == "pending" || b.status?.lowercase() == "done") {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        navHostController.navigate("booking_qr/${b.id}")
                                    }
                                ) {
                                    Text(
                                        "Hiển thị mã QR",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (b.status?.lowercase() == "pending") {
                                Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        vm.cancelBooking(
                                            b.id ?: return@Button
                                        ) {
                                            navHostController.popBackStack()
                                        }
                                    }
                                ) {
                                    Text("Huỷ đặt chỗ", fontWeight = FontWeight.Bold)
                                }
                            }
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                onClick = { navHostController.popBackStack() }
                            ) {
                                Text("Quay lại lịch sử")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun CenterDetailMessage(
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

@Composable
private fun InfoRow(
    label: String,
    value: String,
    bold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        )
        Text(
            text = value,
            style = if (bold) {
                MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            textAlign = TextAlign.End
        )
    }
}
