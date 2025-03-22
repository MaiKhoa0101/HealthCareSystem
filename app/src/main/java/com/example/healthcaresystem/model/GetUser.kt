package com.example.healthcaresystem.model

import com.google.gson.annotations.SerializedName

data class GetUser(
    @SerializedName("_id") val id: String,  // Maps `_id` from the JSON response to this property
    val role: String,
    val email: String,
    val name: String,
    val phone: String,
    val password: String,
)
