package com.hellodoc.healthcaresystem.model.roomDb.data.dao

import androidx.room.*
import com.hellodoc.healthcaresystem.model.roomDb.data.entity.QuickResponseEntity

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
interface WordDao {

    @Insert
    suspend fun insert(response: QuickResponseEntity)

    @Delete
    suspend fun delete(response: QuickResponseEntity)

    @Query("SELECT response FROM quick_responses WHERE question = :question")
    suspend fun findByQuestion(question: String): List<String>

}