
package com.hellodoc.healthcaresystem.user.personal

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.hellodoc.healthcaresystem.user.home.ZoomableImageDialog
import com.hellodoc.healthcaresystem.viewmodel.PostViewModel

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
fun ProfileScreen(navHostController: NavHostController) {
    val sharedPreferences = navHostController.context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val viewModel: DoctorViewModel = viewModel(factory = viewModelFactory {
        initializer { DoctorViewModel(sharedPreferences) }
    })

    var selectedTab by remember { mutableStateOf(0) }
    val showWriteReviewScreen = remember { mutableStateOf(false) }
    var doctorId by remember { mutableStateOf<String?>(null) }

    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    //
    LaunchedEffect(savedStateHandle) {
        doctorId = savedStateHandle?.get<String>("doctorId")
        savedStateHandle?.remove<String>("doctorId")

        selectedTab = savedStateHandle?.get<Int>("selectedTab") ?: 0
        savedStateHandle?.remove<Int>("selectedTab")
    }


    LaunchedEffect(doctorId) {
        doctorId?.let { viewModel.fetchDoctorWithStats(it) }
    }
    val doctor by viewModel.doctor.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    if (selectedImageUrl != null) {
        ZoomableImageDialog(selectedImageUrl = selectedImageUrl, onDismiss = { selectedImageUrl = null })
    }
    Scaffold(
        bottomBar = {
            if (!showWriteReviewScreen.value) {
                when (selectedTab) {
                    0 -> BookingButton()
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
                        doctor = doctor,
                        navHostController = navHostController,
                        onImageClick = { selectedImageUrl = it}
                    )
                }
            }
            item {
                OtherUserListScreen(
                    navHostController = navHostController,
                    doctor = doctor,
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    showWriteReviewScreen = showWriteReviewScreen,
                    onImageClick = { selectedImageUrl = it}
                )
            }
        }
    }
}

@Composable
fun UserInfo(
    doctor: GetDoctorResponse?,
    navHostController: NavHostController,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .background(Color.Cyan)
            .height(330.dp)
            .fillMaxWidth()
    ) {
        val (imgIcon, backIcon, tvTitle, tvName, tvNFollower, tvFollowers, tvNFollowing, tvFollowing, tvNLike, tvLikes) = createRefs()

        val imageUrl = doctor?.avatarURL ?: ""
        val name = doctor?.name ?: "Tên bác sĩ"
        val experience = doctor?.experience?.toString() ?: "69"
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

        Image(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = "Back Icon",
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp)
                .clickable { navHostController.popBackStack() }
                .constrainAs(backIcon) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                },
            contentScale = ContentScale.Crop
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OtherUserListScreen(
    navHostController: NavHostController,
    doctor: GetDoctorResponse?,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    showWriteReviewScreen: MutableState<Boolean>,
    onImageClick: (String) -> Unit
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
                navController =  navHostController,
                userId = currentUserId,
                onClickReport = { postId ->
                    reportedPostId = postId
                    showReportDialog = true
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
fun BookingButton() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .align(Alignment.Center)
        ) {
            Text(
                text = "Đặt khám",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
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
    val navController = rememberNavController()
    HealthCareSystemTheme {
        ProfileScreen(navController)
    }
}

