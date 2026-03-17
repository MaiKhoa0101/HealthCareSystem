package com.hellodoc.healthcaresystem.view.user.home.detection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

@Composable
fun PoseOverlay(
    poseResults: PoseLandmarkerResult?,
    handResults: HandLandmarkerResult?,
    faceResults: FaceLandmarkerResult?,
    modifier: Modifier = Modifier
) {
    // ❌ ĐÃ XÓA .rotate(90f) ở Modifier.
    // Việc xoay phải được tính toán bằng tọa độ bên trong Canvas.
    Canvas(modifier = modifier.fillMaxSize()) {

        // 1. Lấy kích thước của Canvas (khung Preview trên màn hình)
        val canvasWidth = size.width
        val canvasHeight = size.height

        // 2. Kích thước mặc định của ImageAnalysis (Thường là 480x640 hoặc 480x640 bị xoay ngang)
        // Vì camera sau đang trả về luồng Bitmap nằm ngang (Landscape) cho MediaPipe,
        // nên ImageWidth thực tế đang lớn hơn ImageHeight (ví dụ: 640x480).
        // Khi vẽ lên màn hình dọc (Portrait), ta phải hoán đổi và tính toán lại.

        // CÔNG THỨC ÁNH XẠ ĐẶC BIỆT CHO CAMERA SAU CỦA ANDROID
        val scaleFactor = maxOf(canvasWidth, canvasHeight)

        // Helper function để map toạ độ Landmark (x,y) -> Toạ độ Canvas (cx,cy)
        fun mapPoint(x: Float, y: Float): Offset {
            // CameraX mặc định trả frame bị xoay 90 độ.
            // Công thức dưới đây ánh xạ (x,y) của frame gốc sang (cx, cy) của màn hình dọc.
            val mappedX = (1- y) * canvasWidth
            val mappedY = ( x) * canvasHeight
            return Offset(mappedX, mappedY)
        }

        // 1. Draw Pose Landmarks (Includes Arms and basic Face)
        poseResults?.landmarks()?.forEach { landmarks ->
            val poseConnections = listOf(
                // Arms
                Pair(11, 13), Pair(13, 15), // Left arm
                Pair(12, 14), Pair(14, 16), // Right arm
                Pair(11, 12), // Shoulders
                // Torso
                Pair(11, 23), Pair(12, 24), Pair(23, 24),
                // Legs
                Pair(23, 25), Pair(25, 27),
                Pair(24, 26), Pair(26, 28),
                // Face (Basic)
                Pair(1, 2), Pair(2, 3), // Left eye
                Pair(4, 5), Pair(5, 6), // Right eye
                Pair(9, 10) // Mouth
            )

            poseConnections.forEach { connection ->
                if (connection.first < landmarks.size && connection.second < landmarks.size) {
                    val start = landmarks[connection.first]
                    val end = landmarks[connection.second]
                    drawLine(
                        color = Color.Cyan,
                        start = mapPoint(start.x(), start.y()),
                        end = mapPoint(end.x(), end.y()),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }
            }
            landmarks.forEach { landmark ->
                drawCircle(
                    color = Color.Yellow,
                    radius = 8f,
                    center = mapPoint(landmark.x(), landmark.y())
                )
            }
        }

        // 2. Draw Hand Landmarks
        handResults?.landmarks()?.forEach { landmarks ->
            val handConnections = HandLandmarker.HAND_CONNECTIONS.map { Pair(it.start(), it.end()) }
            handConnections.forEach { connection ->
                val start = landmarks[connection.first]
                val end = landmarks[connection.second]
                drawLine(
                    color = Color.Green,
                    start = mapPoint(start.x(), start.y()),
                    end = mapPoint(end.x(), end.y()),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }
            landmarks.forEach { landmark ->
                drawCircle(
                    color = Color.Red,
                    radius = 6f,
                    center = mapPoint(landmark.x(), landmark.y())
                )
            }
        }

        // 3. Draw Detailed Face Landmarks
        faceResults?.faceLandmarks()?.forEach { landmarks ->
            val faceConnections = FaceLandmarker.FACE_LANDMARKS_TESSELATION
            faceConnections.forEach { connection ->
                val start = landmarks[connection.start()]
                val end = landmarks[connection.end()]
                drawLine(
                    color = Color.Magenta.copy(alpha = 0.5f),
                    start = mapPoint(start.x(), start.y()),
                    end = mapPoint(end.x(), end.y()),
                    strokeWidth = 2f
                )
            }
        }
    }
}
