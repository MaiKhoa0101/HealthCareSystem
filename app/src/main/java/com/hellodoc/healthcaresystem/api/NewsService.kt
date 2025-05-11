package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.CreateNewsCommentRequest
import com.hellodoc.healthcaresystem.requestmodel.UpdateNewsFavoriteRequest
import com.hellodoc.healthcaresystem.responsemodel.GetFavoritePostResponse
import com.hellodoc.healthcaresystem.responsemodel.GetNewsCommentResponse
import com.hellodoc.healthcaresystem.responsemodel.ManagerResponse
import com.hellodoc.healthcaresystem.responsemodel.NewsResponse
import com.hellodoc.healthcaresystem.responsemodel.UpdateFavoritePostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

interface NewsService {
    @GET("/news")
    suspend fun getAllNews(): Response<List<NewsResponse>>

    @Multipart
    @POST("/news/create")
    suspend fun createNews(
        @Part("adminId") adminId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<Any>

    @DELETE("/news/{id}")
    suspend fun deleteNews(@Path("id") id: String): Response<Any>

    @POST("news/{newsId}/comment/create")
    suspend fun createCommentByNewsId(
        @Path("newsId") newsId: String,
        @Body createNewsCommentRequest: CreateNewsCommentRequest
    ): Response<Unit>

    @GET("news/{newsId}/comment")
    suspend fun getCommentByNewsId(
        @Path("newsId") newsId: String
    ): Response<List<GetNewsCommentResponse>>

    @PATCH("news/{commentId}/comment/update")
    suspend fun updateCommentById(
        @Path("commentId") commentId: String,
        @Body update: CreateNewsCommentRequest
    ): Response<Unit>

    @DELETE("news/{commentId}/comment/delete")
    suspend fun deleteCommentById(@Path("commentId") commentId: String): Response<Unit>

    //FAVORITE
    @POST("news/{newsId}/favorite/update")
    suspend fun updateFavoriteByNewsId(
        @Path("newsId") newsId: String,
        @Body request: UpdateNewsFavoriteRequest
    ): Response<UpdateFavoritePostResponse>

    @GET("news/{newsId}/favorite/get")
    suspend fun getFavoriteByNewsId(
        @Path("newsId") newsId: String,
        @Query("userId") userId: String
    ): Response<GetFavoritePostResponse>

    @GET("news/user/{userId}/favorite/get")
    suspend fun getUserFavoriteNews(@Path("userId") userId: String): Response<List<ManagerResponse>>

}
