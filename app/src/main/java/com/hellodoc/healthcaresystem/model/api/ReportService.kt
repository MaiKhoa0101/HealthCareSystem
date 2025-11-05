package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.AdminResponseRequest
import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReportService {
    @POST("/report")
    suspend fun sendReport(@Body report: ReportRequest): Response<Void>

    @GET("/report")
    suspend fun getAllReports(): List<ReportResponse>

    @PATCH("/report/{id}/response")
    suspend fun sendAdminResponse(
        @Path("id") id: String,
        @Body response: AdminResponseRequest
    ): Response<Void>

    @DELETE("/report/{id}")
    suspend fun deleteReport(@Path("id") id: String): Response<Void>

}
