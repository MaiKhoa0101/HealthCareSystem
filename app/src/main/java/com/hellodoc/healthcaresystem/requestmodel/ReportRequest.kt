package com.hellodoc.healthcaresystem.requestmodel

data class ReportRequest(
    val reporter: String,
    val reporterModel: String,
    val content: String,
    val type: String
)