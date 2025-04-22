package com.hellodoc.healthcaresystem.api

import retrofit2.http.GET
import retrofit2.Call

data class PostResponse(
    val _id: String,
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
}
