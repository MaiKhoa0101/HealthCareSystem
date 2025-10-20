package com.parkingSystem.parkingSystem.api

import com.parkingSystem.parkingSystem.requestmodel.CreateCommentPostRequest
import com.parkingSystem.parkingSystem.requestmodel.UpdateFavoritePostRequest
import com.parkingSystem.parkingSystem.responsemodel.CreateCommentPostResponse
import com.parkingSystem.parkingSystem.responsemodel.CreatePostResponse
import com.parkingSystem.parkingSystem.responsemodel.GetCommentPageResponse
import com.parkingSystem.parkingSystem.responsemodel.GetFavoritePostResponse
import com.parkingSystem.parkingSystem.responsemodel.GetPostPageResponse
import com.parkingSystem.parkingSystem.responsemodel.PostResponse
import com.parkingSystem.parkingSystem.responsemodel.UpdateFavoritePostResponse
import com.parkingSystem.parkingSystem.responsemodel.ManagerResponse
import com.parkingSystem.parkingSystem.responsemodel.SimilarPostResponse
import com.parkingSystem.parkingSystem.viewmodel.PostViewModel.UpdateKeywordsRequest
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {
    @Headers("Content-Type: application/json")
    @GET("/post")
    suspend fun getAllPosts(
        @Query("skip") skip: Int,
        @Query("limit") limit: Int
    ): Response<GetPostPageResponse>

    @GET("post/{postId}/comment/get")
    suspend fun getCommentByPostId(
        @Path("postId") postId: String,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int
    ): Response<GetCommentPageResponse>

    // comment
    @POST("post/{postId}/comment/create")
    suspend fun createCommentByPostId(
        @Path("postId") postId: String,
        @Body createCommentPostRequest: CreateCommentPostRequest
    ): Response<CreateCommentPostResponse>

    @GET("post/{id}")
    suspend fun getPostById(@Path("id") id: String): Response<PostResponse>

    @GET("post/get-by-user-id/{id}")
    suspend fun getPostByUserId(
        @Path("id") id: String,
        @Query("skip") skip: Int,
        @Query("limit") limit: Int
    ): Response<GetPostPageResponse>

    @Multipart
    @POST("post/create")
    suspend fun createPost(
        @Part userId: MultipartBody.Part,
        @Part userModel: MultipartBody.Part,
        @Part content: MultipartBody.Part,
        @Part images: List<MultipartBody.Part>?,
        @Part keywordsPart: MultipartBody.Part?,
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

    @PATCH("post/{commentId}/comment/update")
    suspend fun updateCommentById(
        @Path("commentId") commentId: String,
        @Body update: CreateCommentPostRequest
    ): Response<Unit>

    @DELETE("post/{commentId}/comment/delete")
    suspend fun deleteCommentById(@Path("commentId") commentId: String): Response<Unit>

    @DELETE("post/{postId}")
    suspend fun deletePostById(@Path("postId") postId: String): Response<Unit>

    @GET("post/user/{id}/comment/get")
    suspend fun getCommentByUserId(
        @Path("id") id: String,
    ): Response<List<ManagerResponse>>

    @GET("post/user/{id}/favorite/get")
    suspend fun getUserFavoritePost(
        @Path("id") id: String,
    ): Response<List<ManagerResponse>>

    @Multipart
    @PATCH("post/{id}")
    suspend fun updatePost(
        @Path("id") postId: String,
        @Part content: MultipartBody.Part?,
        @Part media: List<MultipartBody.Part>,
        @Part images: List<MultipartBody.Part>
    ): Response<Unit>


    @GET ("post/search/advanced")
    suspend fun searchAdvanced(
        @Query("query") query: String
    ): Response<List<PostResponse>>

    @PATCH("post/update/postKeywords/{id}")
    suspend fun addKeywords(@Path("id") id: String, @Body keywords: UpdateKeywordsRequest): Response<Void>

    @POST("post/generateEmbedding/{id}")
    suspend fun createEmbedding(@Path("id") id: String, @Body keywords: String): Response<Void>



    @GET("post/{id}/similar")
    suspend fun getSimilarPosts(
        @Path("id") id: String,
        @Query("limit") limit: Int = 5,
        @Query("minSimilarity") minSimilarity: Double = 0.6
    ): Response<List<SimilarPostResponse>>

}
