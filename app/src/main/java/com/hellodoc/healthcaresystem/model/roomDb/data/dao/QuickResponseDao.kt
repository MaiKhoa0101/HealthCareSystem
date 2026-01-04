package com.hellodoc.healthcaresystem.model.roomDb.data.dao

import androidx.room.*
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.QuickResponseEntity

@Dao
interface QuickResponseDao {

    @Insert
    suspend fun insert(response: QuickResponseEntity)

    @Delete
    suspend fun delete(response: QuickResponseEntity)

    // Tìm câu trả lời theo câu hỏi
    @Query("SELECT * FROM quick_responses WHERE question = :question LIMIT 1")
    suspend fun findResponse(question: String): QuickResponseEntity?

}