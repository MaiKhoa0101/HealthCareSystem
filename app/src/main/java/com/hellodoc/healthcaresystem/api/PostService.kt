package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.Path


interface PostService {
    @Headers("Content-Type: application/json")
    @GET("/post")
    suspend fun getAllPosts(): Response<List<PostResponse>>

    @GET("post/getById/{id}")
    suspend fun getPostById(@Path("id") id: String): Response<List<PostResponse>>
}
