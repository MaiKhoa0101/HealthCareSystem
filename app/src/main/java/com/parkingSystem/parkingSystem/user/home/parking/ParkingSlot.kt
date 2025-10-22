package com.parkingSystem.parkingSystem.user.home.parking

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.parkingSystem.parkingSystem.responsemodel.Slot
import com.parkingSystem.parkingSystem.viewmodel.ParkingViewModel


@Composable
fun ParkingSlot(
    context: Context,
    navHostController: NavHostController
) {
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    var parkId by remember { mutableStateOf("") }
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val viewModel: ParkingViewModel = viewModel(factory = viewModelFactory {
        initializer { ParkingViewModel(sharedPreferences) }
    })

    val slots by viewModel.slots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val currentPark by viewModel.currentPark.collectAsState()
    LaunchedEffect(Unit) {
        savedStateHandle?.get<String>("parkId")?.let {
            parkId = it
            viewModel.fetchSlotsByPark(parkId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(
            parkName = "Bãi đậu xe",
            onClick = { navHostController.popBackStack() }
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lỗi: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            slots != null -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Thông tin bãi đậu xe
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = currentPark!!.park_name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Địa chỉ: ${currentPark!!.address}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Giá: ${currentPark!!.price}đ/${currentPark!!.type_vehicle}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Sơ đồ bãi đậu xe",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyRow {
                            item {
                                ParkingGridLayout(
                                    slots = currentPark!!.slots,
                                    onSpotClick = { slot ->
                                        if (!slot.isBooked) {

                                        }
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Chú thích
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Chú thích:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            LegendItem(
                                color = Color(0xFFFFEB3B),
                                text = "Chỗ trống"
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            LegendItem(
                                color = Color(0xFFFF5722),
                                text = "Đã đặt"
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(color, RoundedCornerShape(4.dp))
                .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(parkName: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back Button",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onClick() }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = parkName,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun ParkingGridLayout(
    slots: List<Slot>,
    onSpotClick: (Slot) -> Unit
) {
    val maxX = slots.maxOfOrNull { it.pos_x } ?: 0
    val maxY = slots.maxOfOrNull { it.pos_y } ?: 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalAlignment = Alignment.Start
    ) {
        for (y in 0..maxY) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (x in 0..maxX) {
                    val slot = slots.find { it.pos_x == x && it.pos_y == y }

                    if (slot != null) {
                        ParkingSpotCell(
                            slot = slot,
                            onClick = { onSpotClick(slot) }
                        )
                    } else {
                        EmptyCell()
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingSpotCell(slot: Slot, onClick: () -> Unit) {
    val backgroundColor = if (slot.isBooked) {
        Color(0xFFFF5722)  // Đỏ cam - đã đặt
    } else {
        Color(0xFFFFEB3B)  // Vàng - còn trống
    }

    val borderColor = if (slot.isBooked) {
        Color(0xFFD84315)
    } else {
        Color(0xFFFBC02D)
    }

    Box(
        modifier = Modifier
            .height(60.dp)
            .width(40.dp)
            .padding(3.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = !slot.isBooked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = slot.slotName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "(${slot.pos_x},${slot.pos_y})",
                fontSize = 8.sp,
                color = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun EmptyCell() {
    Box(
        modifier = Modifier
            .height(60.dp)
            .width(40.dp)
            .padding(3.dp)
    )
}