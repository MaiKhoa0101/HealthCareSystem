package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class PostResponse(
    val user: User,
    val content: String,
    val media: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val userModel: String
) {
    data class User(
        @SerializedName("_id")
        val id: String,
        val name: String,
        val avatarURL: String?
    )
}

data class CreatePostResponse(
    val user: String,
    val content: String,
    val imageUrls: List<String>
)
