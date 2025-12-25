package com.hellodoc.healthcaresystem.view.model_human

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.filament.Engine
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GestureFrame
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Rotation
import io.github.sceneview.Scene
import io.github.sceneview.environment.Environment
import io.github.sceneview.math.Position
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberMainLightNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import updateBoneRotation
import java.io.InputStream
import kotlin.math.roundToInt

@Composable
fun SignLanguageAnimatableScreen(
    engine: Engine,
    modelInstance: ModelInstance?,
    environment: Environment?
) {
    val context = LocalContext.current
    var characterNode by remember { mutableStateOf<ModelNode?>(null) }

    // Animation state
    var gestureFrames by remember { mutableStateOf<List<GestureFrame>>(emptyList()) }
    var currentFrameIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // ===== ANIMATABLE CHÍNH =====
    val frameProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        try {
            val inputStream: InputStream = context.assets.open("model3d_gestures.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val json = Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
            println("Đọc được json "+ json)
            val frames = json.decodeFromString(
                ListSerializer(GestureFrame.serializer()),
                jsonString
            )

            gestureFrames = frames
            isLoading = false

            // ===== DEBUG LOG =====
            Log.d("SignLanguage", "✅ Loaded ${frames.size} frames")

            // Test frame 0
            Log.d("SignLanguage", "Frame 0 spine_01: ${frames[0].gestures.spine.spine01}")
            Log.d("SignLanguage", "Frame 0 neck: ${frames[0].gestures.neckHead.neck}")

            // Test frame 17 (first frame with right_arm data)
            Log.d("SignLanguage", "Frame 17 shoulder_r: ${frames[17].gestures.rightArm.shoulderR}")
            Log.d("SignLanguage", "Frame 17 hand_r: ${frames[17].gestures.rightArm.handR}")
            Log.d("SignLanguage", "Frame 17 thumb: ${frames[17].gestures.rightArm.fingers.thumb}")
            // ===================

        } catch (e: Exception) {
            Log.e("SignLanguage", "❌ Error loading JSON", e)
            e.printStackTrace()
        }
    }

    // ===== KỊCH BẢN ANIMATION (giống code cũ) =====
    LaunchedEffect(gestureFrames) {
        if (gestureFrames.isEmpty()) return@LaunchedEffect

        var frameIndex = 0

        while (isActive) {
            currentFrameIndex = frameIndex

            // Animate từ 0 → 1 trong 1 giây (mượt mà)
            frameProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 100,
                    easing = LinearEasing
                )
            )

            // Giữ ở frame cuối 500ms trước khi chuyển
            delay(200)

            // Reset về 0 và chuyển frame
            frameProgress.snapTo(0f)
            frameIndex = if (frameIndex >= gestureFrames.size - 1) 0 else frameIndex + 1
        }
    }

    // ===== LOGIC CẬP NHẬT XƯƠNG (giống code cũ với snapshotFlow) =====
    LaunchedEffect(engine, modelInstance, gestureFrames) {
        if (modelInstance == null || gestureFrames.isEmpty()) return@LaunchedEffect

        snapshotFlow {
            Triple(frameProgress.value, currentFrameIndex, gestureFrames.size)
        }.collect { (progress, frameIdx, totalFrames) ->

            // Lấy frame hiện tại và frame tiếp theo
            val currentFrame = gestureFrames[frameIdx]
            val nextFrameIdx = if (frameIdx >= totalFrames - 1) 0 else frameIdx + 1
            val nextFrame = gestureFrames[nextFrameIdx]

            // Apply interpolated rotations
            applyInterpolatedFrameRotations(
                engine,
                modelInstance,
                currentFrame,
                nextFrame,
                progress
            )
        }
    }

    // Setup scene
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
        if (modelInstance == null || isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Scene(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
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

// =======================
// INTERPOLATED ROTATIONS - SỬA LẠI
// =======================
fun applyInterpolatedFrameRotations(
    engine: Engine,
    modelInstance: ModelInstance,
    currentFrame: GestureFrame,
    nextFrame: GestureFrame,
    progress: Float  // 0.0 → 1.0
) {
    val currentBones = currentFrame.gestures
    val nextBones = nextFrame.gestures

    println("Vào được applyInterpolatedFrameRotations "+currentBones+"\n"+ nextBones)
    // ===== LEFT ARM =====
    interpolateAndApply(
        engine, modelInstance, "shoulder_l",
        currentBones.leftArm.shoulderL, nextBones.leftArm.shoulderL, progress
    )

    // CẬP NHẬT: Thêm default cho Upperarm Left
    interpolateAndApply(
        engine, modelInstance, "upperarm_l",
        currentBones.leftArm.upperarmL, nextBones.leftArm.upperarmL, progress,
        defaultRotStr = "upperarm_l(x=0, y=80, z=0)" // <--- Giá trị mặc định bạn yêu cầu
    )

    // CẬP NHẬT: Thêm default cho Lowerarm Left
    interpolateAndApply(
        engine, modelInstance, "lowerarm_l",
        currentBones.leftArm.lowerarmL, nextBones.leftArm.lowerarmL, progress,
        defaultRotStr = "lowerarm_l(x=0, y=0, z=0)" // <--- Giá trị mặc định bạn yêu cầu
    )

    interpolateAndApply(
        engine, modelInstance, "hand_l",
        currentBones.leftArm.handL, nextBones.leftArm.handL, progress
    )
    // Spine
    interpolateAndApply(
        engine, modelInstance, "spine_01",
        currentBones.spine.spine01, nextBones.spine.spine01, progress
    )
    interpolateAndApply(
        engine, modelInstance, "spine_02",
        currentBones.spine.spine02, nextBones.spine.spine02, progress
    )
    interpolateAndApply(
        engine, modelInstance, "spine_03",
        currentBones.spine.spine03, nextBones.spine.spine03, progress
    )

    // Neck & Head
    interpolateAndApply(
        engine, modelInstance, "neck",
        currentBones.neckHead.neck, nextBones.neckHead.neck, progress
    )
    interpolateAndApply(
        engine, modelInstance, "head",
        currentBones.neckHead.head, nextBones.neckHead.head, progress
    )

    // Facial
    interpolateAndApply(engine, modelInstance, "jaw", currentBones.facial.jaw, nextBones.facial.jaw, progress)
    interpolateAndApply(engine, modelInstance, "eye_l", currentBones.facial.eyeL, nextBones.facial.eyeL, progress)
    interpolateAndApply(engine, modelInstance, "eye_r", currentBones.facial.eyeR, nextBones.facial.eyeR, progress)
    interpolateAndApply(engine, modelInstance, "eyelid_l", currentBones.facial.eyelidL, nextBones.facial.eyelidL, progress)
    interpolateAndApply(engine, modelInstance, "eyelid_r", currentBones.facial.eyelidR, nextBones.facial.eyelidR, progress)
    interpolateAndApply(engine, modelInstance, "eyebrow_l", currentBones.facial.eyebrowL, nextBones.facial.eyebrowL, progress)
    interpolateAndApply(engine, modelInstance, "eyebrow_r", currentBones.facial.eyebrowR, nextBones.facial.eyebrowR, progress)
    interpolateAndApply(engine, modelInstance, "mouth_l", currentBones.facial.mouthL, nextBones.facial.mouthL, progress)
    interpolateAndApply(engine, modelInstance, "mouth_r", currentBones.facial.mouthR, nextBones.facial.mouthR, progress)

    // Left Arm
    interpolateAndApply(engine, modelInstance, "shoulder_l", currentBones.leftArm.shoulderL, nextBones.leftArm.shoulderL, progress)
    interpolateAndApply(engine, modelInstance, "upperarm_l", currentBones.leftArm.upperarmL, nextBones.leftArm.upperarmL, progress)
    interpolateAndApply(engine, modelInstance, "lowerarm_l", currentBones.leftArm.lowerarmL, nextBones.leftArm.lowerarmL, progress)
    interpolateAndApply(engine, modelInstance, "hand_l", currentBones.leftArm.handL, nextBones.leftArm.handL, progress)

    // Left Fingers
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.leftArm.fingers.thumb, nextBones.leftArm.fingers.thumb, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.leftArm.fingers.index, nextBones.leftArm.fingers.index, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.leftArm.fingers.middle, nextBones.leftArm.fingers.middle, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.leftArm.fingers.ring, nextBones.leftArm.fingers.ring, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.leftArm.fingers.pinky, nextBones.leftArm.fingers.pinky, progress)

    // Right Arm
    interpolateAndApply(engine, modelInstance, "shoulder_r", currentBones.rightArm.shoulderR, nextBones.rightArm.shoulderR, progress)
    interpolateAndApply(engine, modelInstance,
        "upperarm_r",
        currentBones.rightArm.upperarmR,
        nextBones.rightArm.upperarmR, progress,
        defaultRotStr = "upperarm_l(x=0, y=80, z=0)")
    interpolateAndApply(engine, modelInstance, "lowerarm_r", currentBones.rightArm.lowerarmR, nextBones.rightArm.lowerarmR, progress)
    interpolateAndApply(engine, modelInstance, "hand_r", currentBones.rightArm.handR, nextBones.rightArm.handR, progress)

    // Right Fingers
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.rightArm.fingers.thumb, nextBones.rightArm.fingers.thumb, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.rightArm.fingers.index, nextBones.rightArm.fingers.index, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.rightArm.fingers.middle, nextBones.rightArm.fingers.middle, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.rightArm.fingers.ring, nextBones.rightArm.fingers.ring, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.rightArm.fingers.pinky, nextBones.rightArm.fingers.pinky, progress)
}

/**
 * Interpolate và apply rotation cho 1 bone - GIỮ TRẠNG THÁI MẶC ĐỊNH
 */
/**
 * Interpolate và apply rotation cho 1 bone
 * Logic: Nếu JSON rỗng -> Dùng defaultRotStr. Nếu cả 2 rỗng -> Bỏ qua.
 */
fun interpolateAndApply(
    engine: Engine,
    modelInstance: ModelInstance,
    boneName: String,
    currentRotStr: String,
    nextRotStr: String,
    progress: Float,
    defaultRotStr: String = "" // <--- THÊM THAM SỐ NÀY (mặc định là chuỗi rỗng)
) {
    // 1. Xác định chuỗi dữ liệu thực tế sẽ dùng
    // Nếu trong file JSON là chuỗi rỗng (""), ta thay thế bằng defaultRotStr do bạn quy định
    val effectiveCurrentStr = if (currentRotStr.isNotBlank()) currentRotStr else defaultRotStr
    val effectiveNextStr = if (nextRotStr.isNotBlank()) nextRotStr else defaultRotStr

    // 2. Kiểm tra lại: Nếu sau khi thay thế mà vẫn rỗng (tức là không có JSON và không có Default)
    // thì mới return (bỏ qua xương này)
    if (effectiveCurrentStr.isBlank() && effectiveNextStr.isBlank()) {
        return
    }

    // Log kiểm tra xem nó đang dùng JSON hay Default
    // Log.d("SignLanguage", "Bone: $boneName | Using: $effectiveCurrentStr -> $effectiveNextStr")

    // 3. Lấy rotation hiện tại từ engine (để làm fallback cuối cùng hoặc để tính toán nội suy mượt mà từ trạng thái hiện tại)
    val runtimeRot = getCurrentBoneRotation(engine, modelInstance, boneName)

    // 4. Parse dữ liệu (Lúc này parse effectiveCurrentStr thay vì currentRotStr gốc)
    val currentRot = if (effectiveCurrentStr.isNotBlank()) {
        Rotation.fromString(effectiveCurrentStr)
    } else {
        runtimeRot
    }

    val nextRot = if (effectiveNextStr.isNotBlank()) {
        Rotation.fromString(effectiveNextStr)
    } else {
        currentRot // Nếu frame sau không có, giữ nguyên frame trước
    }

    // 5. Nội suy và apply
    val x = currentRot.x + (nextRot.x - currentRot.x) * progress
    val y = currentRot.y + (nextRot.y - currentRot.y) * progress
    val z = currentRot.z + (nextRot.z - currentRot.z) * progress

    updateBoneRotation(engine, modelInstance, boneName, x, y, z)
}

/**
 * Interpolate và apply rotations cho nhiều bones (fingers) - GIỮ TRẠNG THÁI MẶC ĐỊNH
 */
fun interpolateAndApplyMultiple(
    engine: Engine,
    modelInstance: ModelInstance,
    currentRotStr: String,
    nextRotStr: String,
    progress: Float
) {
    // Nếu cả 2 frame đều KHÔNG có giá trị → không làm gì
    if (currentRotStr.isBlank() && nextRotStr.isBlank()) {
        return
    }

    // Parse JSON
    val currentRotations = Rotation.parseMultiple(currentRotStr)
    val nextRotations = Rotation.parseMultiple(nextRotStr)

    // Nếu cả 2 đều rỗng → không làm gì
    if (currentRotations.isEmpty() && nextRotations.isEmpty()) {
        return
    }

    // Union tất cả bones
    val allBoneNames = (currentRotations.keys + nextRotations.keys).toSet()

    allBoneNames.forEach { boneName ->
        // Runtime rotation (fallback - trạng thái hiện tại)
        val runtimeRot = getCurrentBoneRotation(engine, modelInstance, boneName)

        val currentRot = currentRotations[boneName] ?: runtimeRot
        val nextRot = nextRotations[boneName] ?: currentRot

        val x = currentRot.x + (nextRot.x - currentRot.x) * progress
        val y = currentRot.y + (nextRot.y - currentRot.y) * progress
        val z = currentRot.z + (nextRot.z - currentRot.z) * progress

        updateBoneRotation(engine, modelInstance, boneName, x, y, z)
    }
}

fun getCurrentBoneRotation(
    engine: Engine,
    modelInstance: ModelInstance,
    boneName: String
): Rotation {
    val entity = modelInstance.asset.getFirstEntityByName(boneName)
    if (entity == 0) return Rotation()

    val tcm = engine.transformManager
    val instance = tcm.getInstance(entity)
    if (instance == 0) return Rotation()

    val mat = FloatArray(16)
    tcm.getTransform(instance, mat)

    return Rotation.fromMatrix(mat)
}











