package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetDoctorResponse (
    @SerializedName("_id") val id: String, // Đổi thành `id` để dễ đọc hơn
    @SerializedName("role") val role: String,
    @SerializedName("avatarURL") val avatarURL: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("specialty") val specialty: Specialty
)
data class Specialty (
    @SerializedName("_id") val id:String,
    @SerializedName("name") val name: String
)