package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.responsemodel.ContentPost
import com.hellodoc.healthcaresystem.responsemodel.FooterItem
import com.hellodoc.healthcaresystem.user.post.model.ViewBanner
import com.hellodoc.healthcaresystem.user.post.model.ViewPost
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.api.PostResponse

@Composable
fun PostColumn(postViewModel: PostViewModel = viewModel()) {
    val posts: List<PostResponse> by postViewModel.posts

    Column {
        ViewBanner()

        posts.forEach { post ->
            ViewPost(
                containerPost = ContainerPost(
                    name = post.user.name,
                    imageUrl = post.user.imageUrl ?: ""
                ),
                contentPost = ContentPost(content = post.content),
                footerItem = FooterItem(
                    name = "",
                    imageUrl = post.media.firstOrNull() ?: ""
                )
            )
        }
    }
}
