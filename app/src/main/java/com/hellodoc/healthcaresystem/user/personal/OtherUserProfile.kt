package com.hellodoc.healthcaresystem.user.personal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hellodoc.healthcaresystem.R
import com.hellodoc.healthcaresystem.ui.theme.HealthCareSystemTheme
import com.hellodoc.healthcaresystem.responsemodel.modeluser.ProfileUser
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.PostColumn
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.ViewIntroduce
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.ViewRating
import com.hellodoc.healthcaresystem.user.personal.otherusercolumn.WriteReviewScreen


@Composable
fun ProfileScreen(navHostController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    var showWriteReviewScreen by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (!showWriteReviewScreen) {
                when (selectedTab) {
                    0 -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .background(color = Color.Transparent)
                        ) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Cyan,
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
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    1 -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Button(
                                onClick = {
                                    showWriteReviewScreen = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .align(Alignment.Center)
                            ) {
                                Text("Viết đánh giá", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }

    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding(),
                top = 0.dp
            )
        ) {
            item {
                UserInfo(
                    profileUser = ProfileUser(
                        image = R.drawable.img,
                        name = "Bác sĩ",
                        title = "Mai Văn Khám",
                        butProf = "Chỉnh sửa hồ sơ",
                        butSchedule = "Quản lý phòng khám",
                        nExper = 69,
                        exper = "Kinh nghiệm",
                        nPatient = 3,
                        patient = "Bệnh nhân",
                        nRate = 70,
                        rate = "Đánh giá",
                        role = "0"
                    ),
                    navHostController
                )
            }

            item {
                OtherUserListScreen(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    showWriteReviewScreen = showWriteReviewScreen,
                    onDismissWriteReview = { showWriteReviewScreen = false }
                )
            }
        }
    }
}

@Composable
fun OtherUserListScreen(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    showWriteReviewScreen: Boolean = false,
    onDismissWriteReview: () -> Unit = {}
) {
    val tabs = listOf("Thông tin", "Đánh giá", "Bài viết")

    Column {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Tab Row
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

            when (selectedTab) {
                0 -> ViewIntroduce()
                1 -> {
                    if (showWriteReviewScreen) {
                        WriteReviewScreen(
                            onBackClick = onDismissWriteReview,
                            onSubmitClick = { star, comment ->
                                onDismissWriteReview()
                            }
                        )
                    } else {
                        ViewRating()
                    }
                }
                2 -> PostColumn()
            }
        }
    }
}

@Composable
fun UserInfo(
    profileUser: ProfileUser,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    val savedStateHandle = navHostController.previousBackStackEntry?.savedStateHandle
    var doctorId by remember { mutableStateOf("chưa có dữ liệu id") }

    // Cập nhật giá trị nếu có trong savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.get<String>("doctorId")?.let {
            doctorId = it
        }
    }
    val backgroundColor = Color.Cyan
    ConstraintLayout(
        modifier = modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .height(330.dp)
            .fillMaxWidth()
    ) {
        val verticalGuideLine30Start = createGuidelineFromStart(0.3f)
        val verticalGuideLine30End = createGuidelineFromEnd(0.3f)
        val horizontalGuideLine50 = createGuidelineFromTop(0.5f)

        val (imgIcon, backIcon) = createRefs()
        Image(
            painter = painterResource(id = profileUser.image),
            contentDescription = null,
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(110.dp)
                .constrainAs(imgIcon) {
                    top.linkTo(parent.top, margin = 45.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(horizontalGuideLine50)
                },
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = null,
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(40.dp)
                .clickable {
                    navHostController.popBackStack()
                }
                .constrainAs(backIcon) {
                    top.linkTo(imgIcon.top)
                    start.linkTo(parent.start, margin = 15.dp)
                },
        )

        val (tvName, tvEmail) = createRefs()
        Text(
            profileUser.name,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color.Black
            ),
            modifier = Modifier.constrainAs(tvName) {
                top.linkTo(imgIcon.bottom, margin = 15.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            doctorId,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = Color.Black
            ),
            modifier = Modifier.constrainAs(tvEmail) {
                top.linkTo(tvName.bottom, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        if (profileUser.role == "1") {
            ButtonProfile(profileUser = profileUser)
        } else {
            ShowInfo(profileUser = profileUser)
        }
    }
}

@Composable
fun ShowInfo(profileUser: ProfileUser) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val verticalGuideLine30Start = createGuidelineFromStart(0.3f)
        val verticalGuideLine30End = createGuidelineFromEnd(0.3f)
        val horizontalGuideLine40Bot = createGuidelineFromBottom(0.4f)
        val horizontalGuideLine20Bot = createGuidelineFromBottom(0.2f)

        val (tvNFollower, tvFollowers, tvNFollowing, tvFollowing, tvNLike, tvLikes) = createRefs()
        val numberTextColor = Color.Blue
        val textColor = Color.Black

        Text(
            profileUser.nExper.toString()+" năm",
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = numberTextColor),
            modifier = Modifier.constrainAs(tvNFollower) {
                top.linkTo(horizontalGuideLine20Bot)
                end.linkTo(verticalGuideLine30Start)
            }
        )
        Text(
            profileUser.exper,
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = textColor),
            modifier = Modifier.constrainAs(tvFollowers) {
                top.linkTo(tvNFollower.bottom, margin = 5.dp)
                end.linkTo(verticalGuideLine30Start)
            }
        )
        Text(
            profileUser.nPatient.toString(),
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = numberTextColor),
            modifier = Modifier.constrainAs(tvNFollowing) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            profileUser.patient,
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = textColor),
            modifier = Modifier.constrainAs(tvFollowing) {
                top.linkTo(tvNFollowing.bottom, margin = 5.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            profileUser.nRate.toString(),
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = numberTextColor),
            modifier = Modifier.constrainAs(tvNLike) {
                top.linkTo(horizontalGuideLine20Bot)
                start.linkTo(verticalGuideLine30End, margin = 20.dp)
            }
        )
        Text(
            profileUser.rate,
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = textColor),
            modifier = Modifier.constrainAs(tvLikes) {
                top.linkTo(tvNLike.bottom, margin = 5.dp)
                start.linkTo(verticalGuideLine30End, margin = 10.dp)
            }
        )
    }
}

@Composable
fun ButtonProfile(profileUser: ProfileUser) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val horizontalGuideLine30Bot = createGuidelineFromBottom(0.3f)
        val (btProf, btSchel) = createRefs()
        Button(
            onClick = {},
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(130.dp)
                .constrainAs(btProf) {
                    top.linkTo(horizontalGuideLine30Bot, margin = 30.dp)
                    start.linkTo(parent.start, margin = 30.dp)
                }
        ) {
            Text(
                profileUser.butProf,
                modifier = Modifier.padding(0.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                maxLines = 1,
            )
        }

        Button(
            onClick = {},
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(130.dp)
                .constrainAs(btSchel) {
                    top.linkTo(horizontalGuideLine30Bot, margin = 30.dp)
                    end.linkTo(parent.end, margin = 30.dp)
                }
        ) {
            Text(
                profileUser.butSchedule,
                modifier = Modifier.padding(0.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                maxLines = 1,
            )
        }
    }
}


@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    val fakeNavController = rememberNavController()
    HealthCareSystemTheme {
        ProfileScreen(fakeNavController)
    }
}