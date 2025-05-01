package com.hellodoc.healthcaresystem.responsemodel

data class ComplaintData (
    val id: String,
    val reportId: String,
    val user: String,
    val content: String,
    val targetType: String,
    var status: String,
    val createdDate: String,
    val reportedId: String,
    val postId: String? = null
)
data class ReportResponse(
    val _id: String,
    val reporter: Reporter?,
    val content: String?,
    val type: String?,
    val status: String?,
    val createdAt: String?,
    val reportedId: String?,
    val postId: String? = null
)

data class Reporter(
    val name: String
)