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
import android.util.Log
import com.hellodoc.healthcaresystem.requestmodel.InlineData
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.collections.forEach

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

    // Xử lý câu hỏi với phản hồi dựa trên data từ API
    fun processUserQuery(query: String) {
        _question.value = query
        _chatMessages.update { it + ChatMessage(message = query, isUser = true) }

        when {
            isDoctorNameQuery(query) -> handleDoctorNameQueryWithData(query)
            isDoctorQuery(query) -> handleDoctorQueryWithData(query)
            isArticleQuery(query) -> searchArticles(query)
            else -> askGeminiDirectly(query)
        }
    }

    // Xử lý câu hỏi về bác sĩ theo tên dựa trên data thực tế
    private fun handleDoctorNameQueryWithData(query: String) {
        _isSearching.value = true
        _answer.value = "Đang tìm kiếm thông tin bác sĩ..."

        viewModelScope.launch {
            try {
                // 1. Lấy dữ liệu từ database trước
                val doctors = searchDoctorByName(query)

                if (doctors.isEmpty()) {
                    _chatMessages.update {
                        it + ChatMessage(
                            message = "Không tìm thấy bác sĩ phù hợp trong hệ thống.",
                            isUser = false
                        )
                    }
                    return@launch
                }

                // 2. Tạo prompt với data thực tế từ API
                val doctorsInfo = doctors.take(3).joinToString("\n") { doctor ->
                    "- Tên: ${doctor.name}\n" +
                            "  Chuyên khoa: ${doctor.specialty}\n" +
                            "  Bệnh viện: ${doctor.hospital}\n" +
                            "  Địa chỉ: ${doctor.address ?: "Chưa cập nhật"}\n" +
                            "  Điện thoại: ${doctor.phone ?: "Chưa cập nhật"}\n" +
                            "  Xác minh: ${if (doctor.verified == true) "Đã xác minh" else "Chưa xác minh"}"
                }

                val aiPrompt = """
                    Người dùng hỏi: "$query"
                    
                    Dựa trên thông tin bác sĩ có trong hệ thống:
                    $doctorsInfo
                    
                    Hãy trả lời câu hỏi của người dùng một cách chi tiết và hữu ích:
                    - Nếu hỏi về thông tin cụ thể (địa chỉ, số điện thoại, chuyên khoa), hãy trả lời chính xác dựa trên data
                    - Nếu hỏi chung về bác sĩ, hãy giới thiệu thông tin tổng quan
                    - Đưa ra lời khuyên về việc liên hệ và đặt lịch khám
                    
                    Sau đó kết thúc bằng: "Thông tin chi tiết được hiển thị bên dưới:"
                    
                    Trả lời bằng tiếng Việt, thân thiện và chuyên nghiệp.
                """.trimIndent()

                // 3. AI phân tích và trả lời dựa trên data
                val aiResponse = askGeminiWithPrompt(aiPrompt)
                _chatMessages.update { it + ChatMessage(message = aiResponse, isUser = false) }

                // 4. Hiển thị thông tin chi tiết dưới dạng card
                doctors.forEach { doctor ->
                    _chatMessages.update {
                        it + ChatMessage(
                            message = "${doctor.name} - ${doctor.specialty} (${doctor.hospital})",
                            isUser = false,
                            type = MessageType.DOCTOR,
                            doctorId = doctor.id,
                            doctorName = doctor.name,
                            doctorAvatar = doctor.avatarURL,
                            doctorAddress = doctor.address,
                            doctorPhone = doctor.phone,
                        )
                    }
                }

            } catch (e: Exception) {
                _chatMessages.update {
                    it + ChatMessage(
                        message = "Lỗi xử lý: ${e.localizedMessage}",
                        isUser = false
                    )
                }
            } finally {
                _isSearching.value = false
            }
        }
    }

    // Xử lý câu hỏi về chuyên khoa dựa trên data thực tế
    private fun handleDoctorQueryWithData(query: String) {
        _isSearching.value = true
        _answer.value = "Đang tìm kiếm thông tin chuyên khoa..."

        viewModelScope.launch {
            try {
                val keyword = extractSearchKeyword(query)
                val doctors = searchDoctorsBySpecialty(keyword)

                if (doctors.isEmpty()) {
                    _chatMessages.update {
                        it + ChatMessage(
                            message = "Không tìm thấy bác sĩ chuyên khoa phù hợp.",
                            isUser = false
                        )
                    }
                    return@launch
                }

                // Tạo thông tin tổng hợp từ data thực tế
                val specialtyInfo = doctors.groupBy { it.specialty }
                val hospitalInfo = doctors.groupBy { it.hospital }.entries.take(3)
                val totalDoctors = doctors.size

                val dataInfo = """
                Thông tin từ hệ thống:
                - Tổng số bác sĩ: $totalDoctors
                - Các chuyên khoa: ${specialtyInfo.keys.joinToString(", ")}
                - Các bệnh viện: ${hospitalInfo.map { "${it.key} (${it.value.size} bác sĩ)" }.joinToString(", ")}
                
                Danh sách một số bác sĩ:
                ${doctors.take(5).joinToString("\n") { "- ${it.name} tại ${it.hospital}" }}
                """.trimIndent()

                val aiPrompt = """
                    Người dùng hỏi về: "$query"
                    
                    $dataInfo
                    
                    Dựa trên thông tin thực tế từ hệ thống, hãy:
                    1. Giải thích về chuyên khoa này và những bệnh thường gặp
                    2. Tư vấn khi nào nên đến khám
                    3. Giới thiệu về các bệnh viện và bác sĩ có sẵn trong hệ thống
                    4. Đưa ra lời khuyên về cách chọn bác sĩ phù hợp
                    
                    Kết thúc bằng: "Danh sách bác sĩ chi tiết:"
                    
                    Trả lời bằng tiếng Việt, dựa trên data thực tế.
                """.trimIndent()

                val aiResponse = askGeminiWithPrompt(aiPrompt)
                _chatMessages.update { it + ChatMessage(message = aiResponse, isUser = false) }

                // Hiển thị danh sách bác sĩ
                doctors.take(5).forEach { doctor ->
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
                _chatMessages.update {
                    it + ChatMessage(
                        message = "Lỗi tìm kiếm: ${e.localizedMessage}",
                        isUser = false
                    )
                }
            } finally {
                _isSearching.value = false
            }
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
                            articleId = article.id,
                            articleImgUrl = article.media.firstOrNull(),
                            articleAuthor = article.user?.name
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

    // Helper functions để tìm kiếm database
    private suspend fun searchDoctorByName(query: String): List<GetDoctorResponse> {
        return try {
            val doctorName = extractDoctorName(query) ?: return emptyList()
            val response = RetrofitInstance.doctor.getDoctorByName(doctorName)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun searchDoctorsBySpecialty(keyword: String): List<GetDoctorResponse> {
        return try {
            val response = RetrofitInstance.doctor.getDoctorBySpecialtyName(keyword)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun isArticleQuery(query: String): Boolean {
        val lower = query.lowercase()
        val keywords = listOf(
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
            "khoa", "chuyên khoa", "phòng khám",
            "ai chữa", "đâu chữa", "nơi chữa",
            "bệnh viện nào", "phòng khám nào",
            "ở đâu", "chỗ", "địa chỉ", "chỗ nào"
        )
        return keywords.any { lower.contains(it) }
    }

    private fun isDoctorNameQuery(query: String): Boolean {
        val lower = query.lowercase()
        val keywords = listOf(
            "bác sĩ", "bác sỹ", "doctor", "chuyên gia"
        )
        return keywords.any { lower.contains(it) }
    }

    // Hỏi Gemini trực tiếp cho câu hỏi sức khỏe thông thường
    private fun askGeminiDirectly(query: String) {
        _answer.value = "Đang phân tích câu hỏi..."
        _isSearching.value = false

        val medicalPrompt = """
            Bạn là một trợ lý y tế AI chuyên nghiệp và thân thiện.
            Câu hỏi: "$query"
            - Chỉ trả lời về y tế & sức khỏe.
            - Nếu không liên quan, nói: "Xin lỗi, tôi chỉ hỗ trợ về y tế và sức khỏe."
            - Đưa ra lời khuyên dễ hiểu, khuyến cáo khám bác sĩ khi cần.
            - Không chẩn đoán chính xác, chỉ tư vấn sơ bộ.
            Trả lời bằng tiếng Việt.
        """.trimIndent()

        viewModelScope.launch {
            val response = askGeminiWithPrompt(medicalPrompt)
            _answer.value = response
            _chatMessages.update { it + ChatMessage(message = response, isUser = false) }
        }
    }

    private fun extractDoctorName(query: String): String? {
        val lower = query.lowercase()

        // Loại bỏ các cụm từ dư thừa liên quan đến intent
        val cleaned = lower
            .replace("bác sĩ", "", ignoreCase = true)
            .replace("bác sỹ", "", ignoreCase = true)
            .replace("có uy tín không", "", ignoreCase = true)
            .replace("ở khoa nào", "", ignoreCase = true)
            .replace("làm việc ở đâu", "", ignoreCase = true)
            .replace("số điện thoại", "", ignoreCase = true)
            .replace("địa chỉ", "", ignoreCase = true)
            .trim()

        // Viết hoa chữ cái đầu cho giống DB (ví dụ: "hà văn quyết" -> "Hà Văn Quyết")
        return cleaned.split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }.trim()
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