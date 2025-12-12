package com.hellodoc.healthcaresystem.view.model_human

import android.service.wallpaper.WallpaperService
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
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import java.nio.ByteBuffer
import io.github.sceneview.model.ModelInstance
import com.google.android.filament.Engine // Import thêm cái này
import io.github.sceneview.environment.Environment
import io.github.sceneview.rememberEnvironment

@Composable
fun Simple3DScreen(
    engine: Engine,
    modelInstance: ModelInstance?, // Nhận Model đã nạp sẵn
    environment: Environment?      // Nhận Môi trường đã nạp sẵn
) {
    // State lưu Node hiển thị
    var characterNode by remember { mutableStateOf<ModelNode?>(null) }

    // --- TẠO NODE TỪ DỮ LIỆU ĐÃ CÓ (KHÔNG NẠP LẠI TỪ FILE) ---
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

    // --- GIAO DIỆN ---
    Box(modifier = Modifier.fillMaxSize()) {
        if (modelInstance == null || environment == null) {
            // Hiển thị Loading nếu dữ liệu chưa được truyền xuống kịp
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Scene(
                modifier = Modifier.fillMaxSize().background(Color.Transparent), // Nền trong suốt
                engine = engine,
                // modelLoader = modelLoader, // Không cần nữa

                mainLightNode = rememberMainLightNode(engine) {
                    intensity = 70_000.0f
                    isShadowCaster = true
                },
                cameraNode = rememberCameraNode(engine) {
                    position = Position(z = 2.5f)
                },
                childNodes = rememberNodes {
                    characterNode?.let { add(it) }
                },
                environment = environment // Dùng môi trường đã nạp sẵn
            )
        }
    }
}