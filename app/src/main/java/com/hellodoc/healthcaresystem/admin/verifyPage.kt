package com.hellodoc.healthcaresystem.admin

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel

@Composable
fun PendingDoctorDetailScreen(
    userId: String, sharedPreferences: SharedPreferences, navController: NavHostController
) {
    val viewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })
    val doctor by viewModel.pendingDoctor.collectAsState()
    val verificationMessage by viewModel.verificationMessage.collectAsState()

    var expandedImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        viewModel.fetchPendingDoctorById(userId)
    }

    LaunchedEffect(verificationMessage) {
        if (verificationMessage == "success") {
            navController.popBackStack() // quay về màn hình trước
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ảnh đại diện
        AsyncImage(
            model = doctor?.avatarURL,
            contentDescription = "Ảnh đại diện",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Họ tên: ${doctor?.name}", style = MaterialTheme.typography.titleMedium)
                Text("Email: ${doctor?.email}")
                Text("Số điện thoại: ${doctor?.phone}")
                Text("CCCD: ${doctor?.CCCD}")
                Text("Mã chuyên khoa: ${doctor?.specialty}")
                Text("License: ${doctor?.license}")
            }
        }

        val imageItems = listOf(
            "Ảnh khuôn mặt" to doctor?.faceUrl,
            "Ảnh CCCD Mặt Trước" to doctor?.frontCccdUrl,
            "Ảnh CCCD Mặt Sau" to doctor?.backCccdUrl,
            "Ảnh Giấy Phép Hành Nghề" to doctor?.licenseUrl
        )

        imageItems.forEach { (label, url) ->
            if (!url.isNullOrBlank()) {
                Column {
                    Text(text = label, fontWeight = FontWeight.SemiBold)
                    AsyncImage(
                        model = url,
                        contentDescription = label,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { expandedImageUrl = url }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                doctor?.userId?.let { viewModel.verifyDoctor(it) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Xác minh tài khoản")
        }
    }

    // Dialog hiển thị ảnh phóng to
    if (expandedImageUrl != null) {
        AlertDialog(
            onDismissRequest = { expandedImageUrl = null },
            confirmButton = {
                TextButton(onClick = { expandedImageUrl = null }) {
                    Text("Đóng")
                }
            },
            text = {
                AsyncImage(
                    model = expandedImageUrl,
                    contentDescription = "Phóng to ảnh",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
        )
    }
}
