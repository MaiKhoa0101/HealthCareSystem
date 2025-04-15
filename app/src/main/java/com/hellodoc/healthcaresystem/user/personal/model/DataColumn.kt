package com.hellodoc.healthcaresystem.user.personal.model

import androidx.annotation.DrawableRes

data class ContentTitle(
    val introduce: String,
    val certificate: String,
    val workplace: String,
    val service: String
)

data class Contents(
    val introduce: String,
    val certificate1: String,
    val certificate2: String,
    val workplace: String,
    val services: List<Pair<String, Int>>
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

