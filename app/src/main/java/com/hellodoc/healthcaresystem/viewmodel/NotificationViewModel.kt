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

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> get() = _unreadCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun fetchNotificationByUserId(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = notificationRepository.getNotificationByUserId(userId)
                if (response.isSuccessful) {
                    _notifications.value = response.body() ?: emptyList()
                    updateUnreadCount()
                    println("OK 1" + response.body())
                } else {
                    println("Lỗi API fetchNotificationByUserId: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchUnreadNotifications(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = notificationRepository.getUnreadNotifications(userId)
                if (response.isSuccessful) {
                    _notifications.value = response.body() ?: emptyList()
                    updateUnreadCount()
                    println("Fetched unread notifications: ${response.body()}")
                } else {
                    println("Lỗi API fetchUnreadNotifications: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchUnreadCount(userId: String) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.getUnreadCount(userId)
                if (response.isSuccessful) {
                    _unreadCount.value = response.body() ?: 0
                    println("Unread count: ${response.body()}")
                } else {
                    println("Lỗi API fetchUnreadCount: ${response.errorBody()?.string()}")
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
                        updateUnreadCount()
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
                    updateUnreadCount()
                } else {
                    println("Lỗi API markAsRead: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.markAllAsRead(userId)
                if (response.isSuccessful) {
                    println("Đánh dấu tất cả đã đọc thành công")
                    // Cập nhật tất cả thông báo thành đã đọc
                    _notifications.value = _notifications.value.map { notification ->
                        notification.copy(isRead = true)
                    }
                    _unreadCount.value = 0
                } else {
                    println("Lỗi API markAllAsRead: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                val response = notificationRepository.deleteNotification(notificationId)
                if (response.isSuccessful) {
                    println("Xóa thông báo thành công")
                    // Xóa thông báo khỏi danh sách
                    _notifications.value = _notifications.value.filter { it.id != notificationId }
                    updateUnreadCount()
                } else {
                    println("Lỗi API deleteNotification: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUnreadCount() {
        _unreadCount.value = _notifications.value.count { !it.isRead }
    }
}