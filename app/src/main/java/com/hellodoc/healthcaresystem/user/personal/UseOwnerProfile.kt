package com.hellodoc.healthcaresystem.user.personal
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.AsyncImage
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.responsemodel.ContainerPost
import com.hellodoc.healthcaresystem.responsemodel.ContentPost
import com.hellodoc.healthcaresystem.responsemodel.FooterItem
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.api.CommentItem
import com.hellodoc.healthcaresystem.api.ReportRequest
import com.hellodoc.healthcaresystem.responsemodel.PostResponse
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.user.post.userId
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun PreviewProfileUserPage() {
    // Fake SharedPreferences
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("preview_prefs", Context.MODE_PRIVATE)

    // Fake NavController
    val navController = rememberNavController()

    ProfileUserPage(
        sharedPreferences = sharedPreferences,
        navHostController = navController
    )
}


@Composable
fun ProfileUserPage(
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
    val post by postViewModel.posts.collectAsState()

    val token = sharedPreferences.getString("access_token", null)

    val jwt = remember(token) {
        try {
            JWT(token ?: throw IllegalArgumentException("Token is null"))
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val userId = jwt?.getClaim("userId")?.asString()

    // Gọi API để fetch user từ server
    LaunchedEffect(userId) {
        userId?.let {
            userViewModel.getUser(it)
            postViewModel.getUserById(it)
        }

    }
    // Lấy dữ liệu user từ StateFlow
    val user by userViewModel.user.collectAsState()
    // Nếu chưa có user (null) thì không hiển thị giao diện
    if (user == null) {
        println("user == null")
        return
    }
    var showReportDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    // Nếu có user rồi thì hiển thị UI
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                ProfileSection(
                    navHostController = navHostController,
                    user = user!!,
                    onClickShowReport = {
                        showReportDialog = true
                    }
                )
            }
            item {
                PostUser(
                    posts = post,
                    postViewModel = postViewModel,
                    userId = userId ?: ""
                )
            }
        }

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
                                .padding(end = 20.dp)
                        ) {
                            RadioButton(
                                selected = selectedType == "Bác sĩ",
                                onClick = null  // <- để dùng chung onClick bên ngoài
                            )
                            Text("Bác sĩ", modifier = Modifier.padding(start = 6.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedType = "Ứng dụng" }
                        ) {
                            RadioButton(
                                selected = selectedType == "Ứng dụng",
                                onClick = null
                            )
                            Text("Ứng dụng", modifier = Modifier.padding(start = 6.dp))
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
                                .clickable { showReportDialog = false }
                                .padding(8.dp),
                            fontWeight = FontWeight.Medium
                        )

                        Button(onClick = {
                            coroutineScope.launch {
                                try {
                                    val model = if (user!!.role.lowercase() == "doctor") "Doctor" else "User"
                                    val response = RetrofitInstance.reportService.sendReport(
                                        ReportRequest(
                                            reporter = user!!.id,
                                            reporterModel = model,
                                            content = reportContent,
                                            type = selectedType
                                        )
                                    )

                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Đã gửi báo cáo thành công", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Gửi báo cáo thất bại", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi kết nối đến server", Toast.LENGTH_SHORT).show()
                                    e.printStackTrace()
                                }
                            }
                            showReportDialog = false
                        }) {
                            Text("Gửi báo cáo")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSection(navHostController: NavHostController, user: User, onClickShowReport: () -> Unit) {
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
                onClickShowReport = onClickShowReport
            )
            Spacer(modifier = Modifier.height(26.dp))
            UserProfileModifierSection(navHostController, user)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}


@Composable
fun PostUser(
    posts: List<PostResponse>,
    postViewModel: PostViewModel,
    userId: String
    ) {
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
        println("San pham lay duoc post: "+posts)

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
                        name = postItem.user.name,
                        imageUrl = postItem.user.avatarURL ?: ""
                    ),
                    contentPost = ContentPost(postItem.content),
                    footerItem = FooterItem(imageUrl = postItem.media.firstOrNull() ?: ""),
                    postViewModel = postViewModel,
                    currentUserId = userId ?: "",
                    likedUserIds = postItem.likes
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun UserIntroSection(
    user: User,
    onClickShowReport: () -> Unit
) {
    var showReportBox by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            AsyncImage(
                model = user.avatarURL,
                contentDescription = "Avatar",
                modifier = Modifier
                    .height(140.dp)
                    .padding(10.dp)
                    .clip(CircleShape)
            )
            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(user.email)
        }

        // ICON ba chấm ở góc phải
        IconButton(
            onClick = { showReportBox = !showReportBox },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = "Menu",
                tint = Color.Black
            )
        }

        // Khung báo cáo
        if (showReportBox) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 50.dp, end = 8.dp)
                    .background(Color.White, shape = RoundedCornerShape(6.dp))
                    .border(5.dp, Color.LightGray)
                    .clickable {
                        showReportBox = false
                        onClickShowReport()
                    }
                    .padding(12.dp)
            ) {
                Text("Tố cáo người dùng", fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tố cáo người dùng vi phạm chính sách hệ thống", fontSize = 15.sp)
            }
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
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(40.dp)
                .width(150.dp)
        ) {
            Text(
                text = "Chỉnh sửa hồ sơ",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = { navHostController.navigate("doctorRegister") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .height(40.dp)
                .width(150.dp)
        ) {
            Text(
                text = "Quản lý phòng khám",
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ViewPostOwner(
    postId: String,
    containerPost: ContainerPost,
    contentPost: ContentPost,
    footerItem: FooterItem,
    postViewModel: PostViewModel,
    currentUserId: String,
    likedUserIds: List<String>,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.White
    var expanded by remember { mutableStateOf(false) }
    var isCommenting by remember { mutableStateOf(false) }
    var newComment by remember { mutableStateOf("") }
    val commentsState = remember { mutableStateOf<List<CommentItem>>(emptyList()) }
    var shouldFetchComments by remember { mutableStateOf(false) }
    var isLiked by remember(postId) {
        mutableStateOf(currentUserId in likedUserIds)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(shouldFetchComments) {
        if (shouldFetchComments) {
            coroutineScope.launch {
                val result = postViewModel.fetchCommentsForPost(postId)
                commentsState.value = result
                shouldFetchComments = false
            }
        }
    }

    println ("footer item: "+footerItem)
    Column(
        modifier = modifier
            .background(backgroundColor, RectangleShape)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
    ) {
        // Row for Avatar and Name
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            AsyncImage(
                model = containerPost.imageUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )

            // Name
            Text(
                text = containerPost.name,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Black
                ),
                modifier = Modifier
                    .padding(start = 10.dp)
            )
        }

        // Content bài viết
        Text(
            text = contentPost.content,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color.Black
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            maxLines = if (expanded) Int.MAX_VALUE else 2,
            overflow = TextOverflow.Ellipsis
        )

        // Nút "Xem thêm" / "Thu gọn"
        Text(
            text = if (expanded) "Thu gọn" else "Xem thêm",
            color = Color.Blue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(top = 4.dp)
        )

        AsyncImage(
            model = footerItem.imageUrl,
            contentDescription = "Post Image",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.LightGray)
        )

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
                    postViewModel.likePost(postId = postId, userId = currentUserId)
                    isLiked = !isLiked
                }
            ) {
                Icon(
                    painter = painterResource(id = if (isLiked) R.drawable.liked else R.drawable.like),
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Black,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Like", fontSize = 18.sp)
            }

            // COMMENT
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    isCommenting = !isCommenting
                    if (isCommenting) {
                        shouldFetchComments = true
                    }
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
        if (isCommenting) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Bình luận:", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                if (commentsState.value.isNotEmpty()) {
                    Column {
                        commentsState.value.forEach { comment ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = comment.user?.avatarURL ?: "",
                                    contentDescription = "avatar",
                                    modifier = Modifier.size(30.dp).clip(CircleShape)
                                )
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(comment.user?.name ?: "Ẩn danh", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(comment.content, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                } else {
                    Text("Chưa có bình luận nào.")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Nhập bình luận...") }
                    )
                    Button(onClick = {
                        postViewModel.sendComment(postId, currentUserId, newComment)
                        newComment = ""
                    }) {
                        Text("Gửi")
                    }
                }
            }
        }
    }
}




