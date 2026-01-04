package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.FastTalkService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AnalyzeRequest
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.QuickResponseDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.QuickResponseEntity
import jakarta.inject.Inject
import retrofit2.Response
class FastTalkRepository @Inject constructor(
    private val fastTalkService: FastTalkService,
    private val quickResponseDao: QuickResponseDao
){
    suspend fun getWordSimilar(word: String) = fastTalkService.getWordSimilar(word)

    // Quick Response functions
    suspend fun insertQuickResponse(question: String, answer: String) {
        val entity = QuickResponseEntity(
            question = question,
            response = answer
        )
        quickResponseDao.insert(entity)
    }
    suspend fun getWordByLabel(word: String, toLabel: String) =
        fastTalkService.getWordByLabel(word, toLabel)

    suspend fun deleteQuickResponse(quickResponse: QuickResponseEntity) =
        quickResponseDao.delete(quickResponse)

    suspend fun findQuickResponse(question: String): List<String> =
        quickResponseDao.findByQuestion(question)

    suspend fun analyzeQuestion(text: String) =
        fastTalkService.analyzeQuestion(AnalyzeRequest(text))
}