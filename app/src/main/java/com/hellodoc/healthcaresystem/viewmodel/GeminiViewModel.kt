package com.hellodoc.healthcaresystem.viewmodel
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.Content
import com.hellodoc.healthcaresystem.requestmodel.GeminiRequest
import com.hellodoc.healthcaresystem.requestmodel.Part
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.responsemodel.ChatMessage
import com.hellodoc.healthcaresystem.responsemodel.MessageType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.net.Uri
import android.util.Base64
import com.hellodoc.healthcaresystem.requestmodel.InlineData

class GeminiHelper() {

    private val apiKey = "AIzaSyCmmkTVG3budXG5bW9R3Yr3Vsi15U8KcR0"

    // Hàm này để phân tích nội dung ảnh và video đính kèm với bài post và trả về list keyword
    suspend fun readImageAndVideo(context: Context, mediaUri: String): List<String> {
        val base64 = uriToBase64(context, mediaUri)
        if (base64 == null) return listOf("Không thể đọc tệp phương tiện.")

        val mimeType = context.contentResolver.getType(Uri.parse(mediaUri)) ?: "image/jpeg"
        val inlineData = InlineData(mime_type = mimeType, data = base64)

        val prompt = "" +
                "Hãy phân tích ảnh/video này và liệt kê các từ khóa mô tả hình ảnh, mỗi từ khóa trên một dòng, chỉ có kí tự chữ và số. Trả lời bằng tiếng Việt. Chỉ trả lời từ khoá, không trả lời thừa"

        val parts = listOf(
            Part(inline_data = inlineData),
            Part(text = prompt)
        )
        val request = GeminiRequest(contents = listOf(Content(parts = parts)))
        val response = RetrofitInstance.geminiService.askGemini(apiKey, request)

        val aiResponse = when {
            !response.isSuccessful -> "Lỗi hệ thống: ${response.code()}"
            response.body()?.candidates.isNullOrEmpty() -> "Không nhận được phản hồi từ AI"
            else -> response.body()!!.candidates.first().content.parts.first().text
        }

        return aiResponse
            .lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
    // Gọi Gemini API
    private suspend fun askGeminiWithPrompt(prompt: String): String {
        return try {
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )
            val response = RetrofitInstance.geminiService.askGemini(apiKey, request)

            when {
                !response.isSuccessful -> "Lỗi hệ thống: ${response.code()}"
                response.body()?.candidates.isNullOrEmpty() -> "Không nhận được phản hồi từ AI"
                else -> response.body()!!.candidates.first().content.parts.first().text
            }
        } catch (e: Exception) {
            "Lỗi kết nối: ${e.localizedMessage}"
        }
    }
    fun uriToBase64(context: Context, uri: String): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(Uri.parse(uri))
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) Base64.encodeToString(bytes, Base64.NO_WRAP) else null
        } catch (e: Exception) {
            null
        }
    }
}

class GeminiViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _question = MutableStateFlow("")
    val question: StateFlow<String> get() = _question

    private val _answer = MutableStateFlow("")
    val answer: StateFlow<String> get() = _answer

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> get() = _chatMessages

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> get() = _isSearching

    private val apiKey = "AIzaSyCmmkTVG3budXG5bW9R3Yr3Vsi15U8KcR0"

    // xử lý câu hỏi
    fun processUserQuery(query: String) {
        _question.value = query
        _chatMessages.update { it + ChatMessage(message = query, isUser = true) }

        when {
            isArticleQuery(query) -> searchArticles(query)
            isDoctorQuery(query) -> searchDoctors(query)
            else -> askGeminiDirectly(query)
        }
    }

    private fun isArticleQuery(query: String): Boolean {
        val lower = query.lowercase()
        val keywords = listOf(
            "tìm kiếm", "bác sĩ", "khoa", "ở đâu", "phòng khám",
            "bài", "bài viết về", "bài viết", "tìm bài viết",
            "thông tin về", "tài liệu về", "tìm hiểu về",
            "có bài nào về", "cho tôi bài viết", "cho tôi",
            "tìm bài", "cho tôi bài", "cho tôi bài viết về",
            "cho tôi bài về", "cho tôi bài nào về", "cho tôi bài nào"
        )
        return keywords.any { lower.contains(it) }
    }

    private fun isDoctorQuery(query: String): Boolean {
        val lower = query.lowercase()
        val keywords = listOf(
            "bác sĩ", "bác sỹ", "doctor", "chuyên gia",
            "khoa", "chuyên khoa", "phòng khám",
            "ai chữa", "đâu chữa", "nơi chữa",
            "bệnh viện nào", "phòng khám nào",
            "ở đâu", "chỗ", "địa chỉ", "chỗ nào"
            )
        return keywords.any { lower.contains(it) }
    }

    // Hỏi Gemini trực tiếp cho câu hỏi sức khỏe thông thường
    private fun askGeminiDirectly(query: String) {
        _answer.value = "Đang phân tích câu hỏi..."
        _isSearching.value = false

        val medicalPrompt = """
            https://res.cloudinary.com/dfklyndun/image/upload/v1751260354/683d726c812c9dcefd7266d2/post/bff4cripsuue1nwhcl77.jpg
            nếu bạn nói bạn đọc được link đã đưa lên server, vậy hãy miêu tả ảnh trong link này, không được phép bịa đặt
            Đối tượng ở giữa là gì, màu nào chiếm nhiều nhất, không gian đang ở đâu, có vật thể gì xung quanh?
        """.trimIndent()

        viewModelScope.launch {
            val response = askGeminiWithPrompt(medicalPrompt)
            _answer.value = response
            _chatMessages.update { it + ChatMessage(message = response, isUser = false) }
        }
    }

    //Tìm kiếm bài viết
    private fun searchArticles(query: String) {
        _isSearching.value = true
        _answer.value = "Đang tìm kiếm bài viết..."

        viewModelScope.launch {
            try {
                val keyword = extractSearchKeyword(query)
                val searchResponse = RetrofitInstance.postService.searchPosts(keyword)
                if (!searchResponse.isSuccessful) {
                    _chatMessages.update { it + ChatMessage(message = "Lỗi hệ thống: ${searchResponse.code()}", isUser = false) }
                    return@launch
                }
                val articles = searchResponse.body()?.take(5) ?: emptyList()

                if (articles.isEmpty()) {
                    _chatMessages.update { it + ChatMessage(message = "Không tìm thấy bài viết phù hợp.", isUser = false) }
                    return@launch
                }

                articles.forEach { article ->
                    _chatMessages.update {
                        it + ChatMessage(
                            message = article.content.take(80) + "...",
                            isUser = false,
                            type = MessageType.ARTICLE,
                            articleId = article.id
                        )
                    }
                }

            } catch (e: Exception) {
                _chatMessages.update { it + ChatMessage(message = "Lỗi tìm kiếm bài viết: ${e.localizedMessage}", isUser = false) }
            } finally {
                _isSearching.value = false
            }
        }
    }

    // Tìm kiếm bác sĩ
    private fun searchDoctors(query: String) {
        _isSearching.value = true
        _answer.value = "Đang tìm kiếm bác sĩ..."

        viewModelScope.launch {
            try {
                val keyword = extractSearchKeyword(query)
                println("keyword: $keyword")
                val searchResponse = RetrofitInstance.doctor.getDoctorBySpecialtyName(keyword)
                println("searchResponse: $searchResponse")
                val doctors = searchResponse.body()?.take(5) ?: emptyList()
                println("doctors: $doctors")

                if (doctors.isEmpty()) {
                    _chatMessages.update { it + ChatMessage(message = "Không tìm thấy bác sĩ phù hợp.", isUser = false) }
                    return@launch
                }

                // Thêm từng bác sĩ vào chat
                doctors.forEach { doctor ->
                    _chatMessages.update {
                        it + ChatMessage(
                            message = "${doctor.name} - ${doctor.specialty} (${doctor.hospital})",
                            isUser = false,
                            type = MessageType.DOCTOR,
                            doctorId = doctor.id
                        )
                    }
                }

            } catch (e: Exception) {
                _chatMessages.update { it + ChatMessage(message = "Lỗi tìm kiếm bác sĩ: ${e.localizedMessage}", isUser = false) }
            } finally {
                _isSearching.value = false
            }
        }
    }

    //Trích xuất từ khóa tìm kiếm
    private fun extractSearchKeyword(query: String): String {
        val lowerQuery = query.lowercase().trim()
        val stopWords = listOf("bài viết", "tìm kiếm", "bác sĩ", "khoa", "ở đâu", "phòng khám")
        var cleaned = lowerQuery
        stopWords.forEach { cleaned = cleaned.replace(it, " ") }
        return cleaned.replace(Regex("\\s+"), " ").trim()
    }

    // Gọi Gemini API
    private suspend fun askGeminiWithPrompt(prompt: String): String {
        return try {
            val request = GeminiRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt))))
            )
            val response = RetrofitInstance.geminiService.askGemini(apiKey, request)

            when {
                !response.isSuccessful -> "Lỗi hệ thống: ${response.code()}"
                response.body()?.candidates.isNullOrEmpty() -> "Không nhận được phản hồi từ AI"
                else -> response.body()!!.candidates.first().content.parts.first().text
            }
        } catch (e: Exception) {
            "Lỗi kết nối: ${e.localizedMessage}"
        }
    }
}