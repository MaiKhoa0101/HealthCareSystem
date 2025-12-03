package com.hellodoc.healthcaresystem.model.dataclass.responsemodel

import com.google.gson.annotations.SerializedName


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

data class DeleteUserResponse(
    val message: String
)
