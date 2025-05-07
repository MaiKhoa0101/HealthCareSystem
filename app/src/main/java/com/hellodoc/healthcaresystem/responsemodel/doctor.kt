package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetDoctorResponse (
    @SerializedName("_id") val id: String, // Đổi thành `id` để dễ đọc hơn
    @SerializedName("role") val role: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("workingHours") val workHour:List<WorkHour>,
    val address: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String,
    @SerializedName("specialty") val specialty: Specialty,
    @SerializedName("experience") val experience: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("avatarURL") val avatarURL: String?,
    @SerializedName("hospital") val hospital: String?,
    @SerializedName("certificates")
    val certificates: String?,
    @SerializedName("services")
    val services: List<ServiceOutput>,
    @SerializedName("patientsCount")
    val patientsCount: Int?,
    @SerializedName("ratingsCount")
    val ratingsCount: Int?
)

data class Specialty (
    @SerializedName("_id") val id:String,
    @SerializedName("name") val name: String
)


data class ApplyDoctor(
    val message: String
)

data class WorkHour(
    val dayOfWeek: Int,
    val hour: Int,
    val minute: Int
)

data class PendingDoctorResponse(
    @SerializedName("_id") val id: String,
    val userId: String,
    val CCCD: String,
    val license: String,
    val name: String,
    val phone: String,
    val email: String,
    val specialty: Specialty,
    val faceUrl: String?,
    val avatarURL: String?,
    val licenseUrl: String?,
    val backCccdUrl: String?,
    val frontCccdUrl: String?
)

data class ReturnPendingDoctorResponse(
    val message: String
)
