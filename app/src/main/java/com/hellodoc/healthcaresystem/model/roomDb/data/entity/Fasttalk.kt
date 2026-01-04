package com.hellodoc.healthcaresystem.model.roomDb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quick_responses")
data class QuickResponseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val response: String,
    val patientId: String = ""
)