package com.parkingSystem.parkingSystem.responsemodel

import androidx.annotation.DrawableRes

data class HeaderItem(
    val title: String,
    @DrawableRes val image: Int,
    val button: String
)

data class ContainerPost(
    val id: String? = null,
    val name: String,
    val imageUrl: String,
    val label: String = ""
)

data class FooterItem(
    val name: String = "",
    val imageUrl: String
)

data class ContentPost(
    val content: String
)
