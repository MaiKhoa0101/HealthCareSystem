package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
import com.hellodoc.healthcaresystem.responsemodel.ReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ReportService {
    @POST("/report")
    suspend fun sendReport(@Body report: ReportRequest): Response<Void>

    @GET("/report")
    suspend fun getAllReports(): List<ReportResponse>
}
