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
        println("🚀 Bắt đầu lưu ${responseList.size} dòng vào Room...")

        try {
            database.withTransaction {
                responseList.forEachIndexed { index, item ->
                    try {
                        // --- KIỂM TRA DỮ LIỆU ĐẦU VÀO (Debug) ---
                        // Nếu item null thì bỏ qua
                        if (item == null) return@forEachIndexed

                        // 1. Xử lý Start Node
                        // Nếu startNode null -> bỏ qua dòng này
                        val sNode = item.startNode
                        if (sNode == null) {
                            println("⚠️ Dòng $index: StartNode bị NULL. Bỏ qua.")
                            return@forEachIndexed
                        }
                        // Nếu label null -> gán "Unknown"
                        val sLabel = item.startLabel ?: "Unknown"

                        // 2. Xử lý End Node
                        val eNode = item.endNode
                        if (eNode == null) {
                            println("⚠️ Dòng $index: EndNode bị NULL. Bỏ qua.")
                            return@forEachIndexed
                        }
                        val eLabel = item.endLabel ?: "Unknown"

                        // 3. Xử lý các chỉ số khác
                        val rType = item.relType ?: "Related_To"
                        // Nếu weight null -> gán 0.0. Dùng Elvis operator ?:
                        val w = item.weight ?: 0.0

                        // --- TẠO ENTITY ---
                        val startEntity = WordEntity(word = sNode, label = sLabel)
                        val endEntity = WordEntity(word = eNode, label = eLabel)

                        // --- LƯU VÀO DB ---
                        wordDao.insertWord(startEntity)
                        wordDao.insertWord(endEntity)

                        val edgeEntity = WordEdgeEntity(
                            fromWord = sNode,
                            toWord = eNode,
                            relateType = rType,
                            weight = w
                        )
                        wordDao.insertEdge(edgeEntity)

                    } catch (e: Exception) {
                        // In ra chi tiết dòng bị lỗi để bạn biết sửa data
                        println("❌ Lỗi tại dòng $index ($item): ${e.message}")
                        e.printStackTrace() // Quan trọng: In stack trace để thấy dòng code lỗi
                    }
                }
            }
            println("✅ Hoàn tất lưu dữ liệu vào Room!")
        } catch (e: Exception) {
            println("❌ Lỗi Transaction tổng: ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun deleteQuickResponse(quickResponse: QuickResponseEntity) =
        quickResponseDao.delete(quickResponse)

    suspend fun findQuickResponse(question: String): List<String> =
        quickResponseDao.findByQuestion(question)

    suspend fun analyzeQuestion(text: String) =
        fastTalkService.analyzeQuestion(AnalyzeRequest(text))

    suspend fun getGraphData()= fastTalkService.getGraphData()

    suspend fun createQuestionAnswer(question: String, answer: String) = fastTalkService.processQuestionAnswer(question, answer)

}