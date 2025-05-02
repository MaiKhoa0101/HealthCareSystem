package com.hellodoc.healthcaresystem.api

import com.hellodoc.healthcaresystem.requestmodel.CreateNotificationRequest
import com.hellodoc.healthcaresystem.responsemodel.CreateNotificationResponse
import com.hellodoc.healthcaresystem.responsemodel.GetNotificationResponse
import com.hellodoc.healthcaresystem.responsemodel.NotificationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationService {
    @Headers("Content-Type: application/json")
    @GET("notification/get-by-user-id/{userId}")
    suspend fun getNotificationByUserId(
        @Path("userId") userId: String
    ): Response<List<NotificationResponse>>

    @POST("notification/create")
    suspend fun createNotification(
        @Body createNotificationRequest: CreateNotificationRequest
    ): Response<NotificationResponse>

    @PATCH("notification/{notificationId}/mark-as-read")
    suspend fun markAsRead(@Path("notificationId") notificationId: String): Response<NotificationResponse>
}