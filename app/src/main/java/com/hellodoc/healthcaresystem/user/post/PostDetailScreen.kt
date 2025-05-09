package com.hellodoc.healthcaresystem.user.post

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.user.home.HomeActivity
import com.hellodoc.healthcaresystem.user.notification.timeAgoInVietnam
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.FullScreenCommentUI
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.launch

var currentUserId = ""

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostDetailScreen(
    context: Context,
    navHostController: NavHostController,
    postId: String
) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    val posts by postViewModel.posts.collectAsState()

    LaunchedEffect(Unit) {
        postViewModel.getPostById(postId)
        userId = userViewModel.getUserAttributeString("userId")
        userModel = if (userViewModel.getUserAttributeString("role") == "user") "User" else "Doctor"
    }

    val post = posts.firstOrNull()
    val postUserId = post?.user?.id
    val postUserName = post?.user?.name
    val postUserAvatarUrl = post?.user?.avatarURL
    val postCreatedAt = post?.createdAt
    val postMediaList = post?.media

    val backgroundColor = Color.White
    var expanded by remember { mutableStateOf(false) }
    var isCommenting by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }

    val isFavoritedMap by postViewModel.isFavoritedMap.collectAsState()
    val totalFavoritesMap by postViewModel.totalFavoritesMap.collectAsState()

    val isFavorited = isFavoritedMap[postId] ?: false
    val totalFavorites = totalFavoritesMap[postId] ?: "0"

    var showPostReportBox by remember { mutableStateOf(false) }
    var editingCommentId by remember { mutableStateOf<String?>(null) }

    val commentsMap by postViewModel.commentsMap.collectAsState()
    val comments = commentsMap[postId] ?: emptyList()
    var editedCommentContent by remember { mutableStateOf("") }
    var activeMenuCommentId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var commentIndex by remember { mutableIntStateOf(6) }

    LaunchedEffect(post) {
        // Gọi API và cập nhật state khi dữ liệu được fetch về
        postViewModel.fetchComments(postId)
        if (postUserId != null) {
            postViewModel.fetchFavoriteForPost(postId, postUserId)
        }
    }

    var shouldShowSeeMore by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState()
    var showFullScreenComment by remember { mutableStateOf(false) }


    if(post != null && postUserName != null && postUserId != null) {
        Column {
            TopBar(
                title = postUserName,
                onClick = { navHostController.popBackStack() })
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
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
                                model = postUserAvatarUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Column(modifier = Modifier.padding(start = 10.dp)) {
                                Text(
                                    text = postUserName,
                                    style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                )
                                if (postCreatedAt != null) {
                                    Text(
                                        text = postCreatedAt.timeAgoInVietnam(),
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
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
                    SubcomposeLayout { constraints ->
                        val measuredText = subcompose("text") {
                            Text(
                                text = post.content,
                                style = TextStyle(fontSize = 16.sp),
                                maxLines = Int.MAX_VALUE,
                                onTextLayout = {
                                    shouldShowSeeMore = it.lineCount > 2
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }[0].measure(constraints)

                        val actualText = subcompose("displayText") {
                            Text(
                                text = post.content,
                                style = TextStyle(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                ),
                                maxLines = if (expanded) Int.MAX_VALUE else 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 8.dp)
                            )
                        }[0].measure(constraints)

                        val seeMoreText = if (shouldShowSeeMore) {
                            subcompose("seeMore") {
                                Text(
                                    text = if (expanded) "Thu gọn" else "Xem thêm",
                                    color = Color.Blue,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable { expanded = !expanded }
                                )
                            }[0].measure(constraints)
                        } else null

                        layout(constraints.maxWidth, actualText.height + (seeMoreText?.height ?: 0)) {
                            actualText.placeRelative(0, 0)
                            seeMoreText?.placeRelative(0, actualText.height)
                        }
                    }
                    //anh
                    if (postMediaList != null) {
                        if (postMediaList.isNotEmpty()) {
                            HorizontalPager(
                                count = postMediaList.size,
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            ) { page ->
                                Box(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = postMediaList[page],
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
                                            .background(
                                                Color.Black.copy(alpha = 0.5f),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${page + 1}/${postMediaList.size}",
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
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
                                    userId = postUserId,
                                    userModel = userModel
                                )
                                postViewModel.fetchFavoriteForPost(postId, postUserId)
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
                                showFullScreenComment = true
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

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // UI COMMENT
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(comments.take(commentIndex)) { comment ->
                            Row(verticalAlignment = Alignment.Top) {
                                AsyncImage(
                                    model = comment.user.avatarURL ?: "",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(comment.user.name ?: "Ẩn danh", fontWeight = FontWeight.Bold)
                                    Text(comment.content)
                                }
                                Box {
                                    IconButton(onClick = { activeMenuCommentId = comment.id }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = null)
                                    }
                                    DropdownMenu(
                                        expanded = activeMenuCommentId == comment.id,
                                        onDismissRequest = { activeMenuCommentId = null }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Xóa") },
                                            onClick = {
                                                activeMenuCommentId = null
                                                coroutineScope.launch {
                                                    postViewModel.deleteComment(comment.id, postId)
                                                    postViewModel.fetchComments(postId)
                                                }
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Sửa") },
                                            onClick = {
                                                editingCommentId = comment.id
                                                editedCommentContent = comment.content
                                                activeMenuCommentId = null
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (commentIndex < comments.size) {
                            item {
                                Text(
                                    text = "Xem thêm...",
                                    modifier = Modifier
                                        .clickable { commentIndex += 6 }
                                        .padding(8.dp),
                                    color = Color.Blue
                                )
                            }
                        }
                    }
                    // Nhập bình luận
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(
                            value = if (editingCommentId != null) editedCommentContent else newComment,
                            onValueChange = {
                                if (editingCommentId != null) editedCommentContent = it else newComment = it
                            },
                            placeholder = { Text("Nhập bình luận...") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Button(onClick = {
                            coroutineScope.launch {
                                if (editingCommentId != null) {
                                    postViewModel.updateComment(editingCommentId!!, postUserId, userModel, editedCommentContent)
                                    editingCommentId = null
                                    editedCommentContent = ""
                                } else {
                                    postViewModel.sendComment(postId, postUserId, userModel, newComment)
                                    newComment = ""
                                }
                                postViewModel.fetchComments(postId)
                            }
                        }) {
                            Text(if (editingCommentId != null) "Lưu" else "Gửi")
                        }
                    }


                }
            }
        }
        if (showFullScreenComment) {
            FullScreenCommentUI(
                postId = postId,
                onClose = { showFullScreenComment = false },
                postViewModel = postViewModel,
                currentUserId = currentUserId
            )
        }

    }
}

@Composable
fun TopBar(title: String,onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF00BCD4))
            .statusBarsPadding()
            .height(30.dp)
    ) {
        // Nút quay lại
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable { onClick() }
        )

        // Tiêu đề ở giữa
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}