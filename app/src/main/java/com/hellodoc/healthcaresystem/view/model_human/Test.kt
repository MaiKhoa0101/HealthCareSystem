package com.hellodoc.healthcaresystem.view.model_human

import androidx.compose.animation.core.*
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
import com.google.android.filament.Engine
import io.github.sceneview.environment.Environment
import io.github.sceneview.model.ModelInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.collect
import updateBoneRotation
import kotlin.math.sin

@Composable
fun Simple3DScreen(
    engine: Engine,
    modelInstance: ModelInstance?,
    environment: Environment?
) {
    var characterNode by remember { mutableStateOf<ModelNode?>(null) }

    // --- 1. BIẾN ANIMATION ---
    val helloAnim = remember { Animatable(0f) }
    val bowAnim = remember { Animatable(0f) }

    // --- 2. KỊCH BẢN CHUYỂN ĐỘNG ---
    LaunchedEffect(Unit) {
        while (isActive) {
            // == HELLO (Vẫy tay) ==
            helloAnim.animateTo(1f, tween(500, easing = LinearEasing))
            delay(1000)
            helloAnim.animateTo(0f, tween(500, easing = LinearEasing))
            delay(1000)

            // == BOW (Cúi chào) ==
            bowAnim.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
            delay(1000)
            bowAnim.animateTo(0f, tween(800, easing = FastOutSlowInEasing))
            delay(1000)
        }
    }

// --- 3. LOGIC CẬP NHẬT XƯƠNG ---
    LaunchedEffect(engine, modelInstance) {
        if (modelInstance != null) {
            snapshotFlow { Pair(helloAnim.value, bowAnim.value) }.collect { (helloVal, bowVal) ->

                // --- 1. TAY TRÁI ---

                // Trạng thái BẮT ĐẦU (Kẹp tay nghỉ):
                // Y = 80 (Kẹp nách), Z = 0 (Thẳng đuột)
                val startX = 0f
                val startY = 80f
                val startZ = 0f

                // Trạng thái KẾT THÚC (Chìa tay ra trước):
                // Y = 10 (Hơi tách khỏi sườn để tránh Gimbal Lock và tự nhiên hơn)
                // Z = 90 (Đưa thẳng ra trước - số dương hay âm tùy chiều rig của bạn)
                // Dựa vào mô tả của bạn "xoay 1 vòng sau ra trước", tôi giả định là 90 hoặc -90.
                val endX = 0
                val endY = -35f
                val endZ = -90f // Thử -90 trước, nếu ra sau lưng thì đổi thành 90

                // --- NỘI SUY (Interpolation) ---
                val currentX = startX + (helloVal * (endX - startX))
                val currentY = startY + (helloVal * (endY - startY))
                val currentZ = startZ + (helloVal * (endZ - startZ))

                updateBoneRotation(
                    engine, modelInstance, "upperarm_l",
                    x = currentX,          // Trục X (Vặn) giữ nguyên hoặc chỉnh nhẹ nếu bàn tay bị úp/ngửa sai
                    y = currentY,    // QUAN TRỌNG: Phải giảm Y để tránh khóa trục
                    z = currentZ     // Trục Z chịu trách nhiệm đưa tay ra trước
                )

                updateBoneRotation(
                    engine, modelInstance, "upperarm_r",
                    x = 0f,
                    y = 80f,
                    z = 0f
                )
            }
        }
    }

    // --- SETUP SCENE ---
    LaunchedEffect(modelInstance) {
        if (modelInstance != null) {
            val node = ModelNode(
                modelInstance = modelInstance,
                scaleToUnits = 1.0f
            ).apply {
                position = Position(x = 0.0f, y = -3f, z = -0.75f)
                scale = Position(x = 5f, y = 5f, z = 5f)
                centerOrigin(Position(0f, 0f, 0f))
                isEditable = true
            }
            characterNode = node
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            characterNode?.destroy()
            characterNode = null
        }
    }

    val childNodes = remember(characterNode) {
        if (characterNode != null) listOf(characterNode!!) else emptyList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (modelInstance == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Scene(
                modifier = Modifier.fillMaxSize().background(Color.Transparent),
                engine = engine,
                mainLightNode = rememberMainLightNode(engine) {
                    intensity = 70_000.0f
                    isShadowCaster = true
                },
                cameraNode = rememberCameraNode(engine) {
                    position = Position(z = 2f)
                },
                childNodes = childNodes,
                environment = environment!!
            )
        }
    }
}