package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.Word
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FastTalkService {
    @GET("nlp/find-word")
    suspend fun getWordSimilar(
        @Query("word") word: String
    ): Response<WordResponse>
}
