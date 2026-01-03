package com.hellodoc.healthcaresystem.view.user.supportfunction

import android.net.Uri
import android.util.TypedValue
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
import io.github.sceneview.environment.Environment
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

/**
 * PRODUCTION-READY VIDEO PLAYER WITH SAFE 3D ASSISTANT
 *
 * ✅ Auto-close 3D assistant on video exit
 * ✅ Crash-proof cleanup sequence
 * ✅ Lifecycle-aware
 * ✅ Proper resource management
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    enable3DAssistant: Boolean = true,
    postViewModel: PostViewModel = hiltViewModel()
) {
    val subtitleUri by postViewModel.subtitle.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // ===== STATE MANAGEMENT =====
    var isPlayerActive by remember { mutableStateOf(false) }
    var isPlayerReleased by remember { mutableStateOf(false) }

    // 3D Assistant State
    var is3DExpanded by remember { mutableStateOf(false) }
    var is3DResourcesReady by remember { mutableStateOf(false) }
    var isCleaningUp by remember { mutableStateOf(false) }

    // ===== SCENEVIEW 3D RESOURCES (Independent lifecycle) =====
    val engine = if (enable3DAssistant) rememberEngine() else null
    val modelLoader = if (enable3DAssistant && engine != null) {
        rememberModelLoader(engine)
    } else null
    val environmentLoader = if (enable3DAssistant && engine != null) {
        rememberEnvironmentLoader(engine)
    } else null

    var ericModelInstance by remember { mutableStateOf<ModelInstance?>(null) }
    var globalEnvironment by remember { mutableStateOf<Environment?>(null) }

    // ===== EXOPLAYER SETUP =====
    val exoPlayer = remember(videoUrl) {
        isPlayerReleased = false
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
            addListener(object : Player.Listener {
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.e("VideoPlayer", "ExoPlayer Error: ${error.message}")
                }
            })
        }
    }

    // ===== LOAD 3D ASSETS (CONDITIONAL) =====
    LaunchedEffect(enable3DAssistant) {
        if (!enable3DAssistant) return@LaunchedEffect

        try {
            // Fetch subtitle
            postViewModel.getSubtitle(videoUrl)
        } catch (e: Exception) {
            android.util.Log.w("VideoPlayer", "Error fetching subtitle: ${e.message}")
        }

        if (modelLoader != null && environmentLoader != null) {
            try {
                // Load 3D Model
                context.assets.open("BoneEric.glb").use { inputStream ->
                    val bytes = inputStream.readBytes()
                    val buffer = ByteBuffer.wrap(bytes)
                    ericModelInstance = modelLoader.createModelInstance(buffer)
                }

                // Load Environment
                globalEnvironment = environmentLoader.createHDREnvironment(
                    assetFileLocation = "environment.hdr"
                )

                // Mark as ready
                is3DResourcesReady = ericModelInstance != null && globalEnvironment != null

            } catch (e: Exception) {
                android.util.Log.e("VideoPlayer", "Error loading 3D resources", e)
                is3DResourcesReady = false
                ericModelInstance = null
                globalEnvironment = null
            }
        }
    }

    // ===== LIFECYCLE-AWARE PLAYBACK =====
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (isPlayerReleased || isCleaningUp) return@LifecycleEventObserver

            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    try {
                        // IMPORTANT: Close 3D assistant first to prevent crash
                        if (is3DExpanded) {
                            android.util.Log.d("VideoPlayer", "Lifecycle PAUSE: Closing 3D assistant")
                            is3DExpanded = false
                        }
                        exoPlayer.playWhenReady = false
                    } catch (e: Exception) {
                        android.util.Log.w("VideoPlayer", "Error pausing", e)
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    try {
                        if (autoPlay && isPlayerActive && !isCleaningUp) {
                            exoPlayer.playWhenReady = true
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("VideoPlayer", "Error resuming", e)
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    try {
                        // CRITICAL: Close 3D assistant BEFORE stopping player
                        if (is3DExpanded) {
                            android.util.Log.d("VideoPlayer", "Lifecycle STOP: Force closing 3D assistant")
                            is3DExpanded = false
                        }
                        exoPlayer.stop()
                    } catch (e: Exception) {
                        android.util.Log.w("VideoPlayer", "Error stopping", e)
                    }
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // ===== SETUP VIDEO WITH SUBTITLE =====
    LaunchedEffect(videoUrl, subtitleUri) {
        if (isPlayerReleased || isCleaningUp) return@LaunchedEffect

        try {
            val mediaItemBuilder = MediaItem.Builder().setUri(videoUrl)

            subtitleUri?.let { uri ->
                val subtitle = MediaItem.SubtitleConfiguration.Builder(
                    Uri.parse(uri.subtitleUrl)
                )
                    .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                    .setLanguage("vi")
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                    .build()

                mediaItemBuilder.setSubtitleConfigurations(listOf(subtitle))
            }

            exoPlayer.setMediaItem(mediaItemBuilder.build())
            exoPlayer.prepare()

            if (autoPlay) {
                delay(100) // Small delay for stability
                exoPlayer.playWhenReady = true
            }

            isPlayerActive = true

        } catch (e: Exception) {
            android.util.Log.e("VideoPlayer", "Error setting up video", e)
        }
    }

    // ===== SAFE CLEANUP ON DISPOSE =====
    // ===== THAY THẾ ĐOẠN CLEANUP CŨ =====
    DisposableEffect(videoUrl) {
        onDispose {
            android.util.Log.d("VideoPlayer", "🧹 Starting SAFE cleanup sequence")
            isCleaningUp = true

            coroutineScope.launch {
                try {
                    // STEP 1: Force đóng 3D assistant NGAY LẬP TỨC
                    if (is3DExpanded) {
                        android.util.Log.d("VideoPlayer", "  → Force closing 3D assistant")
                        is3DExpanded = false
                        delay(200) // Chờ animation hoàn tất
                    }

                    // STEP 2: Vô hiệu hóa resources trước
                    android.util.Log.d("VideoPlayer", "  → Marking resources as unavailable")
                    is3DResourcesReady = false
                    isPlayerActive = false
                    delay(100)

                    // STEP 3: Xóa references 3D TRƯỚC (quan trọng!)
                    android.util.Log.d("VideoPlayer", "  → Clearing 3D references")
                    ericModelInstance = null
                    globalEnvironment = null
                    delay(100)

                    // STEP 4: Release ExoPlayer cuối cùng
                    android.util.Log.d("VideoPlayer", "  → Releasing ExoPlayer")
                    if (!isPlayerReleased) {
                        exoPlayer.stop()
                        delay(50)
                        exoPlayer.clearMediaItems()
                        delay(50)
                        exoPlayer.release()
                        isPlayerReleased = true
                    }

                    android.util.Log.d("VideoPlayer", "✅ Cleanup completed successfully")

                } catch (e: Exception) {
                    android.util.Log.e("VideoPlayer", "⚠️ Error during cleanup", e)
                } finally {
                    // Đảm bảo flag cleanup được reset
                    delay(100)
                    isCleaningUp = false
                }
            }
        }
    }

    // ===== UI LAYOUT =====
    Box(modifier = modifier.fillMaxSize()) {

        // VIDEO PLAYER VIEW
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setShowSubtitleButton(true)
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    keepScreenOn = true

                    subtitleView?.apply {
                        setApplyEmbeddedStyles(false)
                        setApplyEmbeddedFontSizes(false)
                        setPadding(16, 8, 16, 8)
                        setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    }
                }
            },
            update = { playerView ->
                if (playerView.player != exoPlayer && !isPlayerReleased) {
                    playerView.player = exoPlayer
                }
            },
            onRelease = { playerView ->
                try {
                    playerView.player = null
                } catch (e: Exception) {
                    android.util.Log.w("VideoPlayer", "Error releasing PlayerView", e)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // SAFE 3D FLOATING ASSISTANT
        if (enable3DAssistant && !isCleaningUp && is3DResourcesReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 40.dp, end = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Floating3DAssistant(
                    isExpanded = is3DExpanded,
                    onExpandChange = { newValue ->
                        // CHỈ CHO PHÉP THAY ĐỔI KHI KHÔNG CLEANUP
                        if (!isCleaningUp && is3DResourcesReady) {
                            is3DExpanded = newValue
                        } else {
                            android.util.Log.w("VideoPlayer", "Cannot change 3D state: Cleaning up or resources not ready")
                        }
                    },
                    engine = engine,
                    modelInstance = ericModelInstance,
                    environment = globalEnvironment
                )
            }
        }
    }
}

// ===== CONVENIENCE VARIANTS =====

@Composable
fun SimpleVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true
) {
    VideoPlayer(
        videoUrl = videoUrl,
        modifier = modifier,
        autoPlay = autoPlay,
        enable3DAssistant = false
    )
}

@Composable
fun FullVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true
) {
    VideoPlayer(
        videoUrl = videoUrl,
        modifier = modifier,
        autoPlay = autoPlay,
        enable3DAssistant = true
    )
}