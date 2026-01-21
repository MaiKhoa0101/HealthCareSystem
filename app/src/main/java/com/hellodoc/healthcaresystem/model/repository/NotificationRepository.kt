package com.hellodoc.healthcaresystem.model.repository

import com.hellodoc.healthcaresystem.model.api.NotificationService
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NotificationResponse
import com.hellodoc.healthcaresystem.requestmodel.CreateNotificationRequest
import jakarta.inject.Inject
import retrofit2.Response

interface NotificationRepository{
    suspend fun getNotificationByUserId(
        userId: String
    ): Response<List<NotificationResponse>>


    suspend fun getUnreadNotifications(
        userId: String
    ): Response<List<NotificationResponse>>

    suspend fun getUnreadCount(
        userId: String
    ): Response<Int>

    suspend fun createNotification(
        createNotificationRequest: CreateNotificationRequest
    ): Response<NotificationResponse>

    suspend fun markAsRead(
        notificationId: String
    ): Response<NotificationResponse>

    suspend fun markAllAsRead(
        userId: String
    ): Response<List<NotificationResponse>>

    suspend fun deleteNotification(
        notificationId: String
    ): Response<Unit>
}

class NotificationRepositoryImpl @Inject constructor(
    private val notificationService: NotificationService
): NotificationRepository {
    override suspend fun getNotificationByUserId(
        userId: String
    ) = notificationService.getNotificationByUserId(userId)

    override suspend fun getUnreadNotifications(
        userId: String
    ) = notificationService.getUnreadNotifications(userId)

    override suspend fun getUnreadCount(
        userId: String
    ) = notificationService.getUnreadCount(userId)

    override suspend fun createNotification(
        createNotificationRequest: CreateNotificationRequest
    ) = notificationService.createNotification(createNotificationRequest)

    override suspend fun markAsRead(
        notificationId: String
    ) = notificationService.markAsRead(notificationId)

    override suspend fun markAllAsRead(
        userId: String
    ) = notificationService.markAllAsRead(userId)

    override suspend fun deleteNotification(
        notificationId: String
    ) = notificationService.deleteNotification(notificationId)
}