package com.hellodoc.healthcaresystem.user.supportfunction

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.hellodoc.healthcaresystem.responsemodel.MediaType
import com.hellodoc.healthcaresystem.user.post.VideoPlayer
import com.hellodoc.healthcaresystem.user.post.ZoomableImage
import com.hellodoc.healthcaresystem.user.post.detectMediaType
import java.io.File
import java.io.FileOutputStream

/**
 * Hàm trích frame từ video (mặc định lấy 1 frame mỗi giây, tối đa maxFrames frame)
 */
fun extractFrames(context: Context, uri: Uri, maxFrames: Int = 5): List<File> {
    val retriever = MediaMetadataRetriever()
    val frameFiles = mutableListOf<File>()
    try {
        retriever.setDataSource(context, uri)

        val durationMs =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L

        val stepMs = (durationMs / maxFrames).coerceAtLeast(1000L) // ít nhất 1s / frame
        var timeUs = 0L
        var count = 0

        while (timeUs < durationMs * 1000 && count < maxFrames) {
            val bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            if (bitmap != null) {
                val file = File(context.cacheDir, "frame_${System.currentTimeMillis()}_${count}.jpg")
                FileOutputStream(file).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                }
                frameFiles.add(file)
                count++
            }
            timeUs += stepMs * 1000
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return frameFiles
}


fun getType(uri: Uri, context: Context): String {
    var type = context.contentResolver.getType(uri) ?: ""
    println(type)
    return type
}



fun extractOneFrame(context: Context, uri: Uri): Uri? {
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, uri)

        val durationMs =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L

        val timeUs = (durationMs / 2) * 1000 // lấy frame giữa video
        val bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)

        if (bitmap != null) {
            val file = File(context.cacheDir, "frame_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
            }
            // trả về Uri của file vừa lưu
            Uri.fromFile(file)
        } else {
            null
        }

    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        retriever.release()
    }
}

@Composable
fun AvatarDetailDialog(
    mediaUrls: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .zIndex(1f)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            ZoomableImage(
                url = mediaUrls,
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}