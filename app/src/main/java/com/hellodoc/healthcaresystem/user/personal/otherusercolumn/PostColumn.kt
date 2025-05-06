package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.DpOffset
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.hellodoc.healthcaresystem.user.notification.timeAgoInVietnam
import com.google.accompanist.pager.*


@Composable
fun PostColumn(
    posts: List<PostResponse>,
    postViewModel: PostViewModel,
    userId: String,
    onClickReport: (String) -> Unit
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
                ViewPostOwner(
                    postId = postItem.id,
                    containerPost = ContainerPost(
                        id = postItem.user.id,
                        name = postItem.user.name,
                        imageUrl = postItem.user.avatarURL ?: ""
                    ),
                    contentPost = ContentPost(postItem.content),
                    footerItem = FooterItem(imageUrl = postItem.media.joinToString("|")),
                    createdAt = postItem.createdAt,
                    postViewModel = postViewModel,
                    currentUserId = userId,
                    onClickReport = onClickReport
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun ViewPostOwner(
    postId: String,
    containerPost: ContainerPost,
    contentPost: ContentPost,
    footerItem: FooterItem,
    createdAt: String,
    postViewModel: PostViewModel,
    currentUserId: String,
    onClickReport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.White
    var expanded by remember { mutableStateOf(false) }
    var isCommenting by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }
    var shouldFetchComments by remember { mutableStateOf(false) }

    val isFavoritedMap by postViewModel.isFavoritedMap.collectAsState()
    val totalFavoritesMap by postViewModel.totalFavoritesMap.collectAsState()

    val isFavorited = isFavoritedMap[postId] ?: false
    val totalFavorites = totalFavoritesMap[postId] ?: "0"

    val commentsMap by postViewModel.commentsMap.collectAsState()
    val comments = commentsMap[postId] ?: emptyList()
    var showPostReportBox by remember { mutableStateOf(false) }
    var editingCommentId by remember { mutableStateOf<String?>(null) }
    var editedCommentContent by remember { mutableStateOf("") }
    var activeMenuCommentId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(editingCommentId) {
        if (editingCommentId == null) {
            // Sau khi lưu xong và thoát khỏi chế độ sửa, cập nhật lại UI
            postViewModel.fetchComments(postId)
        }
    }
    LaunchedEffect(postId) {
        // Gọi API và cập nhật state khi dữ liệu được fetch về
        postViewModel.fetchFavoriteForPost(postId, userId)
    }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val mediaList = footerItem.imageUrl.split("|").filter { it.isNotBlank() }

    LaunchedEffect(shouldFetchComments) {
        if (shouldFetchComments) {
            coroutineScope.launch {
                postViewModel.fetchComments(postId)
                shouldFetchComments = false
            }
        }
    }
    Box(modifier = modifier.fillMaxWidth()) {
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // để dồn 2 phần trái - phải
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = containerPost.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = containerPost.name,
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                        Text(
                            text = createdAt.timeAgoInVietnam(),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = { showPostReportBox = !showPostReportBox },
                    modifier = Modifier
                        .padding(end = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }
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
            //anh
            if (mediaList.isNotEmpty()) {
                HorizontalPager(
                    count = mediaList.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) { page ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = mediaList[page],
                            contentDescription = "Post Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.LightGray)
                        )
                        // Ô số thứ tự ảnh
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${page + 1}/${mediaList.size}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

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
                                val coroutineScope = rememberCoroutineScope()
//                                var showMenu by remember { mutableStateOf(false) }
                                ConstraintLayout(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    val (avatar, contentCol, menuIcon) = createRefs()

                                    AsyncImage(
                                        model = comment.user?.avatarURL ?: "",
                                        contentDescription = "avatar",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .constrainAs(avatar) {
                                                top.linkTo(parent.top)
                                                start.linkTo(parent.start)
                                            }
                                    )

                                    Column(modifier = Modifier
                                        .padding(start = 8.dp)
                                        .constrainAs(contentCol) {
                                            top.linkTo(avatar.top)
                                            start.linkTo(avatar.end)
                                            end.linkTo(menuIcon.start)
                                            width = androidx.constraintlayout.compose.Dimension.fillToConstraints
                                        }
                                    ) {
                                        Text(comment.user?.name ?: "Ẩn danh", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(comment.content, fontSize = 14.sp)
                                    }

                                    Box(
                                        modifier = Modifier.constrainAs(menuIcon) {
                                            top.linkTo(contentCol.top)
                                            end.linkTo(parent.end)
                                        }
                                    ) {
                                        IconButton(onClick = { activeMenuCommentId = comment.id }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                                        }

                                        DropdownMenu(
                                            expanded = activeMenuCommentId == comment.id,
                                            onDismissRequest = { activeMenuCommentId = null },
                                            offset = DpOffset((-16).dp, 0.dp)
                                        ) {
                                            DropdownMenuItem(
                                                text = { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Xóa") } },
                                                onClick = {
                                                    activeMenuCommentId = null
                                                    coroutineScope.launch {
                                                        postViewModel.deleteComment(comment.id, postId)
                                                        postViewModel.fetchComments(postId) // cập nhật ngay
                                                    }
                                                }
                                            )
                                            Divider(thickness = 1.dp, color = Color.LightGray)
                                            DropdownMenuItem(
                                                text = { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Sửa") } },
                                                onClick = {
                                                    activeMenuCommentId = null
                                                    editingCommentId = comment.id
                                                    editedCommentContent = comment.content
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Hiển thị TextField nhập mới hoặc sửa bình luận tại vị trí cuối cùng
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextField(
                                    value = if (editingCommentId == null) newComment else editedCommentContent,
                                    onValueChange = {
                                        if (editingCommentId == null) newComment = it else editedCommentContent = it
                                    },
                                    modifier = Modifier.weight(1f),
                                    placeholder = {
                                        Text(if (editingCommentId == null) "Nhập bình luận..." else "Chỉnh sửa bình luận...")
                                    }
                                )
                                Button(onClick = {
                                    coroutineScope.launch {
                                        if (editingCommentId == null) {
                                            postViewModel.sendComment(postId, currentUserId, userModel, newComment)
                                            newComment = ""
                                            postViewModel.fetchComments(postId) // cập nhật ngay
                                        } else {
                                            postViewModel.updateComment(
                                                commentId = editingCommentId!!,
                                                userId = currentUserId,
                                                userModel = userModel,
                                                content = editedCommentContent
                                            )
                                            editingCommentId = null
                                            editedCommentContent = ""
                                            postViewModel.fetchComments(postId) // cập nhật ngay
                                        }
                                    }
                                }) {
                                    Text(if (editingCommentId == null) "Gửi" else "Lưu")
                                }
                            }
                        }
                    } else {
                        Text("Chưa có bình luận nào.")
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
        if (showPostReportBox) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 50.dp, end = 8.dp)
                    .width(220.dp)
                    .background(Color.White, shape = RoundedCornerShape(6.dp))
                    .border(5.dp, Color.LightGray)
                    .padding(12.dp)
            ) {
                // Tố cáo
                Column(
                    modifier = Modifier
                        .clickable {
                            showPostReportBox = false
                            onClickReport(postId)
                        }
                        .padding(8.dp)
                ) {
                    Text("Tố cáo bài viết", fontWeight = FontWeight.Bold)
                    Text("Bài viết có nội dung vi phạm", fontSize = 13.sp)
                }

                // Chỉ hiển thị nút XÓA nếu là chính người đăng
                if (currentUserId == containerPost.id) {
                    Divider(thickness = 3.dp, color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
                    Column(
                        modifier = Modifier
                            .clickable {
                                showPostReportBox = false
                                postViewModel.deletePost(postId)
                            }
                            .padding(8.dp)
                    ) {
                        Text("Xóa bài viết", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("Xóa khỏi cuộc đời của bạn", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}