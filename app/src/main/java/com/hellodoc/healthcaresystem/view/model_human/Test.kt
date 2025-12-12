package com.hellodoc.healthcaresystem.view.model_human

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberNodes
import com.google.android.filament.Engine
import io.github.sceneview.environment.Environment
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.Node

@Composable
fun Simple3DScreen(
    engine: Engine,
    modelInstance: ModelInstance?, // Nhận Model đã nạp sẵn
    environment: Environment?      // Nhận Môi trường đã nạp sẵn
) {
    // State lưu Node hiển thị
    var characterNode by remember { mutableStateOf<ModelNode?>(null) }

    // --- TẠO NODE TỪ DỮ LIỆU ĐÃ CÓ ---
    LaunchedEffect(modelInstance) {
        if (modelInstance != null) {
            val node = ModelNode(
                modelInstance = modelInstance,
                scaleToUnits = 1.0f
            ).apply {
                // Cấu hình vị trí/tỉ lệ như cũ
                position = Position(x = 0.0f, y = -3f, z = -0.75f)
                scale = Position(x = 5f, y = 5f, z = 5f)
                centerOrigin(Position(0f, 0f, 0f))
                isEditable = true
            }
            characterNode = node
        }
    }

    // --- DỌN DẸP KHI ẨN ---
    DisposableEffect(Unit) {
        onDispose {
            characterNode?.destroy() // Chỉ hủy Node hiển thị
            characterNode = null
        }
    }
// Khi characterNode thay đổi, danh sách nodes sẽ được tạo lại
    val childNodes = remember(characterNode) {
        if (characterNode != null) listOf(characterNode!!) else emptyList()
    }
    // --- GIAO DIỆN ---
    Box(modifier = Modifier.fillMaxSize()) {
        // Logic: Chỉ cần có ModelInstance là vẽ, Environment có hay không cũng được (để tránh kẹt loading)
        if (modelInstance == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Scene(
                modifier = Modifier.fillMaxSize().background(Color.Transparent),
                engine = engine,
                // modelLoader = modelLoader, // Không cần nữa

                mainLightNode = rememberMainLightNode(engine) {
                    intensity = 70_000.0f
                    isShadowCaster = true
                },
                cameraNode = rememberCameraNode(engine) {
                    position = Position(z = 2f)
                },

                // --- SỬA LỖI QUAN TRỌNG Ở ĐÂY ---
                // Thêm `characterNode` vào trong ngoặc đơn () của rememberNodes
                // Để khi characterNode thay đổi (từ null -> có), SceneView sẽ cập nhật lại danh sách con
                childNodes =childNodes,

                // Camera Manipulator: Cho phép dùng tay xoay nhân vật

                environment = environment!!
            )
        }
    }
}