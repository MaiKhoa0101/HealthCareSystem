package com.hellodoc.healthcaresystem.view.user.personal
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.UiState
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.skeleton.UserSkeleton
import com.hellodoc.healthcaresystem.view.user.home.confirm.ConfirmDeletePostModal

import com.hellodoc.healthcaresystem.view.user.home.report.ReportPostUser
import com.hellodoc.healthcaresystem.view.user.post.Post
import com.hellodoc.healthcaresystem.view.user.supportfunction.AvatarDetailDialog
import com.hellodoc.healthcaresystem.viewmodel.GeminiHelper
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileUserPage(
    navHostController: NavHostController
) {
    val userViewModel: UserViewModel = hiltViewModel()

    val postViewModel: PostViewModel = hiltViewModel()

    val uiStatePost by postViewModel.uiStatePost.collectAsState()
    val user by userViewModel.user.collectAsState()
    val posts by postViewModel.posts.collectAsState()
    val hasMore by postViewModel.hasMorePosts.collectAsState()
    val isLoadingMorePosts by postViewModel.isLoadingMorePosts.collectAsState()
    val progress by postViewModel.uploadProgress.collectAsState()
    var showMediaDetail by remember { mutableStateOf(false) }
    var selectedMediaIndex by remember { mutableStateOf(0) }
    var userId by remember { mutableStateOf("") }
    var userModel by remember { mutableStateOf("") }
    // LazyListState có rememberSaveable để giữ vị trí scroll khi back
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    // Lấy userId, role từ SharedPreferences chỉ 1 lần
    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttribute("userId", context)
        println("USERID NHAN DUOC LA " +  userId)
        userModel = userViewModel.getUserAttribute("role", context)

        if (userId.isNotEmpty()) {
            userViewModel.getUser(userId)
            postViewModel.getPostByUserId(userId, skip = 0, limit = 10, append = false)
        }
    }

    // Scroll tới đáy thì load thêm
    LaunchedEffect(listState, hasMore, isLoadingMorePosts) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 2
        }.distinctUntilChanged().collect { isAtEnd ->
            if (isAtEnd && hasMore && !isLoadingMorePosts) {
                println("Load thêm bài viết skip=${posts.size}")
                postViewModel.getPostByUserId(
                    userId = userId,
                    skip = posts.size,
                    limit = 10,
                    append = true
                )
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    postViewModel.closeAllPostMenus()
                }
            }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            item{
                Box (
                    modifier = Modifier.fillMaxWidth(),
                ){
                    if (user == null) {
                        UserSkeleton()
                    } else {
                        ProfileSection(
                            user = user!!,
                            onImageClick = {
                                selectedImageUrl = it
                                showMediaDetail = true
                            },
                            navHostController = navHostController
                        )
                    }
                }

            }

            items(posts) { post ->
                var showPostReportDialog by remember { mutableStateOf(false) }
                var showPostDeleteConfirmDialog by remember { mutableStateOf(false) }

                Post(
                    navHostController = navHostController,
                    postViewModel = postViewModel,
                    post = post,
                    userWhoInteractWithThisPost = user!!,
                    onClickReport = { showPostReportDialog = true },
                    onClickDelete = { showPostDeleteConfirmDialog = true }
                )

                if (showPostReportDialog) {
                    post.userInfo?.let {
                        ReportPostUser(
                            context = navHostController.context,
                            youTheCurrentUserUseThisApp = user!!,
                            userReported = it,
                            postId = post.id,
                            onClickShowPostReportDialog = { showPostReportDialog = false }
                        )
                    }
                }

                if (showPostDeleteConfirmDialog) {
                    ConfirmDeletePostModal(
                        postId = post.id,
                        postViewModel = postViewModel,
                        sharedPreferences = navHostController.context.getSharedPreferences(
                            "MyPrefs",
                            Context.MODE_PRIVATE
                        ),
                        onClickShowConfirmDeleteDialog = { showPostDeleteConfirmDialog = false },
                    )
                }
            }

            // Footer load thêm
            item {
                when {
                    isLoadingMorePosts -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    !hasMore && posts.isNotEmpty() -> {
                        Text(
                            text = "Đã hết bài viết",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        // Thanh trạng thái upload
        Row(
            modifier = Modifier
                .padding(bottom = 50.dp)
                .align(Alignment.TopCenter)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (uiStatePost) {
                is UiState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đang đăng bài: ${(progress * 100).toInt()}%",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                is UiState.Success -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color.Green,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Đăng bài thành công",
                            color = Color.Green,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {}
            }
        }
        if (showMediaDetail && selectedImageUrl != null) {
            AvatarDetailDialog(
                mediaUrls = selectedImageUrl!!,
                onDismiss = {
                    showMediaDetail = false
                }
            )
        }
    }
}



@Composable
fun ProfileSection(
    user: User,
    onImageClick: (String) -> Unit,
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxWidth()
                .height(280.dp) // Tổng chiều cao header + phần avatar lòi ra
        ) {
            // Background Header
            AsyncImage(
                model = user.avatarURL, // Hoặc ảnh bìa nếu có
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .blur(20.dp)
            )

            // Settings Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 230.dp) // Căn chỉnh vị trí
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { navHostController.navigate("setting") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.settingbtn),
                    contentDescription = "Setting",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Avatar overlapping
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp)
                    .offset(y = (-20).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(4.dp) // Viền trắng
                        .clip(CircleShape)
                ) {
                    AsyncImage(
                        model = user.avatarURL,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onImageClick(user.avatarURL) }
                    )
                }
            }

            // Info Section (Name & Email) aligned with Avatar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 150.dp, bottom = 20.dp) // Căn lề trái tránh avatar, căn lề dưới để khớp
            ) {
                Text(
                    text = user.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = user.email,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        // Edit Button Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Edit Button
            Button(
                onClick = { navHostController.navigate("editOptionPage") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Chỉnh sửa",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}





