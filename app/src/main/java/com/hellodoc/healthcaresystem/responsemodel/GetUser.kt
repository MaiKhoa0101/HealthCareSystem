package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("_id") val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String,
    val isDoctor: Boolean,
    val verified: Boolean,
    val insurance: List<String>,
    val workingHours: List<String>,
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("__v") val timeChange: Int,
    val userImage: String

    )