package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.GetUserID
import com.hellodoc.healthcaresystem.requestmodel.UpdateUser
import com.hellodoc.healthcaresystem.responsemodel.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface AdminService {
    @Headers("Content-Type: application/json")
    @GET("admin/users")
    suspend fun getUsers(): Response<List<User>> //trả về một đối tượng GetUser

    @GET("user/get/{id}")
    suspend fun GetByUserID(@Body request: GetUserID): Response<User>

    @PUT("admin/updateUser/{id}")
    suspend fun updateUserByID(
        @Path("id") id: String,
        @Body user: UpdateUser
    ):  Response<User>

    @Multipart
    @PUT("admin/updateUser/{id}")
    suspend fun updateUsersByID(
        @Path("id") id: String,
        @Part user: UpdateUser
    ):  Response<User>

}