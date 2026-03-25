package com.hellodoc.healthcaresystem.model.dataclass.responsemodel

import com.google.gson.annotations.SerializedName

// 1. Đối tượng đại diện cho một từ gợi ý (item trong mảng results)
data class WordResult(
    @SerializedName("word") val word: String,           // JSON là "word"
    @SerializedName("finalScore") val score: Double,    // JSON là "finalScore"
    @SerializedName("posTag") val posTag: String,       // JSON là "posTag" (String, không phải List)
    @SerializedName("relationType") val relationType: String? = null
)

// 2. Đối tượng phản hồi tổng (Root JSON)
data class WordResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("word") val inputWord: String,
    @SerializedName("results") val results: List<WordResult>? // JSON là "results", có thể null
)

// 3. Request Body cho API analyze (để gửi text dạng JSON)
data class AnalyzeRequest(
    @SerializedName("text") val text: String
)

data class AlignmentAndDirection(
    val alignment: String,
    val direction: Float
)

// Danh sách cung (hướng và vị trí)
val alignmentAndDirection = listOf(
    AlignmentAndDirection(alignment = "Top", direction = 90f),
    AlignmentAndDirection(alignment = "Left", direction = 0f),
    AlignmentAndDirection(alignment = "Right", direction = 180f),
    AlignmentAndDirection(alignment = "Bottom", direction = 270f),
)
