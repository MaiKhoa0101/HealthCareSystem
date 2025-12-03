package com.hellodoc.healthcaresystem.model.repository

import android.util.Log
import com.hellodoc.healthcaresystem.api.ReportService
// Import các mô hình dữ liệu
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ComplaintData
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ReportResponse // Giả sử đây là mô hình API thô
import com.hellodoc.healthcaresystem.requestmodel.AdminResponseRequest
import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
import jakarta.inject.Inject
import javax.inject.Singleton

@Singleton // Thêm @Singleton
class ReportRepository @Inject constructor(private val reportService: ReportService) {
    suspend fun sendReport(report: ReportRequest): Result<Unit> {
        return try {
            val response = reportService.sendReport(report)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Log.e("ReportRepository", "sendReport thất bại: ${response.code()}")
                Result.failure(Exception("Gửi báo cáo thất bại (code: ${response.code()})"))
            }
        } catch (e: Exception) {
            Log.e("ReportRepository", "sendReport lỗi: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Lấy tất cả báo cáo và ánh xạ chúng sang mô hình UI (ComplaintData).
     */
    suspend fun getAllReports(): Result<List<ComplaintData>> {
        return try {
            // Giả sử service trả về List<ReportResponse> (mô hình API thô)
            val rawReports: List<ReportResponse> = reportService.getAllReports()

            // Ánh xạ (map) từ mô hình API sang mô hình UI
            val complaintDataList = rawReports.mapIndexed { index, report ->
                ComplaintData(
                    id = (index + 1).toString(), // Dùng ID này cho UI
                    reportId = report._id, // Dùng ID này để gọi API
                    user = report.reporter?.name ?: "Không rõ",
                    content = report.content ?: "Không có nội dung",
                    targetType = report.type ?: "Không xác định",
                    status = report.status ?: "opened",
                    createdDate = report.createdAt?.substring(0, 10) ?: "Không rõ",
                    reportedId = report.reportedId ?: "Không rõ",
                    postId = report.postId
                )
            }
            Result.success(complaintDataList)
        } catch (e: Exception) {
            Log.e("ReportRepository", "getAllReports lỗi: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Xóa một báo cáo. Trả về Result.success hoặc Result.failure.
     */
    suspend fun deleteReport(reportId: String): Result<Unit> {
        return try {
            val response = reportService.deleteReport(reportId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Log.e("ReportRepository", "deleteReport thất bại: ${response.code()}")
                Result.failure(Exception("Xóa thất bại (code: ${response.code()})"))
            }
        } catch (e: Exception) {
            Log.e("ReportRepository", "deleteReport lỗi: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun sendAdminResponse(
        id: String,
        responseContent: String,
        responseTime: String
    ) = reportService.sendAdminResponse(
        id = id,
        response =AdminResponseRequest(responseContent, responseTime)
    )
    
}