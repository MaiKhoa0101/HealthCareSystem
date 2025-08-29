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
import kotlin.collections.forEach

class GeminiHelper() {

    private val apiKey = "AIzaSyCmmkTVG3budXG5bW9R3Yr3Vsi15U8KcR0"

    suspend fun readImageAndVideo(context: Context, mediaUris: List<Uri>): List<String> {
        try {
            // Chuyển Uri → base64 và lấy mimeType tương ứng
            val mediaParts = mediaUris.map { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return listOf("Không thể đọc tệp phương tiện: $uri")

                val bytes = inputStream.use { it.readBytes() }
                val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)

                val mimeType = context.contentResolver.getType(uri)
                    ?: when {
                        uri.toString().endsWith(".png", true) -> "image/png"
                        uri.toString().endsWith(".jpg", true) || uri.toString().endsWith(".jpeg", true)
                            -> "image/jpeg"
                        uri.toString().endsWith(".mp4", true) -> "video/mp4"
                        uri.toString().endsWith(".mov", true) -> "video/quicktime"
                        else -> "application/octet-stream"
                    }

                Part(
                    inline_data = InlineData(
                        mime_type = mimeType,
                        data = base64
                    )
                )
            }

            // Prompt text
            val promptPart = Part(
                text = "Hãy phân tích tất cả ảnh/video này và liệt kê các từ khóa mô tả, " +
                        "mỗi từ khóa trên một dòng, viết thường, chỉ có kí tự chữ và số. " +
                        "Trả lời bằng tiếng Việt. Chỉ trả lời từ khoá, không trả lời thừa"
            )

            // Gom mediaParts + promptPart trong MỘT Content duy nhất
            val request = GeminiRequest(
                contents = listOf(
                    Content(parts = mediaParts + promptPart)
                )
            )

            val response = RetrofitInstance.geminiService.askGemini(apiKey, request)

            val aiResponse = when {
                !response.isSuccessful ->
                    "Lỗi hệ thống: ${response.code()} - ${response.errorBody()?.string()}"
                response.body()?.candidates.isNullOrEmpty() ->
                    "Không nhận được phản hồi từ AI"
                else ->
                    response.body()!!.candidates.first().content.parts.first().text
            }

            return aiResponse
                .lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }

        } catch (e: Exception) {
            return listOf("Lỗi khi xử lý: ${e.message}")
        }
    }

    // Chỉ encode 1 file
    fun uriToBase64(context: Context, uri: String): String? {
        return try {
            context.contentResolver.openInputStream(Uri.parse(uri)).use { inputStream ->
                val bytes = inputStream?.readBytes()
                if (bytes != null) Base64.encodeToString(bytes, Base64.NO_WRAP) else null
            }
        } catch (e: Exception) {
            null
        }
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
        _isSearching.value = true

        viewModelScope.launch {
            try{
                //bước 1: cho AI phân tích query để tách
                val queryAnalysis = analyzeQueryWithAI(query)

                //bước 2: dựa vào kết quả so khi ptich để dùng service phù hợp
                when{
                    queryAnalysis.doctorName.isNotBlank() -> {
                        handleDoctorQuery(query, queryAnalysis)
                    }
                    queryAnalysis.specialty.isNotBlank() -> {
                        handleSpecialtyQuery(query, queryAnalysis)
                    }
                    queryAnalysis.articleKeyword.isNotBlank() -> {
                        handleArticleQuery(query, queryAnalysis)
                    }
                    else -> {
                        // Câu hỏi sức khỏe thông thường
                        askGeminiDirectly(query)
                    }
                }
            } catch (e: Exception){
                _chatMessages.update {
                    it + ChatMessage(
                        message = "Lỗi xử lý câu hỏi: ${e.localizedMessage}",
                        isUser = false
                    )
                }
                _isSearching.value = false
            }
        }
    }


    // Data class để chứa kết quả phân tích
    data class QueryAnalysis(
        val doctorName: String = "",
        val specialty: String = "",
        val articleKeyword: String = "",
        val intent: String = "",
        val remainingQuery: String = ""
    )

    // AI phân tích query và tách thông tin
    private suspend fun analyzeQueryWithAI(query: String): QueryAnalysis {
        val analysisPrompt = """
            Phân tích câu hỏi người dùng và trích xuất thông tin theo format JSON:
            
            Câu hỏi: "$query"
            
            Hãy trả về JSON với các trường:
            - doctorName: tên bác sĩ (nếu có) - viết hoa chữ cái đầu
            - specialty: chuyên khoa (nếu có) 
            - articleKeyword: từ khóa bài viết (nếu có)
            - intent: mục đích (tìm bác sĩ, tìm chuyên khoa, tìm bài viết, hỏi sức khỏe)
            - remainingQuery: phần còn lại của câu hỏi sau khi tách thông tin
            
            Ví dụ:
            - "Bác sĩ Nguyễn Văn A làm việc ở đâu?" 
            → {"doctorName":"Nguyễn Văn A","specialty":"","articleKeyword":"","intent":"tìm bác sĩ","remainingQuery":"làm việc ở đâu"}
            
            - "Khoa tim mạch có bác sĩ nào giỏi?"
            → {"doctorName":"","specialty":"tim mạch","articleKeyword":"","intent":"tìm chuyên khoa","remainingQuery":"có bác sĩ nào giỏi"}
            
            - "Tìm bài viết về bệnh tiểu đường"
            → {"doctorName":"","specialty":"","articleKeyword":"tiểu đường","intent":"tìm bài viết","remainingQuery":""}
            
            Chỉ trả về JSON, không giải thích thêm.
        """.trimIndent()

        return try {
            val response = askGeminiWithPrompt(analysisPrompt)
            parseQueryAnalysisResponse(response)
        } catch (e: Exception) {
            Log.e("GeminiViewModel", "Error analyzing query: ${e.message}")
            QueryAnalysis(remainingQuery = query, intent = "hỏi sức khỏe")
        }
    }


    // Parse JSON response từ AI
    private fun parseQueryAnalysisResponse(response: String): QueryAnalysis {
        return try {
            // Tìm JSON trong response
            val jsonStart = response.indexOf("{")
            val jsonEnd = response.lastIndexOf("}") + 1

            if (jsonStart == -1 || jsonEnd <= jsonStart) {
                return QueryAnalysis(remainingQuery = response, intent = "hỏi sức khỏe")
            }

            val jsonString = response.substring(jsonStart, jsonEnd)

            // Parse JSON thủ công
            val doctorName = extractJsonValue(jsonString, "doctorName")
            val specialty = extractJsonValue(jsonString, "specialty")
            val articleKeyword = extractJsonValue(jsonString, "articleKeyword")
            val intent = extractJsonValue(jsonString, "intent")
            val remainingQuery = extractJsonValue(jsonString, "remainingQuery")

            QueryAnalysis(
                doctorName = doctorName,
                specialty = specialty,
                articleKeyword = articleKeyword,
                intent = intent,
                remainingQuery = remainingQuery
            )
        } catch (e: Exception) {
            Log.e("GeminiViewModel", "Error parsing analysis: ${e.message}")
            QueryAnalysis(remainingQuery = response, intent = "hỏi sức khỏe")
        }
    }

    // Helper function để extract value từ JSON string
    private fun extractJsonValue(json: String, key: String): String {
        return try {
            val pattern = "\"$key\"\\s*:\\s*\"([^\"]*)\""
            val regex = Regex(pattern)
            regex.find(json)?.groupValues?.get(1) ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    // Xử lý query về bác sĩ cụ thể
    private suspend fun handleDoctorQuery(originalQuery: String, analysis: QueryAnalysis) {
        try {
            // Gọi API tìm bác sĩ
            val doctors = searchDoctorByName(analysis.doctorName)

            if (doctors.isEmpty()) {
                _chatMessages.update {
                    it + ChatMessage(
                        message = "Không tìm thấy bác sĩ ${analysis.doctorName} trong hệ thống.",
                        isUser = false
                    )
                }
                return
            }

            // AI trả lời dựa trên data và remaining query
            val aiResponse = generateDoctorResponse(originalQuery, analysis, doctors)
            _chatMessages.update { it + ChatMessage(message = aiResponse, isUser = false) }

            // Hiển thị card bác sĩ
            doctors.take(3).forEach { doctor ->
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
        } finally {
            _isSearching.value = false
        }
    }

    // Xử lý query về chuyên khoa
    private suspend fun handleSpecialtyQuery(originalQuery: String, analysis: QueryAnalysis) {
        try {
            val doctors = searchDoctorsBySpecialty(analysis.specialty)

            if (doctors.isEmpty()) {
                _chatMessages.update {
                    it + ChatMessage(
                        message = "Không tìm thấy bác sĩ chuyên khoa ${analysis.specialty}.",
                        isUser = false
                    )
                }
                return
            }

            val aiResponse = generateSpecialtyResponse(originalQuery, analysis, doctors)
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
        } finally {
            _isSearching.value = false
        }
    }

    // Xử lý query về bài viết
    private suspend fun handleArticleQuery(originalQuery: String, analysis: QueryAnalysis) {
        try {
            val searchResponse = RetrofitInstance.postService.searchPosts(analysis.articleKeyword)

            if (!searchResponse.isSuccessful) {
                _chatMessages.update {
                    it + ChatMessage(message = "Lỗi tìm kiếm bài viết: ${searchResponse.code()}", isUser = false)
                }
                return
            }

            val articles = searchResponse.body()?.take(5) ?: emptyList()

            if (articles.isEmpty()) {
                _chatMessages.update {
                    it + ChatMessage(message = "Không tìm thấy bài viết về ${analysis.articleKeyword}.", isUser = false)
                }
                return
            }

            val aiResponse = generateArticleResponse(originalQuery, analysis, articles)
            _chatMessages.update { it + ChatMessage(message = aiResponse, isUser = false) }

            // Hiển thị card bài viết
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
        } finally {
            _isSearching.value = false
        }
    }

    // Xử lý câu hỏi về bác sĩ
    private suspend fun generateDoctorResponse(
        originalQuery: String,
        analysis: QueryAnalysis,
        doctors: List<GetDoctorResponse>
    ): String {
        val doctorsInfo = doctors.take(3).joinToString("\n") { doctor ->
            "- Tên: ${doctor.name}\n" +
                    "  Chuyên khoa: ${doctor.specialty}\n" +
                    "  Bệnh viện: ${doctor.hospital}\n" +
                    "  Địa chỉ: ${doctor.address ?: "Chưa cập nhật"}\n" +
                    "  Điện thoại: ${doctor.phone ?: "Chưa cập nhật"}\n" +
                    "  Xác minh: ${if (doctor.verified == true) "Đã xác minh" else "Chưa xác minh"}"
        }

        val responsePrompt = """
            Người dùng hỏi: "$originalQuery"
            Phần thông tin cần trả lời: "${analysis.remainingQuery}"
            
            Thông tin bác sĩ ${analysis.doctorName} trong hệ thống:
            $doctorsInfo
            
            Hãy trả lời câu hỏi của người dùng dựa trên thông tin thực tế:
            - Trả lời trực tiếp phần "${analysis.remainingQuery}" 
            - Đưa ra thông tin chi tiết và hữu ích
            - Kết thúc bằng: "Thông tin chi tiết:"
            
            Trả lời bằng tiếng Việt, thân thiện và chuyên nghiệp.
        """.trimIndent()

        return askGeminiWithPrompt(responsePrompt)
    }

    // Xử lý câu hỏi về chuyên khoa
    private suspend fun generateSpecialtyResponse(
        originalQuery: String,
        analysis: QueryAnalysis,
        doctors: List<GetDoctorResponse>
    ): String {
        val specialtyStats = """
        Thông tin chuyên khoa ${analysis.specialty}:
        - Tổng số bác sĩ: ${doctors.size}
        - Các bệnh viện: ${doctors.groupBy { it.hospital }.keys.joinToString(", ")}
        - Bác sĩ nổi bật: ${doctors.take(3).joinToString(", ") { it.name }}
        """.trimIndent()

        val responsePrompt = """
            Người dùng hỏi: "$originalQuery"
            Phần cần trả lời: "${analysis.remainingQuery}"
            
            $specialtyStats
            
            Hãy trả lời dựa trên thông tin thực tế về chuyên khoa ${analysis.specialty}:
            1. Giải thích về chuyên khoa này
            2. Trả lời cụ thể phần "${analysis.remainingQuery}"
            3. Giới thiệu các bác sĩ có sẵn trong hệ thống
            4. Kết thúc bằng: "Danh sách bác sĩ:"
            
            Trả lời bằng tiếng Việt.
        """.trimIndent()

        return askGeminiWithPrompt(responsePrompt)
    }

    // Xử lý câu hỏi về bài viết
    private suspend fun generateArticleResponse(
        originalQuery: String,
        analysis: QueryAnalysis,
        articles: List<Any>
    ): String {
        val responsePrompt = """
            Người dùng tìm bài viết: "$originalQuery"
            Từ khóa: "${analysis.articleKeyword}"
            
            Đã tìm thấy ${articles.size} bài viết liên quan.
            
            Hãy:
            1. Xác nhận đã tìm thấy bài viết về chủ đề này
            2. Tóm tắt ngắn gọn về chủ đề
            3. Kết thúc bằng: "Các bài viết liên quan:"
            
            Trả lời bằng tiếng Việt.
        """.trimIndent()

        return askGeminiWithPrompt(responsePrompt)
    }

    // Hỏi Gemini trực tiếp cho câu hỏi sức khỏe thông thường
    private suspend fun askGeminiDirectly(query: String) {
        val medicalPrompt = """
            Bạn là một trợ lý y tế AI chuyên nghiệp và thân thiện.
            Câu hỏi: "$query"
            - Chỉ trả lời về y tế & sức khỏe.
            - Nếu không liên quan, nói: "Xin lỗi, tôi chỉ hỗ trợ về y tế và sức khỏe."
            - Đưa ra lời khuyên dễ hiểu, khuyến cáo khám bác sĩ khi cần.
            - Không chẩn đoán chính xác, chỉ tư vấn sơ bộ.
            Trả lời bằng tiếng Việt.
        """.trimIndent()

        val response = askGeminiWithPrompt(medicalPrompt)
        _answer.value = response
        _chatMessages.update { it + ChatMessage(message = response, isUser = false) }
        _isSearching.value = false
    }

    // Helper functions để tìm kiếm database
    private suspend fun searchDoctorByName(doctorName: String): List<GetDoctorResponse> {
        return try {
            val response = RetrofitInstance.doctor.getDoctorByName(doctorName)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun searchDoctorsBySpecialty(specialty: String): List<GetDoctorResponse> {
        return try {
            val response = RetrofitInstance.doctor.getDoctorBySpecialtyName(specialty)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
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