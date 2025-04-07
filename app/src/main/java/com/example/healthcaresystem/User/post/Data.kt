package com.example.healthcaresystem.User.post

import androidx.annotation.DrawableRes

data class HeaderItem(
    val title: String,
    @DrawableRes val image: Int,
    val button: String
)

data class ContainerPost(
    val name: String,
    @DrawableRes val image: Int,
    @DrawableRes val image2: Int,
    val lable: String
)

data class FooterItem(
    val name: String,
    @DrawableRes val image: Int
)