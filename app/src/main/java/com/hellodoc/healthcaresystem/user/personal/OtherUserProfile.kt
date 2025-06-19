package com.hellodoc.healthcaresystem.user.personal
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.user.home.root.ZoomableImageDialog
import com.hellodoc.healthcaresystem.user.post.PostColumn
import com.hellodoc.healthcaresystem.user.post.userId
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel


//Trang nguoi dung khac
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileOtherUserPage(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController
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

    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
        userModel = if (userViewModel.getUserAttributeString("role") == "user") "User" else "Doctor"
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
    // Nếu chưa có user (null) thì không hiển thị giao diện
    if (user==null) return


    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    if (selectedImageUrl != null) {
        ZoomableImageDialog(selectedImageUrl = selectedImageUrl, onDismiss = { selectedImageUrl = null })
    }
    var showReportBox by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

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
                if (user!=null) {
                    OtherUserIntroSection(
                        navHostController = navHostController,
                        user = user!!,
                        onClickShowReport = { showReportDialog = true },
                        onImageClick = { selectedImageUrl = it },
                        showReportBox = showReportBox,
                        onToggleReportBox = { showReportBox = !showReportBox }
                    )
                    PostColumn(
                        navHostController = navHostController,
                        idUserOfPost = user!!.id,
                        userWhoInteractWithThisPost = user!!,
                        postViewModel = postViewModel,
                    )
                }
            }
        }
    }
}

//Phan thong tin cua nguoi dung khac
@Composable
fun OtherUserIntroSection(
    navHostController: NavHostController,
    user: User, onClickShowReport: () -> Unit,
    onImageClick: (String) -> Unit,
    showReportBox: Boolean,
    onToggleReportBox: () -> Unit
)
{
    Column(
        modifier = Modifier.background(Color.Cyan)
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
                onToggleReportBox = onToggleReportBox
            )
            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}
