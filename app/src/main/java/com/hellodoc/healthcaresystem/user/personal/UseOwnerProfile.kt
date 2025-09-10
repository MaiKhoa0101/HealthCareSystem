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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.UiState
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.skeleton.ShimmerEffect
import com.hellodoc.healthcaresystem.skeleton.UserSkeleton
import com.hellodoc.healthcaresystem.user.home.confirm.ConfirmDeletePostModal
import com.hellodoc.healthcaresystem.user.home.report.ReportPostUser
import com.hellodoc.healthcaresystem.user.home.root.AssistantAnswerDialog
import com.hellodoc.healthcaresystem.user.post.MediaDetailDialog
import com.hellodoc.healthcaresystem.user.post.Post
import com.hellodoc.healthcaresystem.user.post.PostColumn
import com.hellodoc.healthcaresystem.user.post.ZoomableImage
import com.hellodoc.healthcaresystem.user.supportfunction.AvatarDetailDialog
import com.hellodoc.healthcaresystem.viewmodel.GeminiHelper
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileUserPage(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    onToggleTheme: () -> Unit,
    darkTheme: Boolean
) {
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences, GeminiHelper()) }
    })

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
    var showSetting by remember { mutableStateOf(false) }
    // LazyListState có rememberSaveable để giữ vị trí scroll khi back
    val listState = rememberSaveable(saver = LazyListState.Saver) {
        LazyListState()
    }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    // Lấy userId, role từ SharedPreferences chỉ 1 lần
    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
        userModel = userViewModel.getUserAttributeString("role")

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
            item {
                if (user == null) {
                    UserSkeleton()
                } else {
                    ProfileSection(
                        user = user!!,
                        onImageClick = {
                            selectedImageUrl = it
                            showMediaDetail = true
                        },
                        onClickSetting = {
                            showSetting = true
                        }
                    )
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
                    post.user?.let {
                        ReportPostUser(
                            context = navHostController.context,
                            youTheCurrentUserUseThisApp = user!!,
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
        if (showSetting) {
            println("SHOW SETTING")
            Setting(
                navHostController,
                sharedPreferences,
                user,
                onToggleTheme = onToggleTheme,
                onExitSetting = { showSetting = false },
                darkTheme = darkTheme
            )
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
    onClickSetting: () -> Unit
)
{
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            UserIntroSection(
                user = user,
                onImageClick = onImageClick,
                onClickSetting = onClickSetting
            )
            Spacer(modifier = Modifier.height(26.dp))
//            UserProfileModifierSection(navHostController, user)
//            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun UserIntroSection(
    user: User,
    onImageClick: (String) -> Unit,
    onClickSetting: () -> Unit
) {
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
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f))
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

        // Icon setting
        IconButton(
            onClick = { onClickSetting() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.settingbtn),
                contentDescription = "Setting",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .height(20.dp)
            )
        }
    }
}


@Composable
fun UserProfileModifierSection(navHostController: NavHostController, user: User?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = { navHostController.navigate("editProfile") },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(60.dp)
                .width(128.dp)
        ) {
            Text(
                text = "Chỉnh sửa hồ sơ",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {
                if (user == null) {
                    return@Button
                }
                else if (user.role=="User"){
                    navHostController.navigate("doctorRegister")
                }
                else{
                    navHostController.navigate("editClinic")
                }
                 },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(60.dp)
                .width(128.dp)
        ) {
            Text(
                text =  if (user?.role == "User") {
                    "Đăng kí phòng khám"
                } else {
                    "Quản lý phòng khám"
                },
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}





