package com.hellodoc.healthcaresystem.model.dataclass.responsemodel

import com.google.gson.annotations.SerializedName

data class GetSpecialtyResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon:String,
    @SerializedName("doctors") val doctors: List<Doctor> = emptyList(),
    @SerializedName("description") val description: String
)

data class Doctor(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("specialty") val specialty: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("avatarURL") val avatarURL: String?,
    @SerializedName("isClinicPaused") val isClinicPaused: Boolean?
)