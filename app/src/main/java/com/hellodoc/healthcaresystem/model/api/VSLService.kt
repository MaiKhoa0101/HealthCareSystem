package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.requestmodel.Subtitle
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.VSL
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface VSLService {
    @POST("gesture_code/get_sign_language_video_playlist")
    suspend fun getSignLanguageVideoPlaylist(
        @Body request: Subtitle
    ): Response<List<VSL>>

}