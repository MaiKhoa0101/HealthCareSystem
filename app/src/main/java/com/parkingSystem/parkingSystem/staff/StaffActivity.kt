package com.parkingSystem.parkingSystem.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ParkingVehicle(
    val id: String,
    val plate: String,
    val owner: String,
    val status: String, // "Parked", "Waiting", "CheckedOut"
    val parkedAt: String,
    val spot: String?
)

data class StaffInfo(
    val name: String,
    val id: String,
    val shift: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffActivityScreen(
    staffInfo: StaffInfo,
    vehicles: List<ParkingVehicle>,
    onAssignSpot: (ParkingVehicle) -> Unit = {},
    onCheckout: (ParkingVehicle) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("Tất cả") } // "Tất cả", "Đang gửi", "Chờ", "Ra về"
    val filtered = vehicles.filter {
        (filterStatus == "Tất cả" || statusLabel(filterStatus) == it.status) &&
                (it.plate.contains(query, ignoreCase = true) ||
                        it.owner.contains(query, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text("Parking Staff", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { /* open menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "menu")
                    }
                },
                actions = {
                    IconButton(onClick = { /* maybe open timer / shift summary */ }) {
                        Icon(Icons.Default.Timer, contentDescription = "shift")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Gọi tiếp theo") },
                onClick = { /* action to call next in queue */ },
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = "next") }
            )
        },
        content = { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp)
            ) {
                StaffCard(staffInfo = staffInfo)

                Spacer(modifier = Modifier.height(12.dp))

                // Stats row
                StatsRow(total = vehicles.size, parked = vehicles.count { it.status == "Parked" }, waiting = vehicles.count { it.status == "Waiting" })

                Spacer(modifier = Modifier.height(12.dp))

                // Search & filters
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Tìm biển số hoặc tên chủ") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "search") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterDropdown(selected = filterStatus, onSelect = { filterStatus = it })
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Map placeholder + quick summary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // Replace with real map or layout later
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Sơ đồ bãi (placeholder)", fontWeight = FontWeight.SemiBold)
                        Text("Hiển thị vị trí xe, chỗ trống, ...", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // List header
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Danh sách xe", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${filtered.size} kết quả", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Vehicles list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filtered, key = { it.id }) { v ->
                        VehicleCard(
                            vehicle = v,
                            onAssign = { onAssignSpot(v) },
                            onCheckout = { onCheckout(v) }
                        )
                    }
                }
            }
        }
    )
}

// --- Helper composables ---
@Composable
private fun StaffCard(staffInfo: StaffInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(staffInfo.name.split(" ").lastOrNull()?.firstOrNull()?.toString() ?: "S", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(staffInfo.name, fontWeight = FontWeight.SemiBold)
                Text("ID: ${staffInfo.id}", style = MaterialTheme.typography.bodySmall)
                Text("Ca: ${staffInfo.shift}", style = MaterialTheme.typography.bodySmall)
            }

            // quick action buttons
            Column(horizontalAlignment = Alignment.End) {
                TextButton(onClick = { /* open profile or settings */ }) {
                    Text("Chi tiết")
                }
            }
        }
    }
}

@Composable
private fun StatsRow(total: Int, parked: Int, waiting: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatCard(title = "Tổng", value = total.toString())
        StatCard(title = "Đang gửi", value = parked.toString())
        StatCard(title = "Chờ", value = waiting.toString())
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    Card(
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FilterDropdown(selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Tất cả", "Đang gửi", "Chờ", "Ra về")
    Box {
        OutlinedButton(onClick = { expanded = true }, shape = RoundedCornerShape(10.dp)) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = {
                    onSelect(opt)
                    expanded = false
                })
            }
        }
    }
}

@Composable
private fun VehicleCard(vehicle: ParkingVehicle, onAssign: () -> Unit, onCheckout: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* open detail */ },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(vehicle.plate, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusChip(status = vehicle.status)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Chủ: ${vehicle.owner}", style = MaterialTheme.typography.bodySmall)
                Text("Gửi lúc: ${vehicle.parkedAt} • Chỗ: ${vehicle.spot ?: "-"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(onClick = onAssign) { Text("Gán chỗ") }
                TextButton(onClick = onCheckout) { Text("Trả xe") }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (text, color) = when (status) {
        "Parked" -> "Đang gửi" to Color(0xFF2E7D32)
        "Waiting" -> "Chờ" to Color(0xFFF57C00)
        "CheckedOut" -> "Ra về" to Color(0xFF616161)
        else -> status to Color.Gray
    }
    Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 2.dp) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// helper to convert filter label to internal status
private fun statusLabel(filterLabel: String) = when (filterLabel) {
    "Đang gửi" -> "Parked"
    "Chờ" -> "Waiting"
    "Ra về" -> "CheckedOut"
    else -> "Any"
}

// --- Preview with sample data ---
@Preview(showBackground = true)
@Composable
fun ParkingStaffScreenPreview() {
    val sampleStaff = StaffInfo(name = "Nguyễn Văn A", id = "PS-001", shift = "Sáng (7:00 - 15:00)")
    val sampleVehicles = listOf(
        ParkingVehicle(id = "1", plate = "29A-123.45", owner = "Trần B.", status = "Parked", parkedAt = "08:15", spot = "A12"),
        ParkingVehicle(id = "2", plate = "30F-888.88", owner = "Lê C.", status = "Waiting", parkedAt = "08:30", spot = null),
        ParkingVehicle(id = "3", plate = "51B-555.00", owner = "Phạm D.", status = "Parked", parkedAt = "07:50", spot = "B03"),
        ParkingVehicle(id = "4", plate = "72C-999.22", owner = "Hoàng E.", status = "CheckedOut", parkedAt = "06:40", spot = null),
    )

    MaterialTheme {
        StaffActivityScreen(
            staffInfo = sampleStaff,
            vehicles = sampleVehicles,
            onAssignSpot = { /* demo */ },
            onCheckout = { /* demo */ }
        )
    }
}