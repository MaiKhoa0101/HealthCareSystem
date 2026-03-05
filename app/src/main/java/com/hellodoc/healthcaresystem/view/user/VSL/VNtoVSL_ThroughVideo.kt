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
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.view.TextureView
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
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

    var displayText by remember { mutableStateOf("") }       // text đã hoàn chỉnh
    var partialText by remember { mutableStateOf("") }       // text đang nói dở
    var isListening by remember { mutableStateOf(false) }    // đang nghe hay không

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
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
    }

    // Tạo recognizer với config silence detection
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Tắt silence detection mặc định, tự xử lý bằng RMS
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 10_000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 300L)
        }
    }

    // Hàm khởi động lắng nghe
    fun startListening() {
        if (!hasAudioPermission) return
        isListening = true
        displayText = ""
        speechRecognizer.startListening(recognizerIntent)
    }

    // Gắn listener cho recognizer
    DisposableEffect(hasAudioPermission) {
        if (!hasAudioPermission) return@DisposableEffect onDispose {}

        // --- Tham số điều chỉnh độ nhạy ---
        val SPEECH_RMS_THRESHOLD = 4f   // RMS > ngưỡng này = đang nói
        val SILENCE_DURATION_MS = 500L // Im đủ lâu này (ms) → kết thúc câu

        var speechDetected = false          // Đã từng nghe thấy giọng nói chưa
        var belowThresholdSince = -1L       // Thời điểm RMS bắt đầu xuống thấp

        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onRmsChanged(rmsdB: Float) {
                val now = System.currentTimeMillis()

                if (rmsdB >= SPEECH_RMS_THRESHOLD) {
                    // 🎙️ Đang nghe thấy giọng nói
                    speechDetected = true
                    belowThresholdSince = -1L  // Reset timer im lặng

                } else {
                    // 🔇 RMS thấp (có thể ồn nhưng không có giọng nói rõ)
                    if (speechDetected) {
                        // Chỉ bắt đầu đếm sau khi đã từng nghe thấy giọng
                        if (belowThresholdSince == -1L) {
                            belowThresholdSince = now  // Bắt đầu đếm giờ
                        } else if (now - belowThresholdSince >= SILENCE_DURATION_MS) {
                            // ✅ Ngừng nói đủ lâu → force stop, onResults sẽ được gọi
                            Log.d("SpeechDetect", "Phát hiện ngừng nói (RMS=$rmsdB), dừng lắng nghe")
                            speechDetected = false
                            belowThresholdSince = -1L
                            speechRecognizer.stopListening()
                        }
                    }
                }
            }

            override fun onBeginningOfSpeech() {
                isListening = true
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: return
                partialText = partial
            }

            override fun onResults(results: Bundle?) {
                val result = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: ""

                // Reset state
                speechDetected = false
                belowThresholdSince = -1L

                if (result.isNotBlank()) {
                    displayText = result
                    partialText = ""
                    vslViewModel.fetchVSL(Subtitle(text = result))
                }

                startListening()
            }

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                speechDetected = false
                belowThresholdSince = -1L
                partialText = ""

                val shouldRestart = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
                    SpeechRecognizer.ERROR_CLIENT -> true
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> false
                    else -> true
                }
                if (shouldRestart) startListening()
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        onDispose { speechRecognizer.destroy() }
    }

    // 🚀 Tự động bắt đầu lắng nghe khi có permission
    LaunchedEffect(hasAudioPermission) {
        if (hasAudioPermission) {
            startListening()
        }
    }

    // Xin permission khi vào màn hình
    LaunchedEffect(Unit) {
        if (!hasCameraPermission || !hasAudioPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Camera Preview
        if (hasCameraPermission) {
            CameraPreviewView(modifier = Modifier.fillMaxSize())
        }

        // 2. Video Player
        Box(
            modifier = Modifier.padding(20.dp)
                .fillMaxSize(0.4f)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center
        ) {
            SequentialVideoPlayer(
                vslList = vslResponse,
                trimStartMs = 500L,
                trimEndFromLastMs = 1_000L,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .alpha(if (vslResponse.isNotEmpty()) 1f else 0f),

            )
        }

        // 3. UI text + trạng thái
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
        ) {
            AutoInputConversation(
                // Bỏ onMicToggle thủ công, giờ là auto
                onMicToggle = {},
                onDelete = {
                    displayText = ""
                    partialText = ""
                },
                inputText = if (partialText.isNotEmpty()) "$displayText $partialText".trim() else displayText,
                isRecording = isListening
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
                val builder = MediaItem.Builder().setUri(Uri.parse(url))
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

    when (backgroundRemoval) {

        // ── Không xóa nền ──────────────────────────────────────────
        is BackgroundRemovalMode.None -> {
            AndroidView(
                modifier = modifier.aspectRatio(16f / 9f),
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

        // ── Chroma Key (màu đơn sắc) ───────────────────────────────
        is BackgroundRemovalMode.ChromaKey -> {
            val mode = backgroundRemoval
            AndroidView(
                modifier = modifier.aspectRatio(16f / 9f),
                factory = { ctx ->
                    FrameLayout(ctx).apply {
                        clipChildren = true
                        outlineProvider = ViewOutlineProvider.BOUNDS
                        clipToOutline = true
                        val glView = ChromaKeyGLSurfaceView(
                            ctx,
                            exoPlayer,
                            mode.colorRGB,
                            mode.threshold
                        ).apply {
                            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            rotation = 90f
                        }
                        addView(glView)
                        setupScaleListener(this, glView, exoPlayer)
                    }
                }
            )
        }

        // ── ML Kit Segmentation (nền phức tạp) ────────────────────
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
                modifier = modifier.aspectRatio(16f / 9f),
                factory = { ctx ->
                    FrameLayout(ctx).apply {
                        clipChildren = true
                        outlineProvider = ViewOutlineProvider.BOUNDS
                        clipToOutline = true

                        // TextureView nhận frame gốc (ẩn)
                        val sourceTexture = TextureView(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            alpha = 0f // ẩn, chỉ dùng để lấy frame
                        }
                        // ImageView hiển thị kết quả sau khi xóa nền
                        val outputView = ImageView(ctx).apply {
                            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                            scaleType = ImageView.ScaleType.FIT_XY
                            rotation = 90f
                        }

                        addView(sourceTexture)
                        addView(outputView)
                        exoPlayer.setVideoTextureView(sourceTexture)

                        // Mỗi frame → chạy segmentation → update outputView
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