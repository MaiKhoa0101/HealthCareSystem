package com.hellodoc.healthcaresystem.presentation.view.user.home.fasttalk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast


fun startSpeechToTextRealtime(
    context: Context,
    speechRecognizer: SpeechRecognizer,
    onPartial: (String) -> Unit,
    onFinal: (String) -> Unit,
    onEnd: () -> Unit
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }

    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {
            onEnd()
        }

        override fun onError(error: Int) {
            onEnd()
        }

        override fun onResults(results: Bundle?) {
            val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!data.isNullOrEmpty()) onFinal(data[0])
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val partialData = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!partialData.isNullOrEmpty()) onPartial(partialData[0])
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    speechRecognizer.startListening(intent)
}
