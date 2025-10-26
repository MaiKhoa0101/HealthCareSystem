package com.parkingSystem.parkingSystem.viewmodel

import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parkingSystem.parkingSystem.requestmodel.ReportRequest
import com.parkingSystem.parkingSystem.retrofit.RetrofitInstance
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
                println("Cac tham so duoc truyen vao ham bao cao:" +
                        "\nReportContent: $reportContent" +
                        "\nSelectedType: $selectedType" +
                        "\nReporterId:$reporterId" +
                        "\nReportedId: $reportedId"+"\nReporterModel: $reporterModel"+"\npostId: $postId")
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
                    println("Ket qua bao cao nguoi dung"+response)
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