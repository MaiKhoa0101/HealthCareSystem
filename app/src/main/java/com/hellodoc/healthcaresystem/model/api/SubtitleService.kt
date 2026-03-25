package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.requestmodel.SubtitleRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.SubtitleResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SubtitleService {
    @POST("api/phowhisper/get-subtitle")
    suspend fun getSubtitle(
        @Body request: SubtitleRequest
    ): Response<SubtitleResponse>

}