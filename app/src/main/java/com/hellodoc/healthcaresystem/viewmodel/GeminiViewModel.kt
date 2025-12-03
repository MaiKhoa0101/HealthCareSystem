package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import android.util.Base64OutputStream
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.BuildConfig
import com.hellodoc.healthcaresystem.requestmodel.Content
import com.hellodoc.healthcaresystem.requestmodel.GeminiRequest
import com.hellodoc.healthcaresystem.requestmodel.InlineData
import com.hellodoc.healthcaresystem.requestmodel.Part
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ChatMessage
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.MessageType
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Specialty
import com.hellodoc.healthcaresystem.model.repository.DoctorRepository
import com.hellodoc.healthcaresystem.model.repository.PostRepository
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.view.user.supportfunction.extractFrames
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import kotlinx.coroutines.Job

// Quản lý API keys
object ApiKeyManager {
    private val apiKeys = BuildConfig.API_KEYS.split(",")
    private var currentIndex = 0
    private val invalidKeys = mutableSetOf<Int>()

    @Synchronized
    fun getCurrentKey(): String = apiKeys[currentIndex]

    @Synchronized
    fun markKeyInvalid() {
        invalidKeys.add(currentIndex)
        rotateKey()
    }

    @Synchronized
    fun rotateKey() {
        var next = (currentIndex + 1) % apiKeys.size
        while (invalidKeys.contains(next) && invalidKeys.size < apiKeys.size) {
            next = (next + 1) % apiKeys.size
        }
        currentIndex = next
    }

    @Synchronized
    fun getTotalKeys(): Int = apiKeys.size
}

// Helper class để xử lý media và gọi API
class GeminiHelper() {
    // Gửi yêu cầu đến Gemini API với logic xoay key
    private suspend fun sendRequestWithRetry(request: GeminiRequest): String {
        var attempts = 0
        val maxAttempts = ApiKeyManager.getTotalKeys()

        while (attempts < maxAttempts) {
            val apiKey = ApiKeyManager.getCurrentKey()
            try {
                val response = RetrofitInstance.geminiService.askGemini(apiKey, request)

                if (response.isSuccessful && !response.body()?.candidates.isNullOrEmpty()) {
                    // Nếu thành công, trả về kết quả
                    return response.body()!!.candidates.first().content.parts.first().text
                } else {
                    // Nếu thất bại, log lỗi và thử lại
                    Log.e(
                        "GeminiHelper",
                        "Lỗi hệ thống với API key: $apiKey - ${response.code()} ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                // Nếu gặp lỗi kết nối, log lỗi
                Log.e("GeminiHelper", "Lỗi kết nối với API key: $apiKey - ${e.localizedMessage}")
            }

            ApiKeyManager.rotateKey()
            attempts++
        }

        // Nếu đã thử hết tất cả các key mà vẫn thất bại
        Log.e("GeminiHelper", "Lỗi: Không thể kết nối với Gemini API sau khi thử tất cả các API key.")
        return ""
    }

    // Gửi media đến Gemini API
    suspend fun sendMediaToGemini(mediaParts: List<Part>): List<String> {
        val promptPart = Part(
            text = """
                Bạn nhận đầu vào là nhiều hình ảnh hoặc video.  
                Nhiệm vụ của bạn: phân tích và trích xuất từ khóa mô tả nội dung.  
                
                Yêu cầu:  
                - Mỗi từ khóa viết trên một dòng.  
                - Viết thường (lowercase).  
                - Chỉ gồm ký tự chữ cái và số, không dấu chấm câu, không ký tự đặc biệt.  
                - Mỗi từ khóa phải có cả tiếng Việt và tiếng Anh, cách nhau bằng dấu phẩy.  
                - Không được trả lời gì ngoài từ khóa.  
                - Nếu không có từ khóa phù hợp, không trả lời gì.
            """.trimIndent()
        )

        val request = GeminiRequest(contents = listOf(Content(parts = mediaParts + promptPart)))
        val response = sendRequestWithRetry(request)

        return response.lines().map { it.trim() }.filter { it.isNotEmpty() }
    }

    // Đọc và phân tích hình ảnh/video từ thiết bị
    suspend fun readImageAndVideo(context: Context, mediaUris: List<Uri>): List<String> {
        val mediaParts = prepareMediaParts(context, mediaUris)
        return sendMediaToGemini(mediaParts)
    }

    // Đọc và phân tích hình ảnh/video từ internet
    suspend fun readImageAndVideoFromInternet(context: Context, mediaUrls: List<String>): List<String> {
        val mediaParts = prepareMediaPartsFromUrls(mediaUrls)
        return sendMediaToGemini(mediaParts)
    }

    // Chuẩn bị media parts từ URI
    private suspend fun prepareMediaParts(context: Context, mediaUris: List<Uri>): List<Part> {
        val mediaParts = mutableListOf<Part>()
        for (uri in mediaUris) {
            val mimeType = context.contentResolver.getType(uri) ?: getMimeTypeFromUri(uri.toString())
            if (mimeType.startsWith("video")) {
                mediaParts.addAll(prepareVideoParts(context, uri))
            } else {
                mediaParts.add(prepareImagePart(context, uri, mimeType))
            }
        }
        return mediaParts
    }

    // Chuẩn bị media parts từ URL
    private suspend fun prepareMediaPartsFromUrls(mediaUrls: List<String>): List<Part> {
        val mediaParts = mutableListOf<Part>()
        for (url in mediaUrls) {
            val mimeType = getMimeTypeFromUrl(url)
            if (mimeType.startsWith("video")) {
                Log.e("GeminiHelper", "Hiện chưa hỗ trợ video từ internet: $url")
            } else {
                val base64 = downloadUrlToBase64(url)
                if (base64 != null) {
                    mediaParts.add(
                        Part(
                            inline_data = InlineData(
                                mime_type = mimeType,
                                data = base64
                            )
                        )
                    )
                }
            }
        }
        return mediaParts
    }

    // Chuẩn bị part cho video (trích xuất frame)
    private suspend fun prepareVideoParts(context: Context, uri: Uri): List<Part> {
        val frames = extractFrames(context, uri, maxFrames = 10)
        return frames.map { file ->
            val base64 = file.toBase64()
            Part(
                inline_data = InlineData(
                    mime_type = "image/jpeg",
                    data = base64
                )
            )
        }
    }

    // Chuẩn bị part cho ảnh
    private fun prepareImagePart(context: Context, uri: Uri, mimeType: String): Part {
        val base64 = context.contentResolver.openInputStream(uri)?.use { it.readBytes().encodeBase64() }
            ?: throw Exception("Không thể đọc tệp phương tiện: $uri")
        return Part(
            inline_data = InlineData(
                mime_type = mimeType,
                data = base64
            )
        )
    }

    // Helper: Lấy MIME type từ URL
    private fun getMimeTypeFromUrl(url: String): String {
        return when {
            url.endsWith(".png", true) -> "image/png"
            url.endsWith(".jpg", true) || url.endsWith(".jpeg", true) -> "image/jpeg"
            url.endsWith(".mp4", true) -> "video/mp4"
            url.endsWith(".mov", true) -> "video/quicktime"
            else -> "application/octet-stream"
        }
    }

    // Helper: Lấy MIME type từ URI
    private fun getMimeTypeFromUri(uri: String): String {
        return when {
            uri.endsWith(".png", true) -> "image/png"
            uri.endsWith(".jpg", true) || uri.endsWith(".jpeg", true) -> "image/jpeg"
            uri.endsWith(".mp4", true) -> "video/mp4"
            uri.endsWith(".mov", true) -> "video/quicktime"
            else -> "application/octet-stream"
        }
    }

    // Helper: Tải URL và chuyển thành Base64
    private fun downloadUrlToBase64(url: String): String? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).header("User-Agent", "Mozilla/5.0").build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null
            response.body?.bytes()?.encodeBase64()
        } catch (e: Exception) {
            null
        }
    }

    // Helper: Chuyển file thành Base64
    private fun File.toBase64(): String {
        return FileInputStream(this).use { input ->
            val output = ByteArrayOutputStream()
            Base64OutputStream(output, Base64.NO_WRAP).use { base64Stream ->
                val buffer = ByteArray(8 * 1024)
                var len: Int
                while (input.read(buffer).also { len = it } != -1) {
                    base64Stream.write(buffer, 0, len)
                }
            }
            output.toString("UTF-8")
        }
    }

    // Helper: Chuyển ByteArray thành Base64
    private fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)
}

@HiltViewModel
class GeminiViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val doctorRepository:DoctorRepository
) : ViewModel() {
    private val _question = MutableStateFlow("")
    val question: StateFlow<String> get() = _question

    private val _answer = MutableStateFlow("")
    val answer: StateFlow<String> get() = _answer

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> get() = _chatMessages

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> get() = _isSearching
    private suspend fun callGeminiWithRetry(request: GeminiRequest): String {
        var attempts = 0
        val maxAttempts = ApiKeyManager.getTotalKeys()

        while (attempts < maxAttempts) {
            val apiKey = ApiKeyManager.getCurrentKey()
            try {
                val response = RetrofitInstance.geminiService.askGemini(apiKey, request)

                if (response.isSuccessful) {
                    val candidates = response.body()?.candidates
                    val parts = candidates?.firstOrNull()?.content?.parts
                    val text = parts?.firstOrNull()?.text

                    if (!text.isNullOrBlank()) return text
                }

                // Xử lý lỗi theo mã code
                when (response.code()) {
                    401 -> { // Key hết hạn
                        ApiKeyManager.markKeyInvalid()
                    }
                    429 -> { // Rate limit
                        delay(2000L * (attempts + 1)) // backoff retry cùng key
                    }
                    else -> {
                        ApiKeyManager.rotateKey()
                    }
                }
            } catch (e: Exception) {
                Log.e("Gemini", "Network error with key=$apiKey: ${e.localizedMessage}")
                ApiKeyManager.rotateKey()
            }

            attempts++
        }

        return ""
    }

    fun processUserQuery(query: String) {
        _question.value = query
        _chatMessages.update { it + ChatMessage(message = query, isUser = true) }
        _isSearching.value = true

        viewModelScope.launch {
            try {
                val analysis = analyzeQueryWithAI(query)
                println("analyst la: "+analysis.toString())
                val jobs = mutableListOf<Job>()
                if (analysis.intent == "hỏi sức khoẻ") {
                    jobs+= launch {
                        handleGeneralHealthQuery(query, analysis)
                    }
                }
                if (analysis.doctorName.isNotBlank()) {
                    jobs += launch {
                        handleDoctorQuery(query, analysis)
                    }
                }
                if (analysis.specialty.isNotBlank()) {
                    jobs += launch {
                        handleSpecialtyQuery(query, analysis)
                    }
                }
                if (analysis.articleKeyword.isNotBlank()){
                    jobs += launch {
                        handleArticleQuery(query, analysis)
                    }
                }

                // Chờ tất cả job xong
                jobs.joinAll()
                println("Tra ve job"+jobs.toString())
            } catch (e: Exception) {
                _chatMessages.update {
                    it + ChatMessage("⚠️ Lỗi: ${e.localizedMessage}", isUser = false)
                }
            } finally {
                _isSearching.value = false
            }
        }
    }

    private suspend fun handleDoctorQuery(originalQuery: String, analysis: QueryAnalysis) {
        val doctors = searchDoctorByName(analysis.doctorName)
        if (doctors.isEmpty()) {
            _chatMessages.update {
                it + ChatMessage("❌ Không tìm thấy bác sĩ ${analysis.doctorName}.", isUser = false)
            }
            return
        }

        val prompt = """
        Người dùng hỏi: "$originalQuery"
        Đây là thông tin bác sĩ: 
        ${doctors.take(10).joinToString("\n") { "- ${it.name}, ${it.specialty}, ${it.hospital}" }}
        Hãy trả lời ngắn gọn, tập trung vào câu hỏi của người dùng.
    """.trimIndent()

        val response = askGeminiWithPrompt(prompt)+"Danh sách bác sĩ:"
        _chatMessages.update { it + ChatMessage(message = response, isUser = false) }

        doctors.take(10).forEach { doctor ->
            _chatMessages.update {
                it + ChatMessage(
                    message = "",
                    isUser = false,
                    type = MessageType.DOCTOR,
                    doctorId = doctor.id,
                    doctorName = doctor.name ?: "Không có tên", // Xử lý null
                    doctorSpecialty = (doctor.specialty ?: "Không có chuyên khoa") as Specialty?,
                    doctorAvatar = doctor.avatarURL ?: "" // Xử lý null cho avatar
                )
            }
        }
    }

    // Xử lý query về chuyên khoa
    private suspend fun handleSpecialtyQuery(originalQuery: String, analysis: QueryAnalysis) {
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

        doctors.take(5).forEach { doctor ->
            _chatMessages.update {
                it + ChatMessage(
                    doctorId = doctor.id,
                    doctorName = doctor.name,
                    doctorSpecialty = doctor.specialty,
                    doctorHospital = doctor.hospital,
                    doctorAvatar = doctor.avatarURL,
                    message = " ",
                    isUser = false,
                    type = MessageType.DOCTOR,
                )
            }
        }
    }// Xử lý câu hỏi về chuyên khoa
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
            Không cần chào hỏi lịch sử, chỉ cần làm đúng trọng tâm
            Hãy trả lời dựa trên thông tin thực tế về chuyên khoa ${analysis.specialty}:
            1. Giải thích ngắn gọn về chuyên khoa này
            2. Kết thúc bằng: "Danh sách bác sĩ trong chuyên khoa:"
            
        """.trimIndent()

        return askGeminiWithPrompt(responsePrompt)
    }


    // Xử lý query về bài viết
    private suspend fun handleArticleQuery(originalQuery: String, analysis: QueryAnalysis) {
        Log.d("GeminiViewModel", "Handling article query: ${analysis.articleKeyword}")
        val searchResponse = postRepository.searchAdvanced(analysis.articleKeyword)

        if (!searchResponse.isSuccessful) {
            _chatMessages.update {
                it + ChatMessage(
                    message = "Lỗi tìm kiếm bài viết: ${searchResponse.code()} - ${searchResponse.errorBody()?.string()}",
                    isUser = false
                )
            }
            return
        }

        val articles = searchResponse.body()?.take(5) ?: emptyList()

        Log.d("GeminiViewModel", "Found ${articles.size} articles")

        if (articles.isEmpty()) {
            _chatMessages.update {
                it + ChatMessage(
                    message = "Không tìm thấy bài viết về ${analysis.articleKeyword}.",
                    isUser = false
                )
            }
            return
        }

        val aiResponse = "Các bài viết liên quan:"
        _chatMessages.update { it + ChatMessage(message = aiResponse, isUser = false) }

        articles.forEach { article ->
            _chatMessages.update {
                it + ChatMessage(
                    message = (article.content?.take(80) ?: "Nội dung không có sẵn") + "...",
                    isUser = false,
                    type = MessageType.ARTICLE,
                    articleId = article.id,
                    articleImgUrl = article?.media?.firstOrNull(),
                    articleAuthor = article.userInfo?.name
                )
            }
        }
    }

    private suspend fun handleGeneralHealthQuery(originalQuery: String, analysis: QueryAnalysis) {
        val prompt = """      
        Bạn là một trợ lý y tế AI chuyên nghiệp và thân thiện, 
            nếu câu hỏi chung chung như tôi bị bệnh A, B, C thì hãy trả lời chi tiết            Tất cả câu hỏi của người dùng, nếu có thể, hãy trả về đầy đủ 5 trường thông tin trên, cố gắng tìm được bác sĩ có chuyên ngành tương đương, bài viết có từ khoá tương đương
            còn nếu câu hỏi chỉ 1 mục đích như "cho tôi biết bác sĩ, cho tôi tìm bài viết,..."
            thì không trả lời dài dòng mà chỉ nói " dưới đây là các phòng khám/ 
            bác sĩ phù hợp với yêu cầu của bạn","dưới đây là các bài vết phù hợp với yêu cầu
            của bạn:"
            Câu hỏi: "$originalQuery"
            - Chỉ trả lời về y tế & sức khoẻ.
            - Nếu không liên quan, nói: "Xin lỗi, tôi chỉ hỗ trợ về y tế và sức khoẻ."
            - Đưa ra lời khuyên dễ hiểu, khuyến cáo khám bác sĩ khi cần.
            - Không chẩn đoán chính xác, chỉ tư vấn sơ bộ.
            Trả lời bằng tiếng Việt.
    """.trimIndent()
        val response = askGeminiWithPrompt(prompt)
        _chatMessages.update { it + ChatMessage(message = response, isUser = false) }
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
        println("vao duoc ham")
        val analysisPrompt = """
            Phân tích câu hỏi người dùng và trích xuất thông tin theo format JSON:
            
            Câu hỏi: "$query"
            Tất cả câu hỏi của người dùng, nếu câu hỏi chung chung như tôi bị bệnh A, B, C thì hãy trả về đầy đủ 5 trường thông tin trên, 
            cố gắng tìm được bác sĩ có chuyên ngành tương đương, bài viết có từ khoá tương đương
            Còn nếu câu hỏi chỉ là tìm bác sĩ chữa bệnh A, tìm bài viết chủ đề B thì điền đúng trường đó, còn các trường còn lại bằng dấu rỗng
            Các chữ cái như hóa đổi thành hoá
            Nếu câu hỏi kiểu nhờ tư ấn thì trả về trường intent = "hỏi sức khoẻ"
            Chỉ trả về JSON, không giải thích thêm.
            Đây là JSON với các trường:
            - doctorName: tên bác sĩ (nếu có) - viết hoa chữ cái đầu
            - specialty: chuyên khoa (nếu có) 
            - articleKeyword: từ khóa bài viết (nếu có)
            - intent: mục đích (tìm bác sĩ, tìm chuyên khoa, tìm bài viết, tìm khoa bác sĩ, hỏi sức khoẻ)
            - remainingQuery: phần còn lại của câu hỏi sau khi tách thông tin
           
            Ví dụ:
            - "Bác sĩ Nguyễn Văn A làm việc ở đâu?" 
            → {"doctorName":"Nguyễn Văn A","specialty":"","articleKeyword":"","intent":"tìm bác sĩ","remainingQuery":"làm việc ở đâu"}
            
            - "Bác sĩ A ở khoa nào, bác sĩ A làm việc ở đâu, thông tin bác sĩ A"
            -> {"doctorName":"A","specialty":"","articleKeyword":"","intent":"tìm khoa bác sĩ","remainingQuery":"ở khoa nào"} 
            
            - "Tìm bài viết về tim mạch"
            → {"doctorName":"","specialty":"","articleKeyword":"tim mạch","intent":"tìm bài viết","remainingQuery":""}
            
            - "Khoa tim mạch có bác sĩ nào giỏi?"
            → {"doctorName":"","specialty":"tim mạch","articleKeyword":"","intent":"tìm chuyên khoa","remainingQuery":"có bác sĩ nào giỏi"}
            
            - "Tôi bị bệnh tiểu đường"
            → {"doctorName":"","specialty":"tim mạch, bài tiết, nội tiết","articleKeyword":"tiểu đường","intent":"hỏi sức khỏe","remainingQuery":""}
            
            - "Bệnh HIV là gì"
            → {"doctorName":"","specialty":"","articleKeyword":"","intent":"hỏi sức khỏe","remainingQuery":""}
        """.trimIndent()

        return try {
            println("vao duoc try ")
            val response = askGeminiWithPrompt(analysisPrompt)

            println("Phan hoi cua AI: "+ response)
            parseQueryAnalysisResponse(response)
        } catch (e: Exception) {
            Log.e("GeminiViewModel", "Error analyzing query: ${e.message}")
            QueryAnalysis(remainingQuery = query, intent = "hỏi sức khoẻ")
        }
    }


    // Parse JSON response từ AI
    private fun parseQueryAnalysisResponse(response: String): QueryAnalysis {
        return try {
            // Tìm JSON trong response
            val jsonStart = response.indexOf("{")
            val jsonEnd = response.lastIndexOf("}") + 1

            if (jsonStart == -1 || jsonEnd <= jsonStart) {
                return QueryAnalysis(remainingQuery = response, intent = "hỏi sức khoẻ")
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
            QueryAnalysis(remainingQuery = response, intent = "hỏi sức khoẻ")
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

    // Helper functions để tìm kiếm database
    private suspend fun searchDoctorByName(doctorName: String): List<GetDoctorResponse> {
        return try {
            val response = doctorRepository.getDoctorByName(doctorName)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun searchDoctorsBySpecialty(specialty: String): List<GetDoctorResponse> {
        return try {
            val response = doctorRepository.getDoctorBySpecialtyName(specialty)
            if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Gọi Gemini API
    private suspend fun askGeminiWithPrompt(prompt: String): String {
        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        var attempts = 0 // Đếm số lần thử
        val maxAttempts = ApiKeyManager.getTotalKeys() // Tổng số key khả dụng
        println("Total: "+maxAttempts)
        while (attempts < maxAttempts) {
            val apiKey = ApiKeyManager.getCurrentKey()
            try {
                val response = RetrofitInstance.geminiService.askGemini(apiKey, request)

                if (response.isSuccessful && !response.body()?.candidates.isNullOrEmpty()) {
                    // Nếu thành công, trả về kết quả
                    return response.body()!!.candidates.first().content.parts.first().text
                } else {
                    // Nếu thất bại, log lỗi và xoay vòng key
                    Log.e(
                        "GeminiViewModel",
                        "Lỗi hệ thống với API key: $apiKey - ${response.code()} ${response.errorBody()?.string()}"
                    )
                    ApiKeyManager.rotateKey()
                    attempts++
                }
            } catch (e: Exception) {
                // Nếu gặp lỗi kết nối, log lỗi và xoay vòng key
                Log.e("GeminiViewModel", "Lỗi kết nối với API key: $apiKey - ${e.localizedMessage}")
                ApiKeyManager.rotateKey()
                attempts++
            }
        }

        // Nếu đã thử hết tất cả các key mà vẫn thất bại
        Log.e("GeminiViewModel","Lỗi: Không thể kết nối với Gemini API sau khi thử tất cả các API key.")
        return ""
    }

}