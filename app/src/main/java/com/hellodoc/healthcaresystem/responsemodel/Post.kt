package com.hellodoc.healthcaresystem.responsemodel

data class CreatePostResponse(
    val user: String,
    val content: String,
    val imageUrls: List<String>
)
