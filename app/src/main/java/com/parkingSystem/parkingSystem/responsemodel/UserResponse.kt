package com.parkingSystem.parkingSystem.responsemodel

import com.google.gson.annotations.SerializedName


data class UserResponse(
    val users: List<User> =emptyList(),
)

data class User(
    @SerializedName("id") val uid: String = "",
    val name: String? = null ,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val address: String? = null,
    val role: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)

data class DeleteUserResponse(
    val message: String
)
