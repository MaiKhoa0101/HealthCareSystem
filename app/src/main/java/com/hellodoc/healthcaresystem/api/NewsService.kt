package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.responsemodel.NewsResponse
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

}
