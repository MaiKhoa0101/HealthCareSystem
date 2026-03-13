package com.hellodoc.healthcaresystem.view.user.VSL

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.model.dataclass.requestmodel.Subtitle
import com.hellodoc.healthcaresystem.view.user.home.fasttalk.AutoInputConversation
import com.hellodoc.healthcaresystem.viewmodel.VSLViewModel

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.view.TextureView
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.VSL
import com.hellodoc.healthcaresystem.view.user.supportfunction.BackgroundRemovalMode
import com.hellodoc.healthcaresystem.view.user.supportfunction.ChromaKeyGLSurfaceView
import com.hellodoc.healthcaresystem.view.user.supportfunction.NoiseSuppressedAudioRecord
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Composable
fun TranslateVoiceToVid(
    navHostController: NavHostController,
    vslViewModel: VSLViewModel = hiltViewModel()
) {
    val context: Context = LocalContext.current
    val vslResponse by vslViewModel.vslResponse.collectAsState()

    var partialText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var hasAudioPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasAudioPermission  = permissions[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
    }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10_000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 200L)
        }
    }

    // ✅ remember để tồn tại suốt vòng đời Composable
    val audioRecord = remember { NoiseSuppressedAudioRecord(context) }

    fun startListening() {
        if (!hasAudioPermission) return
        isListening = true
        speechRecognizer.startListening(recognizerIntent)
    }

    // ✅ DisposableEffect chỉ lo VAD + SpeechRecognizer
    DisposableEffect(hasAudioPermission) {
        if (!hasAudioPermission) {
            return@DisposableEffect onDispose {
                speechRecognizer.destroy()
                audioRecord.release()
            }
        }

        // ✅ Initialize hardware noise suppression — thất bại thì vẫn chạy VAD bình thường
        val hardwareNsOk = audioRecord.initialize()
        Log.d("VAD", if (hardwareNsOk) "✅ Hardware NS enabled" else "⚠️ Hardware NS unavailable — software VAD only")

        // ================================================================
        // ⚙️ VAD PARAMETERS
        // ================================================================
        val NOISE_ADAPT_RATE_SPEECH    = 0.001f
        val NOISE_ADAPT_RATE_SILENCE   = 0.05f
        val NOISE_FLOOR_INIT_MS        = 800L
        val SNR_SPEECH_THRESHOLD_DB    = 8f
        val SNR_NOISE_THRESHOLD_DB     = 3f
        val VAD_HANGOVER_MS            = 350L
        val VAD_RETROACTIVE_MS         = 150L
        val PROB_RISE_ALPHA            = 0.6f
        val PROB_FALL_ALPHA            = 0.15f
        val SPEECH_PROB_THRESHOLD      = 0.65f
        val ENERGY_WINDOW_SIZE         = 8
        val SUSTAINED_SPEECH_FRAMES    = 3
        val MIN_SPEECH_DURATION_MS     = 250L
        val MIN_CONFIDENCE             = 0.55f

        // ================================================================
        // ⚙️ NOISE SUPPRESSION PARAMETERS
        // ================================================================
        val SPECTRAL_GATE_RATIO        = 1.8f
        val SPIKE_WINDOW_SIZE          = 5
        val SPIKE_RATIO_THRESHOLD      = 3.0f
        val NEAR_FIELD_SLOPE_THRESHOLD = 1.5f

        // ================================================================
        // 🔧 VAD STATE
        // ================================================================
        var smoothedRms             = 0f
        var speechProbability       = 0f
        var adaptiveNoiseFloor      = 2f
        var isCalibrating           = true
        var calibrationStart        = -1L
        var speechDetected          = false
        var speechStartTime         = -1L
        var belowThresholdSince     = -1L
        var consecutiveSpeechFrames = 0
        val recentEnergyWindow      = ArrayDeque<Float>(ENERGY_WINDOW_SIZE)
        val spikeDetectionWindow    = ArrayDeque<Float>(SPIKE_WINDOW_SIZE)
        var prevSmoothedRms         = 0f

        fun resetState() {
            smoothedRms             = 0f
            speechProbability       = 0f
            adaptiveNoiseFloor      = 2f
            isCalibrating           = true
            calibrationStart        = -1L
            speechDetected          = false
            speechStartTime         = -1L
            belowThresholdSince     = -1L
            consecutiveSpeechFrames = 0
            prevSmoothedRms         = 0f
            recentEnergyWindow.clear()
            spikeDetectionWindow.clear()
        }

        fun computeSNR(rms: Float) =
            if (adaptiveNoiseFloor <= 0f) 0f
            else 20f * Math.log10((rms / adaptiveNoiseFloor).toDouble()).toFloat()

        fun energyVariance(): Float {
            if (recentEnergyWindow.size < 2) return 0f
            val mean = recentEnergyWindow.average().toFloat()
            return recentEnergyWindow.map { (it - mean) * (it - mean) }.average().toFloat()
        }

        fun passesSpectralGate(rms: Float) = rms >= adaptiveNoiseFloor * SPECTRAL_GATE_RATIO

        fun isSpike(rms: Float): Boolean {
            if (spikeDetectionWindow.size < SPIKE_WINDOW_SIZE) return false
            val median = spikeDetectionWindow.sorted()[SPIKE_WINDOW_SIZE / 2]
            return median > 0f && rms > median * SPIKE_RATIO_THRESHOLD
        }

        fun isNearField(rms: Float): Boolean {
            val slope = rms - prevSmoothedRms
            return slope >= NEAR_FIELD_SLOPE_THRESHOLD || speechDetected
        }

        fun fireRequest(text: String) {
            val cleaned = text.trim()
            if (cleaned.isBlank()) return
            Log.d("VAD", "🚀 Fire: \"$cleaned\"")
            partialText = ""

            //Dịch sang tiếng anh rồi dịch lại tiếng việt gán cho cleaned
            vslViewModel.fetchVSL(Subtitle(text = cleaned))
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                resetState()
                calibrationStart = System.currentTimeMillis()
            }

            override fun onRmsChanged(rmsdB: Float) {
                val now = System.currentTimeMillis()

                val alpha = if (rmsdB > smoothedRms) PROB_RISE_ALPHA else PROB_FALL_ALPHA
                smoothedRms = alpha * rmsdB + (1 - alpha) * smoothedRms

                if (recentEnergyWindow.size >= ENERGY_WINDOW_SIZE) recentEnergyWindow.removeFirst()
                recentEnergyWindow.addLast(smoothedRms)
                if (spikeDetectionWindow.size >= SPIKE_WINDOW_SIZE) spikeDetectionWindow.removeFirst()
                spikeDetectionWindow.addLast(smoothedRms)

                if (isCalibrating) {
                    if (calibrationStart == -1L) calibrationStart = now
                    adaptiveNoiseFloor = NOISE_ADAPT_RATE_SILENCE * smoothedRms +
                            (1 - NOISE_ADAPT_RATE_SILENCE) * adaptiveNoiseFloor
                    if (now - calibrationStart >= NOISE_FLOOR_INIT_MS) {
                        isCalibrating = false
                        Log.d("VAD", "📊 Noise floor = $adaptiveNoiseFloor")
                    }
                    prevSmoothedRms = smoothedRms
                    return
                }

                val spike     = isSpike(smoothedRms)
                val gated     = passesSpectralGate(smoothedRms)
                val nearField = isNearField(smoothedRms)
                val isNoisyFrame = spike || (!gated && !speechDetected) || (!nearField && !speechDetected)

                prevSmoothedRms = smoothedRms

                val snr = computeSNR(smoothedRms)
                val rawFrameProb = when {
                    snr >= SNR_SPEECH_THRESHOLD_DB -> 1.0f
                    snr >= SNR_NOISE_THRESHOLD_DB  ->
                        (snr - SNR_NOISE_THRESHOLD_DB) / (SNR_SPEECH_THRESHOLD_DB - SNR_NOISE_THRESHOLD_DB)
                    else -> 0.0f
                }
                val frameProbability = if (isNoisyFrame) 0f else rawFrameProb

                val probAlpha = if (frameProbability > speechProbability) PROB_RISE_ALPHA else PROB_FALL_ALPHA
                speechProbability = probAlpha * frameProbability + (1 - probAlpha) * speechProbability

                val noiseAdaptRate = if (speechProbability > SPEECH_PROB_THRESHOLD)
                    NOISE_ADAPT_RATE_SPEECH else NOISE_ADAPT_RATE_SILENCE
                adaptiveNoiseFloor = noiseAdaptRate * smoothedRms + (1 - noiseAdaptRate) * adaptiveNoiseFloor

                val isSpeechFrame = speechProbability >= SPEECH_PROB_THRESHOLD
                val isLikelySustainedNoise = !speechDetected &&
                        energyVariance() < 0.5f &&
                        smoothedRms > adaptiveNoiseFloor &&
                        consecutiveSpeechFrames < SUSTAINED_SPEECH_FRAMES

                if (isSpeechFrame && !isLikelySustainedNoise) {
                    consecutiveSpeechFrames++
                    if (consecutiveSpeechFrames >= SUSTAINED_SPEECH_FRAMES && !speechDetected) {
                        speechDetected  = true
                        speechStartTime = now - VAD_RETROACTIVE_MS
                        belowThresholdSince = -1L
                        Log.d("VAD", "▶️ START")
                    }
                } else {
                    consecutiveSpeechFrames = 0
                    if (speechDetected) {
                        if (belowThresholdSince == -1L) {
                            belowThresholdSince = now
                        } else if (now - belowThresholdSince >= VAD_HANGOVER_MS) {
                            val duration = now - speechStartTime
                            if (duration >= MIN_SPEECH_DURATION_MS) {
                                Log.d("VAD", "⏹️ END ${duration}ms")
                                fireRequest(partialText)
                                speechRecognizer.stopListening()
                            } else {
                                speechDetected          = false
                                speechStartTime         = -1L
                                belowThresholdSince     = -1L
                                consecutiveSpeechFrames = 0
                            }
                        }
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                if (isCalibrating || !speechDetected) return
                partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()?.let { partialText = it }
            }

            override fun onResults(results: Bundle?) {
                if (partialText.isNotBlank()) {
                    fireRequest(partialText)
                } else {
                    val confidences = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.indices
                        ?.filter { i -> (confidences?.getOrNull(i) ?: 1f) >= MIN_CONFIDENCE }
                        ?.maxByOrNull { i -> confidences?.getOrNull(i) ?: 0f }
                        ?.let { i ->
                            fireRequest(
                                results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![i]
                            )
                        }
                }
                startListening()
            }

            override fun onBeginningOfSpeech() { isListening = true }
            override fun onEndOfSpeech()       {}

            override fun onError(error: Int) {
                resetState()
                partialText = ""
                if (error != SpeechRecognizer.ERROR_RECOGNIZER_BUSY) startListening()
            }

            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        // ✅ Release CẢ HAI trong cùng 1 onDispose
        onDispose {
            speechRecognizer.destroy()
            audioRecord.release()
        }
    }

    LaunchedEffect(hasAudioPermission) { if (hasAudioPermission) startListening() }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission || !hasAudioPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (hasCameraPermission) CameraPreviewView(modifier = Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(0.4f)
                .align(Alignment.CenterStart)
                .background(
                    color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            SequentialVideoPlayer(
                vslList = vslResponse,
                trimStartMs = 500L,
                trimEndFromLastMs = 1_000L,
                backgroundRemoval = BackgroundRemovalMode.None,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (vslResponse.isNotEmpty()) 1f else 0f)
            )
        }
    }
}
@OptIn(UnstableApi::class)
@Composable
fun SequentialVideoPlayer(
    vslList: List<VSL>,
    modifier: Modifier = Modifier,
    trimStartMs: Long = 0L,
    trimEndFromLastMs: Long = 0L,
    backgroundRemoval: BackgroundRemovalMode = BackgroundRemovalMode.None
) {
    val context = LocalContext.current

    // 1. Biến state để lưu text hiển thị của video ĐANG PHÁT
    var currentGrossText by remember { mutableStateOf("") }

    val exoPlayer = remember {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val unsafeOkHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
        val dataSourceFactory = OkHttpDataSource.Factory(unsafeOkHttpClient)
        val mediaSourceFactory = DefaultMediaSourceFactory(context).setDataSourceFactory(dataSourceFactory)
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(1_000, 10_000, 500, 1_000)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .build().apply {
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        if (trimStartMs > 0) seekTo(trimStartMs)

                        // 2. Cập nhật currentGrossText khi ExoPlayer chuyển qua video mới
                        currentGrossText = mediaItem?.mediaMetadata?.displayTitle?.toString() ?: ""
                    }
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("VideoPlayerError", "Lỗi: ${error.errorCodeName} | ${error.message}")
                    }
                })
            }
    }

    LaunchedEffect(exoPlayer, trimEndFromLastMs) {
        while (true) {
            delay(100)
            if (exoPlayer.isPlaying) {
                val duration = exoPlayer.duration
                if (duration > 0 && trimEndFromLastMs > 0) {
                    val endMs = duration - trimEndFromLastMs
                    if (endMs > trimStartMs && exoPlayer.currentPosition >= endMs) {
                        exoPlayer.seekToNextMediaItem()
                    }
                }
            }
        }
    }

    LaunchedEffect(vslList) {
        if (vslList.isNotEmpty()) {
            exoPlayer.clearMediaItems()
            val mediaItems = vslList.map { vsl ->
                val url = resolveVideoUrl(vsl.url)

                // 3. Lưu gross text vào MediaMetadata của từng Item
                val metadata = androidx.media3.common.MediaMetadata.Builder()
                    .setDisplayTitle(vsl.gross)
                    .build()

                val builder = MediaItem.Builder()
                    .setUri(Uri.parse(url))
                    .setMediaMetadata(metadata)

                when {
                    url.endsWith(".m3u8", ignoreCase = true) ||
                            url.contains("stream.mux.com") -> builder.setMimeType(MimeTypes.APPLICATION_M3U8)
                }
                builder.build()
            }
            exoPlayer.setMediaItems(mediaItems)
            exoPlayer.prepare()
            if (trimStartMs > 0) exoPlayer.seekTo(trimStartMs)
            exoPlayer.play()
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    // 4. Wrap Video và Text vào chung một Column
    Column(
        modifier = modifier, // Áp dụng modifier từ cha ở đây
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Cố định tỉ lệ video 16:9
        val videoModifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)

        when (backgroundRemoval) {
            is BackgroundRemovalMode.None -> {
                AndroidView(
                    modifier = videoModifier,
                    factory = { ctx ->
                        FrameLayout(ctx).apply {
                            clipChildren = true
                            outlineProvider = ViewOutlineProvider.BOUNDS
                            clipToOutline = true
                            val textureView = TextureView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                rotation = 90f
                            }
                            addView(textureView)
                            exoPlayer.setVideoTextureView(textureView)
                            setupScaleListener(this, textureView, exoPlayer)
                        }
                    }
                )
            }

            is BackgroundRemovalMode.ChromaKey -> {
                val mode = backgroundRemoval
                AndroidView(
                    modifier = videoModifier,
                    factory = { ctx ->
                        FrameLayout(ctx).apply {
                            clipChildren = true
                            outlineProvider = ViewOutlineProvider.BOUNDS
                            clipToOutline = true

                            val sourceTexture = TextureView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(1, 1)
                            }
                            val outputView = ImageView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                scaleType = ImageView.ScaleType.FIT_XY
                                rotation = 90f
                            }

                            addView(sourceTexture)
                            addView(outputView)

                            sourceTexture.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                                override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {
                                    Handler(Looper.getMainLooper()).post {
                                        exoPlayer.setVideoTextureView(sourceTexture)
                                    }
                                }
                                override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) {}
                                override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                                    Handler(Looper.getMainLooper()).post {
                                        exoPlayer.clearVideoTextureView(sourceTexture)
                                    }
                                    return true
                                }
                                override fun onSurfaceTextureUpdated(st: SurfaceTexture) {
                                    val bitmap = sourceTexture.getBitmap(sourceTexture.width, sourceTexture.height) ?: return
                                    val output = applyChromaKey(bitmap, mode.colorRGB, mode.threshold)
                                    outputView.post { outputView.setImageBitmap(output) }
                                }
                            }

                            setupScaleListener(this, outputView, exoPlayer)
                        }
                    }
                )
            }

            is BackgroundRemovalMode.Segmentation -> {
                val segmenter = remember {
                    Segmentation.getClient(
                        SelfieSegmenterOptions.Builder()
                            .setDetectorMode(SelfieSegmenterOptions.STREAM_MODE)
                            .enableRawSizeMask()
                            .build()
                    )
                }
                DisposableEffect(Unit) {
                    onDispose { segmenter.close() }
                }

                AndroidView(
                    modifier = videoModifier,
                    factory = { ctx ->
                        FrameLayout(ctx).apply {
                            clipChildren = true
                            outlineProvider = ViewOutlineProvider.BOUNDS
                            clipToOutline = true

                            val sourceTexture = TextureView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                rotation = 90f
                            }
                            val outputView = ImageView(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                                scaleType = ImageView.ScaleType.FIT_XY
                                rotation = 90f
                            }

                            addView(sourceTexture)
                            addView(outputView)
                            exoPlayer.setVideoTextureView(sourceTexture)

                            sourceTexture.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                                override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {}
                                override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) {}
                                override fun onSurfaceTextureDestroyed(st: SurfaceTexture) = true
                                override fun onSurfaceTextureUpdated(st: SurfaceTexture) {
                                    val bitmap = sourceTexture.bitmap ?: return
                                    val inputImage = InputImage.fromBitmap(bitmap, 0)
                                    segmenter.process(inputImage)
                                        .addOnSuccessListener { result ->
                                            val mask = result.buffer
                                            val maskW = result.width
                                            val maskH = result.height
                                            val output = applySegmentationMask(bitmap, mask, maskW, maskH)
                                            outputView.post { outputView.setImageBitmap(output) }
                                        }
                                }
                            }

                            setupScaleListener(this, outputView, exoPlayer)
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 5. Hiển thị text gross ngay bên dưới video
        if (currentGrossText.isNotEmpty()) {
            Text(
                text = currentGrossText,
                modifier = Modifier
                    .rotate(90f)
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}
// Tách helper để tái sử dụng scale logic
private fun setupScaleListener(container: FrameLayout, targetView: View, exoPlayer: ExoPlayer) {
    var containerW = 0
    var containerH = 0
    var videoW = 0
    var videoH = 0

    fun updateScale() {
        if (containerW <= 0 || containerH <= 0 || videoW <= 0 || videoH <= 0) return
        val fillScale = maxOf(containerW.toFloat() / videoH, containerH.toFloat() / videoW)
        targetView.scaleX = videoW * fillScale / containerW
        targetView.scaleY = videoH * fillScale / containerH
    }

    container.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
        containerW = right - left
        containerH = bottom - top
        updateScale()
    }

    exoPlayer.addListener(object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            videoW = videoSize.width
            videoH = videoSize.height
            updateScale()
        }
    })
}

// Helper: apply mask bitmap
private fun applySegmentationMask(
    original: Bitmap, mask: ByteBuffer, maskW: Int, maskH: Int
): Bitmap {
    val result = original.copy(Bitmap.Config.ARGB_8888, true)
    mask.rewind()
    for (y in 0 until maskH) {
        for (x in 0 until maskW) {
            val confidence = mask.float  // 0 = nền, 1 = người
            if (confidence < 0.5f) {
                // Scale tọa độ mask → tọa độ bitmap gốc
                val bx = (x.toFloat() / maskW * original.width).toInt()
                val by = (y.toFloat() / maskH * original.height).toInt()
                result.setPixel(bx, by, Color.TRANSPARENT)
            }
        }
    }
    return result
}

fun resolveVideoUrl(url: String): String {
    return if (url.contains("player.mux.com/")) {
        val playbackId = url.substringAfterLast("/")
        "https://stream.mux.com/$playbackId.m3u8"
    } else {
        url // MP4 (qipedc...), HLS trực tiếp, v.v.
    }
}

@Composable
fun CameraPreviewView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                // ĐÂY LÀ DÒNG CODE QUAN TRỌNG NHẤT ĐỂ SỬA LỖI MÀN HÌNH ĐEN!
                // Ép CameraX dùng TextureView thay vì SurfaceView để không bị đụng độ với Video
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Camera binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )
}

private fun applyChromaKey(original: Bitmap, chromaRGB: FloatArray, threshold: Float): Bitmap {
    val result = original.copy(Bitmap.Config.ARGB_8888, true)
    val pixels = IntArray(result.width * result.height)
    result.getPixels(pixels, 0, result.width, 0, 0, result.width, result.height)

    val cr = if (chromaRGB[0] > 1f) chromaRGB[0] / 255f else chromaRGB[0]
    val cg = if (chromaRGB[1] > 1f) chromaRGB[1] / 255f else chromaRGB[1]
    val cb = if (chromaRGB[2] > 1f) chromaRGB[2] / 255f else chromaRGB[2]

    // 🔍 Log diff của center pixel để biết cần threshold bao nhiêu
    val centerIdx = (result.height / 2) * result.width + (result.width / 2)
    if (pixels.isNotEmpty()) {
        val cp = pixels[centerIdx]
        val dr = Color.red(cp) / 255f - cr
        val dg = Color.green(cp) / 255f - cg
        val db = Color.blue(cp) / 255f - cb
        val diff = kotlin.math.sqrt(dr*dr + dg*dg + db*db)
        Log.d("ChromaKey", "Center diff=$diff (threshold=$threshold) → sẽ ${if (diff < threshold) "XÓA" else "GIỮ"}")
    }

    var removedCount = 0
    for (i in pixels.indices) {
        val pixel = pixels[i]
        val r = Color.red(pixel) / 255f
        val g = Color.green(pixel) / 255f
        val b = Color.blue(pixel) / 255f
        val diff = kotlin.math.sqrt((r-cr)*(r-cr) + (g-cg)*(g-cg) + (b-cb)*(b-cb))
        if (diff < threshold) {
            pixels[i] = Color.TRANSPARENT
            removedCount++
        }
    }
    Log.d("ChromaKey", "Removed $removedCount / ${pixels.size} pixels")

    result.setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
    return result
}