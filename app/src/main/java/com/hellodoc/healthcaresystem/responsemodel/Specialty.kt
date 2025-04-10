package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetSpecialtyResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("doctors") val doctors: List<Doctor> = emptyList()
)

data class Doctor(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("specialty") val specialty: String?,
    @SerializedName("address") val address: String?
)