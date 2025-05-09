package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName


data class PostResponse(
    @SerializedName("_id")
    val id: String,
    val content: String,
    val media: List<String>,
    val user: User,
    val userModel: String,
    @SerializedName("createdAt")
    val createdAt: String
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

data class GetFavoritePostResponse(
    val isFavorited: Boolean,
    val totalFavorites: Int
)

data class UpdateFavoritePostResponse(
    val isFavorited: Boolean,
    val totalFavorites: Int
)

data class CreateCommentPostResponse(
    val user: String,
    val post: String,
    val content: String
)

data class GetCommentPostResponse(
    @SerializedName("_id")
    val id: String,
    val user: User,
    val post: String,
    val content: String,
    val createdAt: String
) {
    data class User(
        @SerializedName("_id")
        val id: String,
        val name: String,
        val avatarURL: String?
    )
}

data class ManagerResponse(
    val user: User,
    val userModel: String,
    val post: CommentPost,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
)

data class CommentPost(
    @SerializedName("_id") val id: String,
    val content: String,
    val media: List<String>
)

