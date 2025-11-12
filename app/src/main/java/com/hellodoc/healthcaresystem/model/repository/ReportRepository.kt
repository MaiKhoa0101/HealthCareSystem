package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.ReportService
import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
import jakarta.inject.Inject

class ReportRepository @Inject constructor(private val reportService: ReportService) {
    suspend fun sendReport(report: ReportRequest) = reportService.sendReport(report)
    suspend fun getAllReports() = reportService.getAllReports()

}