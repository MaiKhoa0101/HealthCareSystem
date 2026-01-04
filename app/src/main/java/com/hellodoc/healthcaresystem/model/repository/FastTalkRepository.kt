package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.FastTalkService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AnalyzeRequest
import jakarta.inject.Inject
import retrofit2.Response
class FastTalkRepository @Inject constructor(
    private val fastTalkService: FastTalkService
){
    suspend fun getWordSimilar(word: String) = fastTalkService.getWordSimilar(word)

    suspend fun getWordByLabel(word: String, toLabel: String) =
        fastTalkService.getWordByLabel(word, toLabel) // Backend của bạn có vẻ không cần fromLabel ở API này

    suspend fun analyzeQuestion(text: String) =
        fastTalkService.analyzeQuestion(AnalyzeRequest(text))
}