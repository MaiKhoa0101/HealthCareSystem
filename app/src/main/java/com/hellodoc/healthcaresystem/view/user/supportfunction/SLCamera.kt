import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import org.json.JSONObject
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream
import java.util.LinkedList
import kotlin.math.*

class SignLanguageInterpreter(private val context: Context) {

    private var module: Module? = null

    private var handLandmarker: HandLandmarker? = null
    private var poseLandmarker: PoseLandmarker? = null

    private val SEQUENCE_LENGTH = 64
    private val NUM_FEATURES = 208
    private val frameBuffer = LinkedList<FloatArray>()
    private var labels: List<String> = emptyList()

    var onResultListener: ((String, Float) -> Unit)? = null
    private val TAG = "SL_DEBUG"

    private var lastFrameTime = 0L
    private val TARGET_FPS = 30L
    private val FRAME_DURATION_MS = 1000L / TARGET_FPS
    private var frameCounter = 0

    // Indices chuẩn xác từ realtime.py
    private val POSE_KEY_INDICES = intArrayOf(0, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24)
    // THÊM LISTENER NÀY: Dùng để truyền data vẽ UI
    var onLandmarksListener: ((HandLandmarkerResult?, PoseLandmarkerResult?, FaceLandmarkerResult?) -> Unit)? = null

    // Khôi phục lại biến FaceLandmarker để phục vụ cho việc vẽ UI
    private var faceLandmarker: FaceLandmarker? = null
    fun initialize() {
        try {
            loadLabelsFromJson()
            val modelPath = assetFilePath(context, "vsl_model.ptl")
            module = LiteModuleLoader.load(modelPath)
            setupMediaPipe()
            Log.d(TAG, "✅ Khởi tạo thành công toàn bộ Engine!")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi khởi tạo: ${e.message}")
        }
    }

    private fun loadLabelsFromJson() {
        try {
            val jsonStr = context.assets.open("label_map.json").bufferedReader().use { it.readText() }
            val jsonObj = JSONObject(jsonStr)
            val tempList = arrayOfNulls<String>(jsonObj.length())
            val keys = jsonObj.keys()
            while (keys.hasNext()) {
                val key = keys.next() as String
                val index = jsonObj.getInt(key)
                if (index in tempList.indices) tempList[index] = key
            }
            labels = tempList.filterNotNull()
            Log.d(TAG, "✅ Đã load thành công ${labels.size} nhãn.")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi đọc label_map.json: ${e.message}")
            labels = listOf("__idle__tay_xuoi_hong", "cam_on", "xin_chao")
        }
    }

    private fun setupMediaPipe() {
        val handOptions = HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(BaseOptions.builder().setModelAssetPath("models/hand_landmarker.task").build())
            .setNumHands(2)
            .setMinHandDetectionConfidence(0.5f)
            .setRunningMode(RunningMode.VIDEO)
            .build()
        handLandmarker = HandLandmarker.createFromOptions(context, handOptions)

        val poseOptions = PoseLandmarker.PoseLandmarkerOptions.builder()
            // SỬA Ở ĐÂY: Thêm chữ _heavy vào tên file cho khớp với thư mục của bạn
            .setBaseOptions(BaseOptions.builder().setModelAssetPath("models/pose_landmarker.task").build())
            .setMinPoseDetectionConfidence(0.5f)
            .setRunningMode(RunningMode.VIDEO)
            .build()
        poseLandmarker = PoseLandmarker.createFromOptions(context, poseOptions)
        // KHÔI PHỤC FACE LANDMARKER (Chỉ dùng để vẽ UI)
        val faceOptions = FaceLandmarker.FaceLandmarkerOptions.builder()
            .setBaseOptions(BaseOptions.builder().setModelAssetPath("models/face_landmarker.task").build())
            .setNumFaces(1)
            .setOutputFaceBlendshapes(true)
            .setRunningMode(RunningMode.VIDEO)
            .build()
        faceLandmarker = FaceLandmarker.createFromOptions(context, faceOptions)
    }

    fun processFrame(bitmap: Bitmap) {
        if (handLandmarker == null || poseLandmarker == null) return

        val currentTime = SystemClock.uptimeMillis()
        if (currentTime - lastFrameTime < FRAME_DURATION_MS) return
        lastFrameTime = currentTime

        try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            val handResult = handLandmarker?.detectForVideo(mpImage, currentTime)
            val poseResult = poseLandmarker?.detectForVideo(mpImage, currentTime)
            val faceResult = faceLandmarker?.detectForVideo(mpImage, currentTime)

            // TRUYỀN DỮ LIỆU RA NGOÀI CHO UI VẼ
            onLandmarksListener?.invoke(handResult, poseResult, faceResult)

            // Vẫn chỉ truyền hand và pose vào xử lý AI (để tránh nhiễu)
            processAllFeatures(handResult, poseResult)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi xử lý frame: ${e.message}")
        }
    }

    private fun processAllFeatures(
        handResult: HandLandmarkerResult?,
        poseResult: PoseLandmarkerResult?
    ) {
        val features = FloatArray(NUM_FEATURES) { 0f }

        // --- TRẠM KIỂM SOÁT 1: In log phát hiện bàn tay ---
        val handCount = handResult?.landmarks()?.size ?: 0
        if (frameCounter % 30 == 0) {
            Log.d(TAG, "MediaPipe Frame check -> Hands detected: $handCount")
        }

        // 1. Ghi Pose (Camera sau: Lấy đúng X gốc, không lật)
        if (poseResult != null && poseResult.landmarks().isNotEmpty()) {
            val poseLms = poseResult.landmarks()[0]
            var idx = 0
            for (i in POSE_KEY_INDICES) {
                if (i < poseLms.size) {
                    features[idx++] = poseLms[i].x()
                    features[idx++] = poseLms[i].y()
                    features[idx++] = poseLms[i].z()
                } else {
                    idx += 3
                }
            }
        }

        // 2. Ghi Hands & Curl (Camera sau: Lấy đúng X gốc, không lật)
        if (handResult != null && handResult.landmarks().isNotEmpty()) {
            val hands = handResult.landmarks()
            if (hands.size == 1) {
                val h = hands[0]
                var idx = 45
                for (lm in h) {
                    features[idx++] = lm.x()
                    features[idx++] = lm.y()
                    features[idx++] = lm.z()
                }
                val curl = computeFingerCurl(h)
                System.arraycopy(curl, 0, features, 171, 15)
            } else {
                // Sắp xếp tay theo trục X thuận (X càng lớn thì tay càng nằm bên phải màn hình)
                val sortedHands = hands.sortedBy { hand -> hand.map { it.x() }.average() }

                var idxA = 45
                for (lm in sortedHands[0]) {
                    features[idxA++] = lm.x()
                    features[idxA++] = lm.y()
                    features[idxA++] = lm.z()
                }
                System.arraycopy(computeFingerCurl(sortedHands[0]), 0, features, 171, 15)

                if (sortedHands.size > 1) {
                    var idxB = 108
                    for (lm in sortedHands[1]) {
                        features[idxB++] = lm.x()
                        features[idxB++] = lm.y()
                        features[idxB++] = lm.z()
                    }
                    System.arraycopy(computeFingerCurl(sortedHands[1]), 0, features, 186, 15)
                }
            }
        }

        // 3. Chuẩn hóa tính năng
        normalizeFeatures(features)

        // 4. Ghi Emotion (Index 201 -> 207) - Trạng thái Neutral
        features[201 + 6] = 1.0f

        // Thêm vào Buffer
        synchronized(frameBuffer) {
            if (frameBuffer.size >= SEQUENCE_LENGTH) frameBuffer.removeFirst()
            frameBuffer.add(features)
        }

        frameCounter++
        if (frameBuffer.size == SEQUENCE_LENGTH && frameCounter % 3 == 0) {
            runInference()
        }
    }

    private fun computeFingerCurl(lms: List<NormalizedLandmark>): FloatArray {
        val curl = FloatArray(15)
        val wrist = lms[0]
        val scale = max(dist(lms[9], wrist), 1e-8f)

        val fingerJoints = listOf(
            intArrayOf(1, 2, 3, 4),   // thumb
            intArrayOf(5, 6, 7, 8),   // index
            intArrayOf(9, 10, 11, 12),// middle
            intArrayOf(13, 14, 15, 16),// ring
            intArrayOf(17, 18, 19, 20)// pinky
        )

        for (i in fingerJoints.indices) {
            val joints = fingerJoints[i]
            val mcp = lms[joints[0]]; val pip = lms[joints[1]]
            val dip = lms[joints[2]]; val tip = lms[joints[3]]

            val dTip = dist(tip, wrist)
            val dMcp = max(dist(mcp, wrist), 1e-8f)
            val angle = angleBetween(mcp, pip, dip)

            val b = i * 3
            curl[b] = (dTip / dMcp / 2.0f).coerceIn(0f, 1f)
            curl[b + 1] = (((Math.PI - angle) / Math.PI).toFloat()).coerceIn(0f, 1f)
            curl[b + 2] = (dTip / scale / 3.0f).coerceIn(0f, 1f)
        }
        return curl
    }

    private fun normalizeFeatures(f: FloatArray) {
        var hasPose = false
        for (i in 0..44) if (f[i] != 0f) { hasPose = true; break }

        if (hasPose) {
            // Hông nằm ở index 13 và 14
            val hipX = (f[13*3] + f[14*3]) / 2f
            val hipY = (f[13*3+1] + f[14*3+1]) / 2f
            val hipZ = (f[13*3+2] + f[14*3+2]) / 2f

            for (i in 0..14) {
                f[i*3] -= hipX; f[i*3+1] -= hipY; f[i*3+2] -= hipZ
            }
            // Vai trái/phải nằm ở index 1 và 2
            val sd = distRaw(f[1*3], f[1*3+1], f[1*3+2], f[2*3], f[2*3+1], f[2*3+2])
            if (sd > 1e-6f) {
                for (i in 0..44) f[i] /= sd
            }
        }

        for (s in listOf(45, 108)) {
            val e = s + 63
            var hasHand = false
            for (i in s until e) if (f[i] != 0f) { hasHand = true; break }

            if (hasHand) {
                val wristX = f[s]; val wristY = f[s+1]; val wristZ = f[s+2]
                for (i in 0..20) {
                    f[s + i*3] -= wristX; f[s + i*3+1] -= wristY; f[s + i*3+2] -= wristZ
                }
                val sc = distRaw(0f, 0f, 0f, f[s + 9*3], f[s + 9*3+1], f[s + 9*3+2])
                if (sc > 1e-6f) {
                    for (i in s until e) f[i] /= sc
                }
            }
        }
    }

    private fun dist(a: NormalizedLandmark, b: NormalizedLandmark): Float {
        return sqrt((a.x() - b.x()).pow(2) + (a.y() - b.y()).pow(2) + (a.z() - b.z()).pow(2))
    }

    private fun distRaw(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Float {
        return sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2) + (z1 - z2).pow(2))
    }

    private fun angleBetween(a: NormalizedLandmark, b: NormalizedLandmark, c: NormalizedLandmark): Float {
        val baX = a.x() - b.x(); val baY = a.y() - b.y(); val baZ = a.z() - b.z()
        val bcX = c.x() - b.x(); val bcY = c.y() - b.y(); val bcZ = c.z() - b.z()

        val dot = baX * bcX + baY * bcY + baZ * bcZ
        val magBa = sqrt(baX*baX + baY*baY + baZ*baZ)
        val magBc = sqrt(bcX*bcX + bcY*bcY + bcZ*bcZ)

        val cosA = dot / (magBa * magBc + 1e-8f)
        return acos(cosA.coerceIn(-1.0f, 1.0f))
    }

    private fun runInference() {
        if (module == null || labels.isEmpty()) return

        try {
            val flatArray = FloatArray(SEQUENCE_LENGTH * NUM_FEATURES)
            var index = 0
            synchronized(frameBuffer) {
                for (frame in frameBuffer) {
                    for (feature in frame) flatArray[index++] = feature
                }
            }

            val shape = longArrayOf(1, SEQUENCE_LENGTH.toLong(), NUM_FEATURES.toLong())
            val inputTensor = Tensor.fromBlob(flatArray, shape)

            val outputTensor = module!!.forward(IValue.from(inputTensor)).toTensor()
            val logits = outputTensor.dataAsFloatArray

            val probabilities = softmax(logits)
            var maxIndex = -1
            var maxConf = 0f

            for (i in probabilities.indices) {
                if (probabilities[i] > maxConf) {
                    maxConf = probabilities[i]
                    maxIndex = i
                }
            }

            val predictedLabel = if (maxIndex != -1 && maxIndex < labels.size) labels[maxIndex] else "Unknown"

            // Log kết quả nhận diện
            Log.d(TAG, "Kết quả AI -> Nhãn: $predictedLabel | Conf: ${maxConf * 100}%")

            if (maxConf > 0.6f && maxIndex != -1 && maxIndex < labels.size) {
                if (predictedLabel != "__idle__tay_xuoi_hong") {
                    onResultListener?.invoke(predictedLabel, maxConf)
                } else {
                    onResultListener?.invoke("...", maxConf)
                }
            } else {
                onResultListener?.invoke("...", 0f)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Lỗi Inference: ${e.message}")
        }
    }

    fun close() {
        handLandmarker?.close()
        poseLandmarker?.close()
        module?.destroy()
    }

    private fun softmax(logits: FloatArray): FloatArray {
        var maxLogit = logits[0]
        for (v in logits) if (v > maxLogit) maxLogit = v
        var sumExp = 0f
        val exps = FloatArray(logits.size)
        for (i in logits.indices) {
            exps[i] = exp((logits[i] - maxLogit).toDouble()).toFloat()
            sumExp += exps[i]
        }
        for (i in exps.indices) {
            exps[i] /= sumExp
        }
        return exps
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) return file.absolutePath

        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }
        return file.absolutePath
    }
}