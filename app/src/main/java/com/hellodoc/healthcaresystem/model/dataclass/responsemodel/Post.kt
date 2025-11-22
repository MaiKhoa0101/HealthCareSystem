package com.hellodoc.healthcaresystem.model.dataclass.responsemodel

import com.google.gson.annotations.SerializedName


sealed class UiState {
    object Loading : UiState()
    object Idle: UiState()
    data class Success(val message: String) : UiState()
    data class FetchSuccess(val message: String) : UiState()
    data class FetchFail(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

data class PostResponse(
    @SerializedName("_id")
    val id: String,
    val content: String,
    val media: List<String> ,
    val userInfo: User?,
    val userModel: String,
    @SerializedName("createdAt")
    val createdAt: String,

    val keywords: String?,
    val embedding: List<Double>?,
)
enum class MediaType {
    IMAGE,
    VIDEO,
    UNKNOWN
}
data class SimilarPostResponse(
    val post: PostResponse,
    val similarity: Double
)


data class CreatePostResponse(
    val user: String,
    val content: String,
    val media: List<String>,
    val keywords: String
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

data class CommentPostResponse(
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

data class GetCommentPageResponse(
    val comments: List<CommentPostResponse>,
    val hasMore: Boolean
)


data class GetPostPageResponse(
    val posts: List<PostResponse>,
    val hasMore: Boolean
)
