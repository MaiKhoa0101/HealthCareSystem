package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GestureCodeResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordCode
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GestureCodeService {
    @Headers("Content-Type: application/json")
    @GET("gesture_code/get")
    suspend fun getGestureCode(
        @Body urlMedia: String
    ): Response<WordCode>

    @POST("gesture_code/post_video_url")
    suspend fun postVideoToGetGestureCode(
        @Body urlMedia: String
    ): Response<List<GestureCodeResponse>>
}