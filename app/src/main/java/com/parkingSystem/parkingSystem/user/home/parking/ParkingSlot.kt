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
import androidx.compose.material.icons.filled.LocationOn
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
import com.parkingSystem.parkingSystem.viewmodel.SpecialtyViewModel

// Data class để lưu thông tin vị trí và trạng thái của mỗi ô
data class ParkingSpot(
    val pos_x: Int,
    val pos_y: Int,
    val spotNumber: String,
    val status: SpotStatus = SpotStatus.AVAILABLE
)

enum class SpotStatus {
    AVAILABLE,  // Chỗ trống
    OCCUPIED,   // Đã đặt
    BLOCKED     // Không khả dụng
}

@Composable
fun ParkingSlot(
    context: Context,
    navHostController: NavHostController
) {
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    var specialtyId by remember { mutableStateOf("") }
    var specialtyName by remember { mutableStateOf("") }
    var specialtyDesc by remember { mutableStateOf("") }
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val viewModel: SpecialtyViewModel = viewModel(factory = viewModelFactory {
        initializer { SpecialtyViewModel(sharedPreferences) }
    })

    var isDataLoaded by remember { mutableStateOf(false) }

    // State để lưu danh sách các vị trí đậu xe
    var parkingSpots by remember { mutableStateOf(generateParkingSpots()) }

    LaunchedEffect(Unit) {
        savedStateHandle?.get<String>("specialtyId")?.let {
            specialtyId = it
        }
        savedStateHandle?.get<String>("specialtyName")?.let {
            specialtyName = it
        }
        savedStateHandle?.get<String>("specialtyDesc")?.let {
            specialtyDesc = it
        }
        isDataLoaded = true
        println(specialtyId + " " + specialtyName + " " + specialtyDesc)
    }

    LaunchedEffect(specialtyId) {
        viewModel.fetchSpecialtyDoctor(specialtyId)
    }

    if (isDataLoaded) {
        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopBar(onClick = {
                navHostController.popBackStack()
            }, viewModel)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Sơ đồ bãi đậu xe",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyRow {
                        item{
                            // Hiển thị sơ đồ bãi đậu xe
                            ParkingGridLayout(
                                parkingSpots = parkingSpots,
                                onSpotClick = { spot ->
                                    println("Clicked spot: ${spot.spotNumber} at position (${spot.pos_x}, ${spot.pos_y})")
                                    // Cập nhật trạng thái khi click
                                    parkingSpots = parkingSpots.map {
                                        if (it.pos_x == spot.pos_x && it.pos_y == spot.pos_y) {
                                            it.copy(
                                                status = if (it.status == SpotStatus.AVAILABLE)
                                                    SpotStatus.OCCUPIED else SpotStatus.AVAILABLE
                                            )
                                        } else it
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

                        Spacer(modifier = Modifier.height(8.dp))

                        LegendItem(
                            color = Color(0xFFBDBDBD),
                            text = "Không khả dụng"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
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
fun TopBar(onClick: () -> Unit, viewModel: SpecialtyViewModel) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

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
                    .clickable {
                        onClick()
                    }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Bãi đậu xe",
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun ParkingGridLayout(
    parkingSpots: List<ParkingSpot>,
    onSpotClick: (ParkingSpot) -> Unit
) {
    // Tìm giá trị max của pos_x và pos_y để tạo grid
    val maxX = parkingSpots.maxOfOrNull { it.pos_x } ?: 0
    val maxY = parkingSpots.maxOfOrNull { it.pos_y } ?: 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Duyệt qua từng hàng (pos_y)
        for (y in 0..maxY) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Duyệt qua từng cột (pos_x)
                for (x in 0..maxX) {
                    // Tìm spot tại vị trí (x, y)
                    val spot = parkingSpots.find { it.pos_x == x && it.pos_y == y }

                    if (spot != null) {
                        ParkingSpotCell(spot = spot, onClick = { onSpotClick(spot) })
                    } else {
                        // Ô trống không có parking spot
                        EmptyCell()
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingSpotCell(spot: ParkingSpot, onClick: () -> Unit) {
    val backgroundColor = when (spot.status) {
        SpotStatus.AVAILABLE -> Color(0xFFFFEB3B) // Vàng
        SpotStatus.OCCUPIED -> Color(0xFFFF5722)  // Đỏ cam
        SpotStatus.BLOCKED -> Color(0xFFBDBDBD)   // Xám
    }

    val borderColor = when (spot.status) {
        SpotStatus.AVAILABLE -> Color(0xFFFBC02D)
        SpotStatus.OCCUPIED -> Color(0xFFD84315)
        SpotStatus.BLOCKED -> Color(0xFF757575)
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .padding(3.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = spot.status != SpotStatus.BLOCKED) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (spot.status == SpotStatus.BLOCKED) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = spot.spotNumber,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "(${spot.pos_x},${spot.pos_y})",
                    fontSize = 8.sp,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun EmptyCell() {
    Box(
        modifier = Modifier
            .size(60.dp)
            .padding(3.dp)
    )
}

// Hàm tạo danh sách parking spots theo layout mẫu
fun generateParkingSpots(): List<ParkingSpot> {
    val spots = mutableListOf<ParkingSpot>()
    var spotNumber = 1

    // Hàng 1: 5 ô từ cột 2-6
    for (x in 2..6) {
        spots.add(ParkingSpot(pos_x = x, pos_y = 0, spotNumber = "A${spotNumber++}"))
    }

    // Hàng 2: 6 ô từ cột 1-6
    for (x in 1..6) {
        spots.add(ParkingSpot(pos_x = x, pos_y = 1, spotNumber = "A${spotNumber++}"))
    }

    // Hàng 3-5: 6 ô từ cột 1-6
    for (y in 2..4) {
        for (x in 1..6) {
            spots.add(ParkingSpot(pos_x = x, pos_y = y, spotNumber = "A${spotNumber++}"))
        }
    }

    // Hàng 6: 6 ô từ cột 1-6 (2 ô cuối blocked)
    for (x in 1..4) {
        spots.add(ParkingSpot(pos_x = x, pos_y = 5, spotNumber = "B${x}"))
    }
    spots.add(ParkingSpot(pos_x = 5, pos_y = 5, spotNumber = "X1", status = SpotStatus.BLOCKED))
    spots.add(ParkingSpot(pos_x = 6, pos_y = 5, spotNumber = "X2", status = SpotStatus.BLOCKED))

    // Hàng 7: 4 ô từ cột 1-4
    for (x in 1..4) {
        spots.add(ParkingSpot(pos_x = x, pos_y = 6, spotNumber = "B${x + 4}"))
    }

    // Hàng 8-10: 3 ô từ cột 2-4
    var bNumber = 9
    for (y in 7..9) {
        for (x in 2..4) {
            spots.add(ParkingSpot(pos_x = x, pos_y = y, spotNumber = "C${bNumber++}"))
        }
    }

    return spots
}