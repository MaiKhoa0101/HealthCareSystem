package com.hellodoc.healthcaresystem.view.user.home.detection

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.model.dataclass.requestmodel.Subtitle
import com.hellodoc.healthcaresystem.view.user.home.fasttalk.AutoInputConversation
import com.hellodoc.healthcaresystem.view.user.home.fasttalk.startSpeechToTextRealtime
import com.hellodoc.healthcaresystem.view.user.supportfunction.vibrate
import com.hellodoc.healthcaresystem.viewmodel.VSLViewModel

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.net.Uri
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.draw.alpha
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.VSL
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
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
    val lifecycleOwner = LocalLifecycleOwner.current

    val vslResponse by vslViewModel.vslResponse.collectAsState()

    var theirsSentence by remember { mutableStateOf("") }
    var tempTheirSpeech by remember { mutableStateOf("") }

    var isRecording by remember { mutableStateOf(false) }
    var isContinuousListening by remember { mutableStateOf(false) }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }

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

    LaunchedEffect(Unit) {
        if (!hasCameraPermission || !hasAudioPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy()
        }
    }

    fun triggerListening() {
        startSpeechToTextRealtime(
            context = context,
            speechRecognizer = speechRecognizer,
            onFinal = { result ->
                theirsSentence = ("$theirsSentence $result").trim()
                tempTheirSpeech = ""

                if (theirsSentence.isNotBlank()) {
                    vslViewModel.fetchVSL(Subtitle(text = theirsSentence))
                }

                if (isContinuousListening) {
                    triggerListening()
                } else {
                    isRecording = false
                }
            },
            onPartial = { result ->
                tempTheirSpeech = result
            },
            onEnd = {
                if (isContinuousListening) {
                    triggerListening()
                } else {
                    isRecording = false
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. LỚP NỀN: Camera Preview
        if (hasCameraPermission) {
            CameraPreviewView(
                modifier = Modifier.fillMaxSize()
            )
        }

        // 2. LỚP GIỮA: Trình phát Video nối tiếp
        // Đã xóa điều kiện if (vslResponse.isNotEmpty()) để view luôn tồn tại
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            SequentialVideoPlayer(
                vslList = vslResponse,
                trimStartMs = 500L,          // Bỏ 0.5 giây đầu
                trimEndFromLastMs = 1_000L,  // Bỏ 1 giây cuối
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(9f / 16f)
                    .alpha(if (vslResponse.isNotEmpty()) 1f else 0f)
            )
        }

        // 3. LỚP TRÊN CÙNG: UI Ghi âm và Text
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter)
        ) {
            AutoInputConversation(
                onMicToggle = {
                    vibrate(context)
                    if (!isContinuousListening) {
                        if (!hasAudioPermission) {
                            permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                        } else {
                            isContinuousListening = true
                            isRecording = true
                            triggerListening()
                        }
                    } else {
                        isContinuousListening = false
                        isRecording = false
                        speechRecognizer.stopListening()

                        val finalSentence = (theirsSentence + " " + tempTheirSpeech).trim()
                        if (finalSentence.isNotBlank()) {
                            vslViewModel.fetchVSL(Subtitle(text = finalSentence))
                            theirsSentence = finalSentence
                            tempTheirSpeech = ""
                        }
                    }
                },
                onDelete = {
                    theirsSentence = ""
                    tempTheirSpeech = ""
                },
                inputText = (theirsSentence + if (tempTheirSpeech.isNotEmpty()) " $tempTheirSpeech" else "").trim(),
                isRecording = isRecording
            )
        }
    }
}
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun SequentialVideoPlayer(
    vslList: List<VSL>,
    modifier: Modifier = Modifier,
    trimStartMs: Long = 0L,        // Bỏ X giây đầu
    trimEndFromLastMs: Long = 0L,  // Bỏ Y giây cuối
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
        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(dataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                playWhenReady = true
                addListener(object : androidx.media3.common.Player.Listener {

                    // ✂️ START TRIM: seek ngay khi chuyển video
                    override fun onMediaItemTransition(
                        mediaItem: androidx.media3.common.MediaItem?,
                        reason: Int
                    ) {
                        if (trimStartMs > 0) seekTo(trimStartMs)
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        Log.e("VideoPlayerError", "Lỗi: ${error.errorCodeName} | ${error.message}")
                    }
                })
            }
    }

    // ✂️ END TRIM: sau khi biết duration → tính endMs = duration - Y
    // Polling 100ms để check position
    LaunchedEffect(exoPlayer, trimEndFromLastMs) {
        while (true) {
            delay(100)
            if (exoPlayer.isPlaying) {
                val duration = exoPlayer.duration  // -1 nếu chưa biết
                if (duration > 0 && trimEndFromLastMs > 0) {
                    val endMs = duration - trimEndFromLastMs
                    // endMs phải lớn hơn trimStartMs mới hợp lệ
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
                Log.d("CheckVideoURL", "URL: $url")

                val builder = MediaItem.Builder().setUri(Uri.parse(url))
                when {
                    url.endsWith(".m3u8", ignoreCase = true) ||
                            url.contains("stream.mux.com") ->
                        builder.setMimeType(MimeTypes.APPLICATION_M3U8)
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

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        update = { playerView -> playerView.player = exoPlayer }
    )
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