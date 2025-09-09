package com.hellodoc.healthcaresystem.user.supportfunction

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
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
