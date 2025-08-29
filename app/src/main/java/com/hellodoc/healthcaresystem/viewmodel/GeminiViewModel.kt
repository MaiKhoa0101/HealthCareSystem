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

        when {
            isDoctorNameQuery(query) -> getDoctorByName(query)
            isDoctorQuery(query) -> searchDoctors(query)
            isArticleQuery(query) -> searchArticles(query)
            else -> askGeminiDirectly(query)
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

    private fun generateDoctorAnswer(query: String, doctor: GetDoctorResponse): String? {
        val lower = query.lowercase()

        return when {
            // Hỏi chuyên khoa
            lower.contains("khoa nào") || lower.contains("chuyên khoa") -> {
                if (doctor.specialty.name != "")
                    "Bác sĩ ${doctor.name} hiện đang công tác tại chuyên khoa ${doctor.specialty}."
                else "Xin lỗi, tôi chưa có thông tin về chuyên khoa của bác sĩ ${doctor.name}."
            }

            // Hỏi nơi làm việc
            lower.contains("làm việc ở đâu") || lower.contains("công tác ở đâu") || lower.contains("bệnh viện") -> {
                if (!doctor.hospital.isNullOrEmpty())
                    "Bác sĩ ${doctor.name} hiện đang làm việc tại ${doctor.hospital}."
                else "Xin lỗi, tôi chưa có thông tin bệnh viện nơi bác sĩ ${doctor.name} công tác."
            }

            // Hỏi địa chỉ
            lower.contains("địa chỉ") -> {
                if (!doctor.address.isNullOrEmpty())
                    "Địa chỉ của bác sĩ ${doctor.name} là: ${doctor.address}."
                else "Xin lỗi, tôi chưa có thông tin địa chỉ của bác sĩ ${doctor.name}."
            }

            // Hỏi số điện thoại
            lower.contains("số điện thoại") || lower.contains("liên hệ") -> {
                if (!doctor.phone.isNullOrEmpty())
                    "Bạn có thể liên hệ bác sĩ ${doctor.name} qua số điện thoại: ${doctor.phone}."
                else "Xin lỗi, tôi chưa có số điện thoại của bác sĩ ${doctor.name}."
            }

            // Hỏi uy tín
            lower.contains("uy tín") || lower.contains("có tốt không") || lower.contains("giỏi không") -> {
                if (doctor.verified == true)
                    "Bác sĩ ${doctor.name} là bác sĩ đã được xác minh và có uy tín trong hệ thống."
                else "Hiện tôi chưa có thông tin xác minh độ uy tín của bác sĩ ${doctor.name}."
            }
            else -> null // fallback: không hiểu intent -> sẽ hiển thị card
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

    private fun getDoctorByName(userQuery: String) {
        _isSearching.value = true
        _answer.value = "Đang tìm kiếm bác sĩ..."

        viewModelScope.launch {
            try {
                val doctorName = extractDoctorName(userQuery) ?: return@launch

                println("Extracted doctor name: $doctorName")

                val response = RetrofitInstance.doctor.getDoctorByName(doctorName)

                if (!response.isSuccessful) {
                    _chatMessages.update {
                        it + ChatMessage(
                            message = "Lỗi hệ thống: ${response.code()}",
                            isUser = false
                        )
                    }
                    return@launch
                }

                val doctors = response.body() ?: emptyList()
                if (doctors.isEmpty()) {
                    _chatMessages.update {
                        it + ChatMessage(
                            message = "Không tìm thấy bác sĩ phù hợp với tên \"$doctorName\".",
                            isUser = false
                        )
                    }
                    return@launch
                }

                val reply = generateDoctorAnswer(userQuery, doctors.first())
                if (reply != null) {
                    _chatMessages.update { it + ChatMessage(message = reply, isUser = false) }
                } else {
                    // fallback: hiển thị card thông tin
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
                }
            } catch (e: Exception) {
                _chatMessages.update {
                    it + ChatMessage(
                        message = "Lỗi tìm kiếm bác sĩ: ${e.localizedMessage}",
                        isUser = false
                    )
                }
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
        // Loại bỏ dấu tiếng Việt
        //return removeDiacritics(cleaned.replace(Regex("\\s+"), " ").trim())
    }

    private fun removeDiacritics(text: String): String {
        val diacriticMap = mapOf(
            'á' to 'a', 'à' to 'a', 'ả' to 'a', 'ã' to 'a', 'ạ' to 'a',
            'ă' to 'a', 'ắ' to 'a', 'ằ' to 'a', 'ẳ' to 'a', 'ẵ' to 'a', 'ặ' to 'a',
            'â' to 'a', 'ấ' to 'a', 'ầ' to 'a', 'ẩ' to 'a', 'ẫ' to 'a', 'ậ' to 'a',
            'đ' to 'd',
            'é' to 'e', 'è' to 'e', 'ẻ' to 'e', 'ẽ' to 'e', 'ẹ' to 'e',
            'ê' to 'e', 'ế' to 'e', 'ề' to 'e', 'ể' to 'e', 'ễ' to 'e', 'ệ' to 'e',
            'í' to 'i', 'ì' to 'i', 'ỉ' to 'i', 'ĩ' to 'i', 'ị' to 'i',
            'ó' to 'o', 'ò' to 'o', 'ỏ' to 'o', 'õ' to 'o', 'ọ' to 'o',
            'ô' to 'o', 'ố' to 'o', 'ồ' to 'o', 'ổ' to 'o', 'ỗ' to 'o', 'ộ' to 'o',
            'ơ' to 'o', 'ớ' to 'o', 'ờ' to 'o', 'ở' to 'o', 'ỡ' to 'o', 'ợ' to 'o',
            'ú' to 'u', 'ù' to 'u', 'ủ' to 'u', 'ũ' to 'u', 'ụ' to 'u',
            'ư' to 'u', 'ứ' to 'u', 'ừ' to 'u', 'ử' to 'u', 'ữ' to 'u', 'ự' to 'u',
            'ý' to 'y', 'ỳ' to 'y', 'ỷ' to 'y', 'ỹ' to 'y', 'ỵ' to 'y'
        )

        return text.map { char -> diacriticMap[char] ?: char }.joinToString("")
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