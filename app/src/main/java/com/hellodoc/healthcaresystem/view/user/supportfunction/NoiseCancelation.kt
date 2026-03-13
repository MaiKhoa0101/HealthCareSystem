package com.hellodoc.healthcaresystem.view.user.supportfunction

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import android.util.Log
import androidx.core.content.ContextCompat

object AudioEffectsInfo {
    fun check(): String = buildString {
        appendLine("NoiseSuppressor:       ${NoiseSuppressor.isAvailable()}")
        appendLine("AcousticEchoCanceler:  ${AcousticEchoCanceler.isAvailable()}")
        appendLine("AutomaticGainControl:  ${AutomaticGainControl.isAvailable()}")
    }
}

class NoiseSuppressedAudioRecord(private val context: Context) {

    private val sampleRate = 16000
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ) * 2

    private var audioRecord:      AudioRecord?           = null
    private var noiseSuppressor:  NoiseSuppressor?       = null
    private var echoCanceler:     AcousticEchoCanceler?  = null
    private var gainControl:      AutomaticGainControl?  = null

    // Trả về true nếu khởi tạo thành công
    fun initialize(): Boolean {
        // ✅ Kiểm tra permission trước — không dùng @RequiresPermission
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            Log.w("AudioFX", "⚠️ RECORD_AUDIO permission not granted — skipping init")
            return false
        }

        return try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e("AudioFX", "❌ AudioRecord failed to initialize")
                audioRecord?.release()
                audioRecord = null
                return false
            }

            enableEffects()
            Log.d("AudioFX", "✅ NoiseSuppressedAudioRecord initialized")
            true

        } catch (e: SecurityException) {
            // Có thể xảy ra nếu permission bị revoke sau khi check
            Log.e("AudioFX", "❌ SecurityException during AudioRecord init: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("AudioFX", "❌ Unexpected error: ${e.message}")
            false
        }
    }

    private fun enableEffects() {
        val sessionId = audioRecord?.audioSessionId ?: return

        if (NoiseSuppressor.isAvailable()) {
            noiseSuppressor = NoiseSuppressor.create(sessionId)?.also {
                it.enabled = true
                Log.d("AudioFX", "✅ NoiseSuppressor enabled")
            }
        }

        if (AcousticEchoCanceler.isAvailable()) {
            echoCanceler = AcousticEchoCanceler.create(sessionId)?.also {
                it.enabled = true
                Log.d("AudioFX", "✅ AcousticEchoCanceler enabled")
            }
        }

        if (AutomaticGainControl.isAvailable()) {
            gainControl = AutomaticGainControl.create(sessionId)?.also {
                it.enabled = true
                Log.d("AudioFX", "✅ AutomaticGainControl enabled")
            }
        }
    }

    fun release() {
        noiseSuppressor?.release().also { noiseSuppressor = null }
        echoCanceler?.release().also    { echoCanceler    = null }
        gainControl?.release().also     { gainControl     = null }
        audioRecord?.release().also     { audioRecord     = null }
        Log.d("AudioFX", "🗑️ AudioFX released")
    }
}