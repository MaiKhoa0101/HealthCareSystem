package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GestureCodeResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.WordCode
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.urlMedia
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
import retrofit2.http.Query

interface GestureCodeService {
    @Headers("Content-Type: application/json")
    @GET("gesture_code/get_gesture_code")
    suspend fun getGestureCode(
        @Query("videoUrl") videoUrl: String
    ): Response<WordCode>

    @POST("gesture_code/post_video_url")
    suspend fun postVideoToGetGestureCode(
        @Body urlMedia: urlMedia
    ): Response<List<GestureCodeResponse>>
}