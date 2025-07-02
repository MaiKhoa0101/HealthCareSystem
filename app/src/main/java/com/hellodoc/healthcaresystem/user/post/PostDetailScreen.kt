package com.hellodoc.healthcaresystem.user.post

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.skeleton.PostSkeleton
import com.hellodoc.healthcaresystem.user.home.report.ReportPostUser
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostDetailScreen(
    navHostController: NavHostController,
    postId: String,
    postViewModel: PostViewModel,
    userViewModel: UserViewModel
) {
    val youTheCurrentUserUseThisApp by userViewModel.thisUser.collectAsState()
    val userOfThisProfile by userViewModel.user.collectAsState()
    val post by postViewModel.post.collectAsState()

    // Lấy bài viết và thông tin user
    LaunchedEffect(Unit) {
        val userId = userViewModel.getUserAttributeString("userId")
        userViewModel.getYou(userId)
        if (postId.isNotEmpty()) {
            postViewModel.getPostById(postId)
        }
    }

    // Lấy thông tin user của bài viết
    LaunchedEffect(post?.user?.id) {
        val postUserId = post?.user?.id
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
                    userWhoInteractWithThisPost = youTheCurrentUserUseThisApp!!,
                    onClickReport = { showPostReportDialog = !showPostReportDialog }
                )
                if (showPostReportDialog) {
                    ReportPostUser(
                        context = navHostController.context,
                        youTheCurrentUserUseThisApp = youTheCurrentUserUseThisApp,
                        userReported = post!!.user,
                        onClickShowPostReportDialog = { showPostReportDialog = false },
                        sharedPreferences = navHostController.context.getSharedPreferences(
                            "MyPrefs",
                            Context.MODE_PRIVATE
                        )
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Đang tải bài viết...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
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
    userWhoInteractWithThisPost: User,
    onClickReport: () -> Unit
) {
    var showImageDetail by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PostDetailHeader(
                navHostController = navHostController,
                userWhoInteractWithThisPost = userWhoInteractWithThisPost,
                post = post,
                onClickReport = onClickReport
            )
            Spacer(modifier = Modifier.height(12.dp))
            PostBody(post)
            Spacer(modifier = Modifier.height(12.dp))
            PostMedia(
                post = post,
                showImageDetail = showImageDetail,
                selectedImageIndex = selectedImageIndex,
                onImageClick = { index ->
                    selectedImageIndex = index
                    showImageDetail = true
                },
                onDismiss = { showImageDetail = false }
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            InteractDetailPostManager(
                navHostController = navHostController,
                postViewModel = postViewModel,
                post = post,
                user = userWhoInteractWithThisPost
            )
        }
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
                if (post.user.id != userWhoInteractWithThisPost.id) {
                    navHostController.navigate("otherUserProfile/${post.user.id}")
                } else {
                    navHostController.navigate("personal")
                }
            }
        ) {
            AsyncImage(
                model = post.user.avatarURL,
                contentDescription = "Avatar of ${post.user.name}",
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
                    text = post.user.name,
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
                if (userWhoInteractWithThisPost.id == post.user.id) {
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
    navHostController: NavHostController,
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

    if (showCommentSheet) {
        CommentDetailPostSection(
            navHostController = navHostController,
            postId = post.id,
            onClose = { showCommentSheet = false },
            postViewModel = postViewModel,
            currentUser = user
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDetailPostSection(
    navHostController: NavHostController,
    postId: String,
    onClose: () -> Unit,
    postViewModel: PostViewModel,
    currentUser: User
) {
    val uiState = rememberCommentUIState(postViewModel, postId)
    val coroutineScope = rememberCoroutineScope()

    BackHandler { onClose() }

    LaunchedEffect(uiState.commentIndex, uiState.hasMore) {
        observeScrollToLoadMore(
            listState = uiState.listState,
            hasMore = uiState.hasMore,
            isLoading = uiState.isLoadingMore,
            onLoadMore = {
                coroutineScope.launch {
                    uiState.loadMoreComments(postViewModel, postId)
                }
            }
        )
    }
    CommentScreenContent(
        uiState = uiState,
        postViewModel = postViewModel,
        postId = postId,
        currentUser = currentUser,
        navHostController = navHostController
    )
}

@Composable
fun HeadbarDetailPost(navHostController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .shadow(4.dp, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
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
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}