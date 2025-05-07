package com.hellodoc.healthcaresystem.admin

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
import com.hellodoc.healthcaresystem.requestmodel.SpecialtyRequest
import com.hellodoc.healthcaresystem.viewmodel.SpecialtyViewModel

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment

@Composable
fun CreateSpecialtyScreen(sharedPreferences: SharedPreferences) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
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
            Text("Tạo chuyên khoa", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên chuyên khoa") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4
            )

            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Chọn ảnh đại diện")
            }

            imageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Ảnh chuyên khoa",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Button(
                onClick = {
                    if (name.isBlank() || description.isBlank() || imageUri == null) {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    } else {
                        val request = SpecialtyRequest(
                            name = name,
                            icon = imageUri,
                            description = description
                        )
                        specialtyViewModel.createSpecialty(request, context)

                        // Xoá form sau khi gửi
                        name = ""
                        description = ""
                        imageUri = null

                        // Làm mới danh sách chuyên khoa
                        specialtyViewModel.fetchSpecialties()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tạo chuyên khoa")
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Text("Các chuyên khoa hiện có", style = MaterialTheme.typography.titleMedium)
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
