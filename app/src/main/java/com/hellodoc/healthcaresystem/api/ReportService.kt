package com.hellodoc.healthcaresystem.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class ReportRequest(
    val reporter: String,
    val reporterModel: String,
    val content: String,
    val type: String
)

interface ReportService {
    @POST("/report")
    suspend fun sendReport(@Body report: ReportRequest): Response<Void>
}
