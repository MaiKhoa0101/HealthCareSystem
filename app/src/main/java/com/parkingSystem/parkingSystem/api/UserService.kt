package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.requestmodel.TokenRequest
import com.parkingSystem.parkingSystem.responsemodel.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @GET("admin/userbyid/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @PUT("user/{id}/fcm-token")
    suspend fun updateFcmToken(
        @Path("id") userId: String,
        @Body tokenRequest: TokenRequest
    ): Response<Void>

}