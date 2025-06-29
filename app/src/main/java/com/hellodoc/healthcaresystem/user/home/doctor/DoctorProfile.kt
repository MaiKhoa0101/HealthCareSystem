package com.hellodoc.healthcaresystem.user.home.doctor

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Brush
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
import com.hellodoc.healthcaresystem.user.post.PostColumn
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.admin.ZoomableImageDialog
import com.hellodoc.healthcaresystem.requestmodel.ReportRequest
import com.hellodoc.healthcaresystem.responsemodel.User
import com.hellodoc.healthcaresystem.retrofit.RetrofitInstance
import com.hellodoc.healthcaresystem.user.home.booking.doctorId
import com.hellodoc.healthcaresystem.user.home.report.ReportDoctor
import com.hellodoc.healthcaresystem.user.home.report.ReportPostDoctor
import com.hellodoc.healthcaresystem.user.home.root.ZoomableImageDialog
import com.hellodoc.healthcaresystem.user.personal.PostSkeleton
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.ReportViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    durationMillis: Int = 1000
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .background(
                Color.LightGray.copy(alpha = alpha),
                RoundedCornerShape(8.dp)
            )
    )
}

@Composable
fun UserInfoSkeleton() {
    ConstraintLayout(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00E5FF),
                        Color(0xFF00C5CB)
                    )
                )
            )
            .height(330.dp)
            .fillMaxWidth()
    ) {
        val (
            backButton, moreButton, avatarPlaceholder,
            titleLine, nameLine, statRow1, statRow2, statRow3
        ) = createRefs()

        // Back button skeleton
        ShimmerEffect(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 30.dp)
                    start.linkTo(parent.start, margin = 30.dp)
                }
        )

        // More button skeleton
        ShimmerEffect(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .constrainAs(moreButton) {
                    top.linkTo(parent.top, margin = 30.dp)
                    end.linkTo(parent.end, margin = 30.dp)
                }
        )

        // Avatar skeleton
        ShimmerEffect(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .constrainAs(avatarPlaceholder) {
                    top.linkTo(parent.top, margin = 45.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Title line skeleton
        ShimmerEffect(
            modifier = Modifier
                .height(28.dp)
                .width(80.dp)
                .constrainAs(titleLine) {
                    top.linkTo(avatarPlaceholder.bottom, margin = 15.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Name line skeleton
        ShimmerEffect(
            modifier = Modifier
                .height(24.dp)
                .width(160.dp)
                .constrainAs(nameLine) {
                    top.linkTo(titleLine.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Stats row skeletons
        val verticalGuideLine30Start = createGuidelineFromStart(0.3f)
        val verticalGuideLine30End = createGuidelineFromEnd(0.3f)
        val horizontalGuideLine20Bot = createGuidelineFromBottom(0.2f)

        // Experience stat
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(statRow1) {
                top.linkTo(horizontalGuideLine20Bot)
                end.linkTo(verticalGuideLine30Start)
            }
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .height(30.dp)
                    .width(60.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            ShimmerEffect(
                modifier = Modifier
                    .height(16.dp)
                    .width(80.dp)
            )
        }

        // Patients stat
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(statRow2) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .height(30.dp)
                    .width(50.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            ShimmerEffect(
                modifier = Modifier
                    .height(16.dp)
                    .width(70.dp)
            )
        }

        // Ratings stat
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(statRow3) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(verticalGuideLine30End, margin = 20.dp)
            }
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .height(30.dp)
                    .width(45.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            ShimmerEffect(
                modifier = Modifier
                    .height(16.dp)
                    .width(65.dp)
            )
        }
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
    val doctorViewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })
    var selectedTab by remember { mutableIntStateOf(0) }
    val showWriteReviewScreen = remember { mutableStateOf(false) }

    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle

    var currentDoctorId by remember { mutableStateOf("") }

    val navEntry = navHostController.currentBackStackEntry
    val reloadTrigger =
        navEntry?.savedStateHandle?.getLiveData<Boolean>("shouldReload")?.observeAsState()
    val youTheCurrentUserUseThisApp by userViewModel.user.collectAsState()
    val doctor by viewModel.doctor.collectAsState()

    var userId by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var userModel by remember { mutableStateOf("") }

    var doctorId by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("") }
    var doctorAddress by remember { mutableStateOf("") }
    var doctorAvatar by remember { mutableStateOf("") }
    var specialtyName by remember { mutableStateOf("") }
    var isClinicPaused by remember { mutableStateOf(false) }
    var hasHomeService by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        val userId = userViewModel.getUserAttributeString("userId")
        userViewModel.getUser(userId)
        savedStateHandle?.get<String>("doctorId")?.let { newDoctorId ->
            currentDoctorId = newDoctorId
        }
        doctorViewModel.fetchDoctorById(currentDoctorId)
        savedStateHandle?.remove<String>("doctorId")

        selectedTab = savedStateHandle?.get<Int>("selectedTab") ?: 0
        savedStateHandle?.remove<Int>("selectedTab")
        //viewModel.fetchDoctorById(doctorId)
        println("doctorId: " + doctorId)
    }

    // Theo dõi thay đổi doctorId và reset state khi cần
    LaunchedEffect(currentDoctorId) {
        if (currentDoctorId.isNotEmpty()) {
            // Reset loading states
            viewModel.resetStates()

            // Fetch doctor data
            viewModel.fetchDoctorById(currentDoctorId)
            viewModel.fetchDoctorWithStats(currentDoctorId)

            // Reset tab về 0 khi chuyển sang bác sĩ khác
            selectedTab = 0
        }
    }

    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingStat by viewModel.isLoadingStats.collectAsState()
    doctorId = doctor?.id ?: ""

    doctorName = doctor?.name ?: ""

    doctorAddress = doctor?.address ?: ""

    doctorAvatar = doctor?.avatarURL ?: ""

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


    // Lấy dữ liệu user từ StateFlow
    // Nếu chưa có user (null) thì không hiển thị giao diện

    // Hiển thị loading skeleton nếu đang tải hoặc chưa có dữ liệu
    if (isLoading || doctor == null) {
        UserInfoSkeleton()
        return
    }

    var selectedImageUrl by remember { mutableStateOf<String?>(null) }
    if (selectedImageUrl != null) {
        ZoomableImageDialog(
            selectedImageUrl = selectedImageUrl,
            onDismiss = { selectedImageUrl = null })
    }
    var reportedPostId by remember { mutableStateOf<String?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showPostReportDialog by remember { mutableStateOf(false) }
    var showFullScreenComment by remember { mutableStateOf(false) }
    var selectedPostIdForComment by remember { mutableStateOf<String?>(null) }
    var showReportBox by remember { mutableStateOf(false) }
    val posts by postViewModel.posts.collectAsState()

    if (selectedImageUrl != null) {
        ZoomableImageDialog(
            selectedImageUrl = selectedImageUrl,
            onDismiss = { selectedImageUrl = null })
    }
    if (youTheCurrentUserUseThisApp == null) {
        return
    } else {
        Scaffold(
            bottomBar = {
                if (!showWriteReviewScreen.value) {
                    when (selectedTab) {
                        0 -> if (doctorId != userId) {
                            BookingButton(userId,
                                doctorId,
                                doctorName,
                                doctorAddress,
                                doctorAvatar,
                                specialtyName,
                                hasHomeService,
                                isClinicPaused,
                                navHostController)
                        }

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
                            onImageClick = { selectedImageUrl = it },
                            onShowReportDialog = { showReportDialog = !showReportDialog }
                        )
                    }
                }
                item {

                    DoctorProfileScreen(
                        navHostController = navHostController,
                        doctor = doctor,
                        youTheCurrentUserUseThisApp = youTheCurrentUserUseThisApp!!,
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        showWriteReviewScreen = showWriteReviewScreen,
                        onImageClick = { selectedImageUrl = it },
                        onShowPostReportDialog = { showPostReportDialog = !showPostReportDialog },
                        isLoadingStat = isLoadingStat
                    )
                }
            }
        }
    }
    if (showReportDialog && youTheCurrentUserUseThisApp != null) {
        ReportDoctor(
            context,
            youTheCurrentUserUseThisApp,
            doctor,
            onClickShowReportDialog = { showReportDialog = !showReportDialog },
            sharedPreferences,
        )
    }
    if (showPostReportDialog && youTheCurrentUserUseThisApp != null) {
        ReportPostDoctor(
            context,
            youTheCurrentUserUseThisApp,
            doctor,
            onClickShowPostReportDialog = { showPostReportDialog = !showPostReportDialog },
            sharedPreferences,
        )
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
    youTheCurrentUserUseThisApp: User,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    showWriteReviewScreen: MutableState<Boolean>,
    onImageClick: (String) -> Unit,
    onShowPostReportDialog: () -> Unit,
    isLoadingStat: Boolean

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
                0 -> {
                    if( doctor == null || isLoadingStat ) {
                        RatingOverviewSkeleton()
                    } else{
                        ViewIntroduce(doctor = doctor,onImageClick)
                    }
                }
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
                navHostController = navHostController,
                idUserOfPost = doctor?.id ?: "",
                userWhoInteractWithThisPost = youTheCurrentUserUseThisApp,
                postViewModel = postViewModel
            )
        }
    }

//    if (showReportDialog) {
//        InteractPostManager(
//            navHostController = navHostController,
//            user = user,
//            postViewModel = postViewModel,
//            reportedPostId = reportedPostId,
//            context = context,
//            showFullScreenComment = showFullScreenComment,
//            selectedPostIdForComment = selectedPostIdForComment,
//            showReportDialog = showReportDialog,
//            onCloseComment = { showFullScreenComment = false },
//            onHideReportDialog = { showReportDialog = false }
//        )
//    }
}

@Composable
fun BookingButton(
    userId: String,
    doctorId: String,
    doctorName: String,
    doctorAddress: String,
    doctorAvatar: String,
    specialtyName: String,
    hasHomeService: Boolean,
    isClinicPaused: Boolean,
    navController: NavHostController) {
    var showReportBox by remember { mutableStateOf(false) }
    if (doctorId != userId) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (!isClinicPaused) {
                Button(
                    onClick = {
                        println("doctorId: " + doctorId)
                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("doctorId", doctorId)
                            set("doctorName", doctorName)
                            set("doctorAddress", doctorAddress)
                            set("doctorAvatar", doctorAvatar)
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

@Composable
fun RatingOverviewSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
    ) {
        // Rating header section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main rating score
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ShimmerEffect(
                        modifier = Modifier
                            .width(100.dp)
                            .height(48.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ShimmerEffect(
                        modifier = Modifier
                            .width(60.dp)
                            .height(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Total reviews count
                ShimmerEffect(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Filter buttons row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // "Tất cả" button
            ShimmerEffect(
                modifier = Modifier
                    .height(40.dp)
                    .width(70.dp)
                    .clip(RoundedCornerShape(20.dp))
            )

            // Star filter buttons
            repeat(5) { index ->
                ShimmerEffect(
                    modifier = Modifier
                        .height(40.dp)
                        .width(50.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Review items
        repeat(4) { index ->
            ReviewItemSkeleton()
            if (index < 3) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ReviewItemSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User profile section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                ShimmerEffect(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // User name
                    ShimmerEffect(
                        modifier = Modifier
                            .width(140.dp)
                            .height(18.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Date
                    ShimmerEffect(
                        modifier = Modifier
                            .width(100.dp)
                            .height(14.dp)
                    )
                }

                // Menu button
                ShimmerEffect(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating stars
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(5) {
                    ShimmerEffect(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comment text lines
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp)
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp)
                )
            }
        }
    }
}

@Composable
fun PostSkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Post header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    ShimmerEffect(
                        modifier = Modifier
                            .width(120.dp)
                            .height(18.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerEffect(
                        modifier = Modifier
                            .width(80.dp)
                            .height(14.dp)
                    )
                }

                ShimmerEffect(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post content
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(16.dp)
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Post image placeholder
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                repeat(3) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ShimmerEffect(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ShimmerEffect(
                            modifier = Modifier
                                .width(40.dp)
                                .height(16.dp)
                        )
                    }
                }
            }
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

