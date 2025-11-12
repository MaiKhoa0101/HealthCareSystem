package com.hellodoc.healthcaresystem.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.viewmodel.GeminiHelper
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostManagerScreen(
    postViewModel: PostViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {

    val posts by postViewModel.posts.collectAsState()

    LaunchedEffect(Unit) {
        postViewModel.fetchPosts()
    }

    Column(
        modifier = modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Danh sách bài viết",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        if (posts.isEmpty()) {
            EmptyPostList()
        } else {
            PostList(posts = posts, postViewModel = postViewModel)
        }
    }
}


@Composable
fun EmptyPostList() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Không có bài viết",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostList(posts: List<PostResponse>, postViewModel: PostViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp)
    ) {
        itemsIndexed(posts) { index, post ->
            PostItem(id = index+1, post = post, postViewModel = postViewModel)
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostItem(id: Int,post: PostResponse,  postViewModel: PostViewModel) {
    var showPostDialog by remember { mutableStateOf(false) }
    var showCommentsDialog by remember { mutableStateOf(false) }
    var isCheckingImages by remember { mutableStateOf(false) }
    var isCheckingComments by remember { mutableStateOf(false) }

    val totalFavoritesMap by postViewModel.totalFavoritesMap.collectAsState()
    val totalFavorites = totalFavoritesMap[post.id] ?: "0"

    val commentsMap by postViewModel.commentsMap.collectAsState()
    val comments = commentsMap[post.id] ?: emptyList()
    val commentsCount = comments.size


    LaunchedEffect(post.id) {
        // Gọi API và cập nhật state khi dữ liệu được fetch về
        postViewModel.fetchFavoriteForPost(post.id, post.user?.id ?: "")
        postViewModel.fetchComments(post.id)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp), // Space between the two cards
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First Card: User Details
            Card(
                modifier = Modifier
                    .weight(1f) // Occupy available space proportionally
                    .height(28.dp.times(7))// Set height for the card
                    .clickable { showPostDialog = true },
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    bottomStart = 16.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                ),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp) // Space between text items
                ) {
                    Text(
                        text = "ID: $id",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Tên người tạo: ${post.user?.name}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Nội dung: ${post.content}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Số lượng ảnh: ${post.media.size}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Số lượt thích: $totalFavorites",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Số lượt bình luận: $commentsCount",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Ngày tạo: ${post.createdAt.timeInVietnam()}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Second Card: Action Buttons
            Card(
                modifier = Modifier
                    .height(28.dp.times(7)) // Match the height of the first card
                    .width(100.dp), // Fixed width for the buttons card
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = 16.dp,
                    bottomEnd = 16.dp
                ),
                elevation = CardDefaults.cardElevation(5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Cyan // Set the background color of the card
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.Center, // Center vertically
                    horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
                ) {
                    IconButton(
                        onClick = {
                            isCheckingImages = !isCheckingImages
                            isCheckingComments = false
                                  },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_image),
                            contentDescription = "Checking Images",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            isCheckingComments = !isCheckingComments
                            isCheckingImages = false
                                  },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.comment),
                            contentDescription = "Checking Comments",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    IconButton(
                        onClick = { postViewModel.deletePost(post.id) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.minus),
                            contentDescription = "Remove",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

            }
        }

        if (showPostDialog) {
            AlertDialog(
                onDismissRequest = { showPostDialog = false },
                confirmButton = {
                    TextButton(onClick = { showPostDialog = false }) {
                        Text("Đóng")
                    }
                },
                title = { Text("Chi tiết bài viết") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text("ID: $id", fontSize = 15.sp)
                        Text("Tên người tạo: ${post.user?.name}", fontSize = 15.sp)
                        Text("Nội dung: ${post.content}", fontSize = 15.sp)
                        Text("Số lượng ảnh: ${post.media.size}", fontSize = 15.sp)
                        Text("Số lượt thích: $totalFavorites", fontSize = 15.sp)
                        Text("Số lượt bình luận: $commentsCount", fontSize = 15.sp)
                        Text("Ngày tạo: ${post.createdAt.timeInVietnam()}", fontSize = 15.sp)
                    }
                }
            )
        }

        if (isCheckingImages && post.media.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Cho phép cuộn nếu nội dung quá dài
            ) {
                post.media.forEach { media ->
                    AsyncImage(
                        model = media,
                        contentDescription = "Post Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray)
                    )
                }
            }
        }

        if (isCheckingComments && comments.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
//                    .padding(top = 10.dp)
            ) {
                itemsIndexed(comments) { index, comment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp), // Space between the two cards
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f) // Occupy available space proportionally
                                .height(29.dp.times(4)) // Set height for the card
                                .clickable { showCommentsDialog = true },
                            shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    bottomStart = 16.dp,
                                    topEnd = 0.dp,
                                    bottomEnd = 0.dp
                                ),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF0F0F0) // Màu xám nhạt
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp) // Space between text items
                            ) {
                                Text(
                                    text = "ID: ${index + 1}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Tên người bình luận: ${comment.user.name}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Nội dung: ${comment.content}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Ngày tạo: ${comment.createdAt.timeInVietnam()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .height(29.dp.times(4)) // Match the height of the first card
                                .width(100.dp), // Fixed width for the buttons card
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                bottomStart = 0.dp,
                                topEnd = 16.dp,
                                bottomEnd = 16.dp
                            ),
                            elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFE0F7FA) // Màu xanh nhạt dễ nhìn
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center, // Center the buttons
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { postViewModel.deleteComment(comment.id, post.id) },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.minus),
                                        contentDescription = "Remove",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (showCommentsDialog) {
                        AlertDialog(
                            onDismissRequest = { showCommentsDialog = false },
                            confirmButton = {
                                TextButton(onClick = { showCommentsDialog = false }) {
                                    Text("Đóng")
                                }
                            },
                            title = { Text("Chi tiết bình luận") },
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    Text("ID: ${index + 1}", fontSize = 15.sp)
                                    Text("Tên người bình luận: ${comment.user.name}", fontSize = 15.sp)
                                    Text("Nội dung: ${comment.content}", fontSize = 15.sp)
                                    Text("Ngày tạo: ${comment.createdAt.timeInVietnam()}", fontSize = 15.sp)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.timeInVietnam(): String {
    return try {
        val instant = Instant.parse(this)
        val vietnamTime = instant.atZone(ZoneId.of("Asia/Ho_Chi_Minh"))
        vietnamTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        "Không xác định"
    }
}
