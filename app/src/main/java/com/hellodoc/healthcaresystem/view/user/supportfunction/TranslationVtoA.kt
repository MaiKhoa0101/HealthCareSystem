package com.hellodoc.healthcaresystem.view.user.supportfunction

import android.util.Log
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TranslationManager {

    // Cache translator để không tạo lại mỗi lần
    private var viToEnTranslator: Translator? = null
    private var enToViTranslator: Translator? = null
    private var isViToEnReady = false
    private var isEnToViReady = false

    // ================================================================
    // Khởi tạo + tải model (gọi 1 lần lúc app start hoặc trước khi dùng)
    // ================================================================
    suspend fun initialize() {
        viToEnTranslator = buildTranslator(TranslateLanguage.VIETNAMESE, TranslateLanguage.ENGLISH)
        enToViTranslator = buildTranslator(TranslateLanguage.ENGLISH, TranslateLanguage.VIETNAMESE)

        // Tải cả 2 model song song
        isViToEnReady = downloadModel(viToEnTranslator!!, "VI→EN")
        isEnToViReady = downloadModel(enToViTranslator!!, "EN→VI")
    }

    private fun buildTranslator(source: String, target: String): Translator {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(source)
            .setTargetLanguage(target)
            .build()
        return Translation.getClient(options)
    }

    private suspend fun downloadModel(translator: Translator, tag: String): Boolean =
        suspendCancellableCoroutine { cont ->
            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    Log.d("Translation", "✅ Model $tag downloaded")
                    cont.resume(true)
                }
                .addOnFailureListener { e ->
                    Log.e("Translation", "❌ Model $tag download failed: ${e.message}")
                    cont.resume(false) // Không crash — trả false
                }
        }

    // ================================================================
    // Dịch VI → EN
    // ================================================================
    suspend fun translateViToEn(text: String): String? {
        if (!isViToEnReady) {
            Log.w("Translation", "⚠️ VI→EN model not ready")
            return null
        }
        return translate(viToEnTranslator!!, text, "VI→EN")
    }

    // ================================================================
    // Dịch EN → VI
    // ================================================================
    suspend fun translateEnToVi(text: String): String? {
        if (!isEnToViReady) {
            Log.w("Translation", "⚠️ EN→VI model not ready")
            return null
        }
        return translate(enToViTranslator!!, text, "EN→VI")
    }

    private suspend fun translate(translator: Translator, text: String, tag: String): String? =
        suspendCancellableCoroutine { cont ->
            translator.translate(text)
                .addOnSuccessListener { result ->
                    Log.d("Translation", "[$tag] \"$text\" → \"$result\"")
                    cont.resume(result)
                }
                .addOnFailureListener { e ->
                    Log.e("Translation", "[$tag] Failed: ${e.message}")
                    cont.resumeWithException(e)
                }
        }

    // ================================================================
    // Tự động nhận diện chiều dịch (không cần biết ngôn ngữ đầu vào)
    // ================================================================
    suspend fun autoTranslate(text: String, targetLanguage: TargetLanguage): String? {
        return when (targetLanguage) {
            TargetLanguage.ENGLISH    -> translateViToEn(text)
            TargetLanguage.VIETNAMESE -> translateEnToVi(text)
        }
    }

    fun release() {
        viToEnTranslator?.close()
        enToViTranslator?.close()
        viToEnTranslator = null
        enToViTranslator = null
        Log.d("Translation", "🗑️ Translators released")
    }

    enum class TargetLanguage { ENGLISH, VIETNAMESE }
}