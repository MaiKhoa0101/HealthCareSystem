package com.parkingSystem.parkingSystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetFAQItemResponse (
    @SerializedName("_id") val id: String,
    @SerializedName("question") val question: String
)