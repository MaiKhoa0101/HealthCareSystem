package com.hellodoc.healthcaresystem.requestmodel

import android.net.Uri

data class CreatePostRequest(
    val userId: String,
    val userModel: String,
    val content: String,
    val images: List<Uri>? // List các ảnh, mỗi ảnh là một Uri
)


data class CreateCommentPostRequest(
    val userId: String,
    val userModel: String,
    val content: String
)

data class UpdateFavoritePostRequest(
    val userId: String,
    val userModel: String
)

data class GetFavoritePostRequest(
    val userId: String
)

data class UpdatePostRequest(
    val content: String,
    val media: List<String>? = null,
    val images: List<Uri> = emptyList()
)
