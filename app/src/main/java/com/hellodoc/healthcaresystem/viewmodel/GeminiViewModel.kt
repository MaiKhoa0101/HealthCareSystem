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

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> get() = _isSearching

    private val apiKey = "AIzaSyCmmkTVG3budXG5bW9R3Yr3Vsi15U8KcR0"

    // Phương thức chính để xử lý câu hỏi
    fun processUserQuery(query: String) {
        _question.value = query
        _chatMessages.update { it + ChatMessage(query, isUser = true) }

        if (isSearchQuery(query)) {
            // Tìm kiếm trong database
            searchDatabaseAndRespond(query)
        } else {
            // Hỏi Gemini trực tiếp
            askGeminiDirectly(query)
        }
    }

    // Trích xuất từ khóa chính để tìm kiếm - HÀM MỚI
    private fun extractSearchKeyword(query: String): String {
        val lowerQuery = query.lowercase().trim()

        // Loại bỏ các từ/cụm từ không cần thiết
        val stopWords = listOf(
            // hỏi về tìm kiếm
            "bài viết về", "bài viết", "tìm bài viết", "cho tôi bài viết",
            "thông tin về", "tài liệu về", "tìm hiểu về", "có bài nào về",
            "tìm kiếm", "search", "tìm", "có không", "có gì về",

            // hỏi về bác sĩ
            "bác sĩ nào", "bác sĩ", "bác sỹ", "doctor", "chuyên gia",
            "ai chữa", "đâu chữa", "nơi chữa", "chữa bệnh",
            "ở khoa", "khoa", "chuyên khoa", "phòng khám nào", "phòng khám",
            "bệnh viện nào", "bệnh viện", "ở đâu", "chỗ nào", "nơi nào",

            // Từ ngữ thông thường
            "là gì", "như thế nào", "ra sao", "thế nào",
            "có", "được", "hay", "nhất", "tốt", "giỏi",
            "nào", "gì", "đâu", "sao", "ai", "làm"
        )

        var cleanedQuery = lowerQuery

        // Loại bỏ các cụm từ dài trước
        stopWords.sortedByDescending { it.length }.forEach { stopWord ->
            cleanedQuery = cleanedQuery.replace(stopWord, " ")
        }

        // Làm sạch và chuẩn hóa
        cleanedQuery = cleanedQuery
            .replace(Regex("\\s+"), " ") // Loại bỏ khoảng trắng thừa
            .trim()

        // Nếu kết quả quá ngắn, lấy từ khóa y tế quan trọng
        if (cleanedQuery.length < 2) {
            cleanedQuery = extractMedicalKeywords(lowerQuery)
        }

        // Trả về từ khóa cuối cùng hoặc query gốc nếu không trích xuất được
        return if (cleanedQuery.isNotEmpty()) cleanedQuery else query.trim()
    }

    // Trích xuất từ khóa y tế quan trọng
    private fun extractMedicalKeywords(query: String): String {
        val medicalTerms = listOf(
            // Bệnh lý
            "tim mạch", "tiểu đường", "cao huyết áp", "ung thư", "gan", "thận",
            "phổi", "dạ dày", "ruột", "xương khớp", "da liễu", "mắt", "tai mũi họng",
            "thần kinh", "tâm thần", "sản phụ khoa", "nhi khoa", "lão khoa",

            // Chuyên khoa
            "nội khoa", "ngoại khoa", "sản khoa", "nhi khoa", "mắt", "răng hàm mặt",
            "da liễu", "tai mũi họng", "thần kinh", "tâm thần", "xương khớp",
            "tiết niệu", "tim mạch", "hô hấp", "tiêu hóa",

            // Triệu chứng
            "đau đầu", "sốt", "ho", "khó thở", "đau bụng", "tiêu chảy",
            "táo bón", "chóng mặt", "mất ngủ", "stress"
        )

        // Tìm từ khóa y tế đầu tiên xuất hiện
        medicalTerms.forEach { term ->
            if (query.contains(term)) {
                return term
            }
        }

        // Nếu không tìm thấy, trả về toàn bộ query đã làm sạch
        return query.replace(Regex("[^\\w\\sáàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    // Phân biệt loại câu hỏi - CẢI TIẾN CHÍNH
    private fun isSearchQuery(query: String): Boolean {
        val lowerQuery = query.lowercase().trim()

        // Các từ khóa cho câu hỏi TÌM KIẾM
        val searchKeywords = listOf(
            "bài viết về", "bài viết", "tìm bài viết",
            "thông tin về", "tài liệu về", "tìm hiểu về",
            "có bài nào về", "cho tôi bài viết",

            "bác sĩ", "bác sỹ", "doctor", "chuyên gia",
            "khoa", "chuyên khoa", "phòng khám",
            "ai chữa", "đâu chữa", "nơi chữa",
            "bệnh viện nào", "phòng khám nào",

            "tìm kiếm", "search", "tìm", "có không",
            "danh sách", "list", "gợi ý", "recommend",
            "ở đâu", "chỗ nào", "nơi nào"
        )

        // Kiểm tra từ khóa tìm kiếm
        val hasSearchKeyword = searchKeywords.any { keyword ->
            lowerQuery.contains(keyword)
        }

        val questionPatterns = listOf(
            "ai là", "ai chữa", "đâu có", "đâu chữa",
            "có bác sĩ nào", "có ai", "có gì về",
            "nơi nào", "chỗ nào có"
        )

        val hasQuestionPattern = questionPatterns.any { pattern ->
            lowerQuery.contains(pattern)
        }

        return hasSearchKeyword || hasQuestionPattern
    }

    // Hỏi Gemini trực tiếp cho câu hỏi sức khỏe thông thường
    private fun askGeminiDirectly(query: String) {
        _answer.value = "Đang phân tích triệu chứng..."
        _isSearching.value = false

        val medicalPrompt = """
            Bạn là một trợ lý y tế AI chuyên nghiệp và thân thiện.
            
            Câu hỏi của người dùng: "$query"
            
            Hướng dẫn:
            - Chỉ trả lời các vấn đề liên quan đến y tế, sức khỏe
            - Nếu không liên quan đến y tế, trả lời: "Xin lỗi, tôi chỉ hỗ trợ tư vấn về sức khỏe và y tế."
            - Đưa ra lời khuyên hữu ích và dễ hiểu
            - Luôn khuyến cáo khám bác sĩ khi cần thiết
            - Không chẩn đoán chính xác, chỉ tư vấn sơ bộ
            
            Trả lời bằng tiếng Việt, thân thiện và chuyên nghiệp:
        """.trimIndent()

        viewModelScope.launch {
            try {
                val response = askGeminiWithPrompt(medicalPrompt)
                _answer.value = response
                _chatMessages.update { it + ChatMessage(response, isUser = false) }
            } catch (e: Exception) {
                val errorMsg = "Lỗi kết nối: ${e.localizedMessage}"
                _answer.value = errorMsg
                _chatMessages.update { it + ChatMessage(errorMsg, isUser = false) }
            }
        }
    }

    // Tìm kiếm trong database và phản hồi
    private fun searchDatabaseAndRespond(query: String) {
        _isSearching.value = true
        _answer.value = "Đang tìm kiếm thông tin trong cơ sở dữ liệu..."

        viewModelScope.launch {
            try {
                val searchKeyword = extractSearchKeyword(query)
                println("DEBUG - Original query: $query")
                println("DEBUG - Extracted keyword: $searchKeyword")

                // Tìm kiếm trong database với từ khóa đã trích xuất
                val searchResponse = RetrofitInstance.postService.searchPosts(searchKeyword)
                val articles = searchResponse.body()?.take(5) ?: emptyList()

                if (articles.isEmpty()) {
                    // Không có dữ liệu -> Hỏi Gemini trực tiếp
                    val fallbackPrompt = """
                        Người dùng hỏi: "$query"
                        Từ khóa tìm kiếm: "$searchKeyword"
                        
                        Không tìm thấy thông tin cụ thể trong cơ sở dữ liệu.
                        Hãy trả lời dựa trên kiến thức y tế chung của bạn.
                        
                        Nếu câu hỏi về bác sĩ/phòng khám cụ thể, hãy khuyến nghị người dùng:
                        - Liên hệ trực tiếp với bệnh viện/phòng khám
                        - Tham khảo website chính thức
                        - Gọi hotline để được tư vấn
                        
                        Trả lời bằng tiếng Việt:
                    """.trimIndent()

                    val response = askGeminiWithPrompt(fallbackPrompt)
                    _answer.value = response
                    _chatMessages.update { it + ChatMessage(response, isUser = false) }
                    return@launch
                }

                // 2. Có dữ liệu -> Kết hợp với Gemini
                val articlesSummary = articles.joinToString("\n\n") { article ->
                    """
                    Tiêu đề: ${article.content.take(50)}...
                    Nội dung: ${article.content.take(200)}${if (article.content.length > 200) "..." else ""}
                    Ngày: ${article.createdAt}
                    """.trimIndent()
                }

                val searchPrompt = """
                    Người dùng tìm kiếm: "$query"
                    Từ khóa đã sử dụng: "$searchKeyword"
                    
                    Thông tin từ cơ sở dữ liệu ứng dụng:
                    $articlesSummary
                    
                    Yêu cầu:
                    1. Tóm tắt và trình bày thông tin từ database một cách có tổ chức
                    2. Sắp xếp theo mức độ liên quan với câu hỏi
                    3. Bổ sung kiến thức y tế nếu cần thiết
                    4. Đề cập nguồn thông tin từ ứng dụng
                    5. Khuyến cáo thêm nếu phù hợp
                    
                    Trả lời chi tiết, có cấu trúc và dễ đọc bằng tiếng Việt:
                """.trimIndent()

                val response = askGeminiWithPrompt(searchPrompt)
                _answer.value = response
                _chatMessages.update { it + ChatMessage(response, isUser = false) }

            } catch (e: Exception) {
                val errorMsg = "Lỗi khi tìm kiếm thông tin: ${e.localizedMessage}"
                _answer.value = errorMsg
                _chatMessages.update { it + ChatMessage(errorMsg, isUser = false) }
            } finally {
                _isSearching.value = false
            }
        }
    }

    // Helper function để gọi Gemini API
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
            "🔌 Lỗi kết nối: ${e.localizedMessage}"
        }
    }

    // Các phương thức cũ để tương thích
    @Deprecated("Sử dụng processUserQuery() thay thế")
    fun askGemini(query: String) {
        processUserQuery(query)
    }

    @Deprecated("Sử dụng processUserQuery() thay thế")
    fun searchArticlesAndAskAI(query: String) {
        processUserQuery(query)
    }
}