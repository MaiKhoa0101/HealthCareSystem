package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.FastTalkService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Word
import jakarta.inject.Inject
import retrofit2.Response

class FastTalkRepository @Inject constructor(
    private val fastTalkService: FastTalkService
){
    suspend fun getWordSimilar(word: String)= fastTalkService.getWordSimilar(word)
    suspend fun getWordByLabel(
        word: String,
        label: String,
        toLabel: String
    ) = fastTalkService.getWordByLabel(
        word,
        label,
        toLabel
    )

    suspend fun analyzeQuestion(text: String) = fastTalkService.analyzeQuestion(text)


}
