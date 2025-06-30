package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.CreateNotificationRequest
import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
import com.hellodoc.healthcaresystem.responsemodel.NotificationResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {



    fun createReport(
        context: android.content.Context,
        reportContent: String,
        selectedType: String,
        reporterId: String,
        reportedId: String,
        reporterModel: String,
        postId: String?= null
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.reportService.sendReport(
                    ReportRequest(
                        reporter = reportedId,
                        reporterModel = reporterModel,
                        content = reportContent,
                        type = selectedType,
                        reportedId = reporterId,
                        postId = postId
                    )
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Đã gửi báo cáo thành công",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    println(response)
                    Toast.makeText(
                        context,
                        "Gửi báo cáo thất bại",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Lỗi kết nối đến server",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

}