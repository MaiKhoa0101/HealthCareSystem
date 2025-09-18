package com.hellodoc.healthcaresystem.user.personal

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Report
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.skeleton.PostSkeleton
import com.hellodoc.healthcaresystem.skeleton.UserSkeleton
import com.hellodoc.healthcaresystem.user.home.confirm.ConfirmDeletePostModal
import com.hellodoc.healthcaresystem.user.home.report.ReportPostUser
import com.hellodoc.healthcaresystem.user.home.report.ReportUser
import com.hellodoc.healthcaresystem.user.post.Post
import com.hellodoc.healthcaresystem.user.post.PostColumn
import com.hellodoc.healthcaresystem.user.post.ZoomableImage
import com.hellodoc.healthcaresystem.user.supportfunction.AvatarDetailDialog
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.flow.distinctUntilChanged


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileOtherUserPage(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    postViewModel: PostViewModel,
    userOwnerID: String
) {
    val userViewModel: UserViewModel = viewModel(
        key = "user_${userOwnerID}",
        factory = viewModelFactory {
            initializer { UserViewModel(sharedPreferences) }
        }
    )


    val context = LocalContext.current

    // State
    val userOfThisProfile by userViewModel.user.collectAsState()
    val youTheCurrentUserUseThisApp by userViewModel.thisUser.collectAsState()
    val posts by postViewModel.posts.collectAsState()
    val hasMore by postViewModel.hasMorePosts.collectAsState()
    val isLoadingMorePosts by postViewModel.isLoadingMorePosts.collectAsState()

    var selectedImageUrl by remember(userOwnerID) { mutableStateOf<String?>(null) }
    var showReportBox by remember(userOwnerID) { mutableStateOf(false) }
    var showMediaDetail by remember(userOwnerID) { mutableStateOf(false) }

    // LazyListState giữ vị trí scroll khi back
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    LaunchedEffect(userOwnerID) {
        val myId = userViewModel.getUserAttributeString("userId")
        userViewModel.getYou(myId)

        postViewModel.clearPosts() // reset về rỗng

        if (userOwnerID.isNotEmpty()) {
            userViewModel.getUser(userOwnerID)
            postViewModel.getPostByUserId(userOwnerID, skip = 0, limit = 10, append = false)
        }
    }


    // Scroll tới đáy load thêm
    LaunchedEffect(listState, hasMore, isLoadingMorePosts) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 2
        }.distinctUntilChanged().collect { isAtEnd ->
            if (isAtEnd && hasMore && !isLoadingMorePosts) {
                println("Load thêm bài viết skip=${posts.size}")
                postViewModel.getPostByUserId(
                    userId = userOwnerID,
                    skip = posts.size,
                    limit = 10,
                    append = true
                )
            }
        }
    }

    if (youTheCurrentUserUseThisApp != null && userOfThisProfile != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        postViewModel.closeAllPostMenus()
                        showReportBox = false
                    }
                }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                item {
                    OtherUserIntroSection(
                        user = userOfThisProfile!!,
                        navHostController = navHostController,
                        onImageClick = {
                            selectedImageUrl = it
                            showMediaDetail = true
                        },
                        showReportBox = showReportBox,
                        onToggleReportBox = { showReportBox = !showReportBox }
                    )
                }

                items(posts) { post ->
                    var showPostReportDialog by remember { mutableStateOf(false) }
                    var showPostDeleteConfirmDialog by remember { mutableStateOf(false) }

                    Box (modifier = Modifier.fillMaxWidth()) {
                        Post(
                            navHostController = navHostController,
                            postViewModel = postViewModel,
                            post = post,
                            userWhoInteractWithThisPost = youTheCurrentUserUseThisApp!!,
                            onClickReport = {
//                        showOptionsMenu = true
                                showPostReportDialog = !showPostReportDialog
                            },
                            onClickDelete = {
//                        showOptionsMenu = true
                                showPostDeleteConfirmDialog = !showPostDeleteConfirmDialog
                            },
                        )

                        if (showPostReportDialog) {
                            post.user?.let {
                                ReportPostUser(
                                    context = navHostController.context,
                                    youTheCurrentUserUseThisApp =youTheCurrentUserUseThisApp,
                                    userReported = it,
                                    onClickShowPostReportDialog = { showPostReportDialog = false },
                                    sharedPreferences = navHostController.context.getSharedPreferences(
                                        "MyPrefs",
                                        Context.MODE_PRIVATE
                                    )
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

            if (showMediaDetail && selectedImageUrl != null) {
                AvatarDetailDialog(
                    mediaUrls = selectedImageUrl!!,
                    onDismiss = {
                        showMediaDetail = false
                    }
                )
            }

            if (showReportBox) {
                ReportUser(
                    context,
                    youTheCurrentUserUseThisApp!!,
                    userOfThisProfile,
                    onClickShowReportDialog = { showReportBox = !showReportBox },
                    sharedPreferences,
                )
            }
        }
    } else {
        Column {
            UserSkeleton()
            PostSkeleton()
        }
    }
}


@Composable
fun OtherUserIntroSection(
    user: User,
    navHostController: NavHostController,
    onImageClick: (String) -> Unit,
    showReportBox: Boolean,
    onToggleReportBox: () -> Unit
) {
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navHostController.popBackStack() },
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                IconButton(
                    onClick = { onToggleReportBox() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Report,
                        contentDescription = "Report",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                    ) {
                        AsyncImage(
                            model = user.avatarURL,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .clickable {
                                    onImageClick(user.avatarURL)
                                },
                            contentScale = ContentScale.Crop,
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        user.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        user.email,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
