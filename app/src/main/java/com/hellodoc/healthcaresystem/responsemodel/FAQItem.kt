package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetFAQItemResponse (
    @SerializedName("_id") val id: String,
    @SerializedName("question") val question: String
)