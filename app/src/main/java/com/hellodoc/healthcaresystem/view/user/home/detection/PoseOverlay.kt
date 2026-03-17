package com.hellodoc.healthcaresystem.view.user.home.detection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
    Canvas(modifier = modifier.fillMaxSize().rotate(90f)) {
        val width = size.height
        val height = size.width

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
                        start = Offset(start.x() * width, start.y() * height),
                        end = Offset(end.x() * width, end.y() * height),
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }
            }
            landmarks.forEach { landmark ->
                drawCircle(
                    color = Color.Yellow,
                    radius = 8f,
                    center = Offset(landmark.x() * width, landmark.y() * height)
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
                    start = Offset(start.x() * width, start.y() * height),
                    end = Offset(end.x() * width, end.y() * height),
                    strokeWidth = 6f,
                    cap = StrokeCap.Round
                )
            }
            landmarks.forEach { landmark ->
                drawCircle(
                    color = Color.Red,
                    radius = 6f,
                    center = Offset(landmark.x() * width, landmark.y() * height)
                )
            }
        }

        // 3. Draw Detailed Face Landmarks (if FaceLandmarker is used)
        faceResults?.faceLandmarks()?.forEach { landmarks ->
            // Drawing main contours: Eyes, Eyebrows, Lips
            val faceConnections = FaceLandmarker.FACE_LANDMARKS_TESSELATION
            faceConnections.forEach { connection ->
                val start = landmarks[connection.start()]
                val end = landmarks[connection.end()]
                drawLine(
                    color = Color.Magenta.copy(alpha = 0.5f),
                    start = Offset(start.x() * width, start.y() * height),
                    end = Offset(end.x() * width, end.y() * height),
                    strokeWidth = 2f
                )
            }
        }
    }
}
