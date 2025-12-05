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

    private val _wordAdjectiveSimilar = MutableStateFlow<List<Word>>(emptyList())
    val wordAdjectiveSimilar: StateFlow<List<Word>> get() = _wordAdjectiveSimilar

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
            println("DÂT lay duoc là "+data)
            if (data.isEmpty()) {
                _wordVerbSimilar.value = emptyList()
                _wordNounSimilar.value = emptyList()
                _wordAdjectiveSimilar.value = emptyList()
                _wordPronounSimilar.value = emptyList()
                return
            }

            // Nhóm theo yêu cầu
            val nounTags = setOf("n", "np", "nc", "nu", "ny", "nb")
            val verbTags = setOf("v", "vb", "vy", "a", "ab")   // động từ + tính từ
            val pronounTags = setOf("p")

            val verbs = mutableListOf<Word>()
            val nouns = mutableListOf<Word>()
            val adjectives = mutableListOf<Word>()   // nhóm "other"
            val pronouns = mutableListOf<Word>()

            data.forEach { wordItem ->
                val tags = wordItem.label.map { it.lowercase() }

                when {
                    tags.any { nounTags.contains(it) } -> nouns.add(wordItem)
                    tags.any { verbTags.contains(it) } -> verbs.add(wordItem)
                    tags.any { pronounTags.contains(it) } -> pronouns.add(wordItem)
                    else -> adjectives.add(wordItem) // Nhóm "các loại từ khác"
                }
            }

            _wordVerbSimilar.value = verbs
            _wordNounSimilar.value = nouns
            _wordAdjectiveSimilar.value = adjectives
            _wordPronounSimilar.value = pronouns

        } catch (e: Exception) {
            println("Lỗi: ${e.message}")
        }
    }


}