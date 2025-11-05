package com.hellodoc.healthcaresystem.view.user.home.fasttalk

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.Locale

fun speakText(context: Context, text: String) {
    var tts: TextToSpeech? = null

    tts = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("vi", "VN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(context, "Không hỗ trợ ngôn ngữ này", Toast.LENGTH_SHORT).show()
            } else {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        } else {
            Toast.makeText(context, "Không thể khởi tạo TTS", Toast.LENGTH_SHORT).show()
        }
    }
}
