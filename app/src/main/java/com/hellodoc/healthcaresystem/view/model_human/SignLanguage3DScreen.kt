package com.hellodoc.healthcaresystem.view.model_human

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
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

    // ===== COROUTINE JOB TRACKING =====
    val animationJob = remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    val postViewModel: PostViewModel = hiltViewModel()
    val gestureCode = postViewModel.gestureCode.collectAsState()

    LaunchedEffect(Unit, videoUrl) {
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
                    Log.d("SignLanguage", "=".repeat(50))
                    Log.d("SignLanguage", "✅ LOADED SUCCESSFULLY")
                    Log.d("SignLanguage", "✅ Total words: ${response.size}")
                    response.forEachIndexed { index, word ->
                        Log.d("SignLanguage", "   [$index] '${word.word}' -> ${word.gestureData.size} frames (accuracy: ${word.accuracy}%)")
                    }
                    Log.d("SignLanguage", "✅ Total frames combined: ${allFrames.size}")
                    Log.d("SignLanguage", "=".repeat(50))

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

    // ===== KỊCH BẢN ANIMATION - CHỈ CHẠY 1 LẦN =====
    LaunchedEffect(gestureFrames) {
        if (gestureFrames.isEmpty()) {
            animationJob.value?.cancel()
            animationJob.value = null
            return@LaunchedEffect
        }

        Log.d("SignLanguage", "🎬 Starting animation (ONE-TIME ONLY) with ${gestureFrames.size} frames")

        // Store job reference for cancellation
        animationJob.value = coroutineContext[kotlinx.coroutines.Job]

        try {
            // ✅ FIX: Loại bỏ while(isActive), chỉ chạy 1 lần
            for (frameIndex in gestureFrames.indices step 5) {
                if (!isActive) break // Check cancellation

                currentFrameIndex = frameIndex

                // Animate từ 0.0 -> 1.0 trong khoảng thời gian của frame
                val currentFrame = gestureFrames[frameIndex]
                val nextFrame = gestureFrames.getOrNull(frameIndex + 1)
                    ?: gestureFrames.last() // Frame cuối thì giữ nguyên

                val frameDuration = if (frameIndex < gestureFrames.size - 1) {
                    ((nextFrame.timestamp - currentFrame.timestamp) * 800).toLong()
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

            // ✅ Animation hoàn thành, dừng ở frame cuối
            Log.d("SignLanguage", "✅ Animation completed (one-time playback finished)")

        } catch (e: CancellationException) {
            // Normal when user closes 3D / composable leaves composition
            Log.d("SignLanguage", "⏹️ Animation cancelled")
        } catch (e: Exception) {
            Log.e("SignLanguage", "❌ Animation error", e)
        }
    }

    // ===== LOGIC CẬP NHẬT XƯƠNG =====
    LaunchedEffect(engine, modelInstance, gestureFrames) {
        // Validate resources
        if (engine == null || !engine.isValid || modelInstance == null || gestureFrames.isEmpty()) {
            Log.d("SignLanguage", "⏸️ Waiting for model or frames...")
            return@LaunchedEffect
        }

        Log.d("SignLanguage", "🦴 Starting bone update loop")

        var frameCounter = 0  // Track frames for periodic flushing

        try {
            snapshotFlow {
                Triple(frameProgress.value, currentFrameIndex, gestureFrames.size)
            }.collect { (progress, frameIdx, totalFrames) ->
                // Double-check engine validity during animation
                if (!engine.isValid) {
                    Log.w("SignLanguage", "⚠️ Engine became invalid, stopping bone updates")
                    return@collect
                }

                val currentFrame = gestureFrames[frameIdx]
                // ✅ FIX: Không loop về frame 0, giữ ở frame cuối
                val nextFrameIdx = if (frameIdx >= totalFrames - 1) totalFrames - 1 else frameIdx + 1
                val nextFrame = gestureFrames[nextFrameIdx]

                // Log mỗi 30 frames để không spam log
                if (frameIdx % 30 == 0) {
                    Log.d("SignLanguage", "🎬 Frame $frameIdx/$totalFrames (progress: ${(progress * 100).toInt()}%)")
                }

                applyInterpolatedFrameRotations(
                    engine,
                    modelInstance,
                    currentFrame,
                    nextFrame,
                    progress
                )

                // ===== PERIODIC FLUSH TO PREVENT BUFFER OVERFLOW =====
                // Flush every 60 frames to keep buffer clean
                frameCounter++
                if (frameCounter >= 60) {
                    try {
                        engine.flushAndWait()
                        frameCounter = 0
                        Log.d("SignLanguage", "🧹 Periodic GPU flush (every 60 frames)")
                    } catch (e: Exception) {
                        Log.e("SignLanguage", "❌ Error during periodic flush", e)
                    }
                }
            }
        } catch (e: CancellationException) {
            // Normal when user closes 3D / composable leaves composition
            Log.d("SignLanguage", "⏹️ Bone update cancelled")
        } catch (e: Exception) {
            Log.e("SignLanguage", "❌ Bone update error", e)
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
        if (modelInstance == null) return@LaunchedEffect
        val asset = modelInstance.asset
        val tcm = engine?.transformManager ?: return@LaunchedEffect

        val bonesOfInterest = listOf(
            "upperarm_l", "upperarm_r",
            "lowerarm_l", "lowerarm_r",
            "hand_l", "hand_r",
            "spine_01", "spine_02"
        )

        bonesOfInterest.forEach { boneName ->
            val entity = asset.getFirstEntityByName(boneName)
            if (entity == 0) {
                Log.d("BoneAxis", "$boneName: NOT FOUND")
                return@forEach
            }

            val instance = tcm.getInstance(entity)
            val mat = FloatArray(16)
            tcm.getTransform(instance, mat)

            // Mỗi cột của matrix = 1 trục
            // Cột 0 (mat[0,1,2])  = hướng trục X local
            // Cột 1 (mat[4,5,6])  = hướng trục Y local
            // Cột 2 (mat[8,9,10]) = hướng trục Z local
            Log.d("BoneAxis", "=== $boneName ===")
            Log.d("BoneAxis", "  X-axis: (${mat[0]}, ${mat[1]}, ${mat[2]})")
            Log.d("BoneAxis", "  Y-axis: (${mat[4]}, ${mat[5]}, ${mat[6]})")
            Log.d("BoneAxis", "  Z-axis: (${mat[8]}, ${mat[9]}, ${mat[10]})")
            Log.d("BoneAxis", "  Position: (${mat[12]}, ${mat[13]}, ${mat[14]})")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            Log.d("SignLanguage", "🧹 Starting cleanup sequence...")

            // 1. Cancel animation coroutines
            animationJob.value?.cancel()
            animationJob.value = null
            Log.d("SignLanguage", "✅ Animation job cancelled")

            // 2. Remove node from parent and destroy
            characterNode?.let { node ->
                try {
                    node.parent?.removeChildNode(node)
                    // NOTE:
                    // Avoid destroying Filament entities from Compose dispose; when SceneView's
                    // Surface/SwapChain is stopping this can SIGSEGV in libfilament-jni.so.
                    // Let SceneView/Engine manage entity lifetimes.
                    Log.d("SignLanguage", "✅ Character node detached")
                } catch (e: Exception) {
                    Log.e("SignLanguage", "❌ Error destroying node", e)
                }
            }
            characterNode = null

            // 3. Flush GPU commands
            try {
                engine?.flushAndWait()
                Log.d("SignLanguage", "✅ GPU commands flushed")
            } catch (e: Exception) {
                Log.e("SignLanguage", "❌ Error flushing GPU", e)
            }

            Log.d("SignLanguage", "🧹 Cleanup completed")
        }
    }

    val childNodes = remember(characterNode) {
        if (characterNode != null) listOf(characterNode!!) else emptyList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Validate resources before rendering
        if (engine != null && engine.isValid && modelInstance != null && environment != null && !isLoading) {
            // Use unique key to force Scene recreation when needed
            key("scene_${videoUrl.hashCode()}") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .size(400.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
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
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            DisposableEffect("scene_dispose_${videoUrl.hashCode()}") {
                onDispose {
                    Log.d("SignLanguage", "🛑 Scene disposed, flushing GPU...")
                    try {
                        SceneViewManager.blockUntilGPUCompletes()
                        Log.d("SignLanguage", "✅ GPU flush completed")
                    } catch (e: Exception) {
                        Log.e("SignLanguage", "❌ Error during Scene disposal", e)
                    }
                }
            }
        } else {
            // Loading or invalid state
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (!isLoading && engine?.isValid == false) {
                        "3D Engine unavailable"
                    } else {
                        "Loading gesture data..."
                    },
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
@Composable
fun BoneAxisTestScreen(
    engine: Engine?,
    modelInstance: ModelInstance?,
    environment: Environment?
) {
    data class TestStep(
        val label: String,
        val boneName: String,
        val x: Float, val y: Float, val z: Float,
        val expected: String
    )

    val testSteps = remember {
        listOf(
            TestStep("T-pose reset", "upperarm_l", 0f, 0f, 0f, "Tay về T-pose"),
            TestStep("upperarm_l Y=45", "upperarm_l", 0f, 45f, 0f, "Tay xuống"),
            TestStep("upperarm_l Y=-45", "upperarm_l", 0f, -45f, 0f, "Tay lên"),
            //Lòng bàn tay xoay xuống mặt đất, thực hiện phép xoay 90 độ để kiểm tra cho thao tác xoay upperarm
            TestStep("upperarm_l Y=45", "upperarm_l", 90f, 0f, 0f, "Lòng bàn tay xoay ra đằng sau cơ thể"),
            TestStep("upperarm_l Y=-45", "upperarm_l", -90f, 0f, 0f, "Lòng bàn tay xoay ra đằng trước cơ thể"),
            TestStep("upperarm_l Z=45", "upperarm_l", 0f, 0f, 45f, "Tay bẻ ra sau"),
            TestStep("upperarm_l Z=-45", "upperarm_l", 0f, 0f, -45f, "Tay đưa ra trước"),
            TestStep("Reset", "upperarm_l", 0f, 0f, 0f, "Reset"),
            //lower arm xoay theo hướng cùng hướng và cùng hệ trục như upper arm
            TestStep("hand_l Y=45", "hand_l", 0f, 0f, 45f, "Cổ tay cúi/ngửa?"),
            TestStep("hand_l Y=-45", "hand_l", 0f, 0f, -45f, "Ngược lại?"),

        )
    }

    var currentStep by remember { mutableStateOf(0) }
    val step = testSteps[currentStep]

    var characterNode by remember { mutableStateOf<ModelNode?>(null) }

    // FIX 1: thêm scale + centerOrigin
    LaunchedEffect(modelInstance) {
        if (modelInstance != null) {
            characterNode = ModelNode(
                modelInstance = modelInstance,
                scaleToUnits = 1.0f
            ).apply {
                position = Position(x = 0.0f, y = -3f, z = -0.75f)
                scale = Position(x = 5f, y = 5f, z = 5f)
                centerOrigin(Position(0f, 0f, 0f))
                isEditable = true
            }
        }
    }

    LaunchedEffect(currentStep, engine, modelInstance) {
        if (engine == null || !engine.isValid || modelInstance == null) return@LaunchedEffect
        listOf("upperarm_l", "lowerarm_l", "hand_l").forEach { bone ->
            updateBoneRotation(engine, modelInstance, bone, 0f, 0f, 0f)
        }
        updateBoneRotation(engine, modelInstance, step.boneName, step.x, step.y, step.z)
        Log.d("BoneTest", "[${currentStep}] ${step.label} → (${step.x}, ${step.y}, ${step.z})")
    }

    val childNodes = remember(characterNode) {
        if (characterNode != null) listOf(characterNode!!) else emptyList()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // FIX 3: giữ cấu trúc Box + CircleShape + camera z=2f như bản gốc
        if (engine != null && engine.isValid && modelInstance != null && environment != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(400.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
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
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Test UI overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                .padding(16.dp)
        ) {
            Text(
                text = "Step ${currentStep + 1}/${testSteps.size}  —  ${step.label}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "${step.boneName}  (x=${step.x}, y=${step.y}, z=${step.z})",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "→ Quan sát: ${step.expected}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { if (currentStep > 0) currentStep-- },
                    enabled = currentStep > 0,
                    modifier = Modifier.weight(1f)
                ) { Text("← Trước") }

                Button(
                    onClick = { if (currentStep < testSteps.size - 1) currentStep++ },
                    enabled = currentStep < testSteps.size - 1,
                    modifier = Modifier.weight(1f)
                ) { Text("Tiếp →") }
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = {
                    if (engine != null && engine.isValid && modelInstance != null) {
                        listOf(
                            "upperarm_l", "upperarm_r", "lowerarm_l", "lowerarm_r",
                            "hand_l", "hand_r", "spine_01", "spine_02", "spine_03"
                        ).forEach { bone ->
                            updateBoneRotation(engine, modelInstance, bone, 0f, 0f, 0f)
                        }
                        currentStep = 0
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Reset toàn bộ T-pose") }
        }
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
        // Double check validity before each bone update
        if (engine.isValid) {
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
    engine: Engine?,
    modelInstance: ModelInstance?,
    boneName: String
): Rotation {
    if (engine == null || !engine.isValid || modelInstance == null) return Rotation()
    
    val asset = modelInstance.asset
    if (asset == null) return Rotation()
    
    val entity = asset.getFirstEntityByName(boneName)
    if (entity == 0) return Rotation()

    val tcm = engine.transformManager
    val instance = tcm.getInstance(entity)
    if (instance == 0) return Rotation()

    val mat = FloatArray(16)
    tcm.getTransform(instance, mat)

    return Rotation.fromMatrix(mat)
}











