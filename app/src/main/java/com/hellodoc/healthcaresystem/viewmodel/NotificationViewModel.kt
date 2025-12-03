package com.hellodoc.healthcaresystem.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hellodoc.healthcaresystem.requestmodel.CreateNotificationRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NotificationResponse
import com.hellodoc.healthcaresystem.model.repository.NotificationRepository
import com.hellodoc.healthcaresystem.model.retrofit.RetrofitInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> get() = _notifications

    fun fetchNotificationByUserId(userId: String) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.getNotificationByUserId(userId)
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
                val response = notificationRepository.createNotification(CreateNotificationRequest(userId, userModel, type, content, navigatePath))
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
                val response = notificationRepository.markAsRead(notificationId)
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