package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Required


data class UserResponse(
    val users: List<User>,
    val doctors: List<User>
)

data class User(
    @SerializedName("_id") val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val address: String,
    val role: String,
    val createdAt: String,
    val updatedAt: String,
    val avatarURL: String
)

data class UserResponse(
    val user: List<User>,
    val doctors: List<Doctor>
) {
    data class User(
        @SerializedName("_id") val id: String,
        val name: String,
        val email: String,
        val phone: String,
        val password: String,
        val address: String,
        val role: String,
        val verified: Boolean,
        val workingHours: List<String>,
        val createdAt: String,
        val updatedAt: String,
        @SerializedName("__v") val timeChange: Int,
        val avatarURL: String
    )

    data class Doctor(
        @SerializedName("_id") val id: String,
        val name: String,
        val email: String,
        val phone: String,
        val password: String,
        val address: String,
        val role: String,
        val verified: Boolean,
        val workingHours: List<String>,
        val createdAt: String,
        val updatedAt: String,
        @SerializedName("__v") val timeChange: Int,
        val avatarURL: String
    )
}

