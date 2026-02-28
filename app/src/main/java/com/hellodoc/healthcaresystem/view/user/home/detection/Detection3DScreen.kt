package com.hellodoc.healthcaresystem.view.user.home.detection

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.DetectionData
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Rotation
import com.hellodoc.healthcaresystem.view.model_human.updateBoneRotation
import com.hellodoc.healthcaresystem.view.user.supportfunction.PoseDetector
import com.hellodoc.healthcaresystem.view.user.supportfunction.SceneViewManager
import com.hellodoc.healthcaresystem.viewmodel.DetectionViewModel
import io.github.sceneview.Scene
import io.github.sceneview.environment.Environment
import io.github.sceneview.math.Position
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberMainLightNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Detection3DScreen(
    navHostController: NavHostController,
    viewModel: DetectionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        CameraDetectionContent(navHostController, viewModel)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Yêu cầu quyền truy cập Camera để sử dụng tính năng này.")
        }
    }
}
@SuppressLint("RestrictedApi")
@Composable
fun CameraDetectionContent(
    navHostController: NavHostController,
    viewModel: DetectionViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // ✅ Ép PreviewView dùng TextureView thay vì SurfaceView
    // SurfaceView luôn render trên cùng (z-order cao nhất) bất kể Compose layout
    // TextureView render như một texture bình thường, cho phép Compose đè lên
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    val detectionData by viewModel.detectionData.collectAsState()
    val isDetecting by viewModel.isDetecting.collectAsState()

    var poseResults by remember { mutableStateOf<PoseLandmarkerResult?>(null) }
    var handResults by remember { mutableStateOf<HandLandmarkerResult?>(null) }
    var faceResults by remember { mutableStateOf<FaceLandmarkerResult?>(null) }
    val modelWarnings = remember { mutableStateListOf<String>() }

    val poseDetector = remember {
        PoseDetector(
            context = context,
            onPoseResults = { result -> poseResults = result },
            onHandResults = { result -> handResults = result },
            onFaceResults = { result -> faceResults = result },
            onInitializationError = { error ->
                if (!modelWarnings.contains(error)) modelWarnings.add(error)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose { poseDetector.close() }
    }

    // 3D Resources
    val is3DReady by SceneViewManager.initializationState.collectAsState()
    var modelInstance by remember { mutableStateOf<ModelInstance?>(null) }
    var characterNode by remember { mutableStateOf<ModelNode?>(null) }

    LaunchedEffect(is3DReady) {
        if (is3DReady && modelInstance == null) {
            modelInstance = SceneViewManager.createModelInstance()
        }
    }

    LaunchedEffect(modelInstance) {
        if (modelInstance != null && characterNode == null) {
            characterNode = ModelNode(
                modelInstance = modelInstance!!,
                scaleToUnits = 1.0f
            ).apply {
                position = Position(x = 0.0f, y = -3f, z = -0.75f)
                scale = Position(x = 5f, y = 5f, z = 5f)
                centerOrigin(Position(0f, 0f, 0f))
            }
        }
    }

    LaunchedEffect(detectionData) {
        val data = detectionData ?: return@LaunchedEffect
        val engine = SceneViewManager.getEngine() ?: return@LaunchedEffect
        val instance = modelInstance ?: return@LaunchedEffect
        applyDetectionDataToBones(engine, instance, data)
    }

    var lastCaptureTime by remember { mutableLongStateOf(0L) }

    // ✅ Camera LaunchedEffect đặt NGOÀI Box
    LaunchedEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(android.util.Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                        val bitmap = imageProxy.toBitmap()

                        poseDetector.detectPose(bitmap, rotationDegrees, true)

                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastCaptureTime > 5000 && !isDetecting) {
                            lastCaptureTime = currentTime
                            saveBitmapToFile(context, bitmap)?.let { file ->
                                viewModel.detectJoints(file)
                            }
                        }

                        imageProxy.close()
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("Detection3D", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Layer 1: Camera Preview dùng TextureView (COMPATIBLE mode)
        // ✅ COMPATIBLE mode = TextureView → Compose có thể đè lên được
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Layer 2: Skeleton Overlay
        PoseOverlay(
            poseResults = poseResults,
            handResults = handResults,
            faceResults = faceResults
        )

        // Layer 3: Scene 3D — nằm trên camera vì TextureView không chiếm z-order đặc biệt
        if (is3DReady && modelInstance != null && characterNode != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
            ) {
                Scene(
                    engine = SceneViewManager.getEngine()!!,
                    mainLightNode = rememberMainLightNode(SceneViewManager.getEngine()!!) {
                        intensity = 70_000.0f
                        isShadowCaster = true
                    },
                    cameraNode = rememberCameraNode(SceneViewManager.getEngine()!!) {
                        position = Position(z = 2f)
                    },
                    childNodes = listOf(characterNode!!),
                    environment = SceneViewManager.getEnvironment()!!,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Layer 4: Back Button
        IconButton(
            onClick = { navHostController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // Layer 4: Warnings
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .padding(top = 60.dp)
        ) {
            modelWarnings.forEach { warning ->
                Surface(
                    color = Color.Red.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(
                        text = warning,
                        color = Color.White,
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Layer 4: Processing Indicator
        if (isDetecting) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .size(24.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
        }
    }
}
private fun saveBitmapToFile(context: Context, bitmap: android.graphics.Bitmap): File? {
    return try {
        val file = File(context.cacheDir, "detection_api_frame.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()
        file
    } catch (e: Exception) {
        Log.e("Detection3D", "Error saving bitmap to file", e)
        null
    }
}

fun applyDetectionDataToBones(
    engine: com.google.android.filament.Engine,
    modelInstance: ModelInstance,
    data: DetectionData
) {
    // Standard singular bones
    applyRotation(engine, modelInstance, "spine_01", data.spine01)
    applyRotation(engine, modelInstance, "spine_02", data.spine02)
    applyRotation(engine, modelInstance, "spine_03", data.spine03)
    applyRotation(engine, modelInstance, "neck", data.neck)
    applyRotation(engine, modelInstance, "head", data.head)
    applyRotation(engine, modelInstance, "jaw", data.jaw)
    applyRotation(engine, modelInstance, "eyelid_l", data.eyelidL)
    applyRotation(engine, modelInstance, "eyelid_r", data.eyelidR)
    applyRotation(engine, modelInstance, "mouth_l", data.mouthL)
    applyRotation(engine, modelInstance, "mouth_r", data.mouthR)
    applyRotation(engine, modelInstance, "shoulder_l", data.shoulderL)
    applyRotation(engine, modelInstance, "shoulder_r", data.shoulderR)
    applyRotation(engine, modelInstance, "upperarm_l", data.upperarmL)
    applyRotation(engine, modelInstance, "upperarm_r", data.upperarmR)
    applyRotation(engine, modelInstance, "lowerarm_l", data.lowerarmL)
    applyRotation(engine, modelInstance, "lowerarm_r", data.lowerarmR)
    applyRotation(engine, modelInstance, "hand_l", data.handL)
    applyRotation(engine, modelInstance, "hand_r", data.handR)

    // Multiple bones (Strings contain multiple bone info)
    applyMultipleRotations(engine, modelInstance, data.eyes)
    applyMultipleRotations(engine, modelInstance, data.eyebrows)
    applyMultipleRotations(engine, modelInstance, data.thumbL)
    applyMultipleRotations(engine, modelInstance, data.indexL)
    applyMultipleRotations(engine, modelInstance, data.middleL)
    applyMultipleRotations(engine, modelInstance, data.ringL)
    applyMultipleRotations(engine, modelInstance, data.pinkyL)
    applyMultipleRotations(engine, modelInstance, data.thumbR)
    applyMultipleRotations(engine, modelInstance, data.indexR)
    applyMultipleRotations(engine, modelInstance, data.middleR)
    applyMultipleRotations(engine, modelInstance, data.ringR)
    applyMultipleRotations(engine, modelInstance, data.pinky_r)
}

private fun applyRotation(
    engine: com.google.android.filament.Engine,
    modelInstance: ModelInstance,
    boneName: String,
    rotStr: String
) {
    if (rotStr.isBlank()) return
    val rot = Rotation.fromString(rotStr)
    updateBoneRotation(engine, modelInstance, boneName, rot.x, rot.y, rot.z)
}

private fun applyMultipleRotations(
    engine: com.google.android.filament.Engine,
    modelInstance: ModelInstance,
    rotStr: String
) {
    if (rotStr.isBlank()) return
    val rotations = Rotation.parseMultiple(rotStr)
    rotations.forEach { (boneName, rot) ->
        updateBoneRotation(engine, modelInstance, boneName, rot.x, rot.y, rot.z)
    }
}
