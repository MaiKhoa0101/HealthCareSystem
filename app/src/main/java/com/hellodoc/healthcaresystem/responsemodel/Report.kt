package com.hellodoc.healthcaresystem.responsemodel

data class ComplaintData (
    val id: String,
    val user: String,
    val content: String,
    val targetType: String,
    val status: String,
    val createdDate: String
)