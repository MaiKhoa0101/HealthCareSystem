package com.hellodoc.healthcaresystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.CategorizedWords
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Neo4jResultItem
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.QA
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordCategory
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResult
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResultResponse
import com.hellodoc.healthcaresystem.model.repository.FastTalkRepository
import com.hellodoc.healthcaresystem.model.repository.SettingsRepository
import com.hellodoc.healthcaresystem.view.user.home.fasttalk.parseTokenJson
import com.hellodoc.healthcaresystem.view.user.supportfunction.JsonAssetHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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

    private val _quickResponse = MutableStateFlow<List<String>>(emptyList())
    val quickResponse: StateFlow<List<String>> get() = _quickResponse


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
    // ========== PUBLIC METHODS ==========

    fun getWordSimilar(word: String) {
        viewModelScope.launch {
            try {
                if (!fastTalkRepository.isOnline()) {
                    println("🌐 Đang offline")
                    return@launch
                }

                println("🌐 Đang Online: Gọi API cho từ '$word'")
                val response = fastTalkRepository.getWordSimilar(word)

                if (!response.isSuccessful || response.body()?.success == false) {
                    println("API Fail hoặc Success=false: ${response.code()}")
                    return@launch
                }

                val data = response.body()?.results ?: emptyList()
                println("Data nhận được: ${data.size} từ")

                categorizeAndUpdateWords(data, word, shouldMerge = false)
            } catch (e: Exception) {
                handleException("getWordSimilar", e)
            }
        }
    }

    fun analyzeSentence(text: String) {
        viewModelScope.launch {
            try {
                val response = fastTalkRepository.analyzeQuestion(text)

                if (!response.isSuccessful || response.body()?.success == false) {
                    println("Analyze API fail")
                    return@launch
                }

                val body = response.body()!!
                val tokens = parseTokenJson(body.answer_tokens_json)
                val posTags = parseTokenJson(body.answer_posTags_json)
                val size = minOf(tokens.size, posTags.size)

                val results = (0 until size).map { index ->
                    WordResult(
                        word = tokens[index],
                        score = 1.0,
                        posTag = posTags[index],
                        relationType = "sentence"
                    )
                }

                categorizeAndUpdateWords(results, shouldMerge = true)
                logCategorizedWords(results)
            } catch (e: Exception) {
                handleException("analyzeSentence", e)
            }
        }
    }

    fun findQuickResponse(question: String) {
        viewModelScope.launch {
            println("🔍 Tìm kiếm với question: '$question'")
            try {
                val result = fastTalkRepository.findQuickResponse(question)
                println("📝 Kết quả tìm được: $result")
                _quickResponse.value = result
            } catch (e: Exception) {
                handleException("findQuickResponse", e)
            }
        }
    }

    suspend fun insertQuickResponse(question: String, answer: String) {
        println("🔵 Đang lưu: Question='$question', Answer='$answer'")
        try {
            fastTalkRepository.insertQuickResponse(question, answer)
            println("✅ Lưu thành công")

            // Kiểm tra ngay sau khi lưu
            val saved = fastTalkRepository.findQuickResponse(question)
            println("🔍 Tìm lại ngay sau khi lưu: $saved")
        } catch (e: Exception) {
            handleException("insertQuickResponse", e)
        }
    }

    suspend fun updateQA(request: QA) {
        try {
            val response = fastTalkRepository.processQuestionAnswer(request)
            if (response.isSuccessful) {
                println("✅ Lưu question và answer thành công")
            } else {
                println("❌ Lỗi khi lưu question và answer: ${response.code()}")
            }
        } catch (e: Exception) {
            handleException("updateQA", e)
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Phân loại từ theo POS tag và cập nhật StateFlow
     * @param shouldMerge: true = merge với dữ liệu cũ, false = thay thế hoàn toàn
     */
    private suspend fun categorizeAndUpdateWords(
        words: List<WordResult>,
        originalWord: String? = null,
        shouldMerge: Boolean
    ) {
        val categorized = categorizeWordsByPosTag(words)

        if (shouldMerge) {
            // Merge với dữ liệu cũ
            _wordVerbSimilar.value = mergeWordLists(_wordVerbSimilar.value, categorized.verbs)
            _wordNounSimilar.value = mergeWordLists(_wordNounSimilar.value, categorized.nouns)
            _wordSupportSimilar.value = mergeWordLists(_wordSupportSimilar.value, categorized.support)
            _wordPronounSimilar.value = mergeWordLists(_wordPronounSimilar.value, categorized.pronouns)
        } else {
            // Thay thế hoàn toàn
            _wordVerbSimilar.value = categorized.verbs
            _wordNounSimilar.value = categorized.nouns
            _wordSupportSimilar.value = categorized.support
            _wordPronounSimilar.value = categorized.pronouns

            // Chỉ gọi fallback khi không merge và có originalWord
            originalWord?.let { word ->
                executeFallbackIfNeeded(categorized, word)
            }
        }
    }

    /**
     * Phân loại danh sách từ theo POS tag
     */
    private fun categorizeWordsByPosTag(words: List<WordResult>): CategorizedWords {
        val verbs = mutableListOf<WordResult>()
        val nouns = mutableListOf<WordResult>()
        val support = mutableListOf<WordResult>()
        val pronouns = mutableListOf<WordResult>()

        words.forEach { item ->
            val tag = item.posTag.uppercase()
            when {
                tag.startsWith("N") -> nouns.add(item)
                tag.startsWith("V") -> verbs.add(item)
                tag == "P" -> pronouns.add(item)
                else -> support.add(item)
            }
        }

        return CategorizedWords(verbs, nouns, support, pronouns)
    }

    /**
     * Merge hai danh sách từ (ưu tiên từ mới)
     */
    private fun mergeWordLists(
        oldList: List<WordResult>,
        newList: List<WordResult>
    ): List<WordResult> {
        val map = oldList.associateBy { it.word }.toMutableMap()
        newList.forEach { map[it.word] = it }
        return map.values.toList()
    }

    /**
     * Thực hiện fallback cho các category rỗng
     */
    private suspend fun executeFallbackIfNeeded(categorized: CategorizedWords, word: String) {
        if (categorized.verbs.isEmpty()) {
            searchFallback(word, "V", WordCategory.VERB)
        }
        if (categorized.nouns.isEmpty()) {
            searchFallback(word, "N", WordCategory.NOUN)
        }
        if (categorized.support.isEmpty()) {
            searchFallback(word, "A", WordCategory.SUPPORT)
        }
        if (categorized.pronouns.isEmpty()) {
            searchFallback(word, "P", WordCategory.PRONOUN)
        }
    }

    private suspend fun searchFallback(
        word: String,
        toLabel: String,
        category: WordCategory,
        depth: Int = 0
    ) {
        if (depth >= 3) return // Giới hạn độ sâu để tránh lag

        try {
            val response = fastTalkRepository.getWordByLabel(word, toLabel)
            val data = response.body()?.results ?: emptyList()

            if (data.isNotEmpty()) {
                updateStateFlowByCategory(category, data)
                println("✅ Fallback thành công cho ${category.displayName} với ${data.size} từ")
            }
        } catch (e: Exception) {
            println("❌ Lỗi Fallback ${category.displayName}: ${e.message}")
        }
    }

    private fun updateStateFlowByCategory(category: WordCategory, data: List<WordResult>) {
        when (category) {
            WordCategory.VERB -> _wordVerbSimilar.value = data
            WordCategory.NOUN -> _wordNounSimilar.value = data
            WordCategory.SUPPORT -> _wordSupportSimilar.value = data
            WordCategory.PRONOUN -> _wordPronounSimilar.value = data
        }
    }

    private fun logCategorizedWords(words: List<WordResult>) {
        val categorized = categorizeWordsByPosTag(words)
        println("✅ Added from sentence:")
        println("VERB=${categorized.verbs.map { it.word }}")
        println("NOUN=${categorized.nouns.map { it.word }}")
        println("PRON=${categorized.pronouns.map { it.word }}")
        println("ADV=${categorized.support.map { it.word }}")
    }

    private fun handleException(functionName: String, e: Exception) {
        println("❌ Lỗi $functionName: ${e.message}")
        e.printStackTrace()
    }

}