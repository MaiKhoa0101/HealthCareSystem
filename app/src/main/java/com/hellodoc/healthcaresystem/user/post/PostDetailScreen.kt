package com.hellodoc.healthcaresystem.user.post

import android.content.Context
import android.os.Build
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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
    // Lấy bài viết đầu tiên
    LaunchedEffect(Unit) {
        val userId = userViewModel.getUserAttributeString("userId")
        userViewModel.getYou(userId)

        if (postId.isNotEmpty()) {
            postViewModel.getPostById(postId)
        }
    }

// Chạy khi post được cập nhật
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
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.background),
            ) {
                PostDetailSection(
                    navHostController = navHostController,
                    postViewModel = postViewModel,
                    post = post!!,
                    userWhoInteractWithThisPost = youTheCurrentUserUseThisApp!!,
                    onClickReport = {
                        showPostReportDialog = !showPostReportDialog
                    })
                Spacer(modifier = Modifier.height(8.dp))
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
    }
    else{
        println("post: "+post + "user: "+youTheCurrentUserUseThisApp)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ){
                PostSkeleton()
                Text("Đang tìm bài viết")
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostDetailSection(
    navHostController:NavHostController,
    postViewModel: PostViewModel,
    post: PostResponse,
    userWhoInteractWithThisPost: User,
    onClickReport: () -> Unit
) {
    var showImageDetail by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    HorizontalDivider(thickness = 2.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        PostHeader(
            navHostController = navHostController,
            userWhoInteractWithThisPost,
            post,
            onClickReport
        )
        Spacer(modifier = Modifier.height(8.dp))
        PostBody(post)
        Spacer(modifier = Modifier.height(8.dp))
        PostMedia(
            post = post,
            showImageDetail = showImageDetail,
            selectedImageIndex = selectedImageIndex,
            onImageClick = { index ->
                selectedImageIndex = index
                showImageDetail = true
            },
            onDismiss = {
                showImageDetail = false
            }
        )
        InteractDetailPostManager(
            navHostController = navHostController,
            postViewModel = postViewModel,
            post = post,
            user = userWhoInteractWithThisPost
        )
    }
}

@Composable
fun InteractDetailPostManager(
    navHostController:NavHostController,
    postViewModel: PostViewModel,
    post: PostResponse,
    user: User
){
    val sizeButton = 28.dp
    val coroutineScope = rememberCoroutineScope()
    var showCommentSheet by remember { mutableStateOf(false) }


    // Lấy trạng thái favorite từ ViewModel
    LaunchedEffect(post.id, user.id) {
        postViewModel.fetchFavoriteForPost(postId = post.id, userId = user.id)
    }
    val isFavoritedMap by postViewModel.isFavoritedMap.collectAsState()
    val isFavorited = isFavoritedMap[post.id] ?: false
    val totalFavoritesMap by postViewModel.totalFavoritesMap.collectAsState()
    val totalFavorites = totalFavoritesMap[post.id]?.toIntOrNull() ?: 0

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(height = sizeButton * 2, width = sizeButton * 6)
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    coroutineScope.launch {
                        println("Nut like dc nhan")
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
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))
        CommentDetailPostSection(
            navHostController = navHostController, // hoặc truyền vào từ tham số
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
){
    val uiState = rememberCommentUIState(postViewModel, postId)
    val coroutineScope = rememberCoroutineScope() // ✅ Thêm dòng này

    BackHandler { onClose() }

    // Load more comments when reaching end of list
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
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { navHostController.popBackStack() }
        )
        Text(
            text = "Bài viết chi tiết",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}