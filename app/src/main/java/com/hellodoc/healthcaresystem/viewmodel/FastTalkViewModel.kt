package com.hellodoc.healthcaresystem.viewmodel

import androidx.lifecycle.ViewModel
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Word
import com.hellodoc.healthcaresystem.model.repository.FastTalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FastTalkViewModel @Inject constructor(
    private val fastTalkRepository: FastTalkRepository
) : ViewModel() {
    private val _wordVerbSimilar = MutableStateFlow<List<Word>>(emptyList())
    val wordVerbSimilar: StateFlow<List<Word>> get() = _wordVerbSimilar

    private val _wordNounSimilar = MutableStateFlow<List<Word>>(emptyList())
    val wordNounSimilar: StateFlow<List<Word>> get() = _wordNounSimilar

    private val _wordSupportSimilar = MutableStateFlow<List<Word>>(emptyList())
    val wordSupportSimilar: StateFlow<List<Word>> get() = _wordSupportSimilar

    private val _wordPronounSimilar = MutableStateFlow<List<Word>>(emptyList())
    val wordPronounSimilar: StateFlow<List<Word>> get() = _wordPronounSimilar

    suspend fun getWordSimilar(word: String) {
        try {
            val response = fastTalkRepository.getWordSimilar(word)
            println("Goi get word với ket qua: " + response.body())
            if (!response.isSuccessful) {
                println("Lỗi API: ${response.code()}")
                return
            }
            val data = response.body()?.nodes ?: emptyList()
            println("Data lay duoc là "+data)

            // Nhóm theo yêu cầu
            val nounTags = setOf("n", "np", "nc", "nu", "ny", "nb")
            val verbTags = setOf("v", "vb", "vy", "a", "ab")
            val pronounTags = setOf("p")

            val verbs = mutableListOf<Word>()
            val nouns = mutableListOf<Word>()
            val support = mutableListOf<Word>()
            val pronouns = mutableListOf<Word>()

            data.forEach { wordItem ->
                val tags = wordItem.label.map { it.lowercase() }
                when {
                    tags.any { nounTags.contains(it) } -> nouns.add(wordItem)
                    tags.any { verbTags.contains(it) } -> verbs.add(wordItem)
                    tags.any { pronounTags.contains(it) } -> pronouns.add(wordItem)
                    else -> support.add(wordItem)
                }
            }

            // ✅ Cập nhật các nhóm có dữ liệu
            _wordVerbSimilar.value = verbs
            _wordNounSimilar.value = nouns
            _wordSupportSimilar.value = support
            _wordPronounSimilar.value = pronouns

            // ✅ Kiểm tra và tìm kiếm cho các nhóm bị rỗng
            if (verbs.isEmpty()) {
                println("Nhóm động từ rỗng, tìm kiếm thêm...")
                getWordByLabelUntilFound(word, "v", "v", "verb")
            }
            if (nouns.isEmpty()) {
                println("Nhóm danh từ rỗng, tìm kiếm thêm...")
                getWordByLabelUntilFound(word, "n", "n", "noun")
            }
            if (support.isEmpty()) {
                println("Nhóm tính từ rỗng, tìm kiếm thêm...")
                getWordByLabelUntilFound(word, "a", "a", "support")
            }
            if (pronouns.isEmpty()) {
                println("Nhóm đại từ rỗng, tìm kiếm thêm...")
                getWordByLabelUntilFound(word, "p", "p", "pronoun")
            }

        } catch (e: Exception) {
            println("Lỗi: ${e.message}")
        }
    }

    // ✅ Hàm tìm kiếm đệ quy cho đến khi tìm được dữ liệu
    private suspend fun getWordByLabelUntilFound(
        word: String,
        label: String,
        toLabel: String,
        groupType: String,
        depth: Int = 0,
        maxDepth: Int = 5 // Giới hạn độ sâu để tránh vòng lặp vô hạn
    ) {
        if (depth >= maxDepth) {
            println("Đã đạt giới hạn tìm kiếm cho nhóm $groupType")
            return
        }

        try {
            val response = fastTalkRepository.getWordByLabel(word, label, toLabel)
            if (!response.isSuccessful) {
                println("Lỗi API getWordByLabel: ${response.code()}")
                return
            }

            val data = response.body()?.nodes ?: emptyList()
            println("Data tìm được cho $groupType (depth $depth): $data")

            if (data.isEmpty()) {
                println("Không tìm được dữ liệu cho $groupType, thử lại với từ khác...")
                // Có thể thử với các biến thể khác của label hoặc dừng lại
                return
            }

            // Nhóm theo yêu cầu
            val nounTags = setOf("n", "np", "nc", "nu", "ny", "nb")
            val verbTags = setOf("v", "vb", "vy", "a", "ab")
            val pronounTags = setOf("p")

            val verbs = mutableListOf<Word>()
            val nouns = mutableListOf<Word>()
            val adjectives = mutableListOf<Word>()
            val pronouns = mutableListOf<Word>()

            data.forEach { wordItem ->
                val tags = wordItem.label.map { it.lowercase() }
                when {
                    tags.any { nounTags.contains(it) } -> nouns.add(wordItem)
                    tags.any { verbTags.contains(it) } -> verbs.add(wordItem)
                    tags.any { pronounTags.contains(it) } -> pronouns.add(wordItem)
                    else -> adjectives.add(wordItem)
                }
            }

            // ✅ Cập nhật nhóm tương ứng nếu tìm được dữ liệu
            when (groupType) {
                "verb" -> {
                    if (verbs.isNotEmpty()) {
                        _wordVerbSimilar.value = verbs
                        println("✓ Đã tìm được ${verbs.size} động từ")
                    } else {
                        // Tìm tiếp với từ đầu tiên trong kết quả
                        val nextWord = data.firstOrNull()?.suggestion
                        if (nextWord != null) {
                            println("Tìm tiếp với từ: $nextWord")
                            getWordByLabelUntilFound(nextWord, label, toLabel, groupType, depth + 1, maxDepth)
                        }
                    }
                }
                "noun" -> {
                    if (nouns.isNotEmpty()) {
                        _wordNounSimilar.value = nouns
                        println("✓ Đã tìm được ${nouns.size} danh từ")
                    } else {
                        val nextWord = data.firstOrNull()?.suggestion
                        if (nextWord != null) {
                            println("Tìm tiếp với từ: $nextWord")
                            getWordByLabelUntilFound(nextWord, label, toLabel, groupType, depth + 1, maxDepth)
                        }
                    }
                }
                "support" -> {
                    if (adjectives.isNotEmpty()) {
                        _wordSupportSimilar.value = adjectives
                        println("✓ Đã tìm được ${adjectives.size} tính từ")
                    } else {
                        val nextWord = data.firstOrNull()?.suggestion
                        if (nextWord != null) {
                            println("Tìm tiếp với từ: $nextWord")
                            getWordByLabelUntilFound(nextWord, label, toLabel, groupType, depth + 1, maxDepth)
                        }
                    }
                }
                "pronoun" -> {
                    if (pronouns.isNotEmpty()) {
                        _wordPronounSimilar.value = pronouns
                        println("✓ Đã tìm được ${pronouns.size} đại từ")
                    } else {
                        val nextWord = data.firstOrNull()?.suggestion
                        if (nextWord != null) {
                            println("Tìm tiếp với từ: $nextWord")
                            getWordByLabelUntilFound(nextWord, label, toLabel, groupType, depth + 1, maxDepth)
                        }
                    }
                }
            }

        } catch (e: Exception) {
            println("Lỗi khi tìm $groupType: ${e.message}")
        }
    }

    suspend fun analyzeQuestion(text: String)
    {
        try {
            val response = fastTalkRepository.analyzeQuestion(text)
            if (!response.isSuccessful) {
                println("Lỗi API getWordByLabel: ${response.code()}")
            }
            else{
                val data = response.body()
                println("Data tìm được cho $data")
                //Pronounce sẽ được trả về cho nên phải thêm nó vào tập pro
                if(data?.label?.contains("P") == true) {
                    //nếu pro đã có từ này thì tăng score nó lên cao nhất
                    val currentPronouns = _wordPronounSimilar.value
                    val existingPronoun = currentPronouns.find { it.suggestion == data.suggestion }
                    if (existingPronoun != null) {
                        val updatedPronouns = currentPronouns.map {
                            if (it == existingPronoun) {
                                it.copy(score = it.score + data.score)
                            } else {
                                it
                            }
                        }
                    }
                    //nếu pro chưa có từ này thì thêm nó vào
                    else{
                        val updatedPronouns = currentPronouns.toMutableList()
                        updatedPronouns.add(data)
                        _wordPronounSimilar.value = updatedPronouns
                    }
                }
            }
        }
        catch (e: Exception) {
            println("Lỗi khi tìm : ${e.message}")
        }

    }




}