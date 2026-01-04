package com.hellodoc.healthcaresystem.view.user.supportfunction

import android.net.Uri
import android.util.TypedValue
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.hellodoc.healthcaresystem.view.model_human.Floating3DAssistant
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    enable3DAssistant: Boolean = true, // Tùy chọn bật tắt 3D trong video
    postViewModel: PostViewModel = hiltViewModel()
) {
    // Check URL rỗng để tránh NullPointerException
    if (videoUrl.isBlank()) return

    val subtitleUri by postViewModel.subtitle.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // ===== STATE VIDEO PLAYER =====
    var isPlayerReleased by remember { mutableStateOf(false) }

    // ===== STATE 3D (Lấy từ Singleton) =====
    // Lắng nghe trạng thái Engine toàn cục
    val isGlobal3DReady by SceneViewManager.initializationState.collectAsState()

    // State riêng của màn hình này để điều khiển việc ẩn hiện Assistant
    var is3DExpanded by remember { mutableStateOf(false) }

    // ===== EXOPLAYER SETUP =====
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
            addListener(object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.e("VideoPlayer", "ExoPlayer Error: ${error.message}")
                }
            })
        }
    }

    // ===== LIFECYCLE VIDEO =====
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (isPlayerReleased) return@LifecycleEventObserver
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    is3DExpanded = false // Thu nhỏ 3D khi pause
                    exoPlayer.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (autoPlay) exoPlayer.play()
                }
                Lifecycle.Event.ON_STOP -> {
                    is3DExpanded = false
                    exoPlayer.stop()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        // CLEANUP CHỈ DÀNH CHO VIDEO PLAYER, KHÔNG ĐỤNG VÀO 3D ENGINE
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            isPlayerReleased = true
            exoPlayer.release() // Chỉ release Player, KHÔNG release Engine
        }
    }

    // ===== LOAD SUBTITLE & VIDEO =====
    LaunchedEffect(videoUrl, subtitleUri) {
        if (isPlayerReleased) return@LaunchedEffect
        try {
            // Lấy subtitle nếu chưa có
            if (subtitleUri == null) {
                postViewModel.getSubtitle(videoUrl)
            }

            val mediaItemBuilder = MediaItem.Builder().setUri(videoUrl)
            subtitleUri?.let { uri ->
                val subtitle = MediaItem.SubtitleConfiguration.Builder(Uri.parse(uri.subtitleUrl))
                    .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                    .setLanguage("vi")
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                    .build()
                mediaItemBuilder.setSubtitleConfigurations(listOf(subtitle))
            }

            exoPlayer.setMediaItem(mediaItemBuilder.build())
            exoPlayer.prepare()
            if (autoPlay) exoPlayer.playWhenReady = true

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ===== UI LAYOUT =====
    Box(modifier = modifier.fillMaxSize()) {

        // 1. LỚP VIDEO (Nằm dưới)
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setShowSubtitleButton(true)
                    keepScreenOn = true
                    subtitleView?.apply {
                        setApplyEmbeddedStyles(false)
                        setPadding(16, 8, 16, 8)
                        setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                }
            },
            update = { pv ->
                if (pv.player != exoPlayer) pv.player = exoPlayer
            },
            onRelease = { pv -> pv.player = null }
        )

            //LỚP 2: Floating 3D Assistant (Nằm đè lên trên)
            // Chỉ hiển thị khi Engine đã sẵn sàng (is3DReady = true)
            val is3DReady by SceneViewManager.initializationState.collectAsState()
            var is3DExpanded by remember { mutableStateOf(false) }

            if (is3DReady) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(100f) // Đảm bảo luôn nằm trên cùng
                        .padding(bottom = 80.dp, end = 16.dp), // Chỉnh padding để không che BottomBar
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Floating3DAssistant(
                        isExpanded = is3DExpanded,
                        onExpandChange = { newValue -> is3DExpanded = newValue },
                        // Lấy dữ liệu an toàn từ Manager
                        engine = SceneViewManager.getEngine(),
                        modelInstance = SceneViewManager.getModelInstance(),
                        environment = SceneViewManager.getEnvironment()
                    )
                }
            } else {
                // Optional: Loading nhỏ ở góc nếu chưa load xong
                Box(
                    modifier = Modifier
                        .padding(bottom = 80.dp, end = 16.dp)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
    }
}