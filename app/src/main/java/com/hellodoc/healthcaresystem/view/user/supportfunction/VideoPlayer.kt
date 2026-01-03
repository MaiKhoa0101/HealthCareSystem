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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
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

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    postViewModel: PostViewModel = hiltViewModel()
) {
    val subtitleUri by postViewModel.subtitle.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ===== SCENEVIEW 3D RESOURCES =====
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)

    // State management cho 3D resources
    var ericModelInstance by remember { mutableStateOf<ModelInstance?>(null) }
    var globalEnvironment by remember { mutableStateOf<Environment?>(null) }
    var is3DExpanded by remember { mutableStateOf(false) }
    var is3DResourcesReady by remember { mutableStateOf(false) }

    // ===== EXOPLAYER SETUP =====
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = false
        }
    }

    // ===== LOAD 3D ASSETS (ONE TIME) =====
    LaunchedEffect(Unit) {
        println("🎬 VideoPlayer: Initializing for URL: $videoUrl")

        // Fetch subtitle
        try {
            postViewModel.getSubtitle(videoUrl)
        } catch (e: Exception) {
            println("⚠️ Error fetching subtitle: ${e.message}")
        }

        // Load 3D Model
        try {
            context.assets.open("BoneEric.glb").use { inputStream ->
                val bytes = inputStream.readBytes()
                val buffer = ByteBuffer.wrap(bytes)
                ericModelInstance = modelLoader.createModelInstance(buffer)
                println("✅ 3D Model loaded successfully")
            }
        } catch (e: Exception) {
            println("❌ Error loading 3D model: ${e.message}")
            e.printStackTrace()
        }

        // Load Environment (HDR)
        try {
            globalEnvironment = environmentLoader.createHDREnvironment(
                assetFileLocation = "environment.hdr"
            )
            println("✅ 3D Environment loaded successfully")
        } catch (e: Exception) {
            println("❌ Error loading environment: ${e.message}")
            e.printStackTrace()
        }

        // Mark 3D resources as ready
        is3DResourcesReady = ericModelInstance != null && globalEnvironment != null
        if (is3DResourcesReady) {
            println("✅ All 3D resources ready")
        }
    }

    // ===== SETUP VIDEO WITH SUBTITLE =====
    LaunchedEffect(videoUrl, subtitleUri) {
        try {
            val mediaItemBuilder = MediaItem.Builder().setUri(videoUrl)

            // Add subtitle if available
            subtitleUri?.let { uri ->
                println("📝 Adding subtitle: ${uri.subtitleUrl}")
                val subtitle = MediaItem.SubtitleConfiguration.Builder(
                    Uri.parse(uri.subtitleUrl)
                )
                    .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                    .setLanguage("vi")
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                    .build()

                mediaItemBuilder.setSubtitleConfigurations(listOf(subtitle))
            }

            // Set media and prepare
            exoPlayer.setMediaItem(mediaItemBuilder.build())
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            println("✅ Video prepared and ready to play")

        } catch (e: Exception) {
            println("❌ Error setting up video: ${e.message}")
            e.printStackTrace()
        }
    }

    // ===== CLEANUP ON DISPOSE =====
    DisposableEffect(videoUrl) {
        onDispose {
            println("🧹 VideoPlayer: Starting cleanup...")

            coroutineScope.launch {
                try {
                    // Step 1: Stop video playback
                    println("  → Stopping ExoPlayer...")
                    exoPlayer.playWhenReady = false
                    exoPlayer.stop()

                    // Small delay to ensure proper cleanup
                    delay(50)

                    // Step 2: Clear media items
                    println("  → Clearing media items...")
                    exoPlayer.clearMediaItems()

                    // Step 3: Release ExoPlayer
                    println("  → Releasing ExoPlayer...")
                    exoPlayer.release()
                    println("  ✅ ExoPlayer cleaned up")

                } catch (e: Exception) {
                    println("  ⚠️ Error during ExoPlayer cleanup: ${e.message}")
                    e.printStackTrace()
                }

                try {
                    // Step 4: Clear 3D resources references
                    // Note: SceneView's rememberEngine() handles actual cleanup
                    println("  → Clearing 3D resource references...")
                    ericModelInstance = null
                    globalEnvironment = null
                    is3DResourcesReady = false
                    is3DExpanded = false
                    println("  ✅ 3D resources cleared")

                } catch (e: Exception) {
                    println("  ⚠️ Error during 3D cleanup: ${e.message}")
                    e.printStackTrace()
                }

                println("✅ VideoPlayer cleanup completed successfully")
            }
        }
    }

    // ===== UI LAYOUT =====
    Box(modifier = Modifier.fillMaxSize()) {

        // VIDEO PLAYER VIEW
        AndroidView(
            factory = { ctx ->
                println("📺 Creating PlayerView...")
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setShowSubtitleButton(true)
                    setShowNextButton(false)
                    setShowPreviousButton(false)

                    // Configure subtitle styling
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
                // Ensure player is properly attached
                if (playerView.player != exoPlayer) {
                    playerView.player = exoPlayer
                }
            },
            onRelease = { playerView ->
                try {
                    println("📺 Releasing PlayerView...")
                    playerView.player = null
                } catch (e: Exception) {
                    println("⚠️ Error releasing PlayerView: ${e.message}")
                }
            },
            modifier = modifier
        )

        // 3D FLOATING ASSISTANT (only show when resources are ready)
        if (is3DResourcesReady && ericModelInstance != null && globalEnvironment != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Floating3DAssistant(
                    isExpanded = is3DExpanded,
                    onExpandChange = { newValue ->
                        is3DExpanded = newValue
                        println("🤖 3D Assistant expanded: $newValue")
                    },
                    engine = engine,
                    modelInstance = ericModelInstance,
                    environment = globalEnvironment
                )
            }
        }
    }
}