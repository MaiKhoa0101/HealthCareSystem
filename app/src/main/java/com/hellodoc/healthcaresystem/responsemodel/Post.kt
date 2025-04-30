package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class PostResponse(
    @SerializedName("_id")
    val id: String,
    val user: User,
    val content: String,
    val media: List<String>,
    val userModel: String,
    val likes: List<String> // danh sách userId đã like bài này
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
    val media: List<String>
)
data class CommentItem(
    val user: PostResponse.User,
    val content: String,
    val createdAt: String
)