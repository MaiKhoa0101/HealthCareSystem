package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.CreateNotificationRequest
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.NotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationService {
    @Headers("Content-Type: application/json")
    @GET("notification/user/{userId}")
    suspend fun getNotificationByUserId(
        @Path("userId") userId: String
    ): Response<List<NotificationResponse>>

    @Headers("Content-Type: application/json")
    @GET("notification/get-by-user-id/{userId}/unread")
    suspend fun getUnreadNotifications(
        @Path("userId") userId: String
    ): Response<List<NotificationResponse>>

    @Headers("Content-Type: application/json")
    @GET("notification/get-by-user-id/{userId}/unread-count")
    suspend fun getUnreadCount(
        @Path("userId") userId: String
    ): Response<Int>

    @POST("notification/create")
    suspend fun createNotification(
        @Body createNotificationRequest: CreateNotificationRequest
    ): Response<NotificationResponse>

    @PATCH("notification/{notificationId}/mark-as-read")
    suspend fun markAsRead(
        @Path("notificationId") notificationId: String
    ): Response<NotificationResponse>

    @PATCH("notification/get-by-user-id/{userId}/mark-all-as-read")
    suspend fun markAllAsRead(
        @Path("userId") userId: String
    ): Response<List<NotificationResponse>>

    @DELETE("notification/{notificationId}/delete")
    suspend fun deleteNotification(
        @Path("notificationId") notificationId: String
    ): Response<Unit>
}