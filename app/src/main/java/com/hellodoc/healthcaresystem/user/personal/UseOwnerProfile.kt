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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.admin.ZoomableImageDialog
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.skeleton.ShimmerEffect
import com.hellodoc.healthcaresystem.skeleton.UserSkeleton
import com.hellodoc.healthcaresystem.user.post.PostColumn
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileUserPage(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    onToggleTheme: () -> Unit,
    darkTheme: Boolean
) {

    // Khởi tạo ViewModel bằng custom factory để truyền SharedPreferences
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })

//    val comments by postViewModel.comments.collectAsState()
    var shouldReloadPosts by remember { mutableStateOf(false) }
    val navEntry = navHostController.currentBackStackEntry
    val reloadTrigger = navEntry?.savedStateHandle?.getLiveData<Boolean>("shouldReload")?.observeAsState()
    var userId: String = ""
    var userModel: String = ""

    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
        userModel = userViewModel.getUserAttributeString("role")
    }

    // Gọi API để fetch user từ server
    LaunchedEffect(userId, shouldReloadPosts) {
        if (userId.isNotEmpty()) {
            userViewModel.getUser(userId)
            postViewModel.getPostByUserId(userId)
        }
    }

    // Lấy dữ liệu user từ StateFlow
    val user by userViewModel.user.collectAsState()
    println("USER: $user")
    val isUserLoading = user == null

    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    if (selectedImageUrl != null) {
        ZoomableImageDialog(selectedImageUrl = selectedImageUrl, onDismiss = { selectedImageUrl = null })
    }
    var reportedPostId by remember { mutableStateOf<String?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showFullScreenComment by remember { mutableStateOf(false) }
    var selectedPostIdForComment by remember { mutableStateOf<String?>(null) }
    var showReportBox by remember { mutableStateOf(false) }
    val posts by postViewModel.posts.collectAsState()
    var showSetting by remember { mutableStateOf(false) }
    // Nếu có user rồi thì hiển thị UI
    Box(modifier = Modifier
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
                if (isUserLoading) {
                    UserSkeleton()
                } else {
                    ProfileSection(
                        navHostController = navHostController,
                        user = user!!,
                        onClickShowReport = { showReportDialog = true },
                        onImageClick = { selectedImageUrl = it },
                        showReportBox = showReportBox,
                        onToggleReportBox = { showReportBox = !showReportBox },
                        onClickSetting = { showSetting = true }
                    )
                    PostColumn(
                        navHostController = navHostController,
                        idUserOfPost = user!!.id,
                        userWhoInteractWithThisPost = user!!,
                        postViewModel = postViewModel
                    )
                }
            }
        }
        if (showSetting) {
            println("SHOW SETTING")
            Setting(
                navHostController,
                sharedPreferences,
                user,
                onToggleTheme = onToggleTheme,
                onExitSetting = {
                    showSetting = false
                },
                darkTheme = darkTheme
            )
        }
    }
}

@Composable
fun ProfileSection(
    navHostController: NavHostController,
    user: User, onClickShowReport: () -> Unit,
    onImageClick: (String) -> Unit,
    showReportBox: Boolean,
    onToggleReportBox: () -> Unit,
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
                onClickShowReport = onClickShowReport,
                navController = navHostController,
                onImageClick = onImageClick,
                showReportBox = showReportBox,
                onToggleReportBox = onToggleReportBox,
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
    onClickShowReport: () -> Unit,
    navController: NavHostController,
    onImageClick: (String) -> Unit,
    showReportBox: Boolean,
    onToggleReportBox: () -> Unit,
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





