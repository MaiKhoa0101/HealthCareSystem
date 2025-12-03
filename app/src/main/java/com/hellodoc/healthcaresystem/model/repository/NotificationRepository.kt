package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.api.NotificationService
import com.hellodoc.healthcaresystem.requestmodel.CreateNotificationRequest
import jakarta.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationService: NotificationService
) {
    suspend fun getNotificationByUserId(
        userId: String
    ) = notificationService.getNotificationByUserId(userId)

    suspend fun getUnreadNotifications(
        userId: String
    ) = notificationService.getUnreadNotifications(userId)

    suspend fun getUnreadCount(
        userId: String
    ) = notificationService.getUnreadCount(userId)

    suspend fun createNotification(
        createNotificationRequest: CreateNotificationRequest
    ) = notificationService.createNotification(createNotificationRequest)

    suspend fun markAsRead(
        notificationId: String
    ) = notificationService.markAsRead(notificationId)

    suspend fun markAllAsRead(
        userId: String
    ) = notificationService.markAllAsRead(userId)

    suspend fun deleteNotification(
        notificationId: String
    ) = notificationService.deleteNotification(notificationId)
}