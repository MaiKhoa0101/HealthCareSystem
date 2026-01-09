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
import com.hellodoc.healthcaresystem.view.model_human.Floating3DAssistant
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import io.github.sceneview.model.ModelInstance

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
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

    // ===== STATE 3D (Per-video ModelInstance) =====
    // Lắng nghe trạng thái Engine toàn cục
    val isGlobal3DReady by SceneViewManager.initializationState.collectAsState()

    // State riêng của màn hình này để điều khiển việc ẩn hiện Assistant
    var is3DExpanded by remember { mutableStateOf(false) }
    
    // ✅ DEFERRED: Create ModelInstance chỉ khi người dùng mở 3D
    // Tránh tạo hàng loạt instance trong list gây tốn memory và crash
    var videoModelInstance by remember { mutableStateOf<ModelInstance?>(null) }
    
    // ===== CRITICAL: Ensure SceneViewManager is initialized =====
    // Fallback nếu MyApp.onCreate() không chạy hoặc fail
    LaunchedEffect(Unit) {
        if (!isGlobal3DReady && !SceneViewManager.isResourcesValid()) {
            android.util.Log.w("VideoPlayer", "⚠️ SceneViewManager chưa khởi tạo! Triggering fallback...")
            try {
                SceneViewManager.initialize(context)
            } catch (e: Exception) {
                android.util.Log.e("VideoPlayer", "❌ Fallback init failed", e)
            }
        }
    }
    
    // Create ModelInstance when 3D is ready AND expanded
    LaunchedEffect(isGlobal3DReady, is3DExpanded) {
        if (isGlobal3DReady && is3DExpanded && videoModelInstance == null) {
            android.util.Log.d("VideoPlayer", "🚀 User requested 3D - Creating ModelInstance for video: $videoUrl")
            videoModelInstance = SceneViewManager.createModelInstance()
        }
    }

    // ✅ FIX: Chỉ tạo 1 ExoPlayer duy nhất và reuse.
    // Dùng remember(Unit) thay vì remember(videoUrl) để tránh leak khi chuyển video.
    val exoPlayer = remember {
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

        // CLEANUP: Destroy ModelInstance riêng của video này
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            isPlayerReleased = true
            
            // Destroy ModelInstance trước khi release Player
            videoModelInstance?.let { _ ->
                try {
                    // Note: ModelInstance doesn't have a direct destroy() method.
                    // Cleanup is handled by the ModelNode and Engine's flush strategy.
                    android.util.Log.d("VideoPlayer", "🧹 Releasing ModelInstance reference for video")
                } catch (e: Exception) {
                    android.util.Log.e("VideoPlayer", "❌ Error releasing ModelInstance", e)
                }
            }
            videoModelInstance = null
            
            exoPlayer.release() // Release Player sau cùng
        }
    }

    // ===== LOAD SUBTITLE & VIDEO =====
    LaunchedEffect(videoUrl) {
        if (isPlayerReleased) return@LaunchedEffect
        try {
            // Reset player trước khi load video mới
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            
            // Tự động thu nhỏ 3D khi chuyển video
            is3DExpanded = false
            // Giải phóng ModelInstance cũ nếu có
            videoModelInstance = null

            // Lấy subtitle
            postViewModel.getSubtitle(videoUrl)

            val mediaItemBuilder = MediaItem.Builder().setUri(videoUrl)
            
            // Xử lý subtitle nếu có 
            // Note: Chúng ta dùng subtitleUri từ collectAsState, nó sẽ trigger LaunchedEffect này lần nữa khi có data
            subtitleUri?.let { uri ->
                if (uri.subtitleUrl.isNotBlank()) {
                    val subtitle = MediaItem.SubtitleConfiguration.Builder(Uri.parse(uri.subtitleUrl))
                        .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                        .setLanguage("vi")
                        .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                        .build()
                    mediaItemBuilder.setSubtitleConfigurations(listOf(subtitle))
                }
            }

            exoPlayer.setMediaItem(mediaItemBuilder.build())
            exoPlayer.prepare()
            if (autoPlay) exoPlayer.playWhenReady = true

        } catch (e: Exception) {
            android.util.Log.e("VideoPlayer", "Error loading video", e)
        }
    }
    
    // Lắng nghe riêng subtitleUri để cập nhật nếu nó load chậm hơn video
    LaunchedEffect(subtitleUri) {
        if (!isPlayerReleased && subtitleUri != null && exoPlayer.mediaItemCount > 0) {
            // Re-prepare with subtitle if needed logic can be added here
            // Nhưng tốt nhất là load cùng lúc trong LaunchedEffect(videoUrl) ở trên
        }
    }

    // ===== UI LAYOUT =====
    Box(modifier = modifier.fillMaxSize()) {

        // 1. LỚP VIDEO (Nằm dưới)
        AndroidView(
            modifier = Modifier.fillMaxSize(),
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
            // Chỉ hiển thị khi Engine đã sẵn sàng (Environment và Buffer đã load)
            val is3DReady by SceneViewManager.initializationState.collectAsState()

            if (is3DReady && SceneViewManager.isResourcesValid()) {
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
                        // ✅ FIXED: Dùng ModelInstance riêng của video này
                        engine = SceneViewManager.getEngine(),
                        modelInstance = videoModelInstance,  // Per-video instance
                        environment = SceneViewManager.getEnvironment(),
                        videoUrl = videoUrl
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