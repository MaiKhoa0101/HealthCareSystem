package com.hellodoc.healthcaresystem.view.user.home.doctor

import android.content.Context
import android.os.Build
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
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.GetDoctorResponse
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.auth0.android.jwt.JWT
import com.hellodoc.healthcaresystem.model.dataclass.responsemodel.User
import com.hellodoc.healthcaresystem.skeleton.RatingOverviewSkeleton
import com.hellodoc.healthcaresystem.skeleton.UserInfoSkeleton
import com.hellodoc.healthcaresystem.view.user.home.report.ReportDoctor
import com.hellodoc.healthcaresystem.view.user.home.report.ReportPostDoctor
import com.hellodoc.healthcaresystem.view.user.post.PostColumn
import com.hellodoc.healthcaresystem.view.user.supportfunction.AvatarDetailDialog

import com.hellodoc.healthcaresystem.viewmodel.GeminiHelper
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel
import com.hellodoc.healthcaresystem.viewmodel.UserViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorScreen(
    context: Context,
    navHostController: NavHostController
) {
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val context = LocalContext.current
    var shouldReloadPosts by remember { mutableStateOf(false) }

    val userViewModel: UserViewModel = hiltViewModel()
    val postViewModel: PostViewModel = hiltViewModel()
    val doctorViewModel: DoctorViewModel = hiltViewModel()
    var selectedTab by remember { mutableIntStateOf(0) }
    val showWriteReviewScreen = remember { mutableStateOf(false) }

    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle

    var currentDoctorId by remember { mutableStateOf("") }

    val navEntry = navHostController.currentBackStackEntry
    val reloadTrigger =
        navEntry?.savedStateHandle?.getLiveData<Boolean>("shouldReload")?.observeAsState()
    val youTheCurrentUserUseThisApp by userViewModel.user.collectAsState()
    val doctor by doctorViewModel.doctor.collectAsState()

    var userId by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var userModel by remember { mutableStateOf("") }

    var isRating by remember { mutableStateOf(false) }

    var doctorId by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("") }
    var doctorAddress by remember { mutableStateOf("") }
    var doctorAvatar by remember { mutableStateOf("") }
    var specialtyName by remember { mutableStateOf("") }
    var isClinicPaused by remember { mutableStateOf(false) }
    var hasHomeService by remember { mutableStateOf(false) }
    var showMediaDetail by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val userId = userViewModel.getUserAttribute("userId", context)
        userViewModel.getUser(userId)
        savedStateHandle?.get<String>("doctorId")?.let { newDoctorId ->
            currentDoctorId = newDoctorId
        }
        doctorViewModel.fetchDoctorById(currentDoctorId)
        savedStateHandle?.remove<String>("doctorId")

        selectedTab = savedStateHandle?.get<Int>("selectedTab") ?: 0
        savedStateHandle?.remove<Int>("selectedTab")
        isRating = savedStateHandle?.get<Boolean>("isRating") ?: false
        //viewModel.fetchDoctorById(doctorId)
    }

    // Theo dõi thay đổi doctorId và reset state khi cần
    LaunchedEffect(currentDoctorId) {
        if (currentDoctorId.isNotEmpty()) {
            // Reset loading states
            doctorViewModel.resetStates()

            // Fetch doctor data
            doctorViewModel.fetchDoctorById(currentDoctorId)
            doctorViewModel.fetchDoctorWithStats(currentDoctorId)

            // Reset tab về 0 khi chuyển sang bác sĩ khác
            selectedTab = 0
        }
    }

    val isLoading by doctorViewModel.isLoading.collectAsState()
    val isLoadingStat by doctorViewModel.isLoadingStats.collectAsState()
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

    // Hiển thị loading skeleton nếu đang tải hoặc chưa có dữ liệu
    if (isLoading || doctor == null) {
        UserInfoSkeleton()
        return
    }

    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    if (showMediaDetail && selectedImageUrl != null) {
        AvatarDetailDialog(
            mediaUrls = selectedImageUrl.toString(),
            onDismiss = {
                showMediaDetail = false
            }
        )
    }
    var reportedPostId by remember { mutableStateOf<String?>(null) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showPostReportDialog by remember { mutableStateOf(false) }
    var showFullScreenComment by remember { mutableStateOf(false) }
    var selectedPostIdForComment by remember { mutableStateOf<String?>(null) }
    var showReportBox by remember { mutableStateOf(false) }
    val posts by postViewModel.posts.collectAsState()

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

                        1 -> if(isRating) {
                            WriteReviewButton { showWriteReviewScreen.value = true }
                        }
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
                            onImageClick = {
                                selectedImageUrl = it
                                showMediaDetail = true
                           },
                            onShowReportDialog = { showReportDialog = !showReportDialog }
                        )
                    }
                }
                item {
                    DoctorProfileScreen(
                        navHostController = navHostController,
                        doctor = doctor,
                        currentUser = youTheCurrentUserUseThisApp!!,
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

    var currentPostId by remember { mutableStateOf<String?>(null) }

    if (showPostReportDialog && youTheCurrentUserUseThisApp != null) {
        ReportPostDoctor(
            context,
            youTheCurrentUserUseThisApp,
            doctor,
            postId = currentPostId!!,
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
            .background(MaterialTheme.colorScheme.primaryContainer)
            .height(330.dp)
            .fillMaxWidth()
    ) {
        val (imgIcon, backIcon, moreFuncIcon, optionDialog, tvTitle, tvName, tvNFollower, tvFollowers, tvNFollowing, tvFollowing, tvNLike, tvLikes) = createRefs()

        val imageUrl = doctor?.avatarURL ?: ""
        val name = doctor?.name ?: "Tên bác sĩ"
        val experience = doctor?.experience?.toString() ?: "0"
        val patientsCount = doctor?.patientsCount?.toString() ?: "0"
        val ratingsCount = doctor?.ratingsCount?.toString() ?: "0"

        println("data của doctor la $doctor")

        // Ảnh bác sĩ
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = "Doctor Avatar",
            modifier = Modifier
                .clip(CircleShape)
                .size(110.dp)
                .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
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
            color = MaterialTheme.colorScheme.onBackground,
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
            color = MaterialTheme.colorScheme.onBackground,
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
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.constrainAs(tvNFollower) {
                top.linkTo(horizontalGuideLine20Bot)
                end.linkTo(verticalGuideLine30Start)
            }
        )

        Text(
            text = "Kinh nghiệm",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.constrainAs(tvFollowers) {
                top.linkTo(tvNFollower.bottom, margin = 5.dp)
                end.linkTo(verticalGuideLine30Start)
            }
        )

        Text(
            text = patientsCount,
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.constrainAs(tvNFollowing) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = "Bệnh nhân",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.constrainAs(tvFollowing) {
                top.linkTo(tvNFollowing.bottom, margin = 5.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = ratingsCount,
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = MaterialTheme.colorScheme.secondary),
            modifier = Modifier.constrainAs(tvNLike) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(verticalGuideLine30End, margin = 20.dp)
            }
        )

        Text(
            text = "Đánh giá",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.constrainAs(tvLikes) {
                top.linkTo(tvNLike.bottom, margin = 5.dp)
                start.linkTo(verticalGuideLine30End, margin = 10.dp)
            }
        )

        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back Button",
            tint = MaterialTheme.colorScheme.onBackground,
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
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        if (showReportBox) {
            Column(
                modifier = Modifier
                    .width(250.dp)
                    .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(8.dp))
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
    currentUser: User,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    showWriteReviewScreen: MutableState<Boolean>,
    onImageClick: (String) -> Unit,
    onShowPostReportDialog: () -> Unit,
    isLoadingStat: Boolean

) {
    val tabs = listOf("Thông tin", "Đánh giá", "Bài viết")
    val postViewModel: PostViewModel = hiltViewModel()
    var refreshReviewsTrigger by rememberSaveable { mutableStateOf(false) }
    var editingReviewId by remember { mutableStateOf<String?>(null) }
    var editingRating by remember { mutableStateOf<Int?>(null) }
    var editingComment by remember { mutableStateOf<String?>(null) }

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
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    selectedContentColor = MaterialTheme.colorScheme.secondary,
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
                        userId = currentUser.id,
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
                        },
                        userId = currentUser.id
                    )
                }
            }

            2 -> PostColumn(
                navHostController = navHostController,
                postViewModel = postViewModel
            )
        }
    }
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
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
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.Center)
        ) {
            Text("Viết đánh giá", fontSize = 16.sp, color = MaterialTheme.colorScheme.background)
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