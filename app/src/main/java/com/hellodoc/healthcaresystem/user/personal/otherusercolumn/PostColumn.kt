package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.responsemodel.ContentPost
import com.hellodoc.healthcaresystem.responsemodel.FooterItem
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.user.post.model.ViewBanner
import com.hellodoc.healthcaresystem.user.post.model.ViewPost
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel

@Composable
fun PostColumn(posts: List<PostResponse>) {
    // Nếu không có bài viết thì hiển thị Empty
    if (posts.isEmpty()) {
        Text(
            text = "Chưa có bài viết nào.",
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp,
            color = Color.Gray
        )
    } else {
        posts.forEach { postItem ->
            ViewPostOwner(
                containerPost = ContainerPost(
                    imageUrl = postItem.user.avatarURL
                        ?: "https://default.avatar.url/no-avatar.jpg",
                    name = postItem.user.name
                ),
                contentPost = ContentPost(
                    content = postItem.content
                ),
                footerItem = FooterItem(
                    imageUrl = postItem.media.firstOrNull()
                        ?: "https://default.image.url/no-image.jpg"
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ViewPostOwner(
    containerPost: ContainerPost,
    contentPost: ContentPost,
    footerItem: FooterItem,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.White
    var expanded by remember { mutableStateOf(false) }
    println ("footer item: "+footerItem)
    Column(
        modifier = modifier
            .background(backgroundColor, RectangleShape)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
    ) {
        // Row for Avatar and Name
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            AsyncImage(
                model = containerPost.imageUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )

            // Name
            Text(
                text = containerPost.name,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
            )
        }

        // Content bài viết
        Text(
            text = contentPost.content,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis
        )

        // Nút "Xem thêm" / "Thu gọn"
        Text(
            text = if (expanded) "Thu gọn" else "Xem thêm",
            color = Color.Blue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(top = 4.dp)
        )

        AsyncImage(
            model = footerItem.imageUrl,
            contentDescription = "Post Image",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.LightGray)
        )

    }
}