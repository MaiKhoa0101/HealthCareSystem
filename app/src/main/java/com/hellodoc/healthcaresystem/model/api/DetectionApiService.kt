package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.DetectionResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DetectionApiService {
    @Multipart
    @POST("detect")
    suspend fun detectJoints(
        @Part file: MultipartBody.Part
    ): Response<DetectionResponse>
}
