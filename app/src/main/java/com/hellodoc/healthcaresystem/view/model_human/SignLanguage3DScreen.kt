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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.filament.Engine
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GestureCodeResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GestureFrame
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Rotation
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Rotation.Companion.fromString
import com.hellodoc.healthcaresystem.view.user.supportfunction.SceneViewManager
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
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
    environment: Environment?,
    videoUrl: String
) {
    val context = LocalContext.current
    var characterNode by remember { mutableStateOf<ModelNode?>(null) }

    // Animation state
    var gestureFrames by remember { mutableStateOf<List<GestureFrame>>(emptyList()) }
    var currentFrameIndex by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Thêm state để theo dõi từ hiện tại
    var currentWordIndex by remember { mutableStateOf(0) }
    var allWords by remember { mutableStateOf<List<GestureCodeResponse>>(emptyList()) }

    // ===== ANIMATABLE CHÍNH =====
    val frameProgress = remember { Animatable(0f) }

    val postViewModel: PostViewModel = hiltViewModel()
    val gestureCode = postViewModel.gestureCode.collectAsState()

    LaunchedEffect(Unit,videoUrl)  {
        try {
            Log.d("SignLanguage", "🚀 Gọi getGestureCode với URL: $videoUrl")
            isLoading = true
            postViewModel.getGestureCode(videoUrl)
        } catch (e: Exception) {
            Log.e("SignLanguage", "❌ Error calling API", e)
            isLoading = false
        }
    }

    // ===== LẮNG NGHE DỮ LIỆU TỪ API =====
    LaunchedEffect(gestureCode.value) {
        try {
            val response = gestureCode.value

            Log.d("SignLanguage", "📦 Response received: ${response?.size ?: 0} words")

            // Kiểm tra response không null và có dữ liệu
            if (response.isNotEmpty()) {
                allWords = response

                // ===== GHÉP TẤT CẢ CÁC FRAME CỦA TẤT CẢ CÁC TỪ =====
                val allFrames = mutableListOf<GestureFrame>()

                response.forEach { wordData ->
                    Log.d("SignLanguage", "📝 Processing word: '${wordData.word}' (${wordData.gestureData.size} frames)")
                    allFrames.addAll(wordData.gestureData)
                }

                if (allFrames.isNotEmpty()) {
                    gestureFrames = allFrames
                    isLoading = false

                    // ===== DEBUG LOG CHI TIẾT =====
                    Log.d("SignLanguage", "=" .repeat(50))
                    Log.d("SignLanguage", "✅ LOADED SUCCESSFULLY")
                    Log.d("SignLanguage", "✅ Total words: ${response.size}")
                    response.forEachIndexed { index, word ->
                        Log.d("SignLanguage", "   [$index] '${word.word}' -> ${word.gestureData.size} frames (accuracy: ${word.accuracy}%)")
                    }
                    Log.d("SignLanguage", "✅ Total frames combined: ${allFrames.size}")
                    Log.d("SignLanguage", "=" .repeat(50))

                    // Log frame đầu tiên để kiểm tra cấu trúc dữ liệu
                    if (allFrames.isNotEmpty()) {
                        val firstFrame = allFrames.first()
                        Log.d("SignLanguage", "🔍 First frame sample:")
                        Log.d("SignLanguage", "   - Frame: ${firstFrame.frame}")
                        Log.d("SignLanguage", "   - Timestamp: ${firstFrame.timestamp}")
                        Log.d("SignLanguage", "   - upperarm_l: ${firstFrame.gestures.upperarmL}")
                        Log.d("SignLanguage", "   - upperarm_r: ${firstFrame.gestures.upperarmR}")
                    }
                } else {
                    Log.w("SignLanguage", "⚠️ API returned empty gesture data")
                    isLoading = false
                }
            } else {
                Log.w("SignLanguage", "⚠️ Response is null or empty")
            }
        } catch (e: Exception) {
            Log.e("SignLanguage", "❌ Error processing API response", e)
            e.printStackTrace()
            isLoading = false
        }
    }

    // ===== KỊCH BẢN ANIMATION =====
    // ===== THÊM ĐOẠN NÀY: ANIMATION LOOP =====
    LaunchedEffect(gestureFrames) {
        if (gestureFrames.isEmpty()) return@LaunchedEffect

        Log.d("SignLanguage", "🎬 Starting animation loop with ${gestureFrames.size} frames")

        while (isActive) { // Loop vô hạn
            for (frameIndex in gestureFrames.indices) {
                currentFrameIndex = frameIndex

                // Animate từ 0.0 -> 1.0 trong khoảng thời gian của frame
                val currentFrame = gestureFrames[frameIndex]
                val nextFrame = gestureFrames.getOrNull(frameIndex + 1)
                    ?: gestureFrames.first() // Loop lại frame đầu

                val frameDuration = if (frameIndex < gestureFrames.size - 1) {
                    ((nextFrame.timestamp - currentFrame.timestamp) * 1000).toLong()
                } else {
                    33L // Frame cuối dùng ~30fps
                }

                // Log mỗi 30 frames
                if (frameIndex % 30 == 0) {
                    Log.d("SignLanguage", "🎬 Frame $frameIndex/${gestureFrames.size} | Duration: ${frameDuration}ms")
                }

                // Animate progress từ 0 -> 1
                frameProgress.snapTo(0f)
                frameProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = frameDuration.toInt(),
                        easing = LinearEasing
                    )
                )
            }

            // Sau khi hết tất cả frames, lặp lại từ đầu
            Log.d("SignLanguage", "🔄 Animation completed, looping...")
        }
    }

    // ===== LOGIC CẬP NHẬT XƯƠNG =====
    LaunchedEffect(engine, modelInstance, gestureFrames) {
        if (modelInstance == null || gestureFrames.isEmpty()) {
            Log.d("SignLanguage", "⏸️ Waiting for model or frames...")
            return@LaunchedEffect
        }

        Log.d("SignLanguage", "🦴 Starting bone update loop")

        snapshotFlow {
            Triple(frameProgress.value, currentFrameIndex, gestureFrames.size)
        }.collect { (progress, frameIdx, totalFrames) ->

            val currentFrame = gestureFrames[frameIdx]
            val nextFrameIdx = if (frameIdx >= totalFrames - 1) 0 else frameIdx + 1
            val nextFrame = gestureFrames[nextFrameIdx]

            // Log mỗi 30 frames để không spam log
            if (frameIdx % 30 == 0) {
                Log.d("SignLanguage", "🎬 Frame $frameIdx/$totalFrames (progress: ${(progress * 100).toInt()}%)")
            }

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
            Log.d("SignLanguage", "✅ Character node created")
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

            val check = asset.getFirstEntityByName("upperarm_l")
            if (check == 0) {
                Log.e("CheckBone", "❌ LỖI: Không tìm thấy xương tên 'upperarm_l'")
            } else {
                Log.d("CheckBone", "✅ TÌM THẤY: 'upperarm_l' có Entity ID = $check")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("SignLanguage", "🧹 Cleaning up character node")
            characterNode?.destroy()
            characterNode = null
        }
    }

    val childNodes = remember(characterNode) {
        if (characterNode != null) listOf(characterNode!!) else emptyList()
    }

    // Box chứa Scene
    Box(modifier = Modifier.clip(CircleShape)) {
        if (engine != null && modelInstance != null && environment != null && !isLoading) {
            key(engine) {
                Scene(
                    engine = engine,
                    mainLightNode = rememberMainLightNode(engine) {
                        intensity = 70_000.0f
                        isShadowCaster = true
                    },
                    cameraNode = rememberCameraNode(engine) {
                        position = Position(z = 2f)
                    },
                    childNodes = childNodes,
                    environment = environment,
                    modifier = Modifier.clip(CircleShape)
                )
            }

            DisposableEffect(Unit) {
                onDispose {
                    Log.d("SignLanguage", "🛑 Scene disposed, blocking GPU")
                    SceneViewManager.blockUntilGPUCompletes()
                }
            }
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading gesture data...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }


//        //Thông tin debug
//        Text(
//            text = "Frame: ${currentFrameIndex + 1} / ${gestureFrames.size}",
//            modifier = Modifier.align(Alignment.BottomCenter)
//        )


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
    val effectiveCurrentStr = currentRotStr.ifBlank { defaultRotStr }
    val effectiveNextStr = nextRotStr.ifBlank { defaultRotStr }

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
// =======================
// THÊM HÀM NÀY VÀO CODE CỦA BẠN
// =======================

/**
 * Xử lý chuỗi chứa nhiều xương (ngón tay, mắt, lông mày)
 * VD: "thumb_02_l(x=10, y=5, z=0); thumb_03_l(x=15, y=10, z=5)"
 */
fun interpolateAndApplyMultiple(
    engine: Engine,
    modelInstance: ModelInstance,
    currentRotStr: String,
    nextRotStr: String,
    progress: Float
) {
    // Nếu cả 2 đều rỗng thì bỏ qua
    if (currentRotStr.isBlank() && nextRotStr.isBlank()) {
        return
    }

    // Parse chuỗi thành Map<boneName, Rotation>
    val currentBones = Rotation.parseMultiple(currentRotStr)
    val nextBones = Rotation.parseMultiple(nextRotStr)

    // Lấy tất cả tên xương từ cả 2 frame
    val allBoneNames = (currentBones.keys + nextBones.keys).distinct()

    // Xử lý từng xương
    allBoneNames.forEach { boneName ->
        val currentRot = currentBones[boneName] ?: Rotation(0f, 0f, 0f)
        val nextRot = nextBones[boneName] ?: currentRot

        // Nội suy
        val x = currentRot.x + (nextRot.x - currentRot.x) * progress
        val y = currentRot.y + (nextRot.y - currentRot.y) * progress
        val z = currentRot.z + (nextRot.z - currentRot.z) * progress

        // Apply rotation
        updateBoneRotation(engine, modelInstance, boneName, x, y, z)
    }
}

// =======================
// SỬA HÀM updateBoneRotation
// =======================



// =======================
// KIỂM TRA LẠI HÀM Rotation.parseMultiple
// =======================

// Đảm bảo hàm này trong companion object của data class Rotation:
fun parseMultiple(data: String): Map<String, Rotation> {
    if (data.isBlank()) return emptyMap()

    val result = mutableMapOf<String, Rotation>()

    // 1. Tách các cụm xương bằng dấu chấm phẩy ;
    val parts = data.split(";")

    for (part in parts) {
        val trimmed = part.trim()
        if (trimmed.isEmpty()) continue

        // 2. Tách tên xương ra khỏi dữ liệu xoay
        val openParenIndex = trimmed.indexOf('(')

        if (openParenIndex > 0) {
            val boneName = trimmed.substring(0, openParenIndex).trim()
            // Phần còn lại chính là data để parse rotation
            val rotationData = trimmed.substring(openParenIndex)

            result[boneName] = fromString(rotationData)
        }
    }
    return result
}

// =======================
// OPTIONAL: Thêm log để debug
// =======================

fun debugBoneRotations(frame: GestureFrame) {
    Log.d("BoneDebug", "=== Frame ${frame.frame} ===")
    Log.d("BoneDebug", "upperarm_l: ${frame.gestures.upperarmL}")
    Log.d("BoneDebug", "thumb_l: ${frame.gestures.thumbL}")
    Log.d("BoneDebug", "index_l: ${frame.gestures.indexL}")

    // Test parseMultiple
    if (frame.gestures.thumbL.isNotBlank()) {
        val parsed = Rotation.parseMultiple(frame.gestures.thumbL)
        parsed.forEach { (name, rot) ->
            Log.d("BoneDebug", "  $name -> x=${rot.x}, y=${rot.y}, z=${rot.z}")
        }
    }
}

// Gọi trong LaunchedEffect để test:
// debugBoneRotations(gestureFrames.first())

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











