package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.requestmodel.TokenRequest
import com.parkingSystem.parkingSystem.requestmodel.UpdateUserInput
import com.parkingSystem.parkingSystem.responsemodel.DeleteUserResponse
import com.parkingSystem.parkingSystem.responsemodel.User
import com.parkingSystem.parkingSystem.responsemodel.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AdminService {

    @Headers("Content-Type: application/json")
    @GET("admin/getallusers")
    suspend fun getAllUser(): Response<UserResponse>

    @Headers("Content-Type: application/json")
    @GET("admin/userbyid/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @Headers("Content-Type: application/json")
    @PUT("admin/update-user-info/{id}")
    suspend fun updateUserInfo(
        @Path("id") userId: String,
        @Body updateData: UpdateUserInput
    ): Response<Void>

    @Headers("Content-Type: application/json")
    @DELETE("admin/delete-user/{id}")
    suspend fun deleteUser(@Path("id") userId: String): Response<DeleteUserResponse>

    @Headers("Content-Type: application/json")
    @PUT("user/{id}/fcm-token")
    suspend fun updateFcmToken(
        @Path("id") userId: String,
        @Body tokenRequest: TokenRequest
    ): Response<Void>
}
