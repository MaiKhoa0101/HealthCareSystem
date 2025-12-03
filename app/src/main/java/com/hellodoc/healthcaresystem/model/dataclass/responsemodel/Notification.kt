package com.hellodoc.healthcaresystem.model.dataclass.responsemodel

import com.google.gson.annotations.SerializedName

data class GetNotificationResponse(
    @SerializedName("user") val user: String,
    @SerializedName("content") val content: String,
    @SerializedName("isRead") val isRead: Boolean,
)

data class CreateNotificationResponse(
    @SerializedName("user") val user: String,
    @SerializedName("content") val content: String,
    @SerializedName("isRead") val isRead: Boolean,
)

data class NotificationResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("user") val user: String,
    @SerializedName("type") val type: String,
    @SerializedName("content") val content: String,
    @SerializedName("navigatePath") val navigatePath: String,
    @SerializedName("isRead") val isRead: Boolean,
    @SerializedName("createdAt") val createdAt: String,
)