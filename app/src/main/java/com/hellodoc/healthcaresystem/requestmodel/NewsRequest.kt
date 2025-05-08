package com.hellodoc.healthcaresystem.requestmodel

data class CreateNewsRequest(
    val adminId: String,
    val title: String,
    val content: String
)