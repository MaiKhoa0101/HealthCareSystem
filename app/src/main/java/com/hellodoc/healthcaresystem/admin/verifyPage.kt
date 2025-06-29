package com.hellodoc.healthcaresystem.admin

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel

@Composable
fun PendingDoctorDetailScreen(
    userId: String,
    sharedPreferences: SharedPreferences,
    navController: NavHostController
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
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Avatar + Name
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = doctor?.avatarURL,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .shadow(4.dp, CircleShape)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = doctor?.name ?: "Đang tải...",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Thông tin cơ bản
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                InfoRow("📧 Email", doctor?.email)
                InfoRow("📞 Điện thoại", doctor?.phone)
                InfoRow("🪪 CCCD", doctor?.CCCD)
                InfoRow("🏥 Mã chuyên khoa", doctor?.specialty)
                InfoRow("🧾 License", doctor?.license)
            }
        }

        // Tài liệu ảnh
        Text("📂 Tài liệu xác minh", fontWeight = FontWeight.SemiBold)

        val imageItems = listOf(
            "Ảnh khuôn mặt" to doctor?.faceUrl,
            "CCCD Mặt trước" to doctor?.frontCccdUrl,
            "CCCD Mặt sau" to doctor?.backCccdUrl,
            "Giấy phép hành nghề" to doctor?.licenseUrl
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            imageItems.forEach { (label, url) ->
                if (!url.isNullOrBlank()) {
                    Column {
                        Text(text = label, fontWeight = FontWeight.Medium)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clickable { expandedImageUrl = url }
                                .shadow(3.dp, MaterialTheme.shapes.medium),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            AsyncImage(
                                model = url,
                                contentDescription = label,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // Nút xác minh
        Button(
            onClick = { doctor?.userId?.let { viewModel.verifyDoctor(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("✅ Xác minh tài khoản")
        }
    }

    // Hiển thị Dialog zoom ảnh
    ZoomableImageDialog(
        selectedImageUrl = expandedImageUrl,
        onDismiss = { expandedImageUrl = null }
    )
}

// Hàm hiển thị từng dòng thông tin với icon
@Composable
fun InfoRow(label: String, value: String?) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$label: ", fontWeight = FontWeight.SemiBold)
        Text(text = value ?: "", modifier = Modifier.weight(1f))
    }
}

@Composable
fun ZoomableImageDialog(selectedImageUrl: String?, onDismiss: () -> Unit) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    if (selectedImageUrl != null) {
        Dialog(onDismissRequest = onDismiss) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable(onClick = onDismiss)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                scale = (scale * zoom).coerceIn(1f, 5f)
                                offset += pan
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUrl),
                        contentDescription = "Zoomable Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )
                }
            }
        }
    } else {
        onDismiss()
    }
}

