package com.hellodoc.healthcaresystem.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.CreateNotificationRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NotificationResponse
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> get() = _notifications

    fun fetchNotificationByUserId(userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.notificationService.getNotificationByUserId(userId)
                if (response.isSuccessful) {
                    _notifications.value = response.body() ?: emptyList()
                    println("OK 1" + response.body())
                } else {
                    println("Lỗi API fetchNotificationByUserId: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createNotification(userId: String, userModel: String, type: String, content: String, navigatePath: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.notificationService.createNotification(CreateNotificationRequest(userId, userModel, type, content, navigatePath))
                if (response.isSuccessful) {
                    response.body()?.let { newNotification ->
                        _notifications.value += newNotification
                        println("OK 1 $newNotification")
                    }
                } else {
                    println("Lỗi API createNotification: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                // Gọi API markAsRead
                val response = RetrofitInstance.notificationService.markAsRead(notificationId)
                if (response.isSuccessful) {
                    println("Cập nhật trạng thái đã đọc thành công")
                    // Cập nhật trạng thái thông báo trong danh sách
                    _notifications.value = _notifications.value.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(isRead = true)
                        } else {
                            notification
                        }
                    }
                } else {
                    println("Lỗi API markAsRead: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}