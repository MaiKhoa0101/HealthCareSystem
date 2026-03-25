package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ComplaintData
import com.hellodoc.healthcaresystem.model.repository.ReportRepository
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.requestmodel.AdminResponseRequest
import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
// XÓA: import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    // Sửa lỗi: Khởi tạo là emptyList(), không phải null
    private val _reportList = MutableStateFlow<List<ComplaintData>>(emptyList())
    val reportList: MutableStateFlow<List<ComplaintData>> get() = _reportList

    fun createReportForPost(
        context: Context,
        reportContent: String,
        reporterId: String,
        postId: String,
        postOwnerId: String,
        reporterModel: String
    ) {
        viewModelScope.launch {
            val result = reportRepository.sendReport(
                ReportRequest(
                    reporter = reporterId,
                    reporterModel = reporterModel,
                    content = reportContent,
                    type = "Bài viết",
                    reportedId = postOwnerId,
                    postId = postId  // Thêm postId cho report bài viết
                )
            )

            if (result.isSuccess) {
                Toast.makeText(context, "Đã gửi báo cáo bài viết thành công", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ReportViewModel", "createReportForPost thất bại", result.exceptionOrNull())
                Toast.makeText(context, "Gửi báo cáo thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createReportForUser(
        context: Context,
        reportContent: String,
        reporterId: String,
        reportedUserId: String,
        reporterModel: String
    ) {
        viewModelScope.launch {
            val result = reportRepository.sendReport(
                ReportRequest(
                    reporter = reporterId,
                    reporterModel = reporterModel,
                    content = reportContent,
                    type = "Người dùng",
                    reportedId = reportedUserId,
                    postId = null  // Không có postId cho report người dùng
                )
            )

            if (result.isSuccess) {
                Toast.makeText(context, "Đã gửi báo cáo người dùng thành công", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ReportViewModel", "createReportForUser thất bại", result.exceptionOrNull())
                Toast.makeText(context, "Gửi báo cáo thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createReportForDoctor(
        context: Context,
        reportContent: String,
        reporterId: String,
        doctorId: String,
        reporterModel: String
    ) {
        viewModelScope.launch {
            val result = reportRepository.sendReport(
                ReportRequest(
                    reporter = reporterId,
                    reporterModel = reporterModel,
                    content = reportContent,
                    type = "Bác sĩ",
                    reportedId = doctorId,
                    postId = null
                )
            )

            if (result.isSuccess) {
                Toast.makeText(context, "Đã gửi báo cáo bác sĩ thành công", Toast.LENGTH_SHORT).show()
            } else {
                Log.e("ReportViewModel", "createReportForDoctor thất bại", result.exceptionOrNull())
                Toast.makeText(context, "Gửi báo cáo thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getAllReport() {
        viewModelScope.launch {
            // Repository trả về Result<List<ComplaintData>>
            val result = reportRepository.getAllReports()

            // Dùng .fold để xử lý cả thành công và thất bại
            result.fold(
                onSuccess = { complaintList ->
                    _reportList.value = complaintList
                    println("Danh sach bao cao:" + complaintList)
                },
                onFailure = { exception ->
                    Log.e("ReportViewModel", "getAllReport lỗi", exception)
                    _reportList.value = emptyList() // Xóa list cũ nếu lỗi
                }
            )
        }
    }

    private val _userReportList = MutableStateFlow<List<ComplaintData>>(emptyList())
    val userReportList: MutableStateFlow<List<ComplaintData>> get() = _userReportList

    fun getReportByUserId(userId: String) {
        viewModelScope.launch {
        val result = reportRepository.getReportByUserId(userId)
            result.fold(
                onSuccess = { complaintList ->
                    _userReportList.value = complaintList
                    println("Danh sach bao cao:" + complaintList)
                },
                onFailure = { exception ->
                    Log.e("ReportViewModel", "getAllReport lỗi", exception)
                    _userReportList.value = emptyList() // Xóa list cũ nếu lỗi
                }
            )

        }
    }
    /**
     * Thêm hàm xóa để UI gọi.
     * VM sẽ xử lý logic (gọi repo, báo toast, và TẢI LẠI danh sách).
     */
    fun deleteReport(reportId: String, context: Context) {
        viewModelScope.launch {
            // 1. Kiểm tra điều kiện (logic này nên ở trong VM)
            val report = _reportList.value.find { it.reportId == reportId }
            if (report?.status == "opened") {
                Toast.makeText(context, "Chưa thể xóa khiếu nại đang chờ xử lý", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // 2. Gọi Repository
            val result = reportRepository.deleteReport(reportId)

            // 3. Xử lý kết quả
            if (result.isSuccess) {
                Toast.makeText(context, "Đã xóa khiếu nại", Toast.LENGTH_SHORT).show()
                // 4. Tải lại danh sách để làm mới UI
                getAllReport()
            } else {
                Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                Log.e("ReportViewModel", "deleteReport thất bại", result.exceptionOrNull())
            }
        }
    }

    fun sendAdminResponse(
        id: String,
        responseContent: String,
        responseTime: String,
        context: Context
    ) {
        viewModelScope.launch {
            if (responseContent.isBlank()) {
                Toast.makeText(context, "Vui lòng nhập nội dung phản hồi", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val result = reportRepository.sendAdminResponse(id, responseContent, responseTime)

            if (result.isSuccessful) {
                Toast.makeText(context, "Phản hồi đã được gửi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Gửi phản hồi thất bại", Toast.LENGTH_SHORT).show()
            }

        }
    }

}