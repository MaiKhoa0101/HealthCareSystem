package com.parkingSystem.parkingSystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetRemoteMedicalOptionResponse (
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String
)