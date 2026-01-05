package com.hellodoc.healthcaresystem.model.repository

import androidx.room.withTransaction
import com.hellodoc.healthcaresystem.model.api.FastTalkService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AnalyzeRequest
import com.hellodoc.healthcaresystem.model.networksupport.NetworkHelper
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.QuickResponseDao
import com.hellodoc.healthcaresystem.model.roomDb.data.dao.WordGraphDao
import com.hellodoc.healthcaresystem.model.roomDb.data.database.AppDatabase
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.* // Import hết entity
import javax.inject.Inject // Lưu ý: Dùng javax.inject hoặc jakarta.inject tuỳ version Hilt
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResultResponse

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
    suspend fun saveNeo4jDataToRoom(responseList: List<WordResultResponse>) {
        database.withTransaction {
            responseList.forEach { item ->
                val sourceText = item.source
                val suggestionText = item.suggestion

                // --- 1. XỬ LÝ NÚT ĐÍCH (SUGGESTION) ---
                // Đây là cái bạn muốn giữ lại. Nếu có suggestion, lưu nó làm Node.
                if (suggestionText != null) {
                    val endEntity = WordEntity(
                        word = suggestionText,
                        label = item.label.firstOrNull() ?: "Unknown"
                    )
                    // Gọi hàm insertWord riêng lẻ (cần đảm bảo hàm này dùng OnConflictStrategy.IGNORE)
                    wordDao.insertWord(endEntity)
                }

                // --- 2. XỬ LÝ NÚT NGUỒN VÀ MỐI QUAN HỆ ---
                // Chỉ khi nào CẢ source VÀ suggestion đều có, ta mới tạo mối nối (Edge)
                if (sourceText != null && suggestionText != null) {

                    // Lưu nút nguồn
                    val startEntity = WordEntity(
                        word = sourceText,
                        label = "Unknown" // Hoặc lấy từ API nếu có update sau này
                    )
                    wordDao.insertWord(startEntity)

                    // Lưu mối quan hệ (Cạnh)
                    val edgeEntity = WordEdgeEntity(
                        fromWord = sourceText,
                        toWord = suggestionText,
                        relateType = "Related_To",
                        weight = item.score
                    )
                    wordDao.insertEdge(edgeEntity)
                }
                // Nếu sourceText == null, ta không làm gì ở bước 2 -> Không tạo Edge, không tạo Source node null.
                // Nhưng Suggestion Node đã được lưu ở bước 1 rồi.
            }
        }
    }

    suspend fun deleteQuickResponse(quickResponse: QuickResponseEntity) =
        quickResponseDao.delete(quickResponse)

    suspend fun findQuickResponse(question: String): List<String> =
        quickResponseDao.findByQuestion(question)

    suspend fun analyzeQuestion(text: String) =
        fastTalkService.analyzeQuestion(AnalyzeRequest(text))

    suspend fun getGraphData()= fastTalkService.getGraphData()

}