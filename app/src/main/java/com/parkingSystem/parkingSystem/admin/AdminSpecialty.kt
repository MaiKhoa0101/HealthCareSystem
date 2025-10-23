package com.parkingSystem.parkingSystem.admin

import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.parkingSystem.parkingSystem.requestmodel.SpecialtyRequest
import com.parkingSystem.parkingSystem.viewmodel.SpecialtyViewModel
import com.parkingSystem.parkingSystem.user.home.parking.ParkingSpot
import com.parkingSystem.parkingSystem.user.home.parking.SpotStatus

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.parkingSystem.parkingSystem.user.home.parking.ParkingSlot

@Composable
fun CreateSpecialtyScreen(sharedPreferences: SharedPreferences) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var park_name by remember { mutableStateOf("") }
    var type_vehicle by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var price by remember { mutableStateOf("") }

    var colsInput by remember { mutableStateOf("") }
    var rowsInput by remember { mutableStateOf("") }
    var slots by remember { mutableStateOf(listOf<ParkingSpot>()) }
    var selectedKeys by remember { mutableStateOf(setOf<String>()) }
    fun slotKey(x: Int, y: Int) = "${x}_${y}"

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    val specialtyViewModel: SpecialtyViewModel = viewModel(factory = viewModelFactory {
        initializer { SpecialtyViewModel(sharedPreferences) }
    })

    val specialties by specialtyViewModel.specialties.collectAsState()
    val createResponse by specialtyViewModel.createSpecialtyMessage.collectAsState()

    LaunchedEffect(Unit) {
        specialtyViewModel.fetchSpecialties()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text("Create a parking lot", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = park_name,
                onValueChange = { park_name = it },
                label = { Text("Parking lot name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = type_vehicle,
                onValueChange = { type_vehicle = it },
                label = { Text("Type of vehicle") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Type of vehicle") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF002E5D),
                    contentColor = Color.White
                ),
                onClick = { imagePickerLauncher.launch("image/*") }
            ) {
                Text("Choose a profile picture")
            }

            imageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Image a parking lot",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("Create parking lot Map", style = MaterialTheme.typography.titleMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = colsInput,
                    onValueChange = { colsInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Số cột (X)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = rowsInput,
                    onValueChange = { rowsInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Số hàng (Y)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF002E5D),
                        contentColor = Color.White
                    ),
                    onClick = {
                        val c = colsInput.toIntOrNull() ?: 0
                        val r = rowsInput.toIntOrNull() ?: 0
                        if (c <= 0 || r <= 0) {
                            Toast.makeText(context, "Enter the number of columns/rows > 0", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val list = mutableListOf<ParkingSlot>()
                        var n = 1
                        for (y in 1..r) {
                            for (x in 1..c) {
                                list += ParkingSlot(
                                    pos_x = x,
                                    pos_y = y,
                                    spotNumber = n.toString(),
                                    status = SpotStatus.AVAILABLE
                                )
                                n++
                            }
                        }
                        slots = list
                        selectedKeys = emptySet()
                    }) {
                    Text("Create Map")
                }
            }

            if (slots.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))

                MinimalParkingGridLazy(
                    slots = slots,
                    selectedKeys = selectedKeys,
                    onToggle = { x, y ->
                        val k = slotKey(x, y)
                        selectedKeys = if (k in selectedKeys) selectedKeys - k else selectedKeys + k
                    }
                )

                Spacer(Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF002E5D),
                            contentColor = Color.White
                        ),
                        onClick = {
                            if (selectedKeys.isEmpty()) return@Button
                            slots = slots.filter { slotKey(it.pos_x, it.pos_y) !in selectedKeys }
                            selectedKeys = emptySet()
                        },
                        enabled = selectedKeys.isNotEmpty()
                    ) { Text("Delete selected slot") }

                    OutlinedButton(
                        onClick = { selectedKeys = emptySet() },
                        enabled = selectedKeys.isNotEmpty(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF002E5D)
                        )
                    ) { Text("Uncheck") }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Danh sách slot", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tên", fontWeight = FontWeight.SemiBold)
                            Text("X", fontWeight = FontWeight.SemiBold)
                            Text("Y", fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(6.dp))
                        slots.forEach { s ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(s.spotNumber)
                                Text(s.pos_x.toString())
                                Text(s.pos_y.toString())
                            }
                        }
                    }
                }
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF002E5D),
                    contentColor = Color.White
                ),
                onClick = {
                    if (park_name.isBlank() || type_vehicle.isBlank() || price.isBlank() || imageUri == null) {
                        Toast.makeText(context, "Please enter complete information", Toast.LENGTH_SHORT).show()
                    } else {
//                        val request = SpecialtyRequest(
//                            park_name = park_name,
//                            icon = imageUri,
//                            type_vehicle = type_vehicle,
//                            price = price,
//                        )
//                        specialtyViewModel.createSpecialty(request, context)

                        // delete form after create
                        park_name = ""
                        type_vehicle = ""
                        price = ""
                        imageUri = null

                        // reload
                        specialtyViewModel.fetchSpecialties()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create a parking lot")
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Text("List of parking lots", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(specialties) { specialty ->
                Text(
                    "- ${specialty.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun MinimalParkingGridLazy(
    slots: List<ParkingSpot>,
    selectedKeys: Set<String>,
    onToggle: (Int, Int) -> Unit
) {
    if (slots.isEmpty()) return

    val maxX = slots.maxOf { it.pos_x }
    val maxY = slots.maxOf { it.pos_y }
    val byPos = remember(slots) { slots.associateBy { it.pos_x to it.pos_y } }

    Text("Parking lot map", style = MaterialTheme.typography.titleSmall)
    Spacer(Modifier.height(8.dp))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = (maxY * 52).dp)
    ) {
        items(maxX) { colIndex ->
            val x = colIndex + 1
            Column {
                for (y in 1..maxY) {
                    val slot = byPos[x to y]
                    val selected = slot != null && ("${x}_${y}" in selectedKeys)

                    val baseModifier = Modifier
                        .size(44.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                slot == null -> Color.Transparent
                                selected -> Color(0xFF0083FF)
                                else -> Color(0xFFE5E7EB)
                            }
                        )
                        .border(
                            width = when {
                                slot == null -> 0.dp
                                selected -> 2.dp
                                else -> 2.dp
                            },
                            color = when {
                                slot == null -> Color.Transparent
                                selected -> Color(0xFF002E5D)
                                else -> Color(0xFF9CA3AF)
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                    Box(
                        modifier = if (slot != null)
                            baseModifier.clickable { onToggle(x, y) }
                        else baseModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        if (slot != null) {
                            Text(
                                slot.spotNumber,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}