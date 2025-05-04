package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.CreateCommentPostRequest
import com.hellodoc.healthcaresystem.requestmodel.CreatePostRequest
import com.hellodoc.healthcaresystem.requestmodel.GetFavoritePostRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateFavoritePostRequest
import com.hellodoc.healthcaresystem.responsemodel.CreateCommentPostResponse
import com.hellodoc.healthcaresystem.responsemodel.CreatePostResponse
import com.hellodoc.healthcaresystem.responsemodel.GetCommentPostResponse
import com.hellodoc.healthcaresystem.responsemodel.GetFavoritePostResponse
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.UpdateFavoritePostResponse
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
import retrofit2.http.Query

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
        @Part userModel: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>?
    ): Response<CreatePostResponse>

    // favorite
    @POST("post/{postId}/favorite/update")
    suspend fun updateFavoriteByPostId(
        @Path("postId") postId: String,
        @Body updateFavoritePostRequest: UpdateFavoritePostRequest
    ): Response<UpdateFavoritePostResponse>

    @GET("post/{postId}/favorite/get")
    suspend fun getFavoriteByPostId(
        @Path("postId") postId: String,
        @Query("userId") userId: String
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
