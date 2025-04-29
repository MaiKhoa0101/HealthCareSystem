package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.responsemodel.CreatePostResponse
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Headers
import retrofit2.http.Path

data class PostResponse(
    val id: String,
    val content: String,
    val media: List<String>,
    val user: UserResponse
)

data class UserResponse(
    val name: String,
    val imageUrl: String?
)



interface PostService {
    @Headers("Content-Type: application/json")
    @GET("/post")
    suspend fun getAllPosts(): Response<List<PostResponse>>

    @GET("post/getById/{id}")
    suspend fun getPostById(@Path("id") id: String): Response<List<PostResponse>>

    @Multipart
    @POST("post/create")
    suspend fun createPost(
        @Part userId: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>?
    ): Response<CreatePostResponse>
    
}
