package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import androidx.compose.runtime.Composable
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.post.model.ContainerPost
import com.hellodoc.healthcaresystem.user.post.model.ContentPost
import com.hellodoc.healthcaresystem.user.post.model.FooterItem
import com.hellodoc.healthcaresystem.user.post.model.ViewBanner
import com.hellodoc.healthcaresystem.user.post.model.ViewPost

@Composable
fun PostColumn(){
    ViewBanner()
    ViewPost(
        containerPost = ContainerPost(
            image = R.drawable.img,
            name = "Khoa xinh gái",
            lable = "bla bla  "
        ),
        contentPost = ContentPost(
            content = "bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla"
        ),
        footerItem = FooterItem(
            name = "null",
            image = R.drawable.avarta
        )
    )
}