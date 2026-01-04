package com.hellodoc.healthcaresystem.view.user.home.doctor

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
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
        //doctorViewModel.fetchDoctorById(currentDoctorId)
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
            //doctorViewModel.fetchDoctorById(currentDoctorId)
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
    val imageUrl = doctor?.avatarURL ?: ""
    val name = doctor?.name ?: "Tên bác sĩ"
    val specialty = doctor?.specialty?.name ?: "Bác sĩ"
    val experience = doctor?.experience?.toString() ?: "0"
    val patientsCount = doctor?.patientsCount?.toString() ?: "0"
    val ratingsCount = doctor?.ratingsCount?.toString() ?: "0"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 280.dp)
        ) {
            // Background Header (Blurred Avatar)
            AsyncImage(
                model = imageUrl,
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .blur(20.dp)
            )

            // Top functional icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navHostController.popBackStack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                IconButton(
                    onClick = { showReportBox = !showReportBox },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = "More",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Report Menu Overlay (Outside the Row to prevent shifting)
            if (showReportBox) {
                Card(
                    modifier = Modifier
                        .width(220.dp)
                        .padding(top = 60.dp, end = 12.dp)
                        .align(Alignment.TopEnd)
                        .clickable {
                            showReportBox = false
                            onShowReportDialog()
                        },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Tố cáo & Báo lỗi",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Phản ánh vi phạm hoặc lỗi hệ thống",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Avatar overlapping
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 140.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    border = BorderStroke(4.dp, Color.White),
                    shadowElevation = 8.dp
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onImageClick(imageUrl) }
                    )
                }
            }

            // Name & Specialty Section
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 150.dp, top = 210.dp, end = 16.dp, bottom = 12.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = specialty,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Stats Row (Experience, Patients, Ratings)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoStatItem(label = "Kinh nghiệm", value = "$experience năm")
            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            InfoStatItem(label = "Bệnh nhân", value = patientsCount)
            VerticalDivider(modifier = Modifier.height(30.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            InfoStatItem(label = "Đánh giá", value = ratingsCount)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun InfoStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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

    println("current user trong doctor profile là $currentUser")

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
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    text = {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = if (selectedTab == index) FontWeight.ExtraBold else FontWeight.Medium
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