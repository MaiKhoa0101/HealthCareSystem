package com.hellodoc.healthcaresystem.user.post.model

import androidx.annotation.DrawableRes

data class HeaderItem(
    val title: String,
    @DrawableRes val image: Int,
    val button: String
)

data class ContainerPost(
    val name: String,
    val imageUrl: String,
    val lable: String = ""
)

data class FooterItem(
    val name: String = "",
    val imageUrl: String
)

data class ContentPost(
    val content: String
)
