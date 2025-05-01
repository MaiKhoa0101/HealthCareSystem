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
    val avatarURL: String?
)

data class GetFavoritePostResponse(
    val favorited: Boolean, 
    val totalLikes: Int
)

data class UpdateFavoritePostResponse(
    val favorited: Boolean, 
    val totalLikes: Int
)

data class CreateCommentPostRequest(
    val userId: String
    val content: String
)

data class CreateCommentPostResponse(
    val user: UserResponse?,
    val post: String
    val content: String
)

data class GetCommentPostResponse(
    val user: UserResponse?,
    val post: String
    val content: String,
    val createdAt: String
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

    // favorite
    @POST("post/{postId}/favorite/update")
    suspend fun updateFavoriteByPostId(
        @Path("postId") postId: String,
        @Body userId: String
    ): Response<UpdateFavoritePostResponse>

    @GET("post/{postId}/favorite/get")
    suspend fun getFavoriteByPostId(
        @Path("postId") postId: String,
        @Body userId: String
    ): Response<GetFavoritePostResponse>


    // comment
    @POST("post/{postId}/comment/create")
    suspend fun createCommentByPostId(
        @Path("postId") postId: String,
        @Body createCommentPostRequest: CreateCommentPostRequest
    ): Response<CreateCommentPostResponse>

    @GET("post/{postId}/comment/get")
    suspend fun getCommentByPostId(
        @Path("postId") postId: String
    ): Response<List<GetCommentPostResponse>>

}
