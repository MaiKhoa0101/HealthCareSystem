package com.hellodoc.healthcaresystem.model.api

import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.DeleteUserResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UserResponse
import com.hellodoc.healthcaresystem.requestmodel.GetUserID
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
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


    @GET("admin/getallusers")
    suspend fun getAllUser(): Response<UserResponse> //trả về một đối tượng GetUser

    @DELETE("admin/delete-user/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<DeleteUserResponse>
}