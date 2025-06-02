
package com.hellodoc.healthcaresystem.user.personal

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hellodoc.healthcaresystem.viewmodel.DoctorViewModel
import com.hellodoc.healthcaresystem.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.PostColumn
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.ViewIntroduce
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.ViewRating
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.WriteReviewScreen
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.user.home.ZoomableImageDialog
import com.hellodoc.healthcaresystem.user.home.booking.doctorId
//import com.hellodoc.healthcaresystem.user.home.showFullScreenComment
//import com.hellodoc.healthcaresystem.user.home.showReportDialog
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.FullScreenCommentUI
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.InteractPostManager
import com.hellodoc.healthcaresystem.user.post.userId
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.launch

var doctorID = ""

var doctorAvatarUrl = ""

var doctorName = ""

var doctorAddress = ""

var specialtyName = ""

var isClinicPaused = false

var hasHomeService = false

var userName = ""

@Composable
fun UserInfoSkeleton() {
    ConstraintLayout(
        modifier = Modifier
            .background(Color.Cyan)
            .height(330.dp)
            .fillMaxWidth()
    ) {
        val (imgPlaceholder, line1, line2) = createRefs()

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .constrainAs(imgPlaceholder) {
                    top.linkTo(parent.top, margin = 45.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Box(
            modifier = Modifier
                .height(24.dp)
                .width(120.dp)
                .background(Color.LightGray)
                .constrainAs(line1) {
                    top.linkTo(imgPlaceholder.bottom, margin = 15.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Box(
            modifier = Modifier
                .height(20.dp)
                .width(180.dp)
                .background(Color.LightGray)
                .constrainAs(line2) {
                    top.linkTo(line1.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorScreen(
    context: Context,
    navHostController: NavHostController
) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val viewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })



    val context = LocalContext.current
    var shouldReloadPosts by remember { mutableStateOf(false) }

    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    var selectedTab by remember { mutableIntStateOf(0) }
    val showWriteReviewScreen = remember { mutableStateOf(false) }

    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    val coroutineScope = rememberCoroutineScope()


    val navEntry = navHostController.currentBackStackEntry
    val reloadTrigger = navEntry?.savedStateHandle?.getLiveData<Boolean>("shouldReload")?.observeAsState()

    LaunchedEffect(Unit) {
        userId = userViewModel.getUserAttributeString("userId")
        userName = userViewModel.getUserAttributeString("name")
        userModel = if (userViewModel.getUserAttributeString("role") == "user") "User" else "Doctor"
        savedStateHandle?.get<String>("doctorId")?.let {
            doctorId = it
        }
        savedStateHandle?.remove<String>("doctorId")

        selectedTab = savedStateHandle?.get<Int>("selectedTab") ?: 0
        savedStateHandle?.remove<Int>("selectedTab")
        //viewModel.fetchDoctorById(doctorId)
    }


    LaunchedEffect(doctorId) {
        doctorId.let { viewModel.fetchDoctorWithStats(it) }
    }

    val doctor by viewModel.doctor.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    doctorID = doctor?.id ?: ""

    doctorAvatarUrl = doctor?.avatarURL ?: ""

    doctorName = doctor?.name ?: ""

    doctorAddress = doctor?.address ?: ""

    specialtyName = doctor?.specialty?.name ?: ""

    isClinicPaused = doctor?.isClinicPaused ?: false

    hasHomeService = doctor?.hasHomeService ?: false

    LaunchedEffect(reloadTrigger?.value) {
        if (reloadTrigger?.value == true) {
            postViewModel.fetchPosts() // gọi lại danh sách mới
            navHostController.currentBackStackEntry
                ?.savedStateHandle?.set("shouldReload", false)
        }
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
    var reportedPostId by remember { mutableStateOf<String?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showPostReportDialog by remember { mutableStateOf(false) }
    var showFullScreenComment by remember { mutableStateOf(false) }
    var selectedPostIdForComment by remember { mutableStateOf<String?>(null) }
    var showReportBox by remember { mutableStateOf(false) }
    val posts by postViewModel.posts.collectAsState()

    if (selectedImageUrl != null) {
        ZoomableImageDialog(selectedImageUrl = selectedImageUrl, onDismiss = { selectedImageUrl = null })
    }
    if ((showFullScreenComment && selectedPostIdForComment != null) ||
        (showReportDialog && user != null)
    ) {
        InteractPostManager(
            navHostController,
            user,
            postViewModel,
            reportedPostId,
            context,
            showFullScreenComment,
            selectedPostIdForComment,
            showReportDialog,
            onCloseComment = {
                showFullScreenComment = false
            },
            onHideReportDialog = {
                showReportDialog = false
            }
        )
    }
    Scaffold(
        bottomBar = {
            if (!showWriteReviewScreen.value) {
                when (selectedTab) {
                    0 -> if (doctorId!=userId) {BookingButton(navHostController)}
                    1 -> WriteReviewButton { showWriteReviewScreen.value = true }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            item {
                if (isLoading) {
                    UserInfoSkeleton()
                } else {
                    UserInfo(
                        context = context,
                        doctor = doctor,
                        navHostController = navHostController,
                        onImageClick = { selectedImageUrl = it},
                        onShowReportDialog = { showReportDialog = !showReportDialog }
                    )
                }
            }
            item {
                DoctorProfileScreen(
                    navHostController = navHostController,
                    doctor = doctor,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    showWriteReviewScreen = showWriteReviewScreen,
                    onImageClick = { selectedImageUrl = it},
                    onShowPostReportDialog = { showPostReportDialog = !showPostReportDialog }
                )
            }
        }
    }
    if (showReportDialog && userId != null) {
        var selectedType by remember { mutableStateOf("Bác sĩ") }
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
                Text(userName, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Loại báo cáo", fontWeight = FontWeight.Medium)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { selectedType = "Bác sĩ" }
                            .padding(end = 10.dp)
                    ) {
                        Text("Bác sĩ", modifier = Modifier.padding(start = 5.dp))
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
                            .clickable { showReportDialog = !showReportDialog  }
                            .padding(8.dp),
                        fontWeight = FontWeight.Medium
                    )

                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                val response = RetrofitInstance.reportService.sendReport(
                                    ReportRequest(
                                        reporter = userId,
                                        reporterModel = userModel,
                                        content = reportContent,
                                        type = selectedType,
                                        reportedId = doctor!!.id,
                                        postId = null
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
                        showReportDialog = !showReportDialog
                    }) {
                        Text("Gửi báo cáo")
                    }
                }
            }
        }
    }

    if (showPostReportDialog && userId != null) {
        var selectedType by remember { mutableStateOf("Bài viết") }
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
                Text(userName, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Loại báo cáo", fontWeight = FontWeight.Medium)

                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clickable { selectedType = "Bác sĩ" }
//                            .padding(end = 10.dp)
//                    ) {
////                        RadioButton(
////                            selected = selectedType == "Bác sĩ",
////                            onClick = null  // <- để dùng chung onClick bên ngoài
////                        )
//                        Text("Bác sĩ", modifier = Modifier.padding(start = 5.dp))
//                    }
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .clickable { selectedType = "Ứng dụng" }
//                            .padding(end = 10.dp)
//                    ) {
//                        RadioButton(
//                            selected = selectedType == "Ứng dụng",
//                            onClick = null
//                        )
//                        Text("Ứng dụng", modifier = Modifier.padding(start = 5.dp))
//                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedType = "Bài viết" }
                    ) {
//                        RadioButton(
//                            selected = selectedType == "Bài viết",
//                            onClick = null
//                        )
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
                            .clickable { showPostReportDialog = !showPostReportDialog  }
                            .padding(8.dp),
                        fontWeight = FontWeight.Medium
                    )

                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                val response = RetrofitInstance.reportService.sendReport(
                                    ReportRequest(
                                        reporter = userId,
                                        reporterModel = userModel,
                                        content = reportContent,
                                        type = selectedType,
                                        reportedId = doctor!!.id,
                                        postId = null
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
                        showPostReportDialog = !showPostReportDialog
                    }) {
                        Text("Gửi báo cáo")
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfo(
    context: Context,
    doctor: GetDoctorResponse?,
    navHostController: NavHostController,
    onImageClick: (String) -> Unit,
    onShowReportDialog: () -> Unit,
    modifier: Modifier = Modifier
) {

    var showReportBox by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = modifier
            .background(Color.Cyan)
            .height(330.dp)
            .fillMaxWidth()
    ) {
        val (imgIcon, backIcon, moreFuncIcon, optionDialog, tvTitle, tvName, tvNFollower, tvFollowers, tvNFollowing, tvFollowing, tvNLike, tvLikes) = createRefs()

        val imageUrl = doctor?.avatarURL ?: ""
        val name = doctor?.name ?: "Tên bác sĩ"
        val experience = doctor?.experience?.toString() ?: "0"
        val patientsCount = doctor?.patientsCount?.toString() ?: "0"
        val ratingsCount = doctor?.ratingsCount?.toString() ?: "0"

        // Ảnh bác sĩ
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = "Doctor Avatar",
            modifier = Modifier
                .clip(CircleShape)
                .size(110.dp)
                .constrainAs(imgIcon) {
                    top.linkTo(parent.top, margin = 45.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .clickable {
                    onImageClick(imageUrl)
                },
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Bác sĩ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.constrainAs(tvTitle) {
                top.linkTo(imgIcon.bottom, margin = 15.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = name,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.constrainAs(tvName) {
                top.linkTo(tvTitle.bottom, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        val verticalGuideLine30Start = createGuidelineFromStart(0.3f)
        val verticalGuideLine30End = createGuidelineFromEnd(0.3f)
        val horizontalGuideLine20Bot = createGuidelineFromBottom(0.2f)

        Text(
            text = "$experience năm",
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = Color.Blue),
            modifier = Modifier.constrainAs(tvNFollower) {
                top.linkTo(horizontalGuideLine20Bot)
                end.linkTo(verticalGuideLine30Start)
            }
        )

        Text(
            text = "Kinh nghiệm",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.Black),
            modifier = Modifier.constrainAs(tvFollowers) {
                top.linkTo(tvNFollower.bottom, margin = 5.dp)
                end.linkTo(verticalGuideLine30Start)
            }
        )

        Text(
            text = patientsCount,
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = Color.Blue),
            modifier = Modifier.constrainAs(tvNFollowing) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = "Bệnh nhân",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.Black),
            modifier = Modifier.constrainAs(tvFollowing) {
                top.linkTo(tvNFollowing.bottom, margin = 5.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = ratingsCount,
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = Color.Blue),
            modifier = Modifier.constrainAs(tvNLike) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(verticalGuideLine30End, margin = 20.dp)
            }
        )

        Text(
            text = "Đánh giá",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 15.sp, color = Color.Black),
            modifier = Modifier.constrainAs(tvLikes) {
                top.linkTo(tvNLike.bottom, margin = 5.dp)
                start.linkTo(verticalGuideLine30End, margin = 10.dp)
            }
        )

//        Image(
//            painter = painterResource(id = R.drawable.arrow_back),
//            contentDescription = "Back Icon",
//            modifier = Modifier
//                .clip(CircleShape)
//                .size(40.dp)
//                .clickable { navHostController.popBackStack() }
//                .constrainAs(backIcon) {
//                    top.linkTo(parent.top, margin = 16.dp)
//                    start.linkTo(parent.start, margin = 16.dp)
//                },
//            contentScale = ContentScale.Crop
//        )

        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = Color.Black,
            modifier = Modifier
                .size(32.dp)
                .padding(end = 8.dp)
                .clickable {
                    navHostController.popBackStack()
                }
                .constrainAs(backIcon) {
                    top.linkTo(parent.top, margin = 30.dp)
                    start.linkTo(parent.start, margin = 30.dp)
                },
        )

        IconButton(
            onClick = { showReportBox = !showReportBox },
            modifier = Modifier
                .padding(8.dp)
                .constrainAs(moreFuncIcon) {
                    top.linkTo(parent.top, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                },
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = "Menu",
                tint = Color.Black
            )
        }

        if (showReportBox) {
            Column(
                modifier = Modifier
                    .width(250.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .constrainAs(optionDialog) {
                        top.linkTo(moreFuncIcon.bottom)
                        end.linkTo(parent.end, margin = 16.dp)
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showReportBox = !showReportBox
                            onShowReportDialog()
//                            onClickShowReport()
                        }
                ) {
                    Text("Tố cáo & Báo lỗi", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Phản ánh vi phạm hoặc lỗi hệ thống", fontSize = 13.sp)
                }
            }
        }


    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorProfileScreen(
    navHostController: NavHostController,
    doctor: GetDoctorResponse?,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    showWriteReviewScreen: MutableState<Boolean>,
    onImageClick: (String) -> Unit,
    onShowPostReportDialog: () -> Unit,
) {
    println("Doctor lay duoc: "+doctor)

    val tabs = listOf("Thông tin", "Đánh giá", "Bài viết")
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    }
    val token = remember { sharedPreferences.getString("access_token", null) }

    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    val posts by postViewModel.posts.collectAsState()

    val jwt = remember(token) {
        runCatching { JWT(token ?: throw IllegalArgumentException("Token is null")) }
            .onFailure { it.printStackTrace() }
            .getOrNull()
    }

    val currentUserId = remember(jwt) {
        jwt?.getClaim("userId")?.asString() ?: ""
    }

    var refreshReviewsTrigger by rememberSaveable { mutableStateOf(false) }
    var editingReviewId by remember { mutableStateOf<String?>(null) }
    var editingRating by remember { mutableStateOf<Int?>(null) }
    var editingComment by remember { mutableStateOf<String?>(null) }

    var reportedPostId by remember { mutableStateOf<String?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showFullScreenComment by remember { mutableStateOf(false) }
    var selectedPostIdForComment by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(doctor?.id) {
        doctor?.id?.let {
            postViewModel.getPostByUserId(it)
        }
    }


    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    if (selectedImageUrl != null) {
        ZoomableImageDialog(selectedImageUrl = selectedImageUrl, onDismiss = { selectedImageUrl = null })
    }
    if (showFullScreenComment && selectedPostIdForComment != null) {
        FullScreenCommentUI(
            navHostController=navHostController,
            postId = selectedPostIdForComment!!,
            onClose = { showFullScreenComment = false },
            postViewModel = postViewModel,
            currentUserId = userId
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Cyan,
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        println("Vao dươc toi trang user khác")
            when (selectedTab) {
                0 -> ViewIntroduce(doctor = doctor,onImageClick)
                1 -> {
                    if (showWriteReviewScreen.value) {
                        WriteReviewScreen(
                            doctorId = doctor?.id ?: "",
                            userId = currentUserId,
                            initialRating = editingRating,
                            initialComment = editingComment,
                            reviewId = editingReviewId,
                            onBackClick = {
                                showWriteReviewScreen.value = false
                                editingReviewId = null
                                editingRating = null
                                editingComment = null
                            },
                            onSubmitClick = { _, _ ->
                                refreshReviewsTrigger = !refreshReviewsTrigger
                                showWriteReviewScreen.value = false
                                editingReviewId = null
                                editingRating = null
                                editingComment = null
                            }
                        )
                    } else {
                        ViewRating(
                            doctorId = doctor?.id ?: "",
                            refreshTrigger = refreshReviewsTrigger,
                            onEditReview = { reviewId, rating, comment ->
                                editingReviewId = reviewId
                                editingRating = rating
                                editingComment = comment
                                showWriteReviewScreen.value = true
                            },
                            onDeleteReview = {
                                refreshReviewsTrigger = !refreshReviewsTrigger
                            }
                        )
                    }
                }

            2 -> PostColumn(
                posts = posts,
                postViewModel = postViewModel,
                userId = userId ?: "",
                navController =  navHostController,
                onClickReport = { postId ->
                    reportedPostId = postId
//                    showReportDialog = true
                    onShowPostReportDialog()
                },
                onShowComment = { postId ->
                    selectedPostIdForComment = postId
                    showFullScreenComment = true
                }
            )
        }
    }
}

@Composable
fun BookingButton(navController: NavHostController) {
    var showReportBox by remember { mutableStateOf(false) }
    if (doctorID != userId) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (!isClinicPaused) {
                Button(
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("doctorId", doctorID)
                            set("doctorName", doctorName)
                            set("doctorAddress", doctorAddress)
                            set("specialtyName", specialtyName)
                            set("hasHomeService", hasHomeService)
                        }
                        navController.navigate("booking")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00C5CB),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = "Đặt khám",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFCDD2),
                        contentColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        text = "Tạm ngưng nhận lịch",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WriteReviewButton(onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.Center)
        ) {
            Text("Viết đánh giá", fontSize = 16.sp, color = Color.White)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OtherUserProfilePreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    HealthCareSystemTheme {
        DoctorScreen(context, navController)
    }
}

