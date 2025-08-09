package com.hellodoc.healthcaresystem.user.personal

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Report
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.admin.ZoomableImageDialog
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.skeleton.PostSkeleton
import com.hellodoc.healthcaresystem.skeleton.UserSkeleton
import com.hellodoc.healthcaresystem.user.home.report.ReportUser
import com.hellodoc.healthcaresystem.user.post.PostColumn
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel

//Trang nguoi dung khac
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileOtherUserPage(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    postViewModel: PostViewModel,
    userOwnerID: String
) {
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    val context = LocalContext.current

    var shouldReloadPosts by remember { mutableStateOf(false) }

    val userOfThisProfile by userViewModel.user.collectAsState()
    val youTheCurrentUserUseThisApp by userViewModel.thisUser.collectAsState()

    // Gọi API để fetch user từ server
    LaunchedEffect(Unit, shouldReloadPosts) {
        val userId = userViewModel.getUserAttributeString("userId")
        userViewModel.getYou(userId)
        if (userOwnerID.isNotEmpty()) {
            userViewModel.getUser(userOwnerID)
            postViewModel.getPostByUserId(userOwnerID)
        }
    }

    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    if (selectedImageUrl != null) {
        ZoomableImageDialog(selectedImageUrl = selectedImageUrl, onDismiss = { selectedImageUrl = null })
    }
    var showReportBox by remember { mutableStateOf(false) }

    if (youTheCurrentUserUseThisApp!= null && userOfThisProfile != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        postViewModel.closeAllPostMenus()  //tắt menu post
                        showReportBox = false
                    }
                }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    if (userOfThisProfile != null) {
                        OtherUserIntroSection(
                            user = userOfThisProfile!!,
                            navHostController = navHostController,
                            onImageClick = { selectedImageUrl = it },
                            showReportBox = showReportBox,
                            onToggleReportBox = { showReportBox = !showReportBox }
                        )
                        PostColumn(
                            navHostController = navHostController,
                            idUserOfPost = userOwnerID,
                            userWhoInteractWithThisPost = youTheCurrentUserUseThisApp!!,
                            postViewModel = postViewModel,
                        )
                    }
                }
            }
            if (showReportBox && youTheCurrentUserUseThisApp != null) {
                ReportUser(
                    context,
                    youTheCurrentUserUseThisApp,
                    userOfThisProfile,
                    onClickShowReportDialog = { showReportBox = !showReportBox },
                    sharedPreferences,
                )
            }
        }
    }
    else {
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
