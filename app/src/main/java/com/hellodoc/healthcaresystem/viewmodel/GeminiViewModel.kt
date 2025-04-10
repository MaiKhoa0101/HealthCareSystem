package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.Content
import com.hellodoc.healthcaresystem.requestmodel.GeminiRequest
import com.hellodoc.healthcaresystem.requestmodel.Part
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeminiViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _question = MutableStateFlow("")
    val question: StateFlow<String> get() = _question

    private val _answer = MutableStateFlow("")
    val answer: StateFlow<String> get() = _answer

    private val apiKey = "AIzaSyCmmkTVG3budXG5bW9R3Yr3Vsi15U8KcR0"

    fun askGemini(query: String) {
        _question.value = query

        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = query)))
            )
        )

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.geminiService.askGemini(apiKey, request)
                if (response.isSuccessful) {
                    val reply = response.body()?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()?.text
                    _answer.value = reply ?: "Không có câu trả lời"
                } else {
                    _answer.value = "Lỗi: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _answer.value = "Lỗi kết nối: ${e.localizedMessage}"
            }
        }
    }
}
