import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.LinkedList
class SignLanguageInterpreter(private val context: Context) {

    private var tflite: Interpreter? = null
    private var handLandmarker: HandLandmarker? = null

    // Buffer lưu 30 frame
    private val frameBuffer = LinkedList<FloatArray>()
    private val SEQUENCE_LENGTH = 30
    private val NUM_FEATURES = 126

    // Đảm bảo labels khớp với model
    private val labels = listOf("Cảm ơn", "anh yêu em", "xin chào")

    var onResultListener: ((String, Float) -> Unit)? = null
    private val TAG = "SL_DEBUG"

    // --- BIẾN ĐỂ TỐI ƯU HIỆU NĂNG ---
    private var lastFrameTime = 0L
    private val TARGET_FPS = 30L // Giả sử bạn train model với 30fps
    private val FRAME_DURATION_MS = 1000L / TARGET_FPS
    private var frameCounter = 0

    fun initialize() {
        try {
            // 1. Setup TFLite
            val modelBuffer = loadModelFile("vsl_model.tflite")
            val tfliteOptions = Interpreter.Options()
            // tfliteOptions.addDelegate(org.tensorflow.lite.flex.FlexDelegate()) // Bỏ comment nếu cần
            tflite = Interpreter(modelBuffer, tfliteOptions)

            // 2. Setup MediaPipe
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath("hand_landmarker.task")
                .build()

            val mpOptions = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setNumHands(2)
                .setMinHandDetectionConfidence(0.5f)
                .setMinHandPresenceConfidence(0.5f) // Tăng độ tin cậy để tránh nhiễu
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, _ -> processLandmarks(result) }
                .build()

            handLandmarker = HandLandmarker.createFromOptions(context, mpOptions)
            Log.d(TAG, "✅ Khởi tạo xong!")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi khởi tạo: ${e.message}")
        }
    }

    fun processFrame(bitmap: Bitmap) {
        if (handLandmarker == null) return

        // 1. FPS THROTTLING (KIỂM SOÁT TỐC ĐỘ)
        // Nếu camera bắn frame quá nhanh (>30fps), ta bỏ qua để model không bị "ngợp"
        val currentTime = SystemClock.uptimeMillis()
        if (currentTime - lastFrameTime < FRAME_DURATION_MS) {
            return
        }
        lastFrameTime = currentTime

        try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            handLandmarker?.detectAsync(mpImage, currentTime)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi frame: ${e.message}")
        }
    }

    private fun processLandmarks(result: HandLandmarkerResult) {
        val currentFeatures = FloatArray(NUM_FEATURES) { 0f }

        if (result.landmarks().isNotEmpty()) {
            var featureIndex = 0
            val hands = result.landmarks()

            for (handIndex in 0 until minOf(hands.size, 2)) {
                val landmarks = hands[handIndex]

                // Lấy Wrist làm gốc (vẫn giữ logic Mirroring 1.0 - x)
                val wristX = 1.0f - landmarks[0].x()
                val wristY = landmarks[0].y()
                val wristZ = landmarks[0].z()

                for (lm in landmarks) {
                    if (featureIndex < NUM_FEATURES) {
                        val mirroredX = 1.0f - lm.x()
                        val rawY = lm.y()
                        val rawZ = lm.z()

                        currentFeatures[featureIndex++] = mirroredX - wristX
                        currentFeatures[featureIndex++] = rawY - wristY
                        currentFeatures[featureIndex++] = rawZ - wristZ
                    }
                }
            }
        } else {
            // Không thấy tay: Đẩy frame rỗng vào để reset chuỗi hành động
            // Điều này giúp model biết hành động đã kết thúc
        }

        synchronized(frameBuffer) {
            if (frameBuffer.size >= SEQUENCE_LENGTH) {
                frameBuffer.removeFirst()
            }
            frameBuffer.add(currentFeatures)
        }

        // 2. INFERENCE STRIDE (GIẢM TẢI SUY LUẬN)
        // Chỉ chạy AI khi buffer đầy VÀ cứ mỗi 3 frame mới chạy 1 lần.
        // Ví dụ: Frame 30 -> Chạy, Frame 31 -> Bỏ qua, Frame 32 -> Bỏ qua, Frame 33 -> Chạy
        // Giúp giảm delay UI đáng kể mà vẫn bắt được hành động.
        frameCounter++
        if (frameBuffer.size == SEQUENCE_LENGTH && frameCounter % 3 == 0) {
            runInference()
        }
    }

    private fun runInference() {
        try {
            val inputBuffer = ByteBuffer.allocateDirect(1 * SEQUENCE_LENGTH * NUM_FEATURES * 4)
            inputBuffer.order(ByteOrder.nativeOrder())

            synchronized(frameBuffer) {
                for (frame in frameBuffer) {
                    for (feature in frame) inputBuffer.putFloat(feature)
                }
            }

            val outputBuffer = Array(1) { FloatArray(labels.size) }
            tflite?.run(inputBuffer, outputBuffer)

            val probabilities = outputBuffer[0]
            var maxIndex = -1
            var maxConf = 0f

            for (i in probabilities.indices) {
                if (probabilities[i] > maxConf) {
                    maxConf = probabilities[i]
                    maxIndex = i
                }
            }

            // Giảm threshold xuống một chút để nhạy hơn (0.55 hoặc 0.6)
            if (maxConf > 0.6f && maxIndex != -1) {
                val resultText = labels[maxIndex]
                onResultListener?.invoke(resultText, maxConf)
            } else {
                // Tùy chọn: Có thể không trả về gì để giữ kết quả cũ trên màn hình cho đỡ nháy
                onResultListener?.invoke("...", 0f)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Lỗi Inference: ${e.message}")
        }
    }

    fun close() {
        handLandmarker?.close()
        tflite?.close()
    }

    private fun loadModelFile(fileName: String): java.nio.MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(fileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }
}