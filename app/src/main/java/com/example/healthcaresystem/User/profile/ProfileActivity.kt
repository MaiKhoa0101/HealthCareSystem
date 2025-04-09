package com.example.healthcaresystem.User.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.healthcaresystem.R
import com.example.healthcaresystem.User.post.ContainerPost
import com.example.healthcaresystem.User.post.ContentPost
import com.example.healthcaresystem.User.post.FooterItem
import com.example.healthcaresystem.User.post.ViewBanner
import com.example.healthcaresystem.User.post.ViewPost
import com.example.healthcaresystem.User.profile.ui.theme.HealthCareSystemTheme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthCareSystemTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProfileScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    LazyColumn() {
        item {
            UserInfo(
//            icon =R.drawable.img,
//            name = "Khoa xinh gái",
//            email = "@mavel_killer",
//            followerValue= 69,
//            followerString= "followers",
//            followingValue= 3,
//            followingString= "following",
//            likeValue= 70,
//            likeString= "likes",
//            buttonProf = "Chỉnh sửa hồ sơ",
//            buttonSchedule = "Lịch khám"
                profileUser = ProfileUser(
                    image = R.drawable.img,
                    name = "Khoa xinh gái",
                    title = "Đẹp gái lâu năm mà giấu",
                    butProf = "Chỉnh sửa hồ sơ",
                    butSchedule = "Quản lý phòng khám",
                    nExper = 69,
                    exper = "Kinh nghiệm",
                    nPatient = 3,
                    patient = "Bệnh nhân",
                    nRate = 70,
                    rate = "Đánh giá",
                    role = "1"
                )
            )
        }

        item {
            ViewBanner()
        }
        item {
            ViewPost(
                containerPost = ContainerPost(
                    image = R.drawable.img,
                    name = "Khoa xinh gái",
                    lable = "bla bla  "
                ),
                contentPost = ContentPost(
                    content = "bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla"
                ),
                footerItem = FooterItem(
                    name = "null",
                    image = R.drawable.avarta
                )
            )
        }

    }
}

@Composable
fun UserInfo(
    profileUser: ProfileUser,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color.Cyan
    ConstraintLayout(
        modifier = modifier
            .background(color = backgroundColor, shape = RectangleShape)
            .height(350.dp)
            .fillMaxWidth()
    ) {
        val verticalGuideLine30Start = createGuidelineFromStart(0.3f)
        val verticalGuideLine30End = createGuidelineFromEnd(0.3f)
        val horizontalGuideLine50 = createGuidelineFromTop(0.5f)

        val imgIcon = createRef()
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

        val (tvName, tvEmail) = createRefs()
        Text(
            profileUser.name,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = Color.Black
            ),
            modifier = Modifier.constrainAs(tvName) {
                top.linkTo(imgIcon.bottom, margin = 15.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )
        Text(
            profileUser.title,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.Gray
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
        val horizontalGuideLine30Bot = createGuidelineFromBottom(0.3f)

        val (tvNFollower, tvFollowers, tvNFollowing, tvFollowing, tvNLike, tvLikes) = createRefs()
        val numberTextColor = Color.Blue
        val textColor = Color.Black

        Text(
            profileUser.nExper.toString()+" năm",
            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 26.sp, color = numberTextColor),
            modifier = Modifier.constrainAs(tvNFollower) {
                top.linkTo(horizontalGuideLine30Bot, margin = 20.dp)
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
                top.linkTo(horizontalGuideLine30Bot, margin = 20.dp)
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
                top.linkTo(horizontalGuideLine30Bot, margin = 20.dp)
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
    HealthCareSystemTheme {
        ProfileScreen()
    }
}