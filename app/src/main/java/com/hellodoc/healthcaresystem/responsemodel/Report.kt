package com.hellodoc.healthcaresystem.responsemodel

data class ComplaintData (
    val id: String,
    val user: String,
    val content: String,
    val targetType: String,
    val status: String,
    val createdDate: String
)
data class ReportResponse(
    val reporter: Reporter?,       // Người gửi (doctor hoặc user)
    val content: String,
    val type: String,              // "Bác sĩ" hoặc "Ứng dụng"
    val status: String,
    val createdAt: String
)

data class Reporter(
    val name: String
)