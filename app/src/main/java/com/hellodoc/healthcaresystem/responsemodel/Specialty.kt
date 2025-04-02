package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class GetSpecialtyResponse (
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String
)