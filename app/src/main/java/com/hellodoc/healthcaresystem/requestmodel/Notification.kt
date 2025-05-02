package com.hellodoc.healthcaresystem.requestmodel

import com.google.gson.annotations.SerializedName

data class CreateNotificationRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("userModel") val userModel: String,
    @SerializedName("content") val content: String,
)