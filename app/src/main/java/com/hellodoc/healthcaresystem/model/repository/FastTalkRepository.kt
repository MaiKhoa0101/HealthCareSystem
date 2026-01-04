package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.FastTalkService
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.QuickResponseDao
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.QuickResponseEntity
import jakarta.inject.Inject

class FastTalkRepository @Inject constructor(
    private val fastTalkService: FastTalkService,
    private val quickResponseDao: QuickResponseDao
){
    suspend fun getWordSimilar(word: String) = fastTalkService.getWordSimilar(word)

    suspend fun getWordByLabel(
        word: String,
        label: String,
        toLabel: String
    ) = fastTalkService.getWordByLabel(word, label, toLabel)

    suspend fun analyzeQuestion(text: String) = fastTalkService.analyzeQuestion(text)

    // Quick Response functions
    suspend fun insertQuickResponse(question: String, answer: String) {
        val entity = QuickResponseEntity(
            question = question,
            response = answer
        )
        quickResponseDao.insert(entity)
    }

    suspend fun deleteQuickResponse(quickResponse: QuickResponseEntity) =
        quickResponseDao.delete(quickResponse)

    suspend fun findQuickResponse(question: String) =
        quickResponseDao.findResponse(question)
}