package com.parkingSystem.parkingSystem.user.home.parking

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    navHostController: NavHostController,
    parkId: String
) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val viewModel: ParkingViewModel = viewModel(factory = viewModelFactory {
        initializer { ParkingViewModel(sharedPreferences) }
    })
    LaunchedEffect(Unit) {
        println("Gọi được launched efect")
        if (parkId.isNotEmpty()) {
            viewModel.fetchParkById(parkId)
        }
    }

    val slots by viewModel.slots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentPark by viewModel.currentPark.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(
            title = "Bãi đậu xe",
            onClick = { navHostController.popBackStack() },
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
                            currentPark?.park_name?.let {
                                Text(
                                    text = it,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Text(
                                text = "Địa chỉ: ${currentPark?.address}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Giá: ${currentPark?.price}đ/${currentPark?.type_vehicle}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Park Map",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyRow {
                            item {
                                ParkingGridLayout(
                                    slots = currentPark?.slots,
                                    onSpotClick = { slot ->
                                        if (!slot.isBooked) {
                                            navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                                set("park_id", currentPark?.park_id)
                                                set("park_name", currentPark?.park_name)
                                                set("address", currentPark?.address)
                                                set("price", currentPark?.price)
                                                set("slotId", slot.slot_id)
                                                set("type_vehicle", currentPark?.type_vehicle)
                                                set("slotName", slot.slotName)
                                                set("slotPosX", slot.pos_X)
                                                set("slotPosY", slot.pos_Y)

                                            }
                                            navHostController.navigate("booking")
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

@Composable
fun ParkingGridLayout(
    slots: List<Slot>?,
    onSpotClick: (Slot) -> Unit
) {
    val maxX = slots?.maxOfOrNull { it.pos_X } ?: 0
    val maxY = slots?.maxOfOrNull { it.pos_Y } ?: 0

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
                    val slot = slots?.find { it.pos_X == x && it.pos_Y == y }

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
                text = "(${slot.pos_X},${slot.pos_Y})",
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