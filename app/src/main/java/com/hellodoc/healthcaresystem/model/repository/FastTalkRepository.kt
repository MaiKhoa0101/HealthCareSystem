package com.hellodoc.healthcaresystem.model.repository

import androidx.room.withTransaction
import com.hellodoc.healthcaresystem.model.api.FastTalkService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AnalyzeRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResult
import com.hellodoc.healthcaresystem.model.networksupport.NetworkHelper
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.QuickResponseDao
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.WordGraphDao
import com.hellodoc.healthcaresystem.model.roomDb.data.database.AppDatabase
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.* // Import hết entity
import javax.inject.Inject // Lưu ý: Dùng javax.inject hoặc jakarta.inject tuỳ version Hilt

class FastTalkRepository @Inject constructor(
    private val networkHelper: NetworkHelper, // <--- 1. Inject thêm NetworkHelper
    private val fastTalkService: FastTalkService,
    private val quickResponseDao: QuickResponseDao,
    private val wordDao: WordGraphDao,
    private val database: AppDatabase // <--- 1. Inject Database vào đây
) {

    fun isOnline(): Boolean = networkHelper.isNetworkAvailable()



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

    fun getPredictions(input: String) = wordDao.getNextWordPredictions(input)

    // 2. Hàm lưu dữ liệu từ Neo4j
    // Bỏ tham số database ở đây, dùng biến của class
    suspend fun saveNeo4jDataToRoom(neo4jPaths: List<Neo4jPath>) {
        // Sử dụng withTransaction từ biến database đã inject
        database.withTransaction {
            neo4jPaths.forEach { path ->
                path.segments.forEach { segment ->
                    // Mapping Node Start
                    val startEntity = WordEntity(
                        word = segment.startNode.properties.name,
                        label = segment.startNode.labels.firstOrNull() ?: "Unknown"
                    )

                    // Mapping Node End
                    val endEntity = WordEntity(
                        word = segment.endNode.properties.name,
                        label = segment.endNode.labels.firstOrNull() ?: "Unknown"
                    )

                    // Mapping Edge
                    val edgeEntity = WordEdgeEntity(
                        fromWord = startEntity.word,
                        toWord = endEntity.word,
                        relateType = segment.relationship.type,
                        weight = segment.relationship.properties.weight
                    )

                    // Gọi DAO để lưu
                    wordDao.insertFullRelationship(startEntity, endEntity, edgeEntity)
                }
            }
        }
    } // <--- Đóng ngoặc hàm saveNeo4jDataToRoom ở đây

    // --- Các hàm dưới này phải nằm NGOÀI hàm saveNeo4jDataToRoom ---

    suspend fun deleteQuickResponse(quickResponse: QuickResponseEntity) =
        quickResponseDao.delete(quickResponse)

    suspend fun findQuickResponse(question: String): List<String> =
        quickResponseDao.findByQuestion(question)

    suspend fun analyzeQuestion(text: String) =
        fastTalkService.analyzeQuestion(AnalyzeRequest(text))

}