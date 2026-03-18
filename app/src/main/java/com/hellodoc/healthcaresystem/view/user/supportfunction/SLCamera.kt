package com.hellodoc.healthcaresystem.interpreter

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.pytorch.IValue
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.*

/**
 * SignLanguageInterpreter — Bản tối ưu hiệu năng
 *
 * 6 tối ưu so với phiên bản gốc:
 *
 * [OPT-1] Ring buffer thay LinkedList
 *   LinkedList: mỗi frame add/remove = 1 heap allocation + GC pressure
 *   Ring buffer: array cố định, chỉ ghi đè slot cũ, ZERO allocation per frame
 *
 * [OPT-2] Pre-allocate tất cả FloatArray scratch
 *   Gốc: feat201, fullFeat208, mirrorFeat, flatArray đều "new" mỗi frame
 *   Mới: cấp phát 1 lần lúc khởi tạo, tái dùng mãi → giảm GC đáng kể
 *
 * [OPT-3] AtomicBoolean isInferring + 1 persistent CoroutineScope
 *   Gốc: CoroutineScope(Dispatchers.Default).launch mỗi 3 frame → tạo object mới + risk race
 *   Mới: compareAndSet() → skip inference nếu lần trước chưa xong, không block thread camera
 *
 * [OPT-4] Face landmarker throttle: chỉ chạy mỗi FACE_EVERY_N frame
 *   Face chỉ dùng vẽ UI, không ảnh hưởng inference → tiết kiệm ~8-12ms/frame
 *
 * [OPT-5] Pose landmarker throttle: chỉ chạy mỗi POSE_EVERY_N frame
 *   Pose thay đổi chậm hơn tay nhiều → tiết kiệm ~5-8ms/frame
 *
 * [OPT-6] Tăng MIRROR_EVERY_N (2→4) và INFER_EVERY_N (3→4)
 *   Giảm 50% số lần gọi mirror inference, bù lại độ trễ không đáng kể
 */
class SignLanguageInterpreter(private val context: Context) {

    // ── Model & MediaPipe ─────────────────────────────────────────────
    private var module: Module? = null
    private var handLandmarker: HandLandmarker? = null
    private var poseLandmarker: PoseLandmarker? = null
    private var faceLandmarker: FaceLandmarker? = null

    // ── Config ────────────────────────────────────────────────────────
    private val SEQ_LEN      = 64
    private val FEAT_DIM     = 208   // 201 landmarks + 7 emotion
    private val FEAT_DIM_201 = 201
    private val TOP_K        = 5
    private val CONF_THRESHOLD = 0.60f
    private val STABLE_FRAMES  = 3
    private val HISTORY_SIZE   = 5

    // [OPT-6] Interval tăng để giảm tải
    private val INFER_EVERY_N  = 4
    // MIRROR_EVERY_N đã bỏ — mirror luôn chạy mỗi lần infer
    // MIRROR_WEIGHT đã bỏ — không giảm weight, lấy max(A, B) thẳng

    // [OPT-4/5] Throttle detector không cần thiết mỗi frame
    private val FACE_EVERY_N = 5    // face chỉ cho UI, ~6fps là đủ
    private val POSE_EVERY_N = 2    // pose thay đổi chậm, ~15fps là đủ

    private val POSE_KEY_INDICES = intArrayOf(0, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24)
    private val FINGER_JOINTS = arrayOf(
        intArrayOf(1, 2, 3, 4), intArrayOf(5, 6, 7, 8), intArrayOf(9, 10, 11, 12),
        intArrayOf(13, 14, 15, 16), intArrayOf(17, 18, 19, 20)
    )

    // ── [OPT-1] Ring buffer ──────────────────────────────────────────
    // 1 FloatArray phẳng kích thước cố định = SEQ_LEN × FEAT_DIM float
    // head = vị trí ghi tiếp theo (overwrite theo vòng)
    // bufferCount = số frame thực sự có trong buffer (tối đa SEQ_LEN)
    private val ringBuffer  = FloatArray(SEQ_LEN * FEAT_DIM)
    private var ringHead    = 0
    private var bufferCount = 0
    private val ringLock    = Any()

    // ── [OPT-2] Pre-allocated scratch buffers — không bao giờ new trong hot path ──
    private val scratchFeat201  = FloatArray(FEAT_DIM_201)  // feature extraction
    private val scratchFeat208  = FloatArray(FEAT_DIM)      // feat201 + emoVec
    private val scratchMirror   = FloatArray(FEAT_DIM_201)  // mirror computation
    private val scratchFlat     = FloatArray(SEQ_LEN * FEAT_DIM) // tensor input pass A
    private val scratchMirFlat  = FloatArray(SEQ_LEN * FEAT_DIM) // tensor input pass B
    private val scratchLastFeat = FloatArray(FEAT_DIM_201)  // snapshot frame cuối cho mirror

    // ── [OPT-3] Single scope + AtomicBoolean guard ───────────────────
    private val inferenceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val isInferring    = AtomicBoolean(false)

    // ── Callbacks ─────────────────────────────────────────────────────
    var onResultListener: ((String, Float) -> Unit)? = null
    var onLandmarksListener: ((HandLandmarkerResult?, PoseLandmarkerResult?, FaceLandmarkerResult?) -> Unit)? = null

    // ── State ──────────────────────────────────────────────────────────
    private var labels: List<String> = emptyList()
    private val predictionHistory = ArrayDeque<Pair<String, Float>>(HISTORY_SIZE + 1)
    private var mirrorCounter = 0  // giữ lại để không lỗi nếu còn ref, không dùng nữa
    private var frameCounter  = 0

    // Cache kết quả pose/face để dùng lại cho frame bị throttle
    @Volatile private var lastPoseResult: PoseLandmarkerResult? = null
    @Volatile private var lastFaceResult: FaceLandmarkerResult? = null

    // Tolerance cho NO HANDS: chỉ emit sau N frame liên tiếp không có tay
    // Tránh 1 frame miss duy nhất (blur, góc tay) xóa sạch buffer
    private var noHandsFrames = 0
    private val NO_HANDS_TOLERANCE = 8

    // Frame rate limiting — giảm từ 30 xuống 20fps
    // 20fps đủ cho nhận diện ký hiệu, giảm 33% tải MediaPipe so với 30fps
    private var lastFrameTime     = 0L
    private val FRAME_DURATION_MS = 1000L / 30L

    // FPS monitoring
    private var fpsFrameCount = 0
    private var fpsStartTime  = 0L
    private var currentFPS    = 0f

    private val TAG = "SL_DEBUG"


    // ═══════════════════════════════════════════════════════════════════
    // KHỞI TẠO
    // ═══════════════════════════════════════════════════════════════════

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Khoi tao...")
                loadLabelsFromJson()
                module = LiteModuleLoader.load(assetFilePath(context, "vsl_model.ptl"))
                setupMediaPipe()
                Log.d(TAG, "Hoan tat! ${labels.size} nhan")
            } catch (e: Exception) {
                Log.e(TAG, "Loi khoi tao: ${e.message}", e)
                throw e
            }
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
                val idx = jsonObj.getInt(key)
                if (idx in tempList.indices) tempList[idx] = key
            }
            labels = tempList.filterNotNull()
        } catch (e: Exception) {
            Log.e(TAG, "Loi label_map.json: ${e.message}")
            labels = listOf("__idle__tay_xuoi_hong", "cam_on", "xin_chao")
        }
    }

    private fun setupMediaPipe() {
        handLandmarker = HandLandmarker.createFromOptions(
            context,
            HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(BaseOptions.builder().setModelAssetPath("models/hand_landmarker.task").build())
                .setNumHands(2)
                .setMinHandDetectionConfidence(0.5f)
                .setMinHandPresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setRunningMode(RunningMode.VIDEO)
                .build()
        )

        poseLandmarker = PoseLandmarker.createFromOptions(
            context,
            PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(BaseOptions.builder().setModelAssetPath("models/pose_landmarker.task").build())
                .setMinPoseDetectionConfidence(0.5f)
                .setMinPosePresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setRunningMode(RunningMode.VIDEO)
                .build()
        )

        faceLandmarker = FaceLandmarker.createFromOptions(
            context,
            FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(BaseOptions.builder().setModelAssetPath("models/face_landmarker.task").build())
                .setNumFaces(1)
                .setMinFaceDetectionConfidence(0.5f)
                .setMinFacePresenceConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setOutputFaceBlendshapes(true)
                .setRunningMode(RunningMode.VIDEO)
                .build()
        )
    }


    // ═══════════════════════════════════════════════════════════════════
    // ENTRY POINT
    // ═══════════════════════════════════════════════════════════════════

    fun processFrame(bitmap: Bitmap) {
        if (handLandmarker == null) return

        val now = SystemClock.uptimeMillis()
        if (now - lastFrameTime < FRAME_DURATION_MS) return
        lastFrameTime = now

        updateFPS()
        frameCounter++

        try {
            val mpImage = BitmapImageBuilder(bitmap).build()

            // Hand: luôn chạy mỗi frame (quan trọng nhất)
            val handResult = handLandmarker?.detectForVideo(mpImage, now)

            // [OPT-5] Pose: chỉ chạy mỗi POSE_EVERY_N frame
            val poseResult = if (frameCounter % POSE_EVERY_N == 0) {
                poseLandmarker?.detectForVideo(mpImage, now).also { lastPoseResult = it }
            } else {
                lastPoseResult
            }

            // [OPT-4] Face: chỉ chạy mỗi FACE_EVERY_N frame (chỉ cho UI)
            val faceResult = if (frameCounter % FACE_EVERY_N == 0) {
                faceLandmarker?.detectForVideo(mpImage, now).also { lastFaceResult = it }
            } else {
                lastFaceResult
            }

            // Gửi landmarks cho PoseOverlay
            onLandmarksListener?.invoke(handResult, poseResult, faceResult)

            processAllFeatures(handResult, poseResult)

        } catch (e: Exception) {
            Log.e(TAG, "Loi processFrame: ${e.message}", e)
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    // TRICH XUAT FEATURE + CAP NHAT RING BUFFER
    // ═══════════════════════════════════════════════════════════════════

    private fun processAllFeatures(
        handResult: HandLandmarkerResult?,
        poseResult: PoseLandmarkerResult?
    ) {
        // [OPT-2] Tái dùng scratchFeat201, không new
        scratchFeat201.fill(0f)

        // ── 1. Pose (15 keypoints × 3 = 45 float) ───────────────────
        if (poseResult != null && poseResult.landmarks().isNotEmpty()) {
            val poseLms = poseResult.landmarks()[0]
            var idx = 0
            for (keyIdx in POSE_KEY_INDICES) {
                if (keyIdx < poseLms.size) {
                    scratchFeat201[idx++] = poseLms[keyIdx].x()
                    scratchFeat201[idx++] = poseLms[keyIdx].y()
                    scratchFeat201[idx++] = poseLms[keyIdx].z()
                } else {
                    idx += 3
                }
            }
        }

        // ── 2. Tay + Curl ────────────────────────────────────────────
        val hasHands = handResult != null && handResult.landmarks().isNotEmpty()
        if (hasHands) {
            noHandsFrames = 0  // reset counter khi tay detect lại được
            val hands = handResult!!.landmarks()
            if (hands.size == 1) {
                val h = hands[0]
                var idx = 45
                for (lm in h) { scratchFeat201[idx++] = lm.x(); scratchFeat201[idx++] = lm.y(); scratchFeat201[idx++] = lm.z() }
                computeFingerCurlInto(h, scratchFeat201, 171)
            } else {
                // Sort 2 tay theo mean x (không dùng .sortedBy để tránh alloc)
                val meanX0 = hands[0].sumOf { it.x().toDouble() }.toFloat() / 21f
                val meanX1 = hands[1].sumOf { it.x().toDouble() }.toFloat() / 21f
                val hL = if (meanX0 <= meanX1) hands[0] else hands[1]
                val hR = if (meanX0 <= meanX1) hands[1] else hands[0]

                var idxA = 45
                for (lm in hL) { scratchFeat201[idxA++] = lm.x(); scratchFeat201[idxA++] = lm.y(); scratchFeat201[idxA++] = lm.z() }
                computeFingerCurlInto(hL, scratchFeat201, 171)

                var idxB = 108
                for (lm in hR) { scratchFeat201[idxB++] = lm.x(); scratchFeat201[idxB++] = lm.y(); scratchFeat201[idxB++] = lm.z() }
                computeFingerCurlInto(hR, scratchFeat201, 186)
            }
        }

        // ── 3. Normalize in-place ─────────────────────────────────────
        normalizeFeat201(scratchFeat201)

        // Snapshot feat201 để dùng cho mirror pass
        scratchFeat201.copyInto(scratchLastFeat)

        // ── 4. Ghep emotion vao scratchFeat208 (in-place, khong new) ──
        scratchFeat201.copyInto(scratchFeat208, destinationOffset = 0)
        scratchFeat208.fill(0f, FEAT_DIM_201, FEAT_DIM)
        scratchFeat208[FEAT_DIM_201 + 6] = 1.0f  // neutral = index 6

        // ── 5. Ghi vao ring buffer (zero allocation) ─────────────────
        synchronized(ringLock) {
            val offset = ringHead * FEAT_DIM
            scratchFeat208.copyInto(ringBuffer, destinationOffset = offset)
            ringHead = (ringHead + 1) % SEQ_LEN
            if (bufferCount < SEQ_LEN) bufferCount++
        }

        // ── 6. Xu ly khi khong co tay (clear buffer) ─────────────────
        if (!hasHands) {
            noHandsFrames++
            if (noHandsFrames >= NO_HANDS_TOLERANCE) {
                synchronized(ringLock) { bufferCount = 0; ringHead = 0 }
                inferenceScope.launch(Dispatchers.Main) {
                    onResultListener?.invoke("[NO HANDS]", 0f)
                }
                noHandsFrames = 0
            }
            return
        }

        // ── 7. Inference moi INFER_EVERY_N frame ─────────────────────
        val bufferFull = synchronized(ringLock) { bufferCount >= SEQ_LEN }
        if (bufferFull && frameCounter % INFER_EVERY_N == 0) {
            // [OPT-3] Skip neu inference truoc chua xong
            if (isInferring.compareAndSet(false, true)) {
                val feat201Snap = scratchLastFeat.copyOf()  // 1 alloc nho, chi khi can infer
                inferenceScope.launch {
                    try { runInferenceWithMirror(feat201Snap) }
                    finally { isInferring.set(false) }
                }
            }
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    // DUAL-PASS MIRROR FUSION
    // ═══════════════════════════════════════════════════════════════════

    private suspend fun runInferenceWithMirror(feat201Norm: FloatArray) {
        val count = synchronized(ringLock) { bufferCount }
        if (count < SEQ_LEN || labels.isEmpty() || module == null) return

        // Pass A: gốc
        flattenRingInto(scratchFlat)
        val resultA = runVSLModelOnFlat(scratchFlat) ?: return

        // Pass B: mirror — LUÔN chạy mỗi lần infer (bỏ throttle MIRROR_EVERY_N)
        // Lý do: nếu người dùng thực hiện động tác bằng tay ngược (trái/phải đảo),
        // mirror pass phải cập nhật liên tục mới bắt được — cache cũ sẽ miss
        flattenRingInto(scratchMirFlat)
        mirrorFeatInto(feat201Norm, scratchMirror)
        val lastOff = (SEQ_LEN - 1) * FEAT_DIM
        scratchMirror.copyInto(scratchMirFlat, destinationOffset = lastOff, endIndex = FEAT_DIM_201)
        scratchMirFlat[lastOff + FEAT_DIM_201 + 6] = 1.0f
        val resultB = runVSLModelOnFlat(scratchMirFlat) ?: emptyMap()

        // Fusion: lấy max(A, B) — không giảm weight mirror
        // Nếu model nhận ra bằng tay ngược (B), conf của B có thể cao hơn A → ưu tiên B
        val merged = HashMap<String, Float>(resultA.size * 2)
        merged.putAll(resultA)
        for ((label, confB) in resultB) {
            val existing = merged[label]
            if (existing == null || confB > existing) merged[label] = confB
        }

        val topPreds = merged.entries.sortedByDescending { it.value }.take(TOP_K)
        if (topPreds.isEmpty()) return

        val (label, conf) = topPreds[0].toPair()

        // Majority vote
        predictionHistory.addLast(Pair(label, conf))
        if (predictionHistory.size > HISTORY_SIZE) predictionHistory.removeFirst()

        val smoothed = getSmoothedPrediction() ?: return
        val (finalLabel, finalConf) = smoothed

        withContext(Dispatchers.Main) {
            if (finalLabel == "__idle__tay_xuoi_hong") onResultListener?.invoke("...", finalConf)
            else onResultListener?.invoke(finalLabel, finalConf)
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    // PYTORCH INFERENCE — chay tren flat array da pre-allocated
    // ═══════════════════════════════════════════════════════════════════

    private fun runVSLModelOnFlat(flat: FloatArray): Map<String, Float>? {
        if (labels.isEmpty() || module == null) return null
        return try {
            val shape        = longArrayOf(1, SEQ_LEN.toLong(), FEAT_DIM.toLong())
            val inputTensor  = Tensor.fromBlob(flat, shape)
            val outputTensor = module!!.forward(IValue.from(inputTensor)).toTensor()
            val probs        = softmax(outputTensor.dataAsFloatArray)

            val result = HashMap<String, Float>(TOP_K * 2)
            probs.indices.sortedByDescending { probs[it] }.take(TOP_K).forEach { i ->
                if (i < labels.size) result[labels[i]] = probs[i]
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Loi runVSLModel: ${e.message}")
            null
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    // RING BUFFER -> FLAT ARRAY (doc theo thu tu thoi gian)
    // ═══════════════════════════════════════════════════════════════════

    private fun flattenRingInto(dst: FloatArray) {
        synchronized(ringLock) {
            val oldest = (ringHead - bufferCount + SEQ_LEN) % SEQ_LEN
            for (i in 0 until SEQ_LEN) {
                val slot   = (oldest + i) % SEQ_LEN
                val srcOff = slot * FEAT_DIM
                val dstOff = i   * FEAT_DIM
                System.arraycopy(ringBuffer, srcOff, dst, dstOff, FEAT_DIM)
            }
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    // MIRROR FEATURE — ghi vao dst (khong return new array)
    // Port tu mirror_feat() Python
    // ═══════════════════════════════════════════════════════════════════

    private fun mirrorFeatInto(src: FloatArray, dst: FloatArray) {
        src.copyInto(dst)

        // Flip x tay trai [45:108]
        for (j in 0 until 21) dst[45 + j * 3] = 1.0f - src[45 + j * 3]
        // Flip x tay phai [108:171]
        for (j in 0 until 21) dst[108 + j * 3] = 1.0f - src[108 + j * 3]

        // Swap 2 slot tay (in-place, khong alloc)
        for (j in 0 until 63) {
            val tmp = dst[45 + j]; dst[45 + j] = dst[108 + j]; dst[108 + j] = tmp
        }
        // Swap curl [171:186] <-> [186:201]
        for (j in 0 until 15) {
            val tmp = dst[171 + j]; dst[171 + j] = dst[186 + j]; dst[186 + j] = tmp
        }
        // Flip x pose [0:45]
        for (i in 0 until 15) dst[i * 3] = 1.0f - src[i * 3]
    }


    // ═══════════════════════════════════════════════════════════════════
    // FINGER CURL — ghi thang vao feat201 (khong return FloatArray)
    // ═══════════════════════════════════════════════════════════════════

    private fun computeFingerCurlInto(
        lms: List<NormalizedLandmark>,
        dst: FloatArray,
        dstOffset: Int
    ) {
        val wrist = lms[0]
        val scale = max(dist(lms[9], wrist), 1e-8f)

        for (i in FINGER_JOINTS.indices) {
            val joints = FINGER_JOINTS[i]
            val mcp = lms[joints[0]]; val pip = lms[joints[1]]
            val dip = lms[joints[2]]; val tip = lms[joints[3]]

            val dTip  = dist(tip, wrist)
            val dMcp  = max(dist(mcp, wrist), 1e-8f)
            val angle = angleBetween(mcp, pip, dip)

            val b = dstOffset + i * 3
            dst[b]     = (dTip / dMcp / 2.0f).coerceIn(0f, 1f)
            dst[b + 1] = ((Math.PI.toFloat() - angle) / Math.PI.toFloat()).coerceIn(0f, 1f)
            dst[b + 2] = (dTip / scale / 3.0f).coerceIn(0f, 1f)
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    // NORMALIZE in-place
    // ═══════════════════════════════════════════════════════════════════

    private fun normalizeFeat201(f: FloatArray) {
        // Pose: center theo hip (index 13, 14), scale theo shoulder (index 1, 2)
        var hasPose = false
        for (i in 0 until 45) { if (f[i] != 0f) { hasPose = true; break } }
        if (hasPose) {
            val hipX = (f[39] + f[42]) / 2f  // 13×3=39, 14×3=42
            val hipY = (f[40] + f[43]) / 2f
            val hipZ = (f[41] + f[44]) / 2f
            for (i in 0 until 15) { f[i*3] -= hipX; f[i*3+1] -= hipY; f[i*3+2] -= hipZ }
            val sd = dist3f(f[3], f[4], f[5], f[6], f[7], f[8]) // 1×3=3, 2×3=6
            if (sd > 1e-6f) for (i in 0 until 45) f[i] /= sd
        }

        // Hand: center theo wrist, scale theo middle MCP (index 9)
        for (s in intArrayOf(45, 108)) {
            var hasHand = false
            for (i in s until s + 63) { if (f[i] != 0f) { hasHand = true; break } }
            if (!hasHand) continue
            val wx = f[s]; val wy = f[s+1]; val wz = f[s+2]
            for (i in 0 until 21) { f[s+i*3] -= wx; f[s+i*3+1] -= wy; f[s+i*3+2] -= wz }
            val sc = dist3f(0f, 0f, 0f, f[s+27], f[s+28], f[s+29]) // 9×3=27
            if (sc > 1e-6f) for (i in s until s + 63) f[i] /= sc
        }
    }


    // ═══════════════════════════════════════════════════════════════════
    // PREDICTION SMOOTHING
    // ═══════════════════════════════════════════════════════════════════

    private fun getSmoothedPrediction(): Pair<String, Float>? {
        if (predictionHistory.size < STABLE_FRAMES) return null
        val votes = predictionHistory
            .filter { it.second >= CONF_THRESHOLD }
            .groupBy { it.first }
            .mapValues { e -> e.value.size to e.value.map { it.second }.average().toFloat() }
        val winner = votes.maxByOrNull { it.value.first } ?: return null
        return Pair(winner.key, winner.value.second)
    }


    // ═══════════════════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════════════════

    private fun dist(a: NormalizedLandmark, b: NormalizedLandmark): Float =
        sqrt((a.x()-b.x()).pow(2) + (a.y()-b.y()).pow(2) + (a.z()-b.z()).pow(2))

    private fun dist3f(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Float =
        sqrt((x1-x2).pow(2) + (y1-y2).pow(2) + (z1-z2).pow(2))

    private fun angleBetween(a: NormalizedLandmark, b: NormalizedLandmark, c: NormalizedLandmark): Float {
        val baX = a.x()-b.x(); val baY = a.y()-b.y(); val baZ = a.z()-b.z()
        val bcX = c.x()-b.x(); val bcY = c.y()-b.y(); val bcZ = c.z()-b.z()
        val dot   = baX*bcX + baY*bcY + baZ*bcZ
        val magBa = sqrt(baX*baX + baY*baY + baZ*baZ)
        val magBc = sqrt(bcX*bcX + bcY*bcY + bcZ*bcZ)
        return acos((dot / (magBa*magBc + 1e-8f)).coerceIn(-1f, 1f))
    }

    private fun softmax(logits: FloatArray): FloatArray {
        val maxLogit = logits.max() ?: 0f
        val exps = FloatArray(logits.size) { exp((logits[it] - maxLogit).toDouble()).toFloat() }
        val sum  = exps.sum()
        return FloatArray(exps.size) { exps[it] / sum }
    }

    private fun updateFPS() {
        fpsFrameCount++
        val now = System.currentTimeMillis()
        if (fpsStartTime == 0L) fpsStartTime = now
        val elapsed = now - fpsStartTime
        if (elapsed >= 1000) {
            currentFPS = fpsFrameCount * 1000f / elapsed
            if (frameCounter % 60 == 0) Log.d(TAG, "FPS: %.1f | Buf: %d/%d | Inferring: %s".format(
                currentFPS, synchronized(ringLock) { bufferCount }, SEQ_LEN, isInferring.get()))
            fpsFrameCount = 0; fpsStartTime = now
        }
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) return file.absolutePath
        context.assets.open(assetName).use { inp ->
            FileOutputStream(file).use { out ->
                val buf = ByteArray(4 * 1024); var n: Int
                while (inp.read(buf).also { n = it } != -1) out.write(buf, 0, n)
            }
        }
        return file.absolutePath
    }


    // ═══════════════════════════════════════════════════════════════════
    // CLEANUP
    // ═══════════════════════════════════════════════════════════════════

    fun close() {
        try {
            handLandmarker?.close(); poseLandmarker?.close()
            faceLandmarker?.close(); module?.destroy()
            synchronized(ringLock) { bufferCount = 0; ringHead = 0 }
            predictionHistory.clear()
            Log.d(TAG, "Closed")
        } catch (e: Exception) {
            Log.e(TAG, "Loi close: ${e.message}")
        }
    }

    fun getDebugInfo(): String = """
        |Labels: ${labels.size} | Buffer: ${synchronized(ringLock){bufferCount}}/$SEQ_LEN
        |FPS: ${"%.1f".format(currentFPS)} | Frame: $frameCounter
        |Inferring: ${isInferring.get()} | NoHandsFrames: $noHandsFrames
    """.trimMargin()
}