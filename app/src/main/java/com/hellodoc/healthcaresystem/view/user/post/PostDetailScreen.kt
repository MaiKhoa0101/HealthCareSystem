package com.hellodoc.healthcaresystem.view.user.post

import android.content.Context
import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.core.common.skeletonloading.SkeletonBox
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.skeleton.PostSkeleton
import com.hellodoc.healthcaresystem.view.user.home.report.ReportPostUser
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostDetailScreen(
    navHostController: NavHostController,
    postId: String,
    postViewModel: PostViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val youTheCurrentUserUseThisApp by userViewModel.you.collectAsState()
    val post by postViewModel.post.collectAsState()
    val similarPosts by postViewModel.similarPosts.collectAsState()
    val context = LocalContext.current
    // Reset và lấy bài viết mới mỗi khi postId thay đổi
    LaunchedEffect(Unit) {
        // Reset dữ liệu cũ
        postViewModel.clearPosts()
        userViewModel.getYou(context)

        if (postId.isNotEmpty() ) {
            postViewModel.getPostById(postId, navHostController.context)
            postViewModel.getSimilarPosts(postId)
        }
        val postUserId = post?.userInfo?.id
        println("Ket qua lay post user id la: "+postUserId+" "+ post)
        if (!postUserId.isNullOrBlank()) {
            userViewModel.getUser(postUserId)
        }
    }


    if (post != null && youTheCurrentUserUseThisApp != null) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HeadbarDetailPost(navHostController)
            var showPostReportDialog by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                PostDetailSection(
                    navHostController = navHostController,
                    postViewModel = postViewModel,
                    post = post!!,
                    similarPosts = similarPosts,
                    userWhoInteractWithThisPost = youTheCurrentUserUseThisApp!!,
                    onClickReport = { showPostReportDialog = !showPostReportDialog }
                )
                if (showPostReportDialog) {
                    post!!.userInfo?.let {
                        ReportPostUser(
                            context = navHostController.context,
                            youTheCurrentUserUseThisApp = youTheCurrentUserUseThisApp,
                            userReported = it,
                            postId = post!!.id,
                            onClickShowPostReportDialog = { showPostReportDialog = false }
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Đang tải bài viết...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            PostSkeleton()
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostDetailSection(
    navHostController: NavHostController,
    postViewModel: PostViewModel,
    post: PostResponse,
    similarPosts: List<PostResponse>,
    userWhoInteractWithThisPost: User,
    onClickReport: () -> Unit
) {
    val uiState = rememberCommentUIState(postViewModel, post.id)
    val coroutineScope = rememberCoroutineScope()

    // Xử lý load thêm khi scroll
    LaunchedEffect(/*uiState.commentIndex,*/ uiState.hasMore) {
        observeScrollToLoadMore(
            listState = uiState.listState,
            hasMore = uiState.hasMore,
            isLoading = uiState.isLoadingMore,
            onLoadMore = {
                coroutineScope.launch {
                    uiState.loadMoreComments(postViewModel, post.id)
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp), // chừa chỗ cho input
            state = uiState.listState
        ) {
            // Nội dung bài viết
            item {
                PostContentSection(
                    navHostController = navHostController,
                    postViewModel = postViewModel,
                    post = post,
                    similarPosts = similarPosts,
                    user = userWhoInteractWithThisPost,
                    onClickReport = onClickReport
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Danh sách comment
            items(uiState.comments) { comment ->
                CommentItem(
                    comment = comment,
                    postId = post.id,
                    postViewModel = postViewModel,
                    currentUser = userWhoInteractWithThisPost,
                    navHostController = navHostController,
                    uiState = uiState
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Footer load thêm
            item {
                CommentListFooter(
                    isLoadingMore = uiState.isLoadingMore,
                    hasMore = uiState.hasMore
                )
            }
        }
        CommentInput(
            uiState = uiState,
            postViewModel = postViewModel,
            postId = post.id,
            currentUser = userWhoInteractWithThisPost,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PostContentSection(
    navHostController: NavHostController,
    postViewModel: PostViewModel,
    post: PostResponse,
    similarPosts: List<PostResponse>,
    user: User,
    onClickReport: () -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(12.dp))
        PostDetailHeader(
            navHostController = navHostController,
            userWhoInteractWithThisPost = user,
            post = post,
            onClickReport = onClickReport
        )
        Spacer(modifier = Modifier.height(12.dp))
        PostBody(post)
        Spacer(modifier = Modifier.height(12.dp))
        PostMedia(post = post)
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        InteractDetailPostManager(
            postViewModel = postViewModel,
            post = post,
            user = user
        )
        Spacer(modifier = Modifier.height(12.dp))
        SimilarPosts(
            navHostController = navHostController,
            similarPosts = similarPosts
        )
    }
}



@Composable
fun SimilarPosts(
    navHostController: NavHostController,
    similarPosts: List<PostResponse>
) {
    Column {
        Text(text = "Bài viết liên quan", style = MaterialTheme.typography.titleMedium)
        if (similarPosts.isEmpty()) {
            Row {
                SkeletonBox(modifier = Modifier.height(40.dp).width(160.dp))
                SkeletonBox(modifier = Modifier.height(40.dp).width(160.dp))
                SkeletonBox(modifier = Modifier.height(40.dp).width(160.dp))
            }
        }
        else {
            LazyRow (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                items(similarPosts.size) { index ->
                    val similarPost = similarPosts[index]
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                navHostController.navigate("post-detail/${similarPost.id}"){
                                    restoreState = true
                                }
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = shortenSentence(similarPost.content,30), style = MaterialTheme.typography.bodyMedium)
                    }
                }

            }
        }
    }
}

fun shortenSentence(sentence: String, maxLength: Int): String {
    return if (sentence.length > maxLength) {
        sentence.take(maxLength) + "..."
    } else {
        sentence
    }
}


@Composable
fun PostDetailHeader(
    navHostController: NavHostController,
    userWhoInteractWithThisPost: User,
    post: PostResponse,
    onClickReport: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val userViewModel: UserViewModel = hiltViewModel()
    val userOfThisPost by userViewModel.user.collectAsState()
    println (post)
//
//    LaunchedEffect(userOfThisPost) {
//        if (post.userInfoInfo.id != "") {
//            userViewModel.getUser(post.userInfoInfo.id)
//            println("Gọi getUser")
//        }
//    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                if (userOfThisPost?.id != userWhoInteractWithThisPost.id) {
                    navHostController.navigate("otherUserProfile/${userOfThisPost?.id}")
                } else {
                    navHostController.navigate("personal")
                }
            }
        ) {
            AsyncImage(
                model = userOfThisPost?.avatarURL,
                contentDescription = "Avatar of ${userOfThisPost?.name}",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(2.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = post.userInfo?.name ?: "Người dùng ẩn",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = post.createdAt ?: "Vừa xong", // Giả sử post có thuộc tính createdAt
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Box {
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset(x = (-8).dp, y = 8.dp),
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                if (userWhoInteractWithThisPost.id == post.userInfo?.id) {
                    DropdownMenuItem(
                        text = { Text("Xoá bài viết", style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            showMenu = false
                            Toast.makeText(
                                context,
                                "Không thể thực hiện thao tác xoá bài viết trong đây do chúng tôi quên làm chức năng này",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                } else {
                    DropdownMenuItem(
                        text = { Text("Báo cáo bài viết", style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            showMenu = false
                            onClickReport()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InteractDetailPostManager(
    postViewModel: PostViewModel,
    post: PostResponse,
    user: User
) {
    val sizeButton = 32.dp
    val coroutineScope = rememberCoroutineScope()
    var showCommentSheet by remember { mutableStateOf(false) }

    LaunchedEffect(post.id, user.id) {
        postViewModel.fetchFavoriteForPost(postId = post.id, userId = user.id)
    }
    val isFavoritedMap by postViewModel.isFavoritedMap.collectAsState()
    val isFavorited = isFavoritedMap[post.id] ?: false
    val totalFavoritesMap by postViewModel.totalFavoritesMap.collectAsState()
    val totalFavorites = totalFavoritesMap[post.id]?.toIntOrNull() ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = sizeButton * 3, height = sizeButton * 1.5f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                .clickable {
                    coroutineScope.launch {
                        postViewModel.updateFavoriteForPost(
                            postId = post.id,
                            userFavouriteId = user.id,
                            userFavouriteModel = user.role
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            LikeButton(
                size = sizeButton,
                isFavorited = isFavorited,
                totalFavorites = totalFavorites
            )
        }

        Box(
            modifier = Modifier
                .size(width = sizeButton * 3, height = sizeButton * 1.5f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                .clickable { showCommentSheet = !showCommentSheet },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Comment,
                    contentDescription = "Comment",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(sizeButton * 0.8f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Bình luận",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

}

@Composable
fun HeadbarDetailPost(navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(28.dp)
                .clip(CircleShape)
                .clickable { navHostController.popBackStack() }
                .padding(4.dp)
        )
        Text(
            text = "Bài viết chi tiết",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}