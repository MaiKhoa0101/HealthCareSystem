package com.hellodoc.healthcaresystem.view.model_human

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import java.nio.ByteBuffer


@Composable
fun Simple3DScreen() {
    val context = LocalContext.current
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    // State quản lý việc load
    var characterNode by remember { mutableStateOf<ModelNode?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // --- FIX CRASH: Dọn dẹp khi đóng màn hình ---
    DisposableEffect(Unit) {
        onDispose {
            characterNode = null // Ngắt kết nối node để tránh rò rỉ bộ nhớ
        }
    }

    // --- LOGIC ĐỌC FILE THỦ CÔNG ---
    LaunchedEffect(Unit) {
        try {
            // 1. Mở luồng đọc file từ assets
            val inputStream = context.assets.open("BoneEric.glb")
            val bytes = inputStream.readBytes()
            inputStream.close()

            // Giữ nguyên tỉ lệ scale bạn mong muốn
            val scaleProp = 5f

            // 2. Đưa vào ByteBuffer
            val buffer = ByteBuffer.wrap(bytes)

            // 3. Tạo Model
            val instance = modelLoader.createModelInstance(buffer)

            if (instance != null) {
                // 4. Tạo Node nhân vật
                val node = ModelNode(
                    modelInstance = instance,
                    scaleToUnits = 1.0f
                ).apply {
                    // --- GIỮ NGUYÊN TỌA ĐỘ CỦA BẠN ---
                    position = Position(x = 0.0f, y = -2.5f, z = -1f)

                    // --- GIỮ NGUYÊN TỈ LỆ CỦA BẠN ---
                    scale = Position(x = scaleProp, y = scaleProp, z = scaleProp)

                    // Chỉnh tâm xoay về giữa nhân vật
                    centerOrigin(Position(0f, 0f, 0f))

                    isEditable = true
                }
                characterNode = node
                isLoading = false
            } else {
                errorMessage = "Đọc được file nhưng dữ liệu bên trong bị lỗi (Model null)."
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = "Lỗi đọc file: ${e.message}"
            isLoading = false
        }
    }

    // --- GIAO DIỆN ---
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Scene(
                modifier = Modifier.fillMaxSize(),
                engine = engine,
                modelLoader = modelLoader,

                // 1. ÁNH SÁNG
                mainLightNode = rememberMainLightNode(engine) {
                    intensity = 70_000.0f
                    isShadowCaster = true
                },

                // 2. CAMERA (Giữ nguyên vị trí bạn yêu cầu)
                cameraNode = rememberCameraNode(engine) {
                    position = Position(z = 2.5f)
                },

                // 3. ĐIỀU KHIỂN (Đã thêm vào để bạn có thể xoay nhân vật)

                // 4. NHÂN VẬT
                childNodes = rememberNodes {
                    characterNode?.let { node ->
                        add(node)
                    }
                },

            )
        }
    }
}