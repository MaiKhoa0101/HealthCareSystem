package com.hellodoc.healthcaresystem.responsemodel

data class NewsResponse(
    val id: String,
    val title: String,
    val content: String,
    val media: List<String>,
    val createdAt: String,
)
