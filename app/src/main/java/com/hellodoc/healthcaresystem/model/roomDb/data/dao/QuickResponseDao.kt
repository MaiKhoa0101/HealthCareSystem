package com.hellodoc.healthcaresystem.model.roomDb.data.dao

import androidx.room.*
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.QuickResponseEntity
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.WordEdgeEntity
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.WordEntity
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.WordPrediction
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickResponseDao {

    @Insert
    suspend fun insert(response: QuickResponseEntity)

    @Delete
    suspend fun delete(response: QuickResponseEntity)

    @Query("SELECT response FROM quick_responses WHERE question = :question")
    suspend fun findByQuestion(question: String): List<String>

}

@Dao
interface WordGraphDao {

    // 1. Insert Node (Nếu trùng thì bỏ qua hoặc đè lên)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: WordEntity)

    // Insert Edge (Nếu trùng thì đè lên)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEdge(edge: WordEdgeEntity)

    // 3. Helper function để insert cả cụm từ JSON
    @Transaction
    suspend fun insertFullRelationship(from: WordEntity, to: WordEntity, edge: WordEdgeEntity) {
        insertWord(from)
        insertWord(to)
        insertEdge(edge)
    }

    // 4. TRUY VẤN QUAN TRỌNG: Tìm từ tiếp theo dựa trên trọng số
    // Lấy tất cả từ mà 'inputWord' trỏ tới, sắp xếp theo weight giảm dần
    @Query("""
        SELECT 
            edges.toWord as nextWord, 
            edges.weight, 
            words.label 
        FROM word_edges as edges
        INNER JOIN words ON edges.toWord = words.word
        WHERE edges.fromWord = :inputWord
        ORDER BY edges.weight DESC
    """)
    fun getNextWordPredictions(inputWord: String): Flow<List<WordPrediction>>
}