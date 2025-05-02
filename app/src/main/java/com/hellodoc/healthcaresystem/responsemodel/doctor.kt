package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetDoctorResponse (
    @SerializedName("_id") val id: String, // Đổi thành `id` để dễ đọc hơn
    @SerializedName("role") val role: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    val address: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("specialty") val specialty: Specialty,
    @SerializedName("experience") val experience: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("avatarURL") val avatarURL: String?,
    @SerializedName("hospital") val hospital: String?,
    @SerializedName("certificates")
    val certificates: List<String>?,
    @SerializedName("services")
    val services: List<ServiceResponse>?,
    @SerializedName("patientsCount")
    val patientsCount: Int?,
    @SerializedName("ratingsCount")
    val ratingsCount: Int?
)

data class Specialty (
    @SerializedName("_id") val id:String,
    @SerializedName("name") val name: String
)

data class ServiceResponse(
    val name: String?,
    val price: Int?
)

data class ApplyDoctor(
    val message: String
)
