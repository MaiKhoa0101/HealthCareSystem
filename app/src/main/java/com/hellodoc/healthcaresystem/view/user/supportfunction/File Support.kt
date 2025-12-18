package com.hellodoc.healthcaresystem.view.user.supportfunction

import android.content.Context
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.SoundPool
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.view.user.home.fasttalk.speakText
import com.hellodoc.healthcaresystem.view.user.post.ZoomableImage
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import kotlin.coroutines.resume

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

object SoundManager {
    private var pool: SoundPool? = null
    private var tap = 0
    private var swipe = 0
    private var hold = 0
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return

        pool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .build()

        // Nếu bạn có file âm thanh, uncomment và thay R.raw.xxx
        // tap = pool?.load(context, R.raw.tap, 1) ?: 0
        // swipe = pool?.load(context, R.raw.swipe, 1) ?: 0
        // hold = pool?.load(context, R.raw.hold, 1) ?: 0

        initialized = true
    }

    fun playTap() {
        pool?.play(tap, 1f, 1f, 1, 0, 1f)
    }

    fun playSwipe() {
        pool?.play(swipe, 1f, 1f, 1, 0, 1f)
    }

    fun playHold() {
        pool?.play(hold, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        pool?.release()
        pool = null
        initialized = false
    }
}

fun vibrate(context: Context, ms: Long = 100) {
    val vib = context.getSystemService(Vibrator::class.java)
    vib?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
}
object FocusTTS {
    private var tts: TextToSpeech? = null
    private var ready = false
    private val initLock = Any()
    private val continuations = mutableMapOf<String, CancellableContinuation<Unit>>()

    fun init(context: Context) {
        synchronized(initLock) {
            if (tts != null) return
            tts = TextToSpeech(context) { status ->
                ready = (status == TextToSpeech.SUCCESS)
                if (ready) {
                    tts?.language = Locale("vi", "VN")

                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            // Không cần xử lý gì
                        }

                        override fun onDone(utteranceId: String?) {
                            utteranceId?.let { id ->
                                continuations.remove(id)?.resume(Unit)
                            }
                        }

                        override fun onError(utteranceId: String?) {
                            utteranceId?.let { id ->
                                continuations.remove(id)?.resume(Unit)
                            }
                        }
                    })
                }
            }
        }
    }

    suspend fun speakAndWait(text: String) = suspendCancellableCoroutine { continuation ->
        if (!ready || tts == null) {
            continuation.resume(Unit)
            return@suspendCancellableCoroutine
        }

        val utteranceId = System.currentTimeMillis().toString()
        continuations[utteranceId] = continuation

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)

        continuation.invokeOnCancellation {
            continuations.remove(utteranceId)
            tts?.stop()
        }
    }

    fun speak(text: String) {
        if (!ready || tts == null) return
        tts?.stop()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, System.currentTimeMillis().toString())
    }

    fun isSpeaking(): Boolean = tts?.isSpeaking == true

    fun stop() {
        tts?.stop()
        continuations.values.forEach { it.cancel() }
        continuations.clear()
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        ready = false
    }
}

suspend fun speakQueue(vararg texts: String) {
    println("speakQueue: $texts")
    for (text in texts) {
        FocusTTS.speakAndWait(text)
        delay(400) // Khoảng nghỉ giữa các câu
    }
}

