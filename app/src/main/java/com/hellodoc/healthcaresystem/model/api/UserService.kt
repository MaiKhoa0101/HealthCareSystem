package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.TokenRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserService {
    @GET("admin/userbyid/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @PUT("user/{id}/fcm-token")
    suspend fun updateFcmToken(
        @Path("id") userId: String,
        @Body tokenRequest: TokenRequest
    ): Response<Void>

    @Multipart
    @PUT("admin/updateUser/{id}")
    suspend fun updateUserByID(
        @Path("id") id: String,
        @Part avatarURL: MultipartBody.Part?,
        @Part name: MultipartBody.Part?,
        @Part email: MultipartBody.Part?,
        @Part address: MultipartBody.Part?,
        @Part phone: MultipartBody.Part?,
        @Part password: MultipartBody.Part?,
    ):  Response<User>
}