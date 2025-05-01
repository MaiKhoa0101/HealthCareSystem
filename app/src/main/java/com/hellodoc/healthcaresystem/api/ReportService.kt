package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface ReportService {
    @POST("/report")
    suspend fun sendReport(@Body report: ReportRequest): Response<Void>
}
