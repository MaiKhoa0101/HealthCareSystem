package com.parkingSystem.parkingSystem.requestmodel

data class CreateNewsRequest(
    val adminId: String,
    val title: String,
    val content: String
)
data class CreateNewsCommentRequest(
    val userId: String,
    val content: String
)
data class UpdateNewsFavoriteRequest(
    val userId: String,
    val userModel: String
)