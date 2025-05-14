package com.hellodoc.healthcaresystem.user.personal.otherusercolumn

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.responsemodel.ContentPost
import com.hellodoc.healthcaresystem.responsemodel.FooterItem
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.user.personal.userModel
import com.hellodoc.healthcaresystem.user.post.userId
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import com.hellodoc.healthcaresystem.user.notification.timeAgoInVietnam
import com.google.accompanist.pager.*
import com.hellodoc.healthcaresystem.user.home.HomeActivity
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.user.home.ZoomableImageDialog
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OtherPostColumn(
    userViewModel: UserViewModel,
    postViewModel: PostViewModel,
    posts: List<PostResponse>,
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    onImageClick: (String) -> Unit,
    onClickReport: (String) -> Unit,
    onShowComment: (String) -> Unit
) {
    val userId = userViewModel.getUserAttributeString("userId")

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            userViewModel.getUser(userId)
        }
    }

    println("chay tut tren day")
    if (posts.isEmpty()) {
        Text(
            text = "Chưa có bài viết nào.",
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp,
            color = Color.Gray
        )
    } else {
        posts.forEach { postItem ->
            ViewPostOwner(
                postId = postItem.id,
                containerPost = ContainerPost(
                    id = postItem.user.id,
                    name = postItem.user.name,
                    imageUrl = postItem.user.avatarURL ?: ""
                ),
                contentPost = ContentPost(postItem.content),
                footerItem = FooterItem(imageUrl = postItem.media.joinToString("|")),
                createdAt = postItem.createdAt,
                postViewModel = postViewModel,
                currentUserId = userId,
                navHostController = navHostController,
                onClickReport = { postId -> onClickReport(postId) },
                onShowComment = { postId -> onShowComment(postId) },
                onImageClick = { imageUrl -> onImageClick(imageUrl) }
            )
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostColumn(
    posts: List<PostResponse>,
    postViewModel: PostViewModel,
    userId: String,
    navController: NavHostController,
    onClickReport: (String) -> Unit,
    onShowComment: (String) -> Unit,
    ) {
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    if (selectedImageUrl != null) {
        ZoomableImageDialog(selectedImageUrl = selectedImageUrl, onDismiss = { selectedImageUrl = null })
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, color = Color.Gray),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Bài viết đã đăng",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Nếu không có bài viết thì hiển thị Empty
        if (posts.isEmpty()) {
            Text(
                text = "Chưa có bài viết nào.",
                modifier = Modifier.padding(16.dp),
                fontSize = 16.sp,
                color = Color.Gray
            )
        } else {
            posts.forEach { postItem ->
                ViewPostOwner(
                    postId = postItem.id,
                    containerPost = ContainerPost(
                        id = postItem.user.id,
                        name = postItem.user.name,
                        imageUrl = postItem.user.avatarURL ?: ""
                    ),
                    contentPost = ContentPost(postItem.content),
                    footerItem = FooterItem(imageUrl = postItem.media.joinToString("|")),
                    createdAt = postItem.createdAt,
                    postViewModel = postViewModel,
                    currentUserId = userId,
                    navHostController = navController,
                    onClickReport = onClickReport,
                    onShowComment = onShowComment,
                    onImageClick = { selectedImageUrl = it}
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ViewPostOwner(
    postId: String,
    containerPost: ContainerPost,
    contentPost: ContentPost,
    footerItem: FooterItem,
    createdAt: String,
    postViewModel: PostViewModel,
    currentUserId: String,
    navHostController: NavHostController,
    onClickReport: (String) -> Unit,
    onShowComment: (String) -> Unit,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
    ) {
    val backgroundColor = Color.White
    var expanded by remember { mutableStateOf(false) }
    var shouldFetchComments by remember { mutableStateOf(false) }

    val isFavoritedMap by postViewModel.isFavoritedMap.collectAsState()
    val totalFavoritesMap by postViewModel.totalFavoritesMap.collectAsState()

    val isFavorited = isFavoritedMap[postId] ?: false
    val totalFavorites = totalFavoritesMap[postId] ?: "0"

    val commentsMap by postViewModel.commentsMap.collectAsState()
    val comments = commentsMap[postId] ?: emptyList()
    val showPostReportBox by postViewModel.activePostMenuId.collectAsState()
    val isMenuOpen = showPostReportBox == postId
    var editingCommentId by remember { mutableStateOf<String?>(null) }
    var editedCommentContent by remember { mutableStateOf("") }
    var activeMenuCommentId by remember { mutableStateOf<String?>(null) }



    LaunchedEffect(postId) {
        // Gọi API và cập nhật state khi dữ liệu được fetch về
        postViewModel.fetchFavoriteForPost(postId, currentUserId)
    }

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val mediaList = footerItem.imageUrl.split("|").filter { it.isNotBlank() }


    var shouldShowSeeMore by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxWidth()
        .pointerInput(showPostReportBox) {
            if (isMenuOpen) {
                detectTapGestures {
                    postViewModel.closeAllPostMenus()
                }
            }
        }
    ) {
        Column(
            modifier = modifier
                .background(backgroundColor, RectangleShape)
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
        ) {
            // Row for Avatar and Name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navHostController.navigate("post-detail/$postId") },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // để dồn 2 phần trái - phải
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = containerPost.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .clickable{
                                if (currentUserId!=containerPost.id) {
                                    navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                        set("UserId", containerPost.id)
                                    }
                                    navHostController.navigate("otherUserProfile")
                                    println("Isdifferent: "+currentUserId!=containerPost.id)
                                }
                                else {
                                    navHostController.navigate("personal")
                                }
                            },
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            modifier= Modifier
                                .clickable{
                                    if (currentUserId!=containerPost.id) {
                                        navHostController.currentBackStackEntry?.savedStateHandle?.apply {
                                            set("UserId", containerPost.id)
                                        }
                                        navHostController.navigate("otherUserProfile")
                                        println("Isdifferent: "+currentUserId!=containerPost.id)
                                    }
                                    else {
                                        navHostController.navigate("personal")
                                    }
                                },
                            text = containerPost.name,
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                        Text(
                            text = createdAt.timeAgoInVietnam(),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(
                    onClick = { postViewModel.togglePostMenu(postId) },
                    modifier = Modifier
                        .padding(end = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }
            }

            // Content bài viết
            SubcomposeLayout { constraints ->
                val measuredText = subcompose("text") {
                    Text(
                        text = contentPost.content,
                        style = TextStyle(fontSize = 16.sp),
                        maxLines = Int.MAX_VALUE,
                        onTextLayout = {
                            shouldShowSeeMore = it.lineCount > 2
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }[0].measure(constraints)

                val actualText = subcompose("displayText") {
                    Text(
                        text = contentPost.content,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp)
                    )
                }[0].measure(constraints)

                val seeMoreText = if (shouldShowSeeMore) {
                    subcompose("seeMore") {
                        Text(
                            text = if (expanded) "Thu gọn" else "Xem thêm",
                            color = Color.Blue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { expanded = !expanded }
                        )
                    }[0].measure(constraints)
                } else null

                layout(constraints.maxWidth, actualText.height + (seeMoreText?.height ?: 0)) {
                    actualText.placeRelative(0, 0)
                    seeMoreText?.placeRelative(0, actualText.height)
                }
            }
            //anh
            if (mediaList.isNotEmpty()) {
                HorizontalPager(
                    count = mediaList.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) { page ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = mediaList[page],
                            contentDescription = "Post Image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.LightGray)
                                .clickable {
                                    onImageClick(mediaList[page])
                                }
                        )
                        // Ô số thứ tự ảnh
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${page + 1}/${mediaList.size}",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            // ICON like & comment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // LIKE
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        postViewModel.updateFavoriteForPost(
                            postId = postId,
                            userId = currentUserId,
                            userModel = userModel
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(id = if (isFavorited) R.drawable.liked else R.drawable.like),
                        contentDescription = "Like",
                        tint = if (isFavorited) Color.Red else Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$totalFavorites Likes", fontSize = 18.sp)
                }

                // COMMENT
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        onShowComment(postId)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.comment),
                        contentDescription = "Comment",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comment", fontSize = 18.sp)
                }
            }

            // UI COMMENT

        }
        val context = LocalContext.current
        if (isMenuOpen) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 50.dp, end = 8.dp)
                    .width(220.dp)
                    .background(Color.White, shape = RoundedCornerShape(6.dp))
                    .border(5.dp, Color.LightGray)
                    .padding(12.dp)
                    .clickable(enabled = false) {}
            ) {
                // Tố cáo
                if (currentUserId != containerPost.id) {
                    Column(
                        modifier = Modifier
                            .clickable {
                                postViewModel.closeAllPostMenus()
                                onClickReport(postId)
                            }
                            .padding(8.dp)
                    ) {
                        Text("Tố cáo bài viết", fontWeight = FontWeight.Bold)
                        Text("Bài viết có nội dung vi phạm", fontSize = 13.sp, color = Color.Gray)
                    }
                }

                // Chỉ hiển thị nút XÓA nếu là chính người đăng
                if (currentUserId == containerPost.id) {
                    Column(
                        modifier = Modifier
                            .clickable {
                                postViewModel.closeAllPostMenus()
                                postViewModel.deletePost(postId)
                            }
                            .padding(8.dp)
                    ) {
                        Text("Xóa bài viết", fontWeight = FontWeight.Bold, color = Color.Red)
                        Text("Xóa khỏi danh sách bài đăng cá nhân", fontSize = 13.sp, color = Color.Gray)
                    }
                    Divider(thickness = 3.dp, color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))
                    Column(
                        modifier = Modifier
                            .clickable {
                                postViewModel.closeAllPostMenus()
                                val intent = Intent(context, HomeActivity::class.java).apply {
                                    putExtra("navigate-to", "edit_post/$postId")
                                }
                                context.startActivity(intent)
                            }
                            .padding(8.dp)
                    ) {
                        Text("Sửa bài viết", fontWeight = FontWeight.Bold, color = Color.Blue)
                        Text("Chỉnh sửa nội dung bài viết", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
    HorizontalDivider(
        thickness = 2.dp,
        color = Color.Gray
    )
}

@Composable
fun InteractPostManager(
    navHostController: NavHostController,
    user:User?,
    postViewModel: PostViewModel,
    reportedPostId:String?,
    context: Context,
    showFullScreenComment: Boolean,
    selectedPostIdForComment: String?,
    showReportDialog: Boolean,
    onCloseComment: () -> Unit,
    onHideReportDialog: ()-> Unit
    ){
    val coroutineScope = rememberCoroutineScope()

    if (showFullScreenComment && selectedPostIdForComment != null) {
        FullScreenCommentUI(
            navHostController = navHostController,
            postId = selectedPostIdForComment,
            onClose = onCloseComment,
            postViewModel = postViewModel,
            currentUserId = user?.id ?: ""
        )
    }
    println("showReportDialog lay duoc cho post menu: "+showReportDialog)
    userId
    println("user lay duoc cho post menu: "+user)
    if (showReportDialog && user != null) {
        var selectedType by remember { mutableStateOf("Ứng dụng") }
        var reportContent by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(enabled = true, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray)
                    .padding(16.dp)
            ) {
                Text("Báo cáo người dùng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Text("Người báo cáo", fontWeight = FontWeight.Medium)
                Text(user!!.name, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Loại báo cáo", fontWeight = FontWeight.Medium)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { selectedType = "Bác sĩ" }
                            .padding(end = 10.dp)
                    ) {
                        RadioButton(
                            selected = selectedType == "Bác sĩ",
                            onClick = null  // <- để dùng chung onClick bên ngoài
                        )
                        Text("Bác sĩ", modifier = Modifier.padding(start = 5.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { selectedType = "Ứng dụng" }
                            .padding(end = 10.dp)
                    ) {
                        RadioButton(
                            selected = selectedType == "Ứng dụng",
                            onClick = null
                        )
                        Text("Ứng dụng", modifier = Modifier.padding(start = 5.dp))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedType = "Bài viết" }
                    ) {
                        RadioButton(
                            selected = selectedType == "Bài viết",
                            onClick = null
                        )
                        Text("Bài viết", modifier = Modifier.padding(start = 5.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Nội dung báo cáo", fontWeight = FontWeight.Medium)
                TextField(
                    value = reportContent,
                    onValueChange = { reportContent = it },
                    placeholder = { Text("Nhập nội dung...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Huỷ",
                        color = Color.Red,
                        modifier = Modifier
                            .clickable { onHideReportDialog()  }
                            .padding(8.dp),
                        fontWeight = FontWeight.Medium
                    )

                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                val model =
                                    if (user!!.role.lowercase() == "doctor") "Doctor" else "User"
                                val response = RetrofitInstance.reportService.sendReport(
                                    ReportRequest(
                                        reporter = user!!.id,
                                        reporterModel = model,
                                        content = reportContent,
                                        type = selectedType,
                                        reportedId = user!!.id,
                                        postId = if (selectedType == "Bài viết") reportedPostId else null
                                    )
                                )

                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Đã gửi báo cáo thành công",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    println(response)
                                    Toast.makeText(
                                        context,
                                        "Gửi báo cáo thất bại",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Lỗi kết nối đến server",
                                    Toast.LENGTH_SHORT
                                ).show()
                                e.printStackTrace()
                            }
                        }
                        onHideReportDialog()
                    }) {
                        Text("Gửi báo cáo")
                    }
                }
            }
        }
    }
}