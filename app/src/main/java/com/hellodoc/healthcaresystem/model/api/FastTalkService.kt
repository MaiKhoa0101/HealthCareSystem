package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.AnalyzeRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface FastTalkService {
    // API tìm từ gợi ý
    @GET("nlp/find-word")
    suspend fun getWordSimilar(
        @Query("word") word: String
    ): Response<WordResponse>

    // API tìm theo label (recursive)
    @GET("nlp/find-word-by-label")
    suspend fun getWordByLabel(
        @Query("word") word: String,
        @Query("toLabel") toLabel: String // Backend của bạn dùng toLabel để lọc đích
    ): Response<WordResponse>

    // API phân tích ngữ nghĩa
    @POST("nlp/analyze-semantic")
    suspend fun analyzeQuestion(
        @Body request: AnalyzeRequest // Gửi object { "text": "..." }
    ): Response<WordResponse> // Giả sử nó trả về cấu trúc giống tìm từ hoặc bạn cần định nghĩa class riêng nếu khác
}