package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.responsemodel.ContentPost
import com.hellodoc.healthcaresystem.responsemodel.FooterItem
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.user.personal.userModel
import com.hellodoc.healthcaresystem.user.post.userId
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import kotlinx.coroutines.launch

@Composable
fun PostColumn(
    posts: List<PostResponse>,
    postViewModel: PostViewModel,
    userId: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, color = Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Bài viết đã đăng",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        println("San pham lay duoc post: "+posts)

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
                ViewUserPost(
                    postId = postItem.id,
                    containerPost = ContainerPost(
                        name = postItem.user.name,
                        imageUrl = postItem.user.avatarURL ?: ""
                    ),
                    contentPost = ContentPost(postItem.content),
                    footerItem = FooterItem(imageUrl = postItem.media.firstOrNull() ?: ""),
                    postViewModel = postViewModel,
                    currentUserId = userId
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun ViewUserPost(
    postId: String,
    containerPost: ContainerPost,
    contentPost: ContentPost,
    footerItem: FooterItem,
    postViewModel: PostViewModel,
    currentUserId: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.White
    var expanded by remember { mutableStateOf(false) }
    var isCommenting by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }
//    val commentsState = remember { mutableStateOf<List<GetCommentPostResponse>>(emptyList()) }
    var shouldFetchComments by remember { mutableStateOf(false) }
//    var isFavorited by remember { mutableStateOf(false) }
//    var totalFavorites by remember { mutableIntStateOf(0) }

    val isFavoritedMap by postViewModel.isFavoritedMap.collectAsState()
    val totalFavoritesMap by postViewModel.totalFavoritesMap.collectAsState()

    val isFavorited = isFavoritedMap[postId] ?: false
    val totalFavorites = totalFavoritesMap[postId] ?: "0"

    val commentsMap by postViewModel.commentsMap.collectAsState()
    val comments = commentsMap[postId] ?: emptyList()


    LaunchedEffect(postId) {
        // Gọi API và cập nhật state khi dữ liệu được fetch về
        postViewModel.fetchFavoriteForPost(postId, userId)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(shouldFetchComments) {
        if (shouldFetchComments) {
            coroutineScope.launch {
                postViewModel.fetchComments(postId)
                shouldFetchComments = false
            }
        }
    }

    println("footer item: " + footerItem)
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

        // ICON like & comment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // LIKE
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    postViewModel.updateFavoriteForPost(
                        postId = postId,
                        userId = currentUserId,
                        userModel = userModel
                    )
//                    isFavorited = !isFavorited
                }
            ) {
                Icon(
                    painter = painterResource(id = if (isFavorited) R.drawable.liked else R.drawable.like),
                    contentDescription = "Like",
                    tint = if (isFavorited) Color.Red else Color.Black,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("$totalFavorites Likes", fontSize = 18.sp)
            }

            // COMMENT
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isCommenting = !isCommenting
                    if (isCommenting) {
                        shouldFetchComments = true
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.comment),
                    contentDescription = "Comment",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Comment", fontSize = 18.sp)
            }
        }

        // UI COMMENT
        if (isCommenting) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Bình luận:", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                if (comments.isNotEmpty()) {
                    Column {
                        comments.forEach { comment ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = comment.user?.avatarURL ?: "",
                                    contentDescription = "avatar",
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                )
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(
                                        comment.user?.name ?: "Ẩn danh",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(comment.content, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                } else {
                    Text("Chưa có bình luận nào.")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Nhập bình luận...") }
                    )
                    Button(onClick = {
                        postViewModel.sendComment(postId, currentUserId, userModel, newComment)
                        newComment = ""
                    }) {
                        Text("Gửi")
                    }
                }
            }
        }
    }
}