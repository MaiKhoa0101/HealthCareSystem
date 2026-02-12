package com.hellodoc.healthcaresystem.view.user.otherUI

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hellodoc.healthcaresystem.viewmodel.SignLanguageViewModel
import java.util.concurrent.Executors

@Composable
fun SignLanguageScreen(
    viewModel: SignLanguageViewModel = viewModel()
) {
    val context = LocalContext.current
    val isCameraActive by viewModel.isCameraActive.collectAsState()
    val predictionText by viewModel.prediction.collectAsState()

    // 1. Tạo launcher để hứng kết quả khi người dùng chọn "Cho phép" hoặc "Từ chối"
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Nếu người dùng đồng ý, bật camera ngay
            viewModel.toggleCamera()
        } else {
            // Nếu từ chối, có thể hiện thông báo (Toast) ở đây
            Log.e("Permission", "Camera permission denied")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            if (isCameraActive) {
                CameraPreviewView(
                    onFrameCaptured = { bitmap ->
                        viewModel.processCameraFrame(bitmap)
                    }
                )
            } else {
                Text("Nhấn nút để bật Camera", color = Color.White)
            }

            if (isCameraActive) {
                Text(
                    text = predictionText,
                    color = Color.Green,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .rotate(90f)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .align(Alignment.CenterStart)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isCameraActive) {
                Text(
                    text = predictionText,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Button(
                onClick = {
                    // 2. Logic kiểm tra quyền trước khi bật
                    if (isCameraActive) {
                        viewModel.toggleCamera() // Đang bật thì tắt luôn, không cần hỏi
                    } else {
                        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            viewModel.toggleCamera() // Đã có quyền -> Bật
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA) // Chưa có -> Hỏi
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCameraActive) Color.Red else Color.Blue
                ),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(text = if (isCameraActive) "Dừng Dịch" else "Bắt Đầu Dịch")
            }
        }
    }
}

@Composable
fun CameraPreviewView(onFrameCaptured: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val executor = Executors.newSingleThreadExecutor()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Lưu ý: Cần setOutputImageFormat để toBitmap() hoạt động mượt mà
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()

                imageAnalysis.setAnalyzer(executor) { imageProxy ->
                    val bitmap = imageProxy.toBitmap()
                    // Xoay bitmap nếu cần thiết (thường camera trước bị xoay 270 độ)
                    // Ở đây gửi raw bitmap, logic xử lý có thể thêm sau
                    onFrameCaptured(bitmap)
                    imageProxy.close()
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_FRONT_CAMERA, // Đảm bảo máy test có camera trước
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.e("Camera", "Bind failed", e)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}