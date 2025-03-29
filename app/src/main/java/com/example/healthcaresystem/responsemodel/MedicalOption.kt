package com.example.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetMedicalOptionResponse (
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String
)