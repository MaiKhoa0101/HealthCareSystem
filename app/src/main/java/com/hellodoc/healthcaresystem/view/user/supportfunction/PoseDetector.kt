package com.hellodoc.healthcaresystem.view.user.supportfunction

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class PoseDetector(
    private val context: Context,
    private val onPoseResults: (PoseLandmarkerResult) -> Unit,
    private val onHandResults: (HandLandmarkerResult) -> Unit,
    private val onFaceResults: (FaceLandmarkerResult) -> Unit,
    private val onInitializationError: (String) -> Unit
) {
    private var poseLandmarker: PoseLandmarker? = null
    private var handLandmarker: HandLandmarker? = null
    private var faceLandmarker: FaceLandmarker? = null

    init {
        setupPoseLandmarker()
        setupHandLandmarker()
        setupFaceLandmarker()
    }

    private fun setupPoseLandmarker() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("pose_landmarker.task")
                .build()

            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, _ -> onPoseResults(result) }
                .build()

            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
            Log.d("PoseDetector", "✅ PoseLandmarker initialized")
        } catch (e: Exception) {
            Log.w("PoseDetector", "⚠️ PoseLandmarker missing: ${e.message}")
            onInitializationError("Missing pose_landmarker.task for arms/body")
        }
    }

    private fun setupHandLandmarker() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("hand_landmarker.task")
                .build()

            val options = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumHands(2)
                .setResultListener { result, _ -> onHandResults(result) }
                .build()

            handLandmarker = HandLandmarker.createFromOptions(context, options)
            Log.d("PoseDetector", "✅ HandLandmarker initialized")
        } catch (e: Exception) {
            Log.e("PoseDetector", "❌ HandLandmarker initialization failed: ${e.message}")
        }
    }

    private fun setupFaceLandmarker() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("face_landmarker.task")
                .build()

            val options = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, _ -> onFaceResults(result) }
                .build()

            faceLandmarker = FaceLandmarker.createFromOptions(context, options)
            Log.d("PoseDetector", "✅ FaceLandmarker initialized")
        } catch (e: Exception) {
            Log.w("PoseDetector", "⚠️ FaceLandmarker missing: ${e.message}")
            onInitializationError("Missing face_landmarker.task for detailed face")
        }
    }

    fun detectPose(bitmap: android.graphics.Bitmap, rotationDegrees: Int, isFrontCamera: Boolean) {
        try {
            val matrix = Matrix().apply {
                postRotate(rotationDegrees.toFloat())
                if (isFrontCamera) {
                    postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
                }
            }
            
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )

            val mpImage = BitmapImageBuilder(rotatedBitmap).build()
            val timestamp = SystemClock.uptimeMillis()
            
            poseLandmarker?.detectAsync(mpImage, timestamp)
            handLandmarker?.detectAsync(mpImage, timestamp)
            faceLandmarker?.detectAsync(mpImage, timestamp)
        } catch (e: Exception) {
            Log.e("PoseDetector", "Error during detection: ${e.message}")
        }
    }

    fun close() {
        poseLandmarker?.close()
        handLandmarker?.close()
        faceLandmarker?.close()
    }
}
