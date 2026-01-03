package com.hellodoc.healthcaresystem.view.model_human

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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

@Composable
fun SignLanguageAnimatableScreen(
    engine: Engine?,
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
                    durationMillis = 300,
                    easing = LinearEasing
                )
            )

            // Giữ ở frame cuối 500ms trước khi chuyển
            delay(50)

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
                engine!!,
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
    LaunchedEffect(modelInstance) {
        if (modelInstance != null) {
            val asset = modelInstance.asset
            val entities = asset.entities
            Log.d("CheckBone", "=== DANH SÁCH TÊN XƯƠNG TRONG FILE 3D ===")
            entities.forEach { entity ->
                val name = asset.getName(entity)
                if (!name.isNullOrEmpty()) {
                    Log.d("CheckBone", "Bone Entity: $entity | Name: '$name'")
                }
            }

            // Kiểm tra trực tiếp xương đang lỗi
            val check = asset.getFirstEntityByName("upperarm_l")
            if (check == 0) {
                Log.e("CheckBone", "❌ LỖI: Không tìm thấy xương tên 'upperarm_l'. Hãy kiểm tra danh sách trên để lấy tên đúng!")
            } else {
                Log.d("CheckBone", "✅ TÌM THẤY: 'upperarm_l' có Entity ID = $check")
            }
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

    Box(modifier = Modifier.clip(CircleShape)) {
        if (modelInstance == null || isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Scene(
                engine = engine!!,
                mainLightNode = rememberMainLightNode(engine) {
                    intensity = 70_000.0f
                    isShadowCaster = true
                },
                cameraNode = rememberCameraNode(engine) {
                    position = Position(z = 2f)
                },
                childNodes = childNodes,
                environment = environment!!,
                modifier = Modifier.clip(CircleShape)
            )
        }

//        //Thông tin debug
//        Text(
//            text = "Frame: ${currentFrameIndex + 1} / ${gestureFrames.size}",
//            modifier = Modifier.align(Alignment.BottomCenter)
//        )

    }
}

// =======================
// UPDATED ANIMATION LOGIC
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

    // 1. SPINE
    interpolateAndApply(engine, modelInstance, "spine_01", currentBones.spine01, nextBones.spine01, progress)
    interpolateAndApply(engine, modelInstance, "spine_02", currentBones.spine02, nextBones.spine02, progress)
    interpolateAndApply(engine, modelInstance, "spine_03", currentBones.spine03, nextBones.spine03, progress)

    // 2. NECK & HEAD
    interpolateAndApply(engine, modelInstance, "neck", currentBones.neck, nextBones.neck, progress)
    interpolateAndApply(engine, modelInstance, "head", currentBones.head, nextBones.head, progress)

    // 3. FACIAL (Single Bones)
    interpolateAndApply(engine, modelInstance, "jaw", currentBones.jaw, nextBones.jaw, progress)
    interpolateAndApply(engine, modelInstance, "eyelid_l", currentBones.eyelidL, nextBones.eyelidL, progress)
    interpolateAndApply(engine, modelInstance, "eyelid_r", currentBones.eyelidR, nextBones.eyelidR, progress)
    interpolateAndApply(engine, modelInstance, "mouth_l", currentBones.mouthL, nextBones.mouthL, progress)
    interpolateAndApply(engine, modelInstance, "mouth_r", currentBones.mouthR, nextBones.mouthR, progress)

    // 4. FACIAL (Combined/Multiple Bones)
    // JSON: "eyes": "eye_l(...); eye_r(...)" -> Dùng hàm Multiple
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.eyes, nextBones.eyes, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.eyebrows, nextBones.eyebrows, progress)

    // 5. LEFT ARM
    interpolateAndApply(engine, modelInstance, "shoulder_l", currentBones.shoulderL, nextBones.shoulderL, progress)

    // Upperarm Left (Có default như yêu cầu cũ)
    interpolateAndApply(
        engine, modelInstance, "upperarm_l",
        currentBones.upperarmL, nextBones.upperarmL, progress,
        defaultRotStr = "upperarm_l(x=0, y=80, z=0)"
    )

    interpolateAndApply(engine, modelInstance, "lowerarm_l", currentBones.lowerarmL, nextBones.lowerarmL, progress)
    interpolateAndApply(engine, modelInstance, "hand_l", currentBones.handL, nextBones.handL, progress)

    // 6. LEFT FINGERS (Multiple)
    // JSON: "thumb_l": "thumb_02_l(...); thumb_03_l(...)"
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.thumbL, nextBones.thumbL, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.indexL, nextBones.indexL, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.middleL, nextBones.middleL, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.ringL, nextBones.ringL, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.pinkyL, nextBones.pinkyL, progress)

    // 7. RIGHT ARM
    interpolateAndApply(engine, modelInstance, "shoulder_r", currentBones.shoulderR, nextBones.shoulderR, progress)

    // Upperarm Right (Có default)
    interpolateAndApply(
        engine, modelInstance, "upperarm_r",
        currentBones.upperarmR, nextBones.upperarmR, progress,
        defaultRotStr = "upperarm_r(x=0, y=80, z=0)"
    )

    interpolateAndApply(engine, modelInstance, "lowerarm_r", currentBones.lowerarmR, nextBones.lowerarmR, progress)
    interpolateAndApply(engine, modelInstance, "hand_r", currentBones.handR, nextBones.handR, progress)

    // 8. RIGHT FINGERS (Multiple)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.thumbR, nextBones.thumbR, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.indexR, nextBones.indexR, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.middleR, nextBones.middleR, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.ringR, nextBones.ringR, progress)
    interpolateAndApplyMultiple(engine, modelInstance, currentBones.pinkyR, nextBones.pinkyR, progress)
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

    // Trong hàm interpolateAndApply
    val currentRot = if (effectiveCurrentStr.isNotBlank()) {
        Rotation.fromString(effectiveCurrentStr)
    } else {
        runtimeRot
    }

    // THÊM ĐOẠN LOG NÀY
    if (boneName == "upperarm_l") {
        Log.d("DebugRotation", "String: '$effectiveCurrentStr' -> Parsed: x=${currentRot.x}, y=${currentRot.y}, z=${currentRot.z}")
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











