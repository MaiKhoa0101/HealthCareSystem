package com.hellodoc.healthcaresystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Neo4jResultItem
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResult
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResultResponse
import com.hellodoc.healthcaresystem.model.repository.FastTalkRepository
import com.hellodoc.healthcaresystem.model.repository.SettingsRepository
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.WordGraphDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.Neo4jPath
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.WordEdgeEntity
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.WordEntity
import com.hellodoc.healthcaresystem.view.user.supportfunction.JsonAssetHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.isNotEmpty
import kotlin.collections.toMutableList


@HiltViewModel
class FastTalkViewModel @Inject constructor(
    private val fastTalkRepository: FastTalkRepository,
    private val jsonAssetHelper: JsonAssetHelper, // <--- 1. Inject Helper mới
    private val settingsRepository: SettingsRepository // <--- 1. Inject thêm cái này
) : ViewModel() {

    // Sử dụng WordResult thay vì Word cũ
    private val _wordVerbSimilar = MutableStateFlow<List<WordResult>>(emptyList())
    val wordVerbSimilar: StateFlow<List<WordResult>> get() = _wordVerbSimilar

    private val _wordNounSimilar = MutableStateFlow<List<WordResult>>(emptyList())
    val wordNounSimilar: StateFlow<List<WordResult>> get() = _wordNounSimilar

    private val _wordSupportSimilar = MutableStateFlow<List<WordResult>>(emptyList())
    val wordSupportSimilar: StateFlow<List<WordResult>> get() = _wordSupportSimilar

    private val _wordPronounSimilar = MutableStateFlow<List<WordResult>>(emptyList())
    val wordPronounSimilar: StateFlow<List<WordResult>> get() = _wordPronounSimilar


    // Trạng thái loading để UI hiển thị vòng xoay
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun downloadDataFromNeo4j() {
        viewModelScope.launch {
            val currentSettings = settingsRepository.appSettings.first()

            if (currentSettings.isDataDownloaded) {
                println("⚠️ Dữ liệu đã được tải trước đó. Bỏ qua.")
                return@launch
            }
            println("Dữ liệu chưa được tải, bắt đầu tải dữ liệu từ Server...")

            _isLoading.value = true
            try {
                // Hàm này trả về Response<List<WordResultResponse>>
                val response = fastTalkRepository.getGraphData()

                if (response.isSuccessful) {
                    val data = response.body() // Kiểu: List<WordResultResponse>?

                    if (!data.isNullOrEmpty()) {
                        println("Tải thành công: ${data.size} items")

                        // Gọi hàm save đã sửa ở Repository
                        fastTalkRepository.saveNeo4jDataToRoom(data)

                        settingsRepository.setDataDownloaded(true)
                        println("✅ Lưu dữ liệu và cập nhật trạng thái thành công!")
                    } else {
                        println("⚠️ Data body is null or empty")
                    }
                } else {
                    println("❌ Lỗi API: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                println("❌ Lỗi Exception khi tải Neo4j: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 1. Hàm đọc file (Logic chính)
    fun readFromLocalFile() {
        viewModelScope.launch(Dispatchers.IO) {
            // Kiểm tra cài đặt xem đã tải chưa
            val isDownloaded = settingsRepository.appSettings.first().isDataDownloaded

            if (!isDownloaded) {
                println("📂 Bắt đầu đọc file local 'fulldata.json'...")

                try {
                    // 1. Đọc dữ liệu thô (List<Neo4jResultItem>)
                    // Giả định hàm getLocalNeo4jData đã viết đúng và trả về List
                    val rawData = jsonAssetHelper.getLocalNeo4jData("fulldata.json")

                    if (!rawData.isNullOrEmpty()) {
                        println("📂 Đã đọc được ${rawData.size} path thô từ JSON.")

                        // 2. CHUYỂN ĐỔI (MAPPING) AN TOÀN
                        val flatList = mapComplexJsonToFlat(rawData)

                        if (flatList.isNotEmpty()) {
                            println("🔄 Đã chuyển đổi thành công sang ${flatList.size} item phẳng.")

                            // 3. Lưu vào Room
                            // Gọi hàm saveNeo4jDataToRoom trong Repository (hàm này đã xử lý check null ở các bước trước)
                            fastTalkRepository.saveNeo4jDataToRoom(flatList)

                            // 4. Cập nhật trạng thái
                            settingsRepository.setDataDownloaded(true)
                            println("✅ IMPORT THÀNH CÔNG! Dữ liệu đã sẵn sàng offline.")

                        } else {
                            println("⚠️ Danh sách sau khi map bị rỗng (Check lại cấu trúc JSON).")
                        }
                    } else {
                        println("❌ File rỗng hoặc không đọc được.")
                    }
                } catch (e: Exception) {
                    println("❌ Lỗi nghiêm trọng khi đọc file local: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                println("ℹ️ Dữ liệu đã có sẵn trong máy, không cần nạp lại.")
            }
        }
    }

    // 2. Hàm chuyển đổi dữ liệu (FIX LỖI NULL POINTER Ở ĐÂY)
    private fun mapComplexJsonToFlat(complexList: List<Neo4jResultItem>): List<WordResultResponse> {
        val result = mutableListOf<WordResultResponse>()

        complexList.forEach { item ->
            // Sử dụng safe call (?.) cho pathData vì nó có thể null
            item.pathData?.segments?.forEach { segment ->

                // --- LẤY DỮ LIỆU AN TOÀN ---
                // Dùng ?. để truy cập, nếu đoạn nào null thì trả về null chứ không crash
                val startProps = segment.startNode?.properties
                val endProps = segment.endNode?.properties
                val relProps = segment.relationship?.properties

                val sName = startProps?.name
                val eName = endProps?.name

                // --- CHỈ LẤY KHI CÓ ĐỦ TÊN 2 ĐẦU ---
                if (!sName.isNullOrEmpty() && !eName.isNullOrEmpty()) {

                    // Lấy Label đầu tiên, nếu không có thì gán "Unknown"
                    val sLabel = segment.startNode?.labels?.firstOrNull() ?: "Unknown"
                    val eLabel = segment.endNode?.labels?.firstOrNull() ?: "Unknown"

                    // Lấy Weight, nếu null thì gán 0.0
                    val w = relProps?.weight ?: 0.0

                    // Lấy Type quan hệ
                    val rType = segment.relationship?.type ?: "Related_To"

                    // Tạo object phẳng
                    val convertedItem = WordResultResponse(
                        startNode = sName,
                        startLabel = sLabel,
                        endNode = eName,
                        endLabel = eLabel,
                        weight = w,
                        relType = rType
                    )
                    result.add(convertedItem)
                }
            }
        }
        return result
    }


    // Hàm gọi chính
    fun getWordSimilar(word: String) {
        viewModelScope.launch {
            try {

                if (!fastTalkRepository.isOnline()) {
                    println("🌐 Đang offline'")
                } else {
                    println("🌐 Đang Online: Gọi API cho từ '$word'")

                    //Thêm data từ assets/datatest.json
                    val response = fastTalkRepository.getWordSimilar(word)

                    if (!response.isSuccessful || response.body()?.success == false) {
                        println("API Fail hoặc Success=false: ${response.code()}")
                        return@launch
                    }

                    // Lấy results từ JSON
                    val data = response.body()?.results ?: emptyList()
                    println("Data nhận được: ${data.size} từ")

                    categorizeWords(data, word)
                }
            } catch (e: Exception) {
                println("Lỗi Exception: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Tách logic phân loại ra hàm riêng cho gọn
    private suspend fun categorizeWords(data: List<WordResult>, originalWord: String) {
        val verbs = mutableListOf<WordResult>()
        val nouns = mutableListOf<WordResult>()
        val support = mutableListOf<WordResult>()
        val pronouns = mutableListOf<WordResult>()

        data.forEach { item ->
            // posTag từ JSON ví dụ: "R", "V", "Ny", "N"
            val tag = item.posTag.uppercase()

            when {
                // Danh từ: Bắt đầu bằng N (N, Np, Nc, Nu, Ny...)
                tag.startsWith("N") -> nouns.add(item)

                // Động từ: Bắt đầu bằng V
                tag.startsWith("V") -> verbs.add(item)

                // Đại từ: P
                tag == "P" -> pronouns.add(item)

                // Các loại khác vào Support (Tính từ A, Trạng từ R, Kết từ C...)
                // Ví dụ: "sẽ" (R), "đã" (R), "rất" (R) sẽ vào đây
                else -> support.add(item)
            }
        }

        _wordVerbSimilar.value = verbs
        _wordNounSimilar.value = nouns
        _wordSupportSimilar.value = support
        _wordPronounSimilar.value = pronouns

        // Kiểm tra rỗng và tìm kiếm đệ quy (Fallback)
        // Lưu ý: toLabel phải khớp với quy ước của Backend (ví dụ 'V', 'N', 'A')
        if (verbs.isEmpty()) searchFallback(originalWord, "V", "verb")
        if (nouns.isEmpty()) searchFallback(originalWord, "N", "noun")
        if (support.isEmpty()) searchFallback(originalWord, "A", "support") // Giả sử A đại diện support
        if (pronouns.isEmpty()) searchFallback(originalWord, "P", "pronoun")
    }

    private suspend fun searchFallback(word: String, toLabel: String, groupType: String, depth: Int = 0) {
        if (depth >= 3) return // Giảm depth xuống 3 cho đỡ lag

        try {
            val response = fastTalkRepository.getWordByLabel(word, toLabel)
            val data = response.body()?.results ?: emptyList()

            if (data.isNotEmpty()) {
                when (groupType) {
                    "verb" -> _wordVerbSimilar.value = data
                    "noun" -> _wordNounSimilar.value = data
                    "support" -> _wordSupportSimilar.value = data
                    "pronoun" -> _wordPronounSimilar.value = data
                }
                println("Fallback thành công cho $groupType với ${data.size} từ")
            } else {
                // Nếu vẫn rỗng, thử tìm tiếp dựa trên từ gợi ý đầu tiên của lần gọi trước (nếu có logic đó)
                // Ở đây tôi tạm dừng để tránh spam API
            }
        } catch (e: Exception) {
            println("Lỗi Fallback $groupType: ${e.message}")
        }
    }

    fun analyzeSentence(text: String) {
        viewModelScope.launch {
            try {
                // API analyze trả về gì?
                // Nếu nó trả về structure giống getWordSimilar, ta xử lý tương tự
                val response = fastTalkRepository.analyzeQuestion(text)

                // Giả sử logic là tìm đại từ trong câu hỏi để thêm vào danh sách Pronoun
                val results = response.body()?.results ?: emptyList()

                println("Analyze: $results")

                val foundPronouns = results.filter { it.posTag == "P" }

                if (foundPronouns.isNotEmpty()) {
                    val currentList = _wordPronounSimilar.value.toMutableList()
                    // Merge logic: Thêm vào hoặc update score
                    foundPronouns.forEach { p ->
                        val exists = currentList.indexOfFirst { it.word == p.word }
                        if (exists != -1) {
                            // Update score (ví dụ cộng thêm)
                            val old = currentList[exists]
                            currentList[exists] = old.copy(score = old.score + p.score)
                        } else {
                            currentList.add(p)
                        }
                    }
                    _wordPronounSimilar.value = currentList
                }

            } catch (e: Exception) {
                println("Lỗi Analyze: ${e.message}")
            }
        }
    }

    private val _quickResponse = MutableStateFlow<List<String>>(emptyList())
    val quickResponse: StateFlow<List<String>> = _quickResponse

    fun findQuickResponse(question: String) { // ✅ Bỏ return type
        viewModelScope.launch { // ✅ Thêm viewModelScope.launch
            println("🔍 Tìm kiếm với question: '$question'")
            try {
                val result = fastTalkRepository.findQuickResponse(question)
                println("📝 Kết quả tìm được: $result")
                _quickResponse.value = result
            } catch (e: Exception) {
                println("❌ Lỗi khi tìm: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // Lưu câu hỏi-trả lời
    suspend fun insertQuickResponse(question: String, answer: String) {
        println("🔵 Đang lưu: Question='$question', Answer='$answer'")
        try {
            fastTalkRepository.insertQuickResponse(question, answer)
            println("✅ Lưu thành công")

            // Kiểm tra ngay sau khi lưu
            val saved = fastTalkRepository.findQuickResponse(question)
            println("🔍 Tìm lại ngay sau khi lưu: $saved")
        } catch (e: Exception) {
            println("❌ Lỗi khi lưu: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun createQuestionAnswer(question: String, answer: String) = fastTalkRepository.createQuestionAnswer(question, answer)


    // Hàm lấy dự đoán (kết nối với UI)
    fun getPredictions(word: String) = fastTalkRepository.getPredictions(word)
}