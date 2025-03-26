package com.example.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetDoctorsResponse (
    @SerializedName("_id") val id: String, // Đổi thành `id` để dễ đọc hơn
    @SerializedName("role") val role: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("specialty") val specialty: String
)