package com.hellodoc.healthcaresystem.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResult
import com.hellodoc.healthcaresystem.model.repository.FastTalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.toMutableList


@HiltViewModel
class FastTalkViewModel @Inject constructor(
    private val fastTalkRepository: FastTalkRepository
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

    // Hàm gọi chính
    fun getWordSimilar(word: String) {
        viewModelScope.launch {
            try {
                val response = fastTalkRepository.getWordSimilar(word)

                if (!response.isSuccessful || response.body()?.success == false) {
                    println("API Fail hoặc Success=false: ${response.code()}")
                    return@launch
                }

                // Lấy results từ JSON
                val data = response.body()?.results ?: emptyList()
                println("Data nhận được: ${data.size} từ")

                categorizeWords(data, word)

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
}