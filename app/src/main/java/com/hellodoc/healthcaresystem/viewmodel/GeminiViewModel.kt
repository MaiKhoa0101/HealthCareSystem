package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.Content
import com.hellodoc.healthcaresystem.requestmodel.GeminiRequest
import com.hellodoc.healthcaresystem.requestmodel.Part
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.responsemodel.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GeminiViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _question = MutableStateFlow("")
    val question: StateFlow<String> get() = _question

    private val _answer = MutableStateFlow("")
    val answer: StateFlow<String> get() = _answer

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> get() = _chatMessages

    private val apiKey = "AIzaSyCmmkTVG3budXG5bW9R3Yr3Vsi15U8KcR0"

    fun askGemini(query: String) {
        _question.value = query
        _answer.value = "Đang xử lý..."

        // Thêm câu hỏi của user vào chat
        _chatMessages.update { it + ChatMessage(query, isUser = true) }

        val customPrompt = """
            Bạn là một trợ lý y tế AI. Bạn chỉ được phép trả lời những câu hỏi liên quan đến y tế, sức khỏe, bệnh lý, thuốc, hoặc tư vấn y khoa. 
            Nếu câu hỏi không liên quan đến y tế, hãy trả lời: "Xin lỗi, tôi chỉ hỗ trợ các câu hỏi liên quan đến y tế."
            Đây là câu hỏi của người dùng: $query
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = customPrompt)))
            )
        )

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.geminiService.askGemini(apiKey, request)
                val reply = if (response.isSuccessful) {
                    response.body()?.candidates?.firstOrNull()
                        ?.content?.parts?.firstOrNull()?.text ?: "Không có câu trả lời"
                } else {
                    "Lỗi: ${response.errorBody()?.string()}"
                }

                _answer.value = reply
                _chatMessages.update { it + ChatMessage(reply, isUser = false) }

            } catch (e: Exception) {
                val errorMsg = "Lỗi kết nối: ${e.localizedMessage}"
                _answer.value = errorMsg
                _chatMessages.update { it + ChatMessage(errorMsg, isUser = false) }
            }
        }
    }
}
