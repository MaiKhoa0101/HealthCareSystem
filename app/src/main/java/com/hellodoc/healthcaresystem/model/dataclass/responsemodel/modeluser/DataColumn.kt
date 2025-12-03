package com.hellodoc.healthcaresystem.model.dataclass.responsemodel.modeluser

import androidx.annotation.DrawableRes
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.ServiceOutput

data class ContentTitle(
    val introduce: String,
    val certificate: String,
    val workplace: String,
    val service: String
)

data class Contents(
    val introduce: String,
    val certificate: String,
    val workplace: String,
    val services: List<ServiceOutput>
)

data class Images(
    @DrawableRes val image1: Int,
    @DrawableRes val image2: Int,
    @DrawableRes val image3: Int,
)

data class Rating(
    val number: Double,
    val sum: Int
)

data class Review(
    val userName: String,
    @DrawableRes val userAvatar: Int,
    val ratingStars: Int,
    val reviewText: String,
    val timeAgo: String
)

