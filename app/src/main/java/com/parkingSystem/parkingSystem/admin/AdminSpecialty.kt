package com.parkingSystem.parkingSystem.admin

import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun CreateSpecialtyScreen(sharedPreferences: SharedPreferences) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var park_name by remember { mutableStateOf("") }
    var type_vehicle by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

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
            Text("Create a parking lot ", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = park_name,
                onValueChange = { park_name = it },
                label = { Text("Parking lot name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = type_vehicle,
                onValueChange = { type_vehicle = it },
                label = { Text("Describe") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    if (park_name.isBlank() || type_vehicle.isBlank() || price.isBlank() || imageUri == null) {
                        Toast.makeText(context, "Please enter complete information", Toast.LENGTH_SHORT).show()
                    } else {
                        val request = SpecialtyRequest(
                            park_name = park_name,
                            icon = imageUri,
                            type_vehicle = type_vehicle,
                            price = price.toDouble()
                        )
                        specialtyViewModel.createSpecialty(request, context)

                        // Delete form after create
                        park_name = ""
                        type_vehicle = ""
                        price = ""
                        imageUri = null

                        // reload list parkinglot
                        specialtyViewModel.fetchSpecialties()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create parking lot")
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Text("List of parking lots", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Chiếm phần còn lại của màn hình
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
