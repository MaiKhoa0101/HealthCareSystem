package com.hellodoc.healthcaresystem.view.user.supportfunction

import android.net.Uri
import android.util.TypedValue
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
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
import androidx.media3.ui.SubtitleView
import com.hellodoc.healthcaresystem.view.model_human.Floating3DAssistant
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import io.github.sceneview.environment.Environment
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
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
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine) // Khởi tạo Loader
// --- 2. BIẾN LƯU TRỮ TÀI NGUYÊN TOÀN CỤC ---
    var ericModelInstance by remember { mutableStateOf<ModelInstance?>(null) }
    var globalEnvironment by remember { mutableStateOf<Environment?>(null) }
    var is3DExpanded by remember { mutableStateOf(false) }

    // ✅ GỌI API 1 LẦN
    LaunchedEffect(Unit) {
        println("VideoPlayer: $videoUrl")
        postViewModel.getSubtitle(videoUrl)
        if (ericModelInstance == null) {
            try {
                val inputStream = context.assets.open("BoneEric.glb")
                val bytes = inputStream.readBytes()
                inputStream.close()
                val buffer = ByteBuffer.wrap(bytes)
                ericModelInstance = modelLoader.createModelInstance(buffer)
                println("Lay hinh thanh cong")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // B. Nạp Môi trường (HDR)
        if (globalEnvironment == null) {
            try {
                // Lưu ý: Đảm bảo file environment.hdr < 10MB để tránh OOM
                globalEnvironment = environmentLoader.createHDREnvironment(
                    assetFileLocation = "environment.hdr"
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ✅ TẠO PLAYER 1 LẦN
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    // ✅ UPDATE MEDIA KHI SUBTITLE CÓ
    LaunchedEffect(videoUrl, subtitleUri) {
        val mediaItemBuilder = MediaItem.Builder()
            .setUri(videoUrl)

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
        exoPlayer.playWhenReady = true
        exoPlayer.playbackLooper
    }

    // ✅ RELEASE ĐÚNG
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // ✅ PLAYER VIEW
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    setShowSubtitleButton(true)

                    subtitleView?.apply {
                        setApplyEmbeddedStyles(false)
                        setApplyEmbeddedFontSizes(false)
                        setPadding(16, 8, 16, 8)
                        setFixedTextSize(
                            TypedValue.COMPLEX_UNIT_SP,
                            14f
                        )
                    }
                }
            },
            modifier = modifier
        )
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Floating3DAssistant(
                isExpanded = is3DExpanded,
                onExpandChange = { is3DExpanded = it },
                engine = engine,
                // TRUYỀN DỮ LIỆU ĐÃ NẠP XUỐNG
                modelInstance = ericModelInstance,
                environment = globalEnvironment
            )
        }    }

}
