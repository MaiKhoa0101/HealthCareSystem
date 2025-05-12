package com.hellodoc.healthcaresystem.responsemodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsResponse(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val content: String,
    val media: List<String>,
    val createdAt: String,
) : Parcelable

data class GetNewsCommentResponse(
    @SerializedName("_id")
    val id: String,
    val user: User,
    val content: String,
    val createdAt: String
)
data class CommentPaginationResponse(
    val comments: List<GetNewsCommentResponse>,
    val hasMore: Boolean
)
data class ManagerResponse1(
    val user: User,
    val userModel: String,
    val news: CommentNews,
    val content: String,
    val createdAt: String,
    val updatedAt: String,
)

data class CommentNews(
    @SerializedName("_id")
    val id: String,
    val content: String,
    val media: List<String>
)