package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.responsemodel.CreatePostResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
    @GET("/post")
    fun getPosts(): Call<List<PostResponse>>

//    @POST("/post/create")
//    fun createPost(@Body post: CreatePostRequest): Call<CreatePostResponse>

    @Multipart
    @POST("post/create")
    suspend fun createPost(
        @Part userId: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>?
    ): Response<CreatePostResponse>
}
