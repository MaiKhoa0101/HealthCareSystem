package com.hellodoc.healthcaresystem.responsemodel

import com.google.gson.annotations.SerializedName

data class ReviewResponse(
    @SerializedName("_id")
    val id: String?,
    @SerializedName("rating")
    val rating: Int?,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("user")
    val user: ReviewUser?
)

data class ReviewUser(
    @SerializedName("_id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("avatarURL")
    val userImage: String?
)
