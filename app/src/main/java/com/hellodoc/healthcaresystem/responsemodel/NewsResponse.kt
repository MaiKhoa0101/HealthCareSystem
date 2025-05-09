package com.hellodoc.healthcaresystem.responsemodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsResponse(
    val id: String,
    val title: String,
    val content: String,
    val media: List<String>,
    val createdAt: String,
) : Parcelable

