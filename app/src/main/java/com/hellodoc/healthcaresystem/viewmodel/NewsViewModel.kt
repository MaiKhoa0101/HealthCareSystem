package com.hellodoc.healthcaresystem.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.responsemodel.NewsResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class NewsViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _newsList = MutableStateFlow<List<NewsResponse>>(emptyList())
    val newsList: StateFlow<List<NewsResponse>> = _newsList

    fun getAllNews() {
        viewModelScope.launch {
            try {
                val result = RetrofitInstance.newsService.getAllNews()
                if (result.isSuccessful) {
                    _newsList.value = result.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Lỗi khi lấy tin tức: ${e.message}")
            }
        }
    }

    fun createNews(
        adminId: String,
        title: String?,
        content: String?,
        imageUris: List<Uri>?,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("Debug", "adminId = $adminId")
                Log.d("Debug", "title = $title")
                Log.d("Debug", "content = $content")
                Log.d("Debug", "image count = ${imageUris?.size}")

                if (title.isNullOrBlank() || content.isNullOrBlank()) {
                    Toast.makeText(context, "Thiếu thông tin bắt buộc", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val adminIdPart: RequestBody = adminId.toRequestBody("text/plain".toMediaTypeOrNull())
                val titlePart: RequestBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val contentPart: RequestBody = content.toRequestBody("text/plain".toMediaTypeOrNull())

                val imageParts = imageUris?.mapNotNull { uri ->
                    try {
                        val inputStream = context.contentResolver.openInputStream(uri)
                        val tempFile = File.createTempFile("image_${System.currentTimeMillis()}", ".jpg", context.cacheDir)
                        inputStream?.use { input ->
                            tempFile.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("images", tempFile.name, requestFile)
                    } catch (e: Exception) {
                        Log.e("NewsViewModel", "Lỗi xử lý ảnh: ${e.message}")
                        null
                    }
                } ?: emptyList()

                val response = RetrofitInstance.newsService.createNews(
                    adminId = adminIdPart,
                    title = titlePart,
                    content = contentPart,
                    images = imageParts
                )

                if (response.isSuccessful) {
                    Toast.makeText(context, "Đăng tin tức thành công", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    Toast.makeText(context, "Thất bại: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi đăng tin: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteNews(newsId: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.newsService.deleteNews(newsId)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Đã xoá tin tức", Toast.LENGTH_SHORT).show()
                    getAllNews() // Cập nhật lại danh sách
                } else {
                    Toast.makeText(context, "Xoá thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi xoá tin tức: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
