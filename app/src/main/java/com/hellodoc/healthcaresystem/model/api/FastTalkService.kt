package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Word
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface FastTalkService {
    @GET("nlp/find-word")
    suspend fun getWordSimilar(
        @Query("word") word: String
    ): Response<WordResponse>

    @GET("nlp/find-word-by-label")
    suspend fun getWordByLabel(
        @Query("word") word: String,
        @Query("fromLabel") label: String,
        @Query("toLabel") toLabel: String
    ):Response<WordResponse>

    @POST("nlp/analyze-semantic")
    suspend fun analyzeQuestion(
        @Body text: String
    ): Response<Word>

}
