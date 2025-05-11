package com.hellodoc.healthcaresystem.responsemodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsResponse(
    @SerializedName("_id")
    val id: String,
    val title: String,
    val content: String,
    val media: List<String>,
    val createdAt: String,
) : Parcelable

data class GetNewsCommentResponse(
    val id: String,
    val user: User,
    val content: String,
    val createdAt: String
)