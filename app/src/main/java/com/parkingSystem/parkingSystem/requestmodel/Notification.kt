package com.parkingSystem.parkingSystem.requestmodel

import com.google.gson.annotations.SerializedName

data class CreateNotificationRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("userModel") val userModel: String,
    @SerializedName("type") val type: String,
    @SerializedName("content") val content: String,
    @SerializedName("navigatePath") val navigatePath: String,
)